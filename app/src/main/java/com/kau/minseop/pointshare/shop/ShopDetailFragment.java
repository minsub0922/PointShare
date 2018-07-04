package com.kau.minseop.pointshare.shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.CouponRecyclerViewAdapter;
import com.kau.minseop.pointshare.adapter.ShopRecyclerViewAdapter;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.model.ShoppingModel;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.utils.GetImageResource;

import org.w3c.dom.Text;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minseop on 2018-07-04.
 */

public class ShopDetailFragment extends BaseFragment {
    private int index;
    private List<ShoppingModel> list;
    private GridView gridView;
    private View v;
    private MyAdapter adapter;
    private Realm mRealm;
    private String walletBalance;
    private Credentials credential;
    private Web3j web3j;
    private WalletModel walletModel = new WalletModel();
    private AlertDialog.Builder alertDialogBuilder;
    private boolean doneGetMyWallet = false;
    private ImageView img_company;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_shop_detail, container, false);

        index = getArguments().getInt("index");

        initView();

        initWallet();

        return v;
    }

    private void initView(){
        if (index == 0) ( (MainActivity)getActivity()).updateToolbarTitle("COFFEE LIST");
        else if (index == 1) ( (MainActivity)getActivity()).updateToolbarTitle("TRAVEL LIST");
        else if (index == 2 ) ( (MainActivity)getActivity()).updateToolbarTitle("STORE LIST");

        list = ((MainActivity) getActivity()).getArrayList(index);

        img_company = v.findViewById(R.id.img_company);
        gridView = v.findViewById(R.id.fragment_shop_detail_gridView);
        adapter = new MyAdapter(getActivity(), list);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setOnItemClickListener(list.get(position));
            }
        });
    }

    private void initWallet(){
        web3j = Web3jFactory.build(new HttpService(testnetAddess));
        mRealm = Realm.getDefaultInstance();
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        getMyWallet();
        getWalletBallance(walletModel.getWalletAddress());
    }

    public void setOnItemClickListener(ShoppingModel model){
        Log.d("TAG", String.valueOf(model.getIndex()));
        alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
        alertDialogBuilder
                .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int remain = BigInteger.valueOf(Long.parseLong(model.getCouponModel().getPrice())).compareTo(BigInteger.valueOf(Long.parseLong(walletBalance)));
                        if (remain==-1) purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice(),model.getQrCode());
                        else Toast.makeText(getActivity(), "잔액이 부족합니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                });
        alertDialogBuilder.create().show();
    }

    private void getMyWallet(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        if (walletModels.size()>0) {
            doneGetMyWallet =true;
            walletModel = walletModels.get(0);
            readyForRequest(walletModel.getPassword(), walletModel.getDetailPath());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void readyForRequest(String pwd, String detailpath){
        //start sending request
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    credential = WalletUtils.loadCredentials(pwd, path+"/"+detailpath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getWalletBallance(String walletAddress) {
        //GetMyBalance
        new AsyncTask () {
            @Override
            protected Object doInBackground(Object[] objects) {
                BigInteger ethGetBalance = null;
                try {
                    ethGetBalance = web3j
                            .ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST)
                            .send()
                            .getBalance();
                    BigInteger wei = ethGetBalance;
                    walletBalance = wei.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Log.d("TAG","your walletBalance: " + walletBalance);
            }
        }.execute();
    }

    private static String decrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[16];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
        byte [] results = cipher.doFinal(Base64.decode(text, 0));
        return new String(results,"UTF-8");
    }

    private void purchaseCoupon(int index, String address, String price, String qrcode){
        MainActivity activity = ((MainActivity) getActivity());
        activity.snackBarOn("purchasing ...");
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Coupondeal contract = Coupondeal.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Transfer.sendFunds(
                            web3j, credential,
                            address,  // you can put any address here
                            BigDecimal.valueOf(Double.parseDouble( price)).multiply(BigDecimal.ONE), Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
                            .send();
                    contract.remove(BigInteger.valueOf(index)).send();  //delete from the smart contract
                    Log.d("TAG", "purchase success!!");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", String.valueOf(e));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                getWalletBallance(walletModel.getWalletAddress());
                Intent intent = new Intent(getActivity(),QRActivity.class);

                try {
                    intent.putExtra("qrcode",decrypt(qrcode,KEY));
                } catch (Exception e) {
                    Log.d("TAG", String.valueOf(e));
                }
                activity.snackBarDismiss();
                startActivity(intent);

            }
        }.execute();
    }


    class MyAdapter extends BaseAdapter {
        Context context;
        List<ShoppingModel> list;

        public MyAdapter(Context context, List<ShoppingModel> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView==null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.model_shop_recycler,parent,false);
            }
            final ShoppingModel model = list.get(position);
            ImageView img = convertView.findViewById(R.id.img_model_shop_recycler);
            ImageView img2 = convertView.findViewById(R.id.img2_model_shop_recycler);
            TextView cname = convertView.findViewById(R.id.txt_model_shop_cname);
            TextView balance = convertView.findViewById(R.id.txt_model_shop_price);
            TextView deadline = convertView.findViewById(R.id.txt_model_shop_deadline);

            GetImageResource getImageResource = new GetImageResource();
            Glide.with(context) .load(getImageResource.getCouponImgRes(model.getCouponModel().getcName())).into(img);
            Glide.with(context) .load(getImageResource.getCompanyImgRes(model.getCouponModel().getCompany())).into(img2);
            cname.setText(model.getCouponModel().getcName());
            balance.setText(model.getCouponModel().getPrice());
            deadline.setText(model.getCouponModel().getDeadline());

            return convertView;
        }
    }
}

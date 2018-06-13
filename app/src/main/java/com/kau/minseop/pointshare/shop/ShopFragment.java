package com.kau.minseop.pointshare.shop;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.ShopRecyclerViewAdapter;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.handler.BackPressHandler;
import com.kau.minseop.pointshare.loading.LoadingFragment;
import com.kau.minseop.pointshare.model.CouponModel;
import com.kau.minseop.pointshare.model.ShoppingModel;
import com.kau.minseop.pointshare.model.WalletModel;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minseop on 2018-06-09.
 */

public class ShopFragment extends LoadingFragment{
    private String KEY = "199301130922";
    private Realm mRealm;
    private String walletBalance;
    private Credentials credential;
    private Web3j web3j;
    private String contractAddress = "0xc4f089BC18CF1Ba71249367294C227BdFc9eb236";
    private WalletModel walletModel = new WalletModel();
    private TextView txt_balance;
    private RecyclerView rv_coffee, rv_travel, rv_store;
    private ShopRecyclerViewAdapter adapter_coffee, adapter_travel, adapter_store;
    private List<ShoppingModel> coffeeList = new ArrayList<>(), travelList = new ArrayList<>(), storeList = new ArrayList<>();
    private AlertDialog.Builder alertDialogBuilder;
    private boolean doneGetMyWallet = false;
    AppCompatDialog progressDialog;
    BackPressHandler backPressHandler;
    Handler mHandler =null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shop, container, false);



        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        mRealm = Realm.getDefaultInstance();

        alertDialogBuilder = new AlertDialog.Builder(getActivity());

        getMyWallet();

        buildTextView(v);

        buildRecyclerView(v);

        if (doneGetMyWallet) {
            startProgresss(1);
            getCouponList();

            getWalletBallance(walletModel.getWalletAddress());
        }

        return v;
    }
    private void timeHandler(AppCompatDialog activity) {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.setCancelable(true);
            }
        },2000);
    }

    private void buildTextView(View v){
        txt_balance = v.findViewById(R.id.txt_shop_mywallet_balance);
    }

    private void buildRecyclerView(View v){
        rv_coffee = v.findViewById(R.id.rv_fragment_shop_coffee);
        rv_coffee.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_coffee.setItemAnimator(new DefaultItemAnimator());
        adapter_coffee = new ShopRecyclerViewAdapter(coffeeList, getActivity());
        rv_coffee.setAdapter(adapter_coffee);

        rv_travel = v.findViewById(R.id.rv_fragment_shop_travel);
        rv_travel.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_travel.setItemAnimator(new DefaultItemAnimator());
        adapter_travel= new ShopRecyclerViewAdapter(travelList, getActivity());
        rv_travel.setAdapter(adapter_travel);

        rv_store = v.findViewById(R.id.rv_fragment_shop_store);
        rv_store.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_store.setItemAnimator(new DefaultItemAnimator());
        adapter_store= new ShopRecyclerViewAdapter(storeList, getActivity());
        rv_store.setAdapter(adapter_store);

        setOnClickListener();
    }

    private void setOnClickListener() {
        adapter_coffee.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = coffeeList.get(position);
                Log.d("TAG", String.valueOf(model.getIndex()));
                alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
                alertDialogBuilder
                        .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice(),model.getQrCode());
                                    }
                                })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                        });
                alertDialogBuilder.create().show();

            }
        });

        adapter_travel.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = travelList.get(position);
                Log.d("TAG", String.valueOf(model.getIndex()));
                alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
                alertDialogBuilder
                        .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice(), model.getQrCode());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                        });
                alertDialogBuilder.create().show();

            }
        });

        adapter_store.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = storeList.get(position);
                Log.d("TAG", String.valueOf(model.getIndex()));
                alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
                alertDialogBuilder
                        .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice(), model.getQrCode());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                        });
                alertDialogBuilder.create().show();
            }
        });
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
                txt_balance.setText(walletBalance);
                Log.d("TAG","your walletBalance: " + walletBalance);
            }
        }.execute();
    }

    private void getCouponList(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                Coupondeal contract = Coupondeal.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                int i=0;
                try {
                    while(true) {
                         Tuple6<String, String, String, String, String, String> coupon = contract.getCouponList(BigInteger.valueOf(i)).send();
                         if (coupon==null) break;
                         determineType(coupon.getValue3()).add(new ShoppingModel(coupon.getValue1(), coupon.getValue4(), i,new CouponModel(coupon.getValue2(), coupon.getValue3(), coupon.getValue5(), coupon.getValue6())));
                         //Log.d("TAG",coupon.getValue3());
                         i++;
                    }
                    Log.d("TAG", String.valueOf(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressOFF();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                adapter_store.notifyDataSetChanged();
                adapter_coffee.notifyDataSetChanged();
                adapter_travel.notifyDataSetChanged();
            }
        }.execute();
    }

    private List<ShoppingModel> determineType(String cname){
        if (cname.contains("스타")||cname.contains("TWOSOME")||cname.contains("COFFEEBEAN")){
            return coffeeList;
        }else if (cname.contains("GS") || cname.contains("CU") || cname.contains("SEVENELEVEN")){
            return storeList;
        }else if (cname.contains("그린")||cname.contains("SOCAR")||cname.contains("모두")){
            return travelList;
        }else return coffeeList;
    }


    private void purchaseCoupon(int index, String address, String price, String qrcode){
        startProgresss(2);
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
                    Log.d("TAG","wht???????" +e);
                }
                progressOFF();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                try {
                    decrypt(qrcode,KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getWalletBallance(walletModel.getWalletAddress());
                Intent intent = new Intent(getActivity(),QRActivity.class);
                intent.putExtra("qrcode","123532323");
                startActivity(intent);
            }
        }.execute();
    }

    public void startProgresss(int i){
        if(i==1)
        progressON(getActivity(),"리스트 받는중...");
        else if(i==2){
            progressON(getActivity(),"쿠폰 구매중...");
        }

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


}

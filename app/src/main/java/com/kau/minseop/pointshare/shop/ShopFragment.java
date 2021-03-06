package com.kau.minseop.pointshare.shop;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.ShopRecyclerViewAdapter;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.handler.BackPressHandler;
import com.kau.minseop.pointshare.loading.LoadingFragment;
import com.kau.minseop.pointshare.model.CouponModel;
import com.kau.minseop.pointshare.model.PurchasingModel;
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

public class ShopFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private Realm mRealm;
    private String walletBalance;
    private Credentials credential;
    private Web3j web3j;
    private WalletModel walletModel = new WalletModel();
    private TextView txt_balance, txt_coffee_more, txt_travel_more, txt_store_more;
    private RecyclerView rv_coffee, rv_travel, rv_store;
    private ShopRecyclerViewAdapter adapter_coffee, adapter_travel, adapter_store;
    private List<ShoppingModel> coffeeList = new ArrayList<>(), travelList = new ArrayList<>(), storeList = new ArrayList<>();
    private AlertDialog.Builder alertDialogBuilder;
    private boolean doneGetMyWallet = false;
    private ProgressBar progressBar;
    private TextView txt_coffee, txt_travel, txt_store;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CardView cardCoffee, cardStore, cardTravel;
    private ConstraintLayout constraintLayout;
    private boolean refreshing=false;

    public ShopFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shop, container, false);

        count++;

        setHasOptionsMenu(true);

        buildView(v);

        buildRecyclerView(v);

        Log.d("tagg", String.valueOf(count));
        if (count <= 1) {
          originPageBuild();
        } else if (count>1){
            copyPageBuild();
        }
        return v;
    }

    private void originPageBuild(){
        cardCoffee.setVisibility(View.INVISIBLE);
        cardStore.setVisibility(View.INVISIBLE);
        cardTravel.setVisibility(View.INVISIBLE);

        web3j = Web3jFactory.build(new HttpService(testnetAddess));
        mRealm = Realm.getDefaultInstance();
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        getMyWallet();

        if (doneGetMyWallet) {
            getCouponList();
            getWalletBallance(walletModel.getWalletAddress());
        }
    }

    private void copyPageBuild(){
        progressBar.setVisibility(View.GONE);
        txt_coffee.setText("커피");
        txt_travel.setText("여행");
        txt_store.setText("편의점");
        txt_balance.setText(walletBalance);
    }

    private void buildView(View v){
        txt_coffee = v.findViewById(R.id.txt_coffee);
        txt_travel = v.findViewById(R.id.txt_travel);
        txt_store = v.findViewById(R.id.txt_store);
        progressBar = v.findViewById(R.id.shop_progressbar);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(247,134,28), PorterDuff.Mode.MULTIPLY);
        txt_balance = v.findViewById(R.id.txt_shop_mywallet_balance);
        txt_coffee_more = v.findViewById(R.id.txt_coffee_more);
        txt_travel_more = v.findViewById(R.id.txt_travel_more);
        txt_store_more = v.findViewById(R.id.txt_store_more);
        txt_coffee_more.setOnClickListener(this);
        txt_travel_more.setOnClickListener(this);
        txt_store_more.setOnClickListener(this);
        cardCoffee = v.findViewById(R.id.cardview_coffee);
        cardStore = v.findViewById(R.id.cardview_store);
        cardTravel = v.findViewById(R.id.cardview_shop);

        swipeRefreshLayout = v.findViewById(R.id.shop_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        ( (MainActivity)getActivity()).updateToolbarTitle("");
    }


    @Override
    public void onRefresh() {
        refreshing=true;
        getCouponList();
        getWalletBallance(walletModel.getWalletAddress());
    }

    private void finishRefreshing(){
        swipeRefreshLayout.setRefreshing(false);
        refreshing=false;
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

        adapter_coffee.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = coffeeList.get(position);
                setOnItemClickListener(model);
            }
        });

        adapter_travel.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = travelList.get(position);
                setOnItemClickListener(model);
            }
        });

        adapter_store.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = storeList.get(position);
                setOnItemClickListener(model);
            }
        });
    }

    private void setOnItemClickListener(ShoppingModel model){
        Log.d("TAG", String.valueOf(model.getIndex()));
        alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
        alertDialogBuilder
                .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int remain = BigInteger.valueOf(Long.parseLong(model.getCouponModel().getPrice())).compareTo(BigInteger.valueOf(Long.parseLong(walletBalance)));
                        if (remain==-1){
                            //purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice(),model.getQrCode());
                            purchaseCoupon(model);
                        }
                        else Toast.makeText(getActivity(), "잔액이 부족합니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                });
        alertDialogBuilder.create().show();
    }

    private void createObject(ShoppingModel m){
        mRealm.beginTransaction();
        RealmResults<PurchasingModel> purchasingModels = mRealm.where(PurchasingModel.class).findAll();
        PurchasingModel purchasingModel;
        try {
            purchasingModel = mRealm.createObject(PurchasingModel.class, m.getIndex()); //primary key
            purchasingModel.setcName(m.getCouponModel().getcName());
            purchasingModel.setCompany(m.getCouponModel().getCompany());
            purchasingModel.setPrice(m.getCouponModel().getPrice());
            purchasingModel.setDeadline(m.getCouponModel().getDeadline());

        }catch (Exception e){
            e.printStackTrace();
        }
        mRealm.commitTransaction();
        Log.v("TAG", String.valueOf(purchasingModels.size()));
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
                coffeeList.clear();
                travelList.clear();
                storeList.clear();

                Coupondeal contract = Coupondeal.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                int i=0;
                try {
                    while(true) {
                         Tuple6<String, String, String, String, String, String> coupon = contract.getCouponList(BigInteger.valueOf(i)).send();
                         if (coupon==null) break;
                         determineType(coupon.getValue3()).add(new ShoppingModel(coupon.getValue1(), coupon.getValue4(), i,new CouponModel(coupon.getValue2(), coupon.getValue3(), coupon.getValue5(), coupon.getValue6())));
                         i++;
                    }
                    Log.d("TAG", String.valueOf(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                adapter_store.notifyDataSetChanged();
                adapter_coffee.notifyDataSetChanged();
                adapter_travel.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                txt_coffee.setText("커피");
                txt_travel.setText("여행");
                txt_store.setText("편의점");

                cardCoffee.setVisibility(View.VISIBLE);
                cardStore.setVisibility(View.VISIBLE);
                cardTravel.setVisibility(View.VISIBLE);

                finishRefreshing();
            }
        }.execute();
    }

    private List<ShoppingModel> determineType(String cname){
        if (cname.contains("스타")||cname.contains("TWOSOME")||cname.contains("COFFEEBEAN")){
            return coffeeList;
        }else if (cname.contains("GS") || cname.contains("CU") || cname.contains("SEVENELEVEN")|| cname.contains("cu")){
            return storeList;
        }else if (cname.contains("그린")||cname.contains("SOCAR")||cname.contains("모두")||cname.contains("socar")){
            return travelList;
        }else return coffeeList;
    }


    private void purchaseCoupon(ShoppingModel model){
        int index = model.getIndex();
        String address = model.getSellerAddress();
        String price = model.getCouponModel().getPrice();
        String qrcode = model.getQrCode();
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
                createObject(model);
                startActivity(intent);

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_coffee_more){
            ((MainActivity) getActivity()).setArrayList(coffeeList,0);
            ShopDetailFragment fragment = new ShopDetailFragment();
            Bundle args = new Bundle();
            args.putInt("index",0);
            fragment.setArguments(args);
            mFragmentNavigation.pushFragment(fragment);
        }else if (v.getId() == R.id.txt_travel_more){
            ((MainActivity) getActivity()).setArrayList(travelList,1);
            ShopDetailFragment fragment = new ShopDetailFragment();
            Bundle args = new Bundle();
            args.putInt("index",1);

            fragment.setArguments(args);
            mFragmentNavigation.pushFragment(fragment);
        }else if (v.getId() == R.id.txt_store_more){
            ((MainActivity) getActivity()).setArrayList(storeList,2);
            ShopDetailFragment fragment = new ShopDetailFragment();
            Bundle args = new Bundle();
            args.putInt("index",2);
            fragment.setArguments(args);
            mFragmentNavigation.pushFragment(fragment);
        }
    }
}

package com.kau.minseop.pointshare.shop;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.ShopRecyclerViewAdapter;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.model.CouponModel;
import com.kau.minseop.pointshare.model.ShoppingModel;
import com.kau.minseop.pointshare.model.WalletModel;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
import java.util.concurrent.Future;
import java.util.function.LongFunction;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minseop on 2018-06-09.
 */

public class ShopFragment extends BaseFragment{
    private Realm mRealm;
    private String walletBalance;
    private Credentials credential;
    private Web3j web3j;
    private String contractAddress = "0xc4f089BC18CF1Ba71249367294C227BdFc9eb236";
    private WalletModel walletModel = new WalletModel();
    private TextView txt_balance;
    private RecyclerView rv_coffee, rv_travel, rv_store;
    private ShopRecyclerViewAdapter adapter_coffee, adapter_travel, adapter_store;
    private Coupondeal contract;
    private List<ShoppingModel> coffeeList = new ArrayList<>(), travelList = new ArrayList<>(), storeList = new ArrayList<>();
    private AlertDialog.Builder alertDialogBuilder;

    AppCompatDialog progressDialog;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shop, container, false);

        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        mRealm = Realm.getDefaultInstance();

        alertDialogBuilder = new AlertDialog.Builder(getActivity());

        getMyWallet();

        buildTextView(v);

        buildRecyclerView(v);

        startProgresss();
        getCouponList();


        getWalletBallance(walletModel.getWalletAddress());

        return v;
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

                alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
                alertDialogBuilder
                        .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice());
                                    }
                                })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                        });
                alertDialogBuilder.create().show();

                Log.d("TAG","get index : "+model.getIndex());
                Toast.makeText(getActivity(), model.getCouponModel().getcName(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter_travel.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = travelList.get(position);
                alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
                alertDialogBuilder
                        .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                        });
                alertDialogBuilder.create().show();

                Log.d("TAG","get index : "+model.getIndex());
                Toast.makeText(getActivity(), model.getCouponModel().getcName(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter_store.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = storeList.get(position);
                alertDialogBuilder.setTitle(model.getCouponModel().getcName()+" 구매하기");
                alertDialogBuilder
                        .setMessage(model.getCouponModel().getPrice() + "구매하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                purchaseCoupon(model.getIndex(),model.getSellerAddress(),model.getCouponModel().getPrice());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {dialog.cancel();}
                        });
                alertDialogBuilder.create().show();

                Log.d("TAG","get index : "+model.getIndex());
                Toast.makeText(getActivity(), model.getCouponModel().getcName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMyWallet(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        walletModel = walletModels.get(0);
        readyForRequest(walletModel.getPassword(), walletModel.getDetailPath());
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
                         Log.d("TAG",coupon.getValue3());
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


    private void purchaseCoupon(int index, String address, String price){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Transfer.sendFunds(
                            web3j, credential,
                            address,  // you can put any address here
                            BigDecimal.valueOf(Double.parseDouble( price)).multiply(BigDecimal.ONE), Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
                            .send();
                    contract.remove(BigInteger.valueOf(index)).send();  //delete from the smart contract
                    Log.d("TAG", "purchase success!!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                getWalletBallance(walletModel.getWalletAddress());
            }
        }.execute();
    }

    public void startProgresss(){
        progressON(getActivity(),"리스트 받는중...");
    }

    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }


    }
    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }
    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

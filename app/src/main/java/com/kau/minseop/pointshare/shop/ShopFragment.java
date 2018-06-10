package com.kau.minseop.pointshare.shop;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.ShopRecyclerViewAdapter;
import com.kau.minseop.pointshare.model.CouponModel;
import com.kau.minseop.pointshare.model.ShoppingModel;
import com.kau.minseop.pointshare.model.WalletModel;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
    private List<ShoppingModel> coffeeList = new ArrayList<>(), travelList = new ArrayList<>(), storeList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shop, container, false);

        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        mRealm = Realm.getDefaultInstance();

        getMyWallet();

        buildTextView(v);

        buildRecyclerView(v);



        test();

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
                Toast.makeText(getActivity(), model.getCouponModel().getcName(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter_travel.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = travelList.get(position);
                Toast.makeText(getActivity(), model.getCouponModel().getcName(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter_store.setOnItemClickListener(new ShopRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShoppingModel model = storeList.get(position);
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
        getWalletBallance(walletModel.getWalletAddress());
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

    private void test(){
        coffeeList.add(new ShoppingModel("", "",new CouponModel("아메리카노","스타벅스","2000","2014-3-3")));
        storeList.add(new ShoppingModel("", "",new CouponModel("아메리카노","GS25","2000","2014-3-3")));
        travelList.add(new ShoppingModel("", "",new CouponModel("아메리카노","SOCAR","2000","2014-3-3")));
        travelList.add(new ShoppingModel("", "",new CouponModel("아메리카노","그린카","2000","2014-3-3")));
        travelList.add(new ShoppingModel("", "",new CouponModel("아메리카노","SOCAR","2000","2014-3-3")));
        travelList.add(new ShoppingModel("", "",new CouponModel("아메리카노","SOCAR","2000","2014-3-3")));
        adapter_store.notifyDataSetChanged();
        adapter_coffee.notifyDataSetChanged();
        adapter_travel.notifyDataSetChanged();
    }
}

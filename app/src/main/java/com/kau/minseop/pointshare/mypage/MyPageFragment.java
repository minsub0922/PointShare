package com.kau.minseop.pointshare.mypage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.CouponRecyclerViewAdapter;
import com.kau.minseop.pointshare.model.CouponModel;
import com.kau.minseop.pointshare.model.PurchasingModel;
import com.kau.minseop.pointshare.model.WalletModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by hihi5 on 2018-07-04.
 */

public class MyPageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private CouponRecyclerViewAdapter adapter;
    private ArrayList<CouponModel> mItems = new ArrayList<>();
    private CouponModel couponModel;
    private AppCompatDialog progressDialog;
    private Realm mRealm;
    private ArrayList<String> transactions;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean refreshing=false;

    private String walletAddress;

    private RecyclerView rv;

    Handler mHandler =null;
    View v;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_mypage, container, false);

        swipeRefreshLayout = v.findViewById(R.id.shop_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        rv = v.findViewById(R.id.couponRecyclerView);
        rv.addItemDecoration(new DividerItemDecoration(getContext(), new LinearLayoutManager(this.getContext()).getOrientation()));
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()) );
        walletAddress = getArguments().getString("walletAddress");

        mRealm = Realm.getDefaultInstance();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        adapter = new CouponRecyclerViewAdapter(mItems);


        getPurchasingList();



        return v;
    }

    private void getPurchasingList(){
        ArrayList<PurchasingModel> pml = new ArrayList<>();
        mRealm.beginTransaction();
        RealmResults<PurchasingModel> purchasingModels = mRealm.where(PurchasingModel.class).findAll();
        mRealm.commitTransaction();
        Log.v("TAG", String.valueOf(purchasingModels.size()));
        if (purchasingModels.size()>0) {
            for(int i=0;i<purchasingModels.size();i++){
                pml.add(purchasingModels.get(i));
            }
            setData(pml);
        }
    }

    private void setData(ArrayList<PurchasingModel> pm) {
        mItems.clear();

        for(int i=0;i<pm.size();i++){
            couponModel = new CouponModel(pm.get(i).getcName(),pm.get(i).getCompany(),pm.get(i).getPrice(),pm.get(i).getDeadline());
            mItems.add(couponModel);
        }

        adapter.notifyDataSetChanged();
        finishRefreshing();
    }

    @Override
    public void onRefresh() {
        getPurchasingList();
        refreshing=true;

    }

    private void finishRefreshing(){
        swipeRefreshLayout.setRefreshing(false);
        refreshing=false;
    }

}
package com.kau.minseop.pointshare.cardlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.CardlistRecyclerViewAdapter;
import com.kau.minseop.pointshare.adapter.CouponRecyclerViewAdapter;
import com.kau.minseop.pointshare.model.CardListModel;

import java.util.ArrayList;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.model.CouponModel;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.model.WalletViewHolerModel;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ManagedTransaction;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import io.realm.Realm;
import io.realm.RealmResults;

public class CardListViewActivity extends AppCompatActivity {
    private CouponRecyclerViewAdapter adapter;
    private ArrayList<CouponModel> mItems = new ArrayList<>();
    private Web3j web3j;
    TextView cardType;
    TextView cardNum;
    ImageView Im_qrCode;
    private String qrCode;
    private Credentials credential;
    private Realm mRealm;
    private String contractAddress = "0xc4f089BC18CF1Ba71249367294C227BdFc9eb236";
    private String walletBalance;
    private Coupondeal contract;
    private WalletModel walletModel = new WalletModel();
    private CouponModel couponModel;
    AppCompatDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list_view);
        cardType = (TextView)findViewById(R.id.cardtype);
        cardNum = (TextView)findViewById(R.id.cardnum);
        Im_qrCode = (ImageView)findViewById(R.id.qrCode);

        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        mRealm = Realm.getDefaultInstance();
        getWallet();

        Intent intent = getIntent();
        String cType = intent.getExtras().getString("cardtype");
        String cNum = intent.getExtras().getString("cardnum");
        String cPassward = intent.getExtras().getString("cardPassward");
        String cPeriod = intent.getExtras().getString("cardPeriod");
        qrCode= cNum+cPassward;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        adapter = new CouponRecyclerViewAdapter(mItems);
        RecyclerView rv;
        rv = findViewById(R.id.recyclerView);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()) );
        setData();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Im_qrCode.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }

        cardType.setText(cType);
        cardNum.setText(cNum);

        adapter.setItemClick(new CouponRecyclerViewAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                // 제목셋팅
                alertDialogBuilder.setTitle("쿠폰 등록");

                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("쿠폰을 정말로 등록할 것입니까?")
                        .setCancelable(true)
                        .setPositiveButton("네",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        startProgresss();
                                        sendCoupon(mItems.get(position),qrCode);
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                // 다이얼로그 보여주기
                alertDialog.show();


                //mItems.remove(position);
            }
        });
    }
    private void setData() {
        mItems.clear();
        couponModel = new CouponModel("아메리카노M","스타벅스","1000","~2018.11.10");
        mItems.add(couponModel);
        couponModel = new CouponModel("체리블라썸M","스타벅스","4000","~2020.12.05");
        mItems.add(couponModel);
        couponModel = new CouponModel("그린라떼200ML","스타벅스","2000","~2018.09.10");
        mItems.add(couponModel);
        couponModel = new CouponModel("2시간무료이용권","그린카","3000","~2023.12.30");
        mItems.add(couponModel);
        couponModel = new CouponModel("3시간이상이용시20%할인권","SOCAR","5000","~2018.07.22");
        mItems.add(couponModel);
        couponModel = new CouponModel("신한은행70%환율우대쿠폰","모두투어","2000","~2018.06.07");
        mItems.add(couponModel);
        couponModel = new CouponModel("하나은행70%환율우대쿠폰","모두투어","2000","~2019.04.10");
        mItems.add(couponModel);
        couponModel = new CouponModel("(스타벅스)콜드브루270ML","GS25","800","~2020.10.05");
        mItems.add(couponModel);
        couponModel = new CouponModel("파이크플레이스블랙275ML","GS25","500","~2020.05.24");
        adapter.notifyDataSetChanged();
    }

    private void getWallet(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        if (walletModels.size()>0){
            walletModel = walletModels.get(0);
            readyForRequest(walletModel.getPassword(), walletModel.getDetailPath());
            getWalletBallance(walletModel.getWalletAddress());
        }
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
                    Log.d("TAG","failed !!! generate Wallet");

                }
                return null;
            }
        }.execute();
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
                    contract = Coupondeal.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    Log.d("TAG","done credential");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG","failed !!!");
                }
                return null;
            }
        }.execute();
    }

    private void sendCoupon(CouponModel couponModel, String qrcode) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    contract = Coupondeal.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    contract.createCoupon(couponModel.getcName(), couponModel.getCompany(), qrcode, couponModel.getPrice(), couponModel.getDeadline()).send();
                    Log.d("TAG","잘했어요");
                    progressOFF();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
    public void startProgresss(){
        progressON(this,"올리는중...");
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

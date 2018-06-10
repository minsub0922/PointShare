package com.kau.minseop.pointshare.cardlist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.R;
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

    TextView cardType;
    private Web3j web3j;
    TextView cardNum;
    ImageView Im_qrCode;
    private Credentials credential;
    private Realm mRealm;
    private String contractAddress = "0xc4f089BC18CF1Ba71249367294C227BdFc9eb236";
    private String walletBalance;
    private Coupondeal contract;
    private WalletModel walletModel = new WalletModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list_view);
        cardType = (TextView)findViewById(R.id.cardtype);
        cardNum = (TextView)findViewById(R.id.cardnum);

        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        mRealm = Realm.getDefaultInstance();

        getWallet();

        Intent intent = getIntent();
        String cType = intent.getExtras().getString("cardtype");
        String cNum = intent.getExtras().getString("cardnum");
        String cPassward = intent.getExtras().getString("cardPassward");
        String qrCode = cNum+cPassward;

        Bitmap bitmap=getIntent().getParcelableExtra("pic");
        cardType.setText(cType);
        cardNum.setText(cNum);
    }

    private void getWallet(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        if (walletModels.size()<0){
            walletModel = walletModels.get(0);
            readyForRequest(walletModel.getPassword(), walletModel.getDetailPath());
            getWalletBallance(walletModel.getWalletAddress());
            Log.d("TAG", String.valueOf(walletModel));
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void sendCoupon(CouponModel couponModel, String qrcode) {
        try {
            contract.createCoupon(couponModel.getcName(),couponModel.getCompany(),qrcode,couponModel.getPrice(),couponModel.getDeadline()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

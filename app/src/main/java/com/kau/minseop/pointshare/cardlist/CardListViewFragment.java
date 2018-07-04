package com.kau.minseop.pointshare.cardlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.CouponRecyclerViewAdapter;

import java.util.ArrayList;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.model.CouponModel;
import com.kau.minseop.pointshare.model.WalletModel;

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
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import io.realm.RealmResults;

public class CardListViewFragment extends BaseFragment {
    private CouponRecyclerViewAdapter adapter;
    private ArrayList<CouponModel> mItems = new ArrayList<>();
    private Web3j web3j;
    private TextView cardNum;
    private ImageView Im_qrCode;
    private String qrCode;
    private Credentials credential;
    private Realm mRealm;
    private String walletBalance;
    private Coupondeal contract;
    private WalletModel walletModel = new WalletModel();
    private CouponModel couponModel;
    View v;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_card_list_view, container, false);

        cardNum = (TextView)v.findViewById(R.id.cardnum);
        Im_qrCode = (ImageView)v.findViewById(R.id.qrCode);

        web3j = Web3jFactory.build(new HttpService(testnetAddess));
        mRealm = Realm.getDefaultInstance();

        getWallet();

        String cType = getArguments().getString("cardtype");
        String cNum = getArguments().getString("cardnum");
        String cPassward = getArguments().getString("cardPassward");
        String cPeriod = getArguments().getString("cardPeriod");
        try {
            qrCode = cNum + cPassward;//= encrypt( cNum+cPassward, KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity() );
        adapter = new CouponRecyclerViewAdapter(mItems);
        RecyclerView rv;
        rv = v.findViewById(R.id.recyclerView);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()) );
        setData();


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            //BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE,250,250);
            BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.CODE_128,400,170);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Im_qrCode.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }


        cardNum.setText(cNum);

        adapter.setItemClick(new CouponRecyclerViewAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                // 제목셋팅
                alertDialogBuilder.setTitle("쿠폰 등록");

                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("쿠폰을 판매하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //startProgresss();
                                sendCoupon(mItems.get(position),qrCode);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(
                                DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }});

                alertDialogBuilder.show();
            }
        });

        ( (MainActivity)getActivity()).updateToolbarTitle(cType);
        return v;
    }

    private static String encrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[16];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.encodeToString(results, 0);
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
        mItems.add(couponModel);
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


        final boolean[] isSuccess = new boolean[1];

        MainActivity activity = ((MainActivity) getActivity());
        activity.snackBarOn("uploading ...");

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    contract = Coupondeal.load(contractAddress, web3j, credential, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
                    contract.createCoupon(couponModel.getcName(), couponModel.getCompany(), encrypt(qrcode,KEY), couponModel.getPrice(), couponModel.getDeadline()).send();
                    Log.d("TAG","잘했어요");
                    isSuccess[0] = true;
                    //progressOFF();
                } catch (Exception e) {
                    e.printStackTrace();
                    isSuccess[0] = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (!isSuccess[0]) {
                    Toast.makeText(getActivity(), "insufficient funds for gas, price ...", Toast.LENGTH_SHORT).show();
                }else{
                    activity.snackBarDismiss();
            }
            }
        }.execute();
    }
}

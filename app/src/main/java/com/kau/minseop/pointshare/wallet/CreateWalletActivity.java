package com.kau.minseop.pointshare.wallet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.generation.GenerationActivity;
import com.kau.minseop.pointshare.generation.GenerationContract;
import com.kau.minseop.pointshare.generation.GenerationPresenter;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.model.WalletViewHolerModel;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minseop on 2018-11-22.
 */

public class CreateWalletActivity extends AppCompatActivity implements GenerationContract.View, View.OnClickListener{

    private Realm mRealm;
    private final String KEY = "199301130922";
    private final String contractAddress = "0xb9e35DC2Ce838b4E7A1cDf722d0363bba61E0bf7";
    private Web3j web3j;
    private String testnetAddess = "https://ropsten.infura.io/wd7279F18YpzuVLkfZTk";
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 0;
    private GenerationContract.Presenter mWalletPresenter;
    private String detailPath;
    private String mWalletAddress;
    private EditText edtID, edtPW;
    private Button btnCreateWallet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_wallet);

        allowExternalStorageAccess();

        web3j = Web3jFactory.build(new HttpService(testnetAddess));

        mRealm = Realm.getDefaultInstance();

        initView();

    }

    private void initView(){

        edtID = findViewById(R.id.edt_create_wallet_name);
        edtPW = findViewById(R.id.edt_create_wallet_password);
        btnCreateWallet = findViewById(R.id.btn_login_create_wallet);
        btnCreateWallet.setOnClickListener(this);

    }


    private void allowExternalStorageAccess(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE_STORAGE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login_create_wallet){
            Log.d("TAGG",edtID.getText().toString());
            if (edtID.getText().toString().replaceAll(" ","").getBytes().length > 0 && edtPW.getText().toString().replaceAll(" ","").getBytes().length > 0) {
                mWalletPresenter = new GenerationPresenter(CreateWalletActivity.this, edtPW.getText().toString());
                mWalletPresenter.generateWallet(edtPW.getText().toString());
                createObject(mWalletAddress, edtPW.getText().toString(), detailPath);
            }
            else Toast.makeText(CreateWalletActivity.this, "양식을 모두 채워주십시오.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setPresenter(GenerationContract.Presenter presenter) {
        mWalletPresenter = presenter;
    }

    @Override
    public void showGeneratedWallet(String walletAddress, String detailPath) {
        mWalletAddress = walletAddress;
        this.detailPath = detailPath;
    }

    private void createObject(String mWalletAddress, String mPassword, String detailPath){
        mRealm.beginTransaction();
        Intent returnIntent = new Intent();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        WalletModel walletModel;
        try {
            walletModel = mRealm.createObject(WalletModel.class, edtID.getText().toString()); //primary key
            walletModel.setWalletAddress(mWalletAddress);
            walletModel.setPassword(mPassword);
            walletModel.setDetailPath(detailPath);
            Log.d("TAG","your account is set:  "+walletModel);
            setResult(Activity.RESULT_OK,returnIntent);
        }catch (Exception e){
            Toast.makeText(CreateWalletActivity.this, "the Name already exist", Toast.LENGTH_SHORT).show();
        }
        mRealm.commitTransaction();

        startActivity(new Intent(CreateWalletActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getObject();

    }

    private void getObject(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();

        if (walletModels.size() > 0){

            startActivity(new Intent(CreateWalletActivity.this, MainActivity.class));
            finish();

        }
    }
}

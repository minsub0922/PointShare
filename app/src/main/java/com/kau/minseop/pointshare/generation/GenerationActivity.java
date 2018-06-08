package com.kau.minseop.pointshare.generation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.wallet.WalletActivity;

import org.web3j.crypto.Wallet;

import io.realm.Realm;
import io.realm.RealmResults;

public class GenerationActivity extends AppCompatActivity implements GenerationContract.View, View.OnClickListener {

    public static final String TAG = GenerationActivity.class.getName();
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 0;
    private GenerationContract.Presenter mWalletPresenter;
    private Button mGenerateWalletButton, myWalletButton;
    private String mWalletAddress;
    private EditText mPassword;
    private String detailPath;
    private Realm mRealm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generation);
        myWalletButton = findViewById(R.id.use_my_wallet_button);
        mGenerateWalletButton = (Button) findViewById(R.id.generate_wallet_button);
        mPassword = (EditText) findViewById(R.id.password);
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        myWalletButton.setOnClickListener(this);
        mGenerateWalletButton.setOnClickListener(this);
    }

    @Override
    public void setPresenter(GenerationContract.Presenter presenter) {
        mWalletPresenter = presenter;
        Log.d("TAG","im in!");
    }

    @Override
    public void showGeneratedWallet(String address, String detailPath) {
        mWalletAddress = address;
        this.detailPath = detailPath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_STORAGE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                } else {
                    mWalletPresenter.generateWallet(mPassword.getText().toString());
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(GenerationActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    GenerationActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE_STORAGE);
        }
        else if (v.getId() == R.id.generate_wallet_button){
            mWalletPresenter = new GenerationPresenter(GenerationActivity.this, mPassword.getText().toString());
            mWalletPresenter.generateWallet(mPassword.getText().toString());
            createObject(mWalletAddress, mPassword.getText().toString(), detailPath);
            Intent intent = new Intent(GenerationActivity.this, WalletActivity.class);
            intent.putExtra("WalletAddress", mWalletAddress);
            intent.putExtra("password", mPassword.getText().toString());
            intent.putExtra("detailPath",detailPath);
            startActivity(intent);
            finish();

        }else if (v.getId()==R.id.use_my_wallet_button){
            WalletModel walletModel = getObject();
            if (walletModel!=null) {
                Intent intent = new Intent(GenerationActivity.this, WalletActivity.class);
                intent.putExtra("WalletAddress", walletModel.getWalletAddress());
                intent.putExtra("password", walletModel.getPassword());
                intent.putExtra("detailPath", walletModel.getDetailPath());
                startActivity(intent);
                finish();
            }else Toast.makeText(GenerationActivity.this,"지갑을 생성해 주십시오.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createObject(String mWalletAddress, String mPassword, String detailPath){
        mRealm.beginTransaction();
        RealmResults <WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        WalletModel walletModel;
        if (walletModels.size()!=0){
            walletModel = walletModels.get(0);
        }
        else {
            walletModel = mRealm.createObject(WalletModel.class, new Long(0));
        }
        walletModel.setWalletAddress(mWalletAddress);
        walletModel.setPassword(mPassword);
        walletModel.setDetailPath(detailPath);
        mRealm.commitTransaction();
    }

    private WalletModel getObject(){
        mRealm.beginTransaction();
        RealmResults <WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        Log.d("TAG", String.valueOf( walletModels.size()));
        Log.d("TAG", String.valueOf( walletModels.get(0)));
        if (walletModels.size()>0) return walletModels.get(0);
        return null;
    }
}

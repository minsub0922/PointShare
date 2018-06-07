package com.kau.minseop.pointshare.generation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.wallet.WalletActivity;

public class GenerationActivity extends AppCompatActivity implements GenerationContract.View {

    public static final String TAG = GenerationActivity.class.getName();

    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 0;

    private GenerationContract.Presenter mWalletPresenter;

    private Button mGenerateWalletButton, mybtn;

    private String mWalletAddress;

    private EditText mPassword;

    private String detailPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generation);
        mybtn = findViewById(R.id.use_my_wallet_button);
        mGenerateWalletButton = (Button) findViewById(R.id.generate_wallet_button);
        mPassword = (EditText) findViewById(R.id.password);

        mybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(GenerationActivity.this, WalletActivity.class);
                    intent.putExtra("WalletAddress", "alstjq5");
                    intent.putExtra("password", "0x07bbd6511fd36677bda7452ebf243acdd63f880f");
                    intent.putExtra("detailPath","UTC--2018-06-07T05-18-20.196--07bbd6511fd36677bda7452ebf243acdd63f880f.json");

                    startActivity(intent);
                    finish();
            }
        });

        mGenerateWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(GenerationActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            GenerationActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_WRITE_STORAGE);
                } else {
                    mWalletPresenter = new GenerationPresenter(GenerationActivity.this,
                    mPassword.getText().toString());
                    mWalletPresenter.generateWallet(mPassword.getText().toString());
                    Intent intent = new Intent(GenerationActivity.this, WalletActivity.class);
                    intent.putExtra("WalletAddress", mWalletAddress);
                    intent.putExtra("password", mPassword.getText().toString());
                    intent.putExtra("detailPath",detailPath);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void setPresenter(GenerationContract.Presenter presenter) {
        mWalletPresenter = presenter;
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
}

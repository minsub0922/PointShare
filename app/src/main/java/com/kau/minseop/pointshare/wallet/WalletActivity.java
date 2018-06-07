package com.kau.minseop.pointshare.wallet;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.R;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;

/**
 * Created by eirlis on 29.06.17.
 */

public class WalletActivity extends AppCompatActivity {

    public static final String TAG = WalletActivity.class.getName();

    private TextView mWalletAddress;
    private TextView mBalance;
    private String password;
    private String walletAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Bundle extras = getIntent().getExtras();
        password = extras.getString("password");
        walletAddress = extras.getString("WalletAddress");

        //Web3j web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        Web3j web3j = Web3jFactory.build(new HttpService());

        try {
            //start sending request
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            Log.d("TAG", "Client version: " + web3ClientVersion.getWeb3ClientVersion());

            //create and deploy my smart contract
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Credentials credentials = WalletUtils.loadCredentials(password, path+"/"+WalletUtils.generateLightNewWalletFile(password, new File(String.valueOf(path))));


           /*
            TransactionReceipt transactionReceipt = Transfer.sendFunds(
                    web3j, credentials, "0x<address>|<ensName>",
                    BigDecimal.valueOf(1.0), Convert.Unit.ETHER)
                    .send();*/

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "Exception : "+e);
        } catch (CipherException e) {
            e.printStackTrace();
            Log.d("TAG", "Exception : "+e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("TAG", "Exception : "+e);
        }/* catch (TransactionException e) {
            e.printStackTrace();
            Log.d("TAG", "Exception : "+e);
        }*/ catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "Exception : "+e);
        }


        mWalletAddress = (TextView) findViewById(R.id.account_address);
        mBalance = (TextView) findViewById(R.id.wallet_balance);

        mWalletAddress.setText(walletAddress);
    }
}

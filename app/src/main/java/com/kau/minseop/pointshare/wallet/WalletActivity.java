package com.kau.minseop.pointshare.wallet;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.greeter.Greeter;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.Future;

/**
 * Created by eirlis on 29.06.17.
 */

public class WalletActivity extends AppCompatActivity {

    public static final String TAG = WalletActivity.class.getName();

    private TextView mWalletAddress;
    private TextView mBalance;
    private String balance;
    private String password;
    private String walletAddress;
    private String detailPath;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Bundle extras = getIntent().getExtras();
        password = extras.getString("password");
        walletAddress = extras.getString("WalletAddress");
        detailPath = extras.getString("detailPath");
     /*   password = "alstjq5";
        walletAddress = "0x07bbd6511fd36677bda7452ebf243acdd63f880f";*/

        Web3j web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));
        //Web3j web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));

        try {
            //start sending request
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            Log.d("TAG", "Client version: " + web3ClientVersion.getWeb3ClientVersion());
            //Log.d("TAG","Connected to Ethereum client version:" + web3j.web3ClientVersion().send().getWeb3ClientVersion());

            //create and deploy my smart contract
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //Credentials credentials = WalletUtils.loadCredentials(password, path+"/"+WalletUtils.generateLightNewWalletFile(password, new File(String.valueOf(path))));
            Credentials credentials = WalletUtils.loadCredentials(password, path+"/"+detailPath);
            Log.d("TAG","Credentials loaded //  "+credentials.getAddress());

           /* //Log.d("TAG", String.valueOf(WalletUtils.isValidAddress(walletAddress)));   쓸수있고

            Log.d("TAG","Sending 1 Wei ("
                    + Convert.fromWei("1", Convert.Unit.ETHER).toPlainString() + " Ether)");

            Future<TransactionReceipt> transferReceipt = Transfer.sendFunds(
                    web3j, credentials,
                    "0xb3FA37CA8918432cAC914C40f8C4748a6dBd0fA4",  // you can put any address here
                    BigDecimal.ONE, Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
                    .sendAsync();
            Log.d("TAG","Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                    //+ transferReceipt.getTransactionHash());
                    + transferReceipt.get());*/

            //GetMyBalance

            // send asynchronous requests to get balance
            EthGetBalance ethGetBalance = web3j
                    .ethGetBalance("0x07bbd6511fd36677bda7452ebf243acdd63f880f", DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();

            BigInteger wei = ethGetBalance.getBalance();
            balance = wei.toString();
            Log.d("TAG", "Wei !!! : "+wei.toString());

            // Now lets deploy a smart contract
            Log.d("TAG", "Deploying smart contract");


            new AsyncTask<String, Void, String> (){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected String doInBackground(String... params) {
                    Greeter contract = null;
                    try {
                        contract = Greeter.deploy(
                                web3j, credentials,
                                ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT,
                                    "Hello blockchain world!").send();

                        String contractAddress = contract.getContractAddress();
                        Log.d("TAG","Smart contract deployed to address " + contractAddress);
                        Log.d("TAG","View contract at https://ropsten.etherscan.io/address/" + contractAddress);
                        Log.d("TAG","Value stored in remote smart contract: " + contract.greet().send());
                        } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if(result != null){
                        Log.d("ASYNC", "result = " + result);
                    }
                }
                @Override
                protected void onCancelled() {
                    super.onCancelled();
                }
            }.execute();


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

        mBalance.setText(balance);
        mWalletAddress.setText(walletAddress);
    }


}

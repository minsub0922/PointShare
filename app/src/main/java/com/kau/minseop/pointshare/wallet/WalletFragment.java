package com.kau.minseop.pointshare.wallet;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.WalletRecyclerViewAdapter;
import com.kau.minseop.pointshare.generation.GenerationFragment;
import com.kau.minseop.pointshare.greeter.Greeter;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.model.WalletViewHolerModel;
import com.kenai.jffi.Main;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by eirlis on 29.06.17.
 */

public class WalletFragment extends Fragment {

    public static final String TAG = WalletFragment.class.getName();

    private TextView mWalletAddress;
    private TextView mBalance;
    private String balance;
    private String password;
    private String walletAddress;
    private String detailPath;
    private Web3j web3j;
    private Credentials credentials;
    private Realm mRealm;
    private String contractAddress = "0x099a1a124c9b2fce8b3ddaf77d16faca3e1f79bb";
    private Button attachWallet;
    private RecyclerView rv;
    private WalletRecyclerViewAdapter adapter;
    private final List<WalletViewHolerModel> modelList = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        mWalletAddress = (TextView) v.findViewById(R.id.account_address);
        mBalance = (TextView) v.findViewById(R.id.wallet_balance);
        rv = v.findViewById(R.id.rv_fragment_wallet);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new WalletRecyclerViewAdapter(modelList);
        rv.setAdapter(adapter);

        modelList.add(new WalletViewHolerModel("","my first wallet","22","123123"));
        modelList.add(new WalletViewHolerModel("","my second wallet","3333","345345"));
        adapter.notifyDataSetChanged();

        attachWallet = v.findViewById(R.id.btn_attach_wallet);
        attachWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MainActivity().attachViewPager(new GenerationFragment());
            }
        });
        mRealm = Realm.getDefaultInstance();
        getWalletInfo();
        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));

        try {
         //   readyForRequest();
           // getWalletBallance();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "Exception : " + e);
        }

        return v;
    }

    private void getWalletInfo(){
        /*Bundle extras = getIntent().getExtras();
        password = extras.getString("password");
        walletAddress = extras.getString("WalletAddress");
        detailPath = extras.getString("detailPath");*/
        WalletModel walletModel = getObject();
        if (walletModel!=null) {
            password = walletModel.getPassword();
            walletAddress = walletModel.getWalletAddress();
            detailPath = walletModel.getDetailPath();
            mWalletAddress.setText(walletAddress);
        }
    }

    private WalletModel getObject(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        Log.d("TAG", String.valueOf( walletModels.size()));
        if (walletModels.size()>0) return walletModels.get(0);
        return null;
    }

    private void readyForRequest() throws Exception{
        //start sending request
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        Log.d("TAG", "Connected to Ethereum client version: " + clientVersion);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        credentials = WalletUtils.loadCredentials(password, path+"/"+detailPath);
        Log.d("TAG","Credentials loaded; wallet address :  "+credentials.getAddress());
        mWalletAddress.setText(walletAddress);
    }

    private void sendToWallet() throws Exception{
        Log.d("TAG","Sending 1 Wei ("
                + Convert.fromWei("1", Convert.Unit.ETHER).toPlainString() + " Ether)");

        Future<TransactionReceipt> transferReceipt = Transfer.sendFunds(
                web3j, credentials,
                "0xb3FA37CA8918432cAC914C40f8C4748a6dBd0fA4",  // you can put any address here
                BigDecimal.ONE, Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
                .sendAsync();
        Log.d("TAG","Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                //+ transferReceipt.getTransactionHash());
                + transferReceipt.get());
    }

    @SuppressLint("StaticFieldLeak")
    private void getWalletBallance() {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BigInteger wei = ethGetBalance;
                balance = wei.toString();
                mBalance.setText(balance);
                Log.d("TAG", "The balance of my Wallet: "+wei.toString());
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void generateNewContract(){
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
    }
}

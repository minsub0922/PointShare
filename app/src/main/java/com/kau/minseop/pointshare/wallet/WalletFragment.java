package com.kau.minseop.pointshare.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.WalletRecyclerViewAdapter;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.event.ActivityResultEvent;
import com.kau.minseop.pointshare.generation.GenerationActivity;
import com.kau.minseop.pointshare.contract.Greeter;
import com.kau.minseop.pointshare.helper.RecyclerItemTouchHelper;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.model.WalletViewHolerModel;
import com.squareup.otto.Subscribe;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by eirlis on 29.06.17.
 */

public class WalletFragment extends BaseFragment implements View.OnClickListener , RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String TAG = WalletFragment.class.getName();

    private CoordinatorLayout coordinatorLayout;
    private String balance;
    private String password;
    private String walletAddress;
    private String detailPath;
    private Web3j web3j;
    private List<Credentials> credentials = new ArrayList<>();
    private Realm mRealm;
    private String contractAddress = "0xa22FC7f4129CF00f2661f53c5753a826b2092CF5";
    private Button btn_attachWallet, btn_attachContract, btn_getContract;
    private RecyclerView rv;
    private WalletRecyclerViewAdapter adapter;
    private final List<WalletViewHolerModel> modelList = new ArrayList<>();
    private boolean isFirst = true;

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/wd7279F18YpzuVLkfZTk"));

        buildButtons(v);

        buildRecyclerView(v);

        coordinatorLayout = v.findViewById(R.id.coordinatorlayout);

        mRealm = Realm.getDefaultInstance();

        getClientVersion();

        getObject();

        return v;
    }

    private void buildButtons(View v){
        btn_attachWallet = v.findViewById(R.id.btn_attach_wallet);
        btn_attachContract = v.findViewById(R.id.btn_attach_contract);
        btn_getContract = v.findViewById(R.id.btn_get_contract);
        btn_attachWallet.setOnClickListener(this);
        btn_attachContract.setOnClickListener(this);
        btn_getContract.setOnClickListener(this);
    }

    private void getClientVersion(){
        Web3ClientVersion web3ClientVersion = null;
        String clientVersion = null;
        try {
            web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
            clientVersion = web3ClientVersion.getWeb3ClientVersion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "Connected to Ethereum client version: " + clientVersion);
    }

    private void buildRecyclerView(View v){
        rv = v.findViewById(R.id.rv_fragment_wallet);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WalletRecyclerViewAdapter(modelList);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);

        adapter.setOnItemClickListener(new WalletRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                WalletViewHolerModel model =  modelList.get(position);
                Log.d("TAG",model.getWalletName());
            }
        });
    }

    private void getObject(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        for (int i =0; i<walletModels.size(); i++){
            modelList.add(new WalletViewHolerModel("",walletModels.get(i).getWalletName(), walletModels.get(i).getWalletAddress(), ""));
            readyForRequest(walletModels.get(i).getPassword(), walletModels.get(i).getDetailPath());
            getWalletBallance(i);
            Log.d("TAG", String.valueOf(walletModels.get(i)));
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("StaticFieldLeak")
    private void readyForRequest(String pwd, String detailpath){
        //start sending request
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    credentials.add(WalletUtils.loadCredentials(pwd, path+"/"+detailpath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getWalletBallance(int position) {
        //GetMyBalance
        new AsyncTask () {
            @Override
            protected Object doInBackground(Object[] objects) {
                BigInteger ethGetBalance = null;
                try {
                    ethGetBalance = web3j
                            .ethGetBalance(modelList.get(position).getWalletAddress(), DefaultBlockParameterName.LATEST)
                            .send()
                            .getBalance();
                    BigInteger wei = ethGetBalance;
                    balance = wei.toString();
                    modelList.get(position).setWalletBalance(balance);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (position==modelList.size()-1) adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent) {
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        Log.d("TAG",String.valueOf( activityResultEvent.getResultCode()));
        if (activityResultEvent.getResultCode()==-1){
            mRealm.beginTransaction();
            RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
            mRealm.commitTransaction();
            WalletModel model = walletModels.get(walletModels.size()-1);
            modelList.add(new WalletViewHolerModel("",model.getWalletName(), model.getWalletAddress(), "0"));
            adapter.notifyDataSetChanged();
            readyForRequest(model.getPassword(),model.getDetailPath());
        }
    }



    private void getMyContract(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                Coupondeal contract = Coupondeal.load(contractAddress, web3j, credentials.get(0),ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);

                try {
                    Log.d("TAG","asd:  "+ String.valueOf(contract.getCouponList(BigInteger.valueOf(0)).send()));
                    Log.d("TAG","asd:  "+ String.valueOf(contract.getCouponList(BigInteger.valueOf(1)).send()));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", String.valueOf(e));
                }
                return null;
            }
        }.execute();
    }

    private void generateNewContract(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d("TAG", "Deploying smart contract");
                Coupondeal contract = null;
                try {
                    Log.d("TAG", String.valueOf(credentials.size()));
                    contract = Coupondeal.deploy(
                            web3j, credentials.get(0),
                            ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();
                    contractAddress = contract.getContractAddress();
                    Log.d("TAG","Smart contract deployed to address " + contractAddress);
                    Log.d("TAG","View contract at https://ropsten.etherscan.io/address/" + contractAddress);
                    Log.d("TAG","Value stored in remote smart contract1: " + contract.createCoupon("asd1","as,d","asd","asd","").send());
                    Log.d("TAG","Value stored in remote smart contract2: " + contract.createCoupon("asd2","as,d","asd","asd","").send());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", String.valueOf(e));
                }
                return null;
            }
        }.execute();
    }

    private void sendToWallet() throws Exception{
        Log.d("TAG","Sending 1 Wei ("
                + Convert.fromWei("1", Convert.Unit.ETHER).toPlainString() + " Ether)");

        Future<TransactionReceipt> transferReceipt = Transfer.sendFunds(
                web3j, credentials.get(0),
                "0xb3FA37CA8918432cAC914C40f8C4748a6dBd0fA4",  // you can put any address here
                BigDecimal.ONE, Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
                .sendAsync();
        Log.d("TAG","Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
                //+ transferReceipt.getTransactionHash());
                + transferReceipt.get());
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_attach_wallet) startActivityForResult(new Intent(getActivity(), GenerationActivity.class),1);
       // else if (v.getId()==R.id.btn_attach_contract) generateNewContract();
        else if (v.getId()==R.id.btn_get_contract) getMyContract();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof WalletRecyclerViewAdapter.WalletViewHoler) {
            // get the removed item name to display it in snack bar
            String name = modelList.get(viewHolder.getAdapterPosition()).getWalletName();

            // backup of removed item for undo purpose
            final WalletViewHolerModel deletedItem = modelList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "your wallet '"+name + "' has been removed from your Wallet!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}

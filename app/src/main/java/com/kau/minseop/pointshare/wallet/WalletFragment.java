package com.kau.minseop.pointshare.wallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.Contract;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.WalletRecyclerViewAdapter;
import com.kau.minseop.pointshare.contract.Coupondeal;
import com.kau.minseop.pointshare.event.ActivityResultEvent;
import com.kau.minseop.pointshare.generation.GenerationActivity;
import com.kau.minseop.pointshare.helper.RecyclerItemTouchHelper;
import com.kau.minseop.pointshare.loading.LoadingFragment;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.model.WalletViewHolerModel;
import com.kau.minseop.pointshare.mypage.MyPageFragment;
import com.squareup.otto.Subscribe;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthLog;
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

public class WalletFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

// RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, {

    public static final String TAG = WalletFragment.class.getName();

    private CoordinatorLayout coordinatorLayout;
    private List<Credentials> credentials = new ArrayList<>();
    private Realm mRealm;
    private Button btn_attachWallet, btn_attachContract, btn_sendether,btn_createCoupon, btnBeg;
    private TextView txtOrdered;
    private RecyclerView rv;
    private WalletRecyclerViewAdapter adapter;
    private final List<WalletViewHolerModel> modelList = new ArrayList<>();
    private boolean isDeleted = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean refreshing=false;
    private DatabaseReference mDatabase;
    private boolean isAsked = false;


    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_wallet, container, false);

        web3j = Web3jFactory.build(new HttpService(testnetAddess));

        count++;

        buildButtons(v);

        buildRecyclerView(v);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        coordinatorLayout = v.findViewById(R.id.coordinatorlayout);

        mRealm = Realm.getDefaultInstance();

        //getClientVersion();
        if (count<=1) getObject();

        return v;
    }

    private void buildButtons(View v){
  //      btn_attachWallet = v.findViewById(R.id.btn_attach_wallet);
        //btn_attachContract = v.findViewById(R.id.btn_attach_contract);
        //btn_sendether = v.findViewById(R.id.btn_send_ether_other)
        txtOrdered = v.findViewById(R.id.txt_ordered);
        txtOrdered.setOnClickListener(this);
        btnBeg = v.findViewById(R.id.btn_beg);
        btnBeg.setOnClickListener(this);
       // btn_createCoupon = v.findViewById(R.id.btn_create_coupon);
  //      btn_attachWallet.setOnClickListener(this);
  //      btn_sendether.setOnClickListener(this);
        //btn_createCoupon.setOnClickListener(this);
        ( (MainActivity)getActivity()).updateToolbarTitle("");

        swipeRefreshLayout = v.findViewById(R.id.shop_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void buildRecyclerView(View v){
        rv = v.findViewById(R.id.rv_fragment_wallet);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WalletRecyclerViewAdapter(modelList);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(adapter);

//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);

//        adapter.setOnItemClickListener(new WalletRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                WalletViewHolerModel model =  modelList.get(position);
//                setSeletWalletAdapter(model);
//            }
//        });
    }
//
//    private void setSeletWalletAdapter(WalletViewHolerModel model){
//        AlertDialog.Builder alertBuilder =new AlertDialog.Builder(getActivity());
//        alertBuilder.setTitle("보내실 지갑을 선택하십시오.");
//        int index =0;
//
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
//        for (int i=0; i<modelList.size(); i++){
//            if (modelList.get(i).getWalletAddress()==model.getWalletAddress()) {
//                index = i;
//                continue;
//            }
//            adapter.add(modelList.get(i).getWalletAddress());
//        }
//
//        alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        int finalIndex = index;
//        alertBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int position) {
//                String strName = adapter.getItem(position);
//                final EditText et = new EditText(getActivity());
//                AlertDialog.Builder innBuilder = new AlertDialog.Builder(getActivity());
//                innBuilder.setMessage(strName);
//                innBuilder.setTitle("송금할 금액을 입력하십시오.");
//                innBuilder.setView(et);
//                innBuilder.setPositiveButton("송금", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //startProgresss();
//                        sendEth(finalIndex, strName, et.getText().toString());
//                        Log.d("TAG", strName+"   "+et.getText().toString());
//                        dialog.dismiss();
//                    }
//                });
//                innBuilder.show();
//                dialog.dismiss();
//            }
//        });
//        alertBuilder.show();
//    }

//    private void sendEth(int index, String othersAddress, String price){
//        new AsyncTask(){
//
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                try {
//                    Transfer.sendFunds(
//                            web3j, credentials.get(index),
//                            othersAddress,  // you can put any address here
//                            BigDecimal.valueOf(Double.parseDouble( price)).multiply(BigDecimal.ONE), Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
//                            .send();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                //progressOFF();
//                Log.d("TAG","done sned eth!");
//                for (int i=0; i<modelList.size(); i++) getWalletBallance(i);
//            }
//        }.execute();
//
//    }

    private void getObject(){
        mRealm.beginTransaction();
        RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
        mRealm.commitTransaction();
        for (int i =0; i<walletModels.size(); i++){
            modelList.add(new WalletViewHolerModel("",walletModels.get(i).getWalletName(), walletModels.get(i).getWalletAddress(), ""));
            readyForRequest(walletModels.get(i).getPassword(), walletModels.get(i).getDetailPath());
            getWalletBallance(i);
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
                if (modelList.size()<=position) return null;
                try {
                    ethGetBalance = web3j
                            .ethGetBalance(modelList.get(position).getWalletAddress(), DefaultBlockParameterName.LATEST)
                            .send()
                            .getBalance();
                    modelList.get(position).setWalletBalance(ethGetBalance.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (position==modelList.size()-1) adapter.notifyDataSetChanged();
                finishRefreshing();
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
//        if(v.getId()==R.id.btn_attach_wallet) startActivityForResult(new Intent(getActivity(), GenerationActivity.class),1);
//        //else if (v.getId()==R.id.btn_attach_contract) generateNewContract();
//        else if (v.getId()==R.id.btn_send_ether_other) sendEtherTo();
//        //else if (v.getId()==R.id.btn_get_contract) getMyContract();
//        /*else if(v.getId() == R.id.btn_create_coupon){
//            Intent intent = new Intent(getActivity(),CreateCouponActivity.class);
//            startActivity(intent);
//        }*/else
        if(v.getId() == R.id.txt_ordered){
            listCoupon();
        }else if(v.getId()== R.id.btn_beg && !isAsked){
            isAsked = true;
            mDatabase.child("EtherRequest").push().setValue(modelList.get(0).getWalletAddress());
            Toast.makeText(getActivity(), "개발자에게 시범 토큰 요청이 접수되었습니다! 조금만 기다려 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void listCoupon(){
        MyPageFragment fragment = new MyPageFragment();
        Bundle args = new Bundle();
        args.putString("walletAddress",modelList.get(0).getWalletAddress());
        fragment.setArguments(args);
        mFragmentNavigation.pushFragment(fragment);
    }
//
//    private void sendEtherTo(){
//        final EditText et = new EditText(getActivity());
//        final String[] address = new String[1];
//        AlertDialog.Builder alertBuilder =new AlertDialog.Builder(getActivity());
//        alertBuilder.setTitle("상대방의 지갑주소를 입력해주십시오.");
//        alertBuilder.setView(et);
//
//        alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                address[0] =  et.getText().toString();
//                final EditText et = new EditText(getActivity());
//                AlertDialog.Builder innBuilder = new AlertDialog.Builder(getActivity());
//                innBuilder.setTitle("송금할 금액을 입력하십시오.");
//                innBuilder.setView(et);
//                innBuilder.setPositiveButton("송금", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        startProgresss();
//                        sendEth(0, address[0], et.getText().toString());
//                        Log.d("TAG", address[0]+"   "+et.getText().toString());
//                        dialog.dismiss();
//                    }
//                });
//                innBuilder.show();
//                dialog.dismiss();
//            }
//        }). setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        alertBuilder.show();
//    }

//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
//        if (viewHolder instanceof WalletRecyclerViewAdapter.WalletViewHoler) {
//            isDeleted =true;
//            String name = modelList.get(viewHolder.getAdapterPosition()).getWalletName();
//
//            final WalletViewHolerModel deletedItem = modelList.get(viewHolder.getAdapterPosition());
//            final int deletedIndex = viewHolder.getAdapterPosition();
//
//            adapter.removeItem(viewHolder.getAdapterPosition());
//
//            Snackbar snackbar = Snackbar
//                    .make(coordinatorLayout, "your wallet '"+name + "' has been removed from your Wallet!", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // undo is selected, restore the deleted item
//                  /*  adapter.restoreItem(deletedItem, deletedIndex);*/
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();
//        }
//    }

    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent) {
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        if (activityResultEvent.getResultCode()==-1){
            if (isDeleted){
                isDeleted = false;
                modelList.clear();
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);
                getObject();
            }else {
                mRealm.beginTransaction();
                RealmResults<WalletModel> walletModels = mRealm.where(WalletModel.class).findAll();
                mRealm.commitTransaction();
                WalletModel model = walletModels.get(walletModels.size() - 1);
                modelList.add(new WalletViewHolerModel("", model.getWalletName(), model.getWalletAddress(), "0"));
                adapter.notifyDataSetChanged();
                readyForRequest(model.getPassword(), model.getDetailPath());
            }
        }
    }

    @Override
    public void onRefresh() {
        getWalletBallance(0);
    }

    private void finishRefreshing(){
        swipeRefreshLayout.setRefreshing(false);
        refreshing=false;
    }

    /*private void generateNewContract(){
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.d("TAG", "Deploying smart contract");
                Coupondeal contract = null;
                try {
                    contract = Coupondeal.deploy(
                            web3j, credentials.get(0),
                            ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT).send();
                    contractAddress = contract.getContractAddress();
                    Log.d("TAG","Smart contract deployed to address " + contractAddress);
                    Log.d("TAG","View contract at https://ropsten.etherscan.io/address/" + contractAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", String.valueOf(e));
                }
                return null;
            }
        }.execute();
    }*/
}

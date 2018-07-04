package com.kau.minseop.pointshare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.kau.minseop.pointshare.wallet.BusProvider;

import org.web3j.protocol.Web3j;

/**
 * Created by minseop on 2018-06-08.
 */

public class BaseFragment extends Fragment {

    public int count=0;
    public final String KEY = "199301130922";
    public final String contractAddress = "0xb9e35DC2Ce838b4E7A1cDf722d0363bba61E0bf7";
    public Web3j web3j;
    public String testnetAddess = "https://ropsten.infura.io/wd7279F18YpzuVLkfZTk";



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    public static final String ARGS_INSTANCE = "com.kau.minseop.pointshare";


    public FragmentNavigation mFragmentNavigation;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) {
            mFragmentNavigation = (FragmentNavigation) context;
        }
    }

    public interface FragmentNavigation {
        void pushFragment(Fragment fragment);
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        super.onDestroyView();

    }
}

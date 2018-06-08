package com.kau.minseop.pointshare.card;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.wallet.WalletFragment;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import io.realm.Realm;

/**
 * Created by minseop on 2018-06-08.
 */

public class CardListFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_cardlist, container, false);
        return v;
    }
}

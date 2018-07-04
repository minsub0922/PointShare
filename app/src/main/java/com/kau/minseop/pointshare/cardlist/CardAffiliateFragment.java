package com.kau.minseop.pointshare.cardlist;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.R;


public class CardAffiliateFragment extends BaseFragment {
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_card_affiliate, container, false);

        return v;
    }



}

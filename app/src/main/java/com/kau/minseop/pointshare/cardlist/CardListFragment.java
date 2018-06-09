package com.kau.minseop.pointshare.cardlist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.R;

/**
 * Created by minseop on 2018-06-08.
 */

public class CardListFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_cardlist, container, false);
        return v;
    }
}

package com.kau.minseop.pointshare.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.R;

/**
 * Created by minseop on 2018-06-09.
 */

public class ShopFragment extends BaseFragment {

    private RecyclerView rv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shop, container, false);

        buildRecyclerView(v);



        return v;
    }

    private void buildRecyclerView(View v){
        rv = v.findViewById(R.id.rv_fragment_shop_coffee);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setNestedScrollingEnabled(false);
        //rv.setAdapter(adapter);

    }
}

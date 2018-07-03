package com.kau.minseop.pointshare.cardlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.CardlistRecyclerViewAdapter;
import com.kau.minseop.pointshare.event.ActivityResultEvent;
import com.kau.minseop.pointshare.helper.CardListDBHelper;
import com.kau.minseop.pointshare.model.CardListModel;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/*
 * Created by minseop on 2018-06-08.
 */

public class CardListFragment extends BaseFragment {
    private CardListModel cards;

    private CardlistRecyclerViewAdapter adapter;
    private ArrayList<CardListModel> mItems = new ArrayList<>();

    RecyclerView rv;
    Button addCard;
    CardListDBHelper dbHelper;

    public static CardListFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        CardListFragment fragment = new CardListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CardListFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cardlist, container, false);
        dbHelper= new CardListDBHelper(this.getActivity(), "CardList.db", null, 1);
        adapter = new CardlistRecyclerViewAdapter(mItems);
        rv = v.findViewById(R.id.recyclerView);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()) );
        rv.setNestedScrollingEnabled(false);

        ( (MainActivity)getActivity()).updateToolbarTitle("CARD LIST");

        setData();
        addCard = (Button)v.findViewById(R.id.addcard);
        adapter.setItemClick(new CardlistRecyclerViewAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                CardListViewFragment fragment = new CardListViewFragment();
                Bundle args = new Bundle();
                args.putString("cardnum",mItems.get(position).getCardNum());
                args.putString("cardPassward",mItems.get(position).getCardPassward());
                args.putString("cardPeriod",mItems.get(position).getCardValidityPeriod());
                args.putString("cardtype",mItems.get(position).getCardType());
                fragment.setArguments(args);
                mFragmentNavigation.pushFragment(fragment);
            }
        });
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddCardListActivity.class);
                startActivityForResult(intent,1);
            }
        });

        return v;
        //
    }

    private void setData() {
        mItems.clear();
        String[] cardDB = dbHelper.getResult().split("%");
        if(dbHelper.getResult().length() == 0){
            ;
        }
        else{
            for(int i=0; i<cardDB.length;i++){
                String[] cardDBCol = cardDB[i].split("@");
                cards = new CardListModel(cardDBCol[0],cardDBCol[1],cardDBCol[2],cardDBCol[3],cardDBCol[4]);
                mItems.add(cards);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent) {
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        if (activityResultEvent.getResultCode()==-1){
            setData();
        }
    }
}

package com.kau.minseop.pointshare.cardlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.CardlistRecyclerViewAdapter;
import com.kau.minseop.pointshare.event.ActivityResultEvent;
import com.kau.minseop.pointshare.helper.CardListDBHelper;
import com.kau.minseop.pointshare.model.CardListModel;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/*
 * Created by minseop on 2018-06-08.
 */

public class CardListFragment extends BaseFragment {
    private CardListModel cards;

    private CardlistRecyclerViewAdapter adapter;
    private ArrayList<CardListModel> mItems = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference cardRef = database.getReference("CardCoupon");
    RecyclerView rv;
    Button addCard;
    CardListDBHelper dbHelper;
    String imgurl;

    public CardListFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        count++;

        View v = inflater.inflate(R.layout.fragment_cardlist, container, false);
        dbHelper= new CardListDBHelper(this.getActivity(), "CardList.db", null, 1);
        adapter = new CardlistRecyclerViewAdapter(mItems,getActivity());
        rv = v.findViewById(R.id.recyclerView);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()) );
        rv.setNestedScrollingEnabled(false);

        ( (MainActivity)getActivity()).updateToolbarTitle("");

        if (count<=1){
            setData();
        }
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

    @Override
    public void onStart() {
        super.onStart();
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

                cardRef.child(cardDBCol[0].toLowerCase()).child("imgurl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        imgurl = dataSnapshot.getValue().toString();
                        cards = new CardListModel(cardDBCol[0],cardDBCol[1],cardDBCol[2],cardDBCol[3],cardDBCol[4],imgurl);
                        mItems.add(cards);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("TAG", "Failed to read value.", databaseError.toException());
                    }
                });

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

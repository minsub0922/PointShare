package com.kau.minseop.pointshare.cardlist;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.adapter.AffiliateGridViewAdapter;
import com.kau.minseop.pointshare.adapter.CardlistRecyclerViewAdapter;

import java.util.ArrayList;


public class CardAffiliateFragment extends BaseFragment {
    private String qrCode;
    private TextView cardNum;
    private ImageView Im_qrCode;
    private ArrayList affName;
    private ArrayList affImg;
    private GridView gridView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference cardRef = database.getReference("CardCoupon");
    DatabaseReference couponRef = database.getReference("coupons");
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_card_affiliate, container, false);
        cardNum = (TextView)v.findViewById(R.id.cardnum);
        Im_qrCode = (ImageView)v.findViewById(R.id.qrCode);
        affName = new ArrayList();
        affImg = new ArrayList();
        AffiliateGridViewAdapter gridAdapters = new AffiliateGridViewAdapter(affName,affImg,getActivity());
        gridView = (GridView) v.findViewById(R.id.gridViews);
        gridView.setAdapter(gridAdapters);
        String cType = getArguments().getString("cardtype");
        String cNum = getArguments().getString("cardnum");
        String cPassward = getArguments().getString("cardPassward");
        String cPeriod = getArguments().getString("cardPeriod");
        try {
            qrCode = cNum + cPassward;//= encrypt( cNum+cPassward, KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            //BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE,250,250);
            BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.CODE_128,400,170);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Im_qrCode.setImageBitmap(bitmap);
        }catch (WriterException e) {
            e.printStackTrace();
        }
        cardNum.setText(cNum);

        cardRef.child(cType.toLowerCase()).child("affiliates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                affName.clear();
                affImg.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    couponRef.child(snapshot.getValue().toString()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            affName.add(dataSnapshot.getValue().toString());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    couponRef.child(snapshot.getValue().toString()).child("imgurl").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            affImg.add(dataSnapshot.getValue().toString());
                            gridAdapters.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        gridAdapters.setItemClick(new AffiliateGridViewAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                CardListViewFragment fragment = new CardListViewFragment();
                Bundle args = new Bundle();
                args.putString("cardCompany",affName.get(position).toString());
                fragment.setArguments(args);
                mFragmentNavigation.pushFragment(fragment);
            }
        });

        return v;



    }

}




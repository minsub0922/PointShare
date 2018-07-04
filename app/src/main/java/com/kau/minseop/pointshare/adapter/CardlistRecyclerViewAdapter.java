package com.kau.minseop.pointshare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.model.CardListModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by khanj on 2018-06-09.
 */

public class CardlistRecyclerViewAdapter extends RecyclerView.Adapter<CardlistRecyclerViewAdapter.ItemViewHolder> {
    ArrayList<CardListModel> mItems;
    //아이템 클릭시 실행 함수
    private ItemClick itemClick;
    private ImageView imgViews;
    Bitmap bitmap;
    private Context context;
    public interface ItemClick {
        public void onClick(View view,int position);
    }


    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
    public CardlistRecyclerViewAdapter(ArrayList<CardListModel> items, Context context){
        mItems = items;
        this.context =context;

    }


    // 새로운 뷰 홀더 생성
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_card_viewitem,parent,false);
        return new ItemViewHolder(view);
    }


    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        imgViews = (ImageView)holder.itemView.findViewById(R.id.cardtypeImage);
        holder.cardType.setText(mItems.get(position).getCardType());

        if(mItems.get(position).getCardType().contains("CJONE")){holder.itemView.setBackgroundResource(R.drawable.cjone);}
        else  if(mItems.get(position).getCardType().contains("HappyPoint")){holder.itemView.setBackgroundResource(R.drawable.happypoint);}
        else  if(mItems.get(position).getCardType().contains("CU")){holder.itemView.setBackgroundResource(R.drawable.cu);}
        else  if(mItems.get(position).getCardType().contains("신세계")){holder.itemView.setBackgroundResource(R.drawable.emart);}
        else  if(mItems.get(position).getCardType().contains("KT")){holder.itemView.setBackgroundResource(R.drawable.kt);}
        else  if(mItems.get(position).getCardType().contains("bithumb")){holder.itemView.setBackgroundResource(R.drawable.bithumb);}
        else  if(mItems.get(position).getCardType().contains("GS")){holder.itemView.setBackgroundResource(R.drawable.gs);}
        Glide.with(context).load(mItems.get(position).getImgurl()).into(imgViews);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v, position);
                }
            }
        });
    }

    // 데이터 셋의 크기를 리턴해줍니다.
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // 커스텀 뷰홀더
// item layout 에 존재하는 위젯들을 바인딩합니다.
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView cardType;
        public ItemViewHolder(View itemView) {
            super(itemView);
            cardType = (TextView) itemView.findViewById(R.id.itemCtype);
        }
    }
}

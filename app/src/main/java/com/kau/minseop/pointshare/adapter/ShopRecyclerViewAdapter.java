package com.kau.minseop.pointshare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.model.ShoppingModel;
import com.kau.minseop.pointshare.model.WalletModel;
import com.kau.minseop.pointshare.model.WalletViewHolerModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minseop on 2018-06-08.
 */

public class ShopRecyclerViewAdapter extends  RecyclerView.Adapter<ShopRecyclerViewAdapter.ShopViewHoler>{

    private OnItemClickListener mListener;
    private final List<ShoppingModel> modelList;
    private Context context;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public ShopRecyclerViewAdapter(List<ShoppingModel> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShopViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.model_shop_viewholder, parent, false);
        return new ShopViewHoler(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHoler holder, int position) {
        final ShoppingModel model = modelList.get(position);

        if (model.getCouponModel().getCompany().contains("스타")){
            Glide.with(context) .load(R.drawable.starbucks).into(holder.img);
        }else if (model.getCouponModel().getCompany().contains("GS")){
            Glide.with(context) .load(R.drawable.gs25).into(holder.img);
        }else if (model.getCouponModel().getCompany().contains("그린")){
            Glide.with(context) .load(R.drawable.greencar).into(holder.img);
        }else if (model.getCouponModel().getCompany().contains("SOCAR")){
            Glide.with(context) .load(R.drawable.socar).into(holder.img);
        }else if (model.getCouponModel().getCompany().contains("모두")){
            Glide.with(context) .load(R.drawable.modutour).into(holder.img);
        }

        holder.txt_couponName.setText(model.getCouponModel().getcName());
        holder.txt_couponPrice.setText(model.getCouponModel().getPrice());
        holder.txt_couponDeadline.setText(model.getCouponModel().getDeadline());
    }

    @Override
    public int getItemCount() {return modelList.size();}

    public static class ShopViewHoler extends RecyclerView.ViewHolder{

        private ImageView img;
        private TextView txt_couponName, txt_couponPrice, txt_couponDeadline;

        public ShopViewHoler(View itemView, OnItemClickListener listener) {
            super(itemView);

            img = itemView.findViewById(R.id.img_model_shop_viewholder);
            txt_couponName = itemView.findViewById(R.id.txt_model_shop_cname);
            txt_couponPrice = itemView.findViewById(R.id.txt_model_shop_price);
            txt_couponDeadline= itemView.findViewById(R.id.txt_model_shop_deadline);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}


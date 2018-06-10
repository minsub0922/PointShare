package com.kau.minseop.pointshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.model.CardListModel;
import com.kau.minseop.pointshare.model.CouponModel;

import java.util.ArrayList;

/**
 * Created by khanj on 2018-06-10.
 */

public class CouponRecyclerViewAdapter extends RecyclerView.Adapter<CouponRecyclerViewAdapter.ItemViewHolder> {

    ArrayList<CouponModel> mItems;
    //아이템 클릭시 실행 함수
    private CouponRecyclerViewAdapter.ItemClick itemClick;
    public interface ItemClick {
        public void onClick(View view, int position);
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(CouponRecyclerViewAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
    public CouponRecyclerViewAdapter(ArrayList<CouponModel> items){
        mItems = items;
    }


    // 새로운 뷰 홀더 생성
    @Override
    public CouponRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_coupon_viewitem,parent,false);
        return new CouponRecyclerViewAdapter.ItemViewHolder(view);
    }


    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    @Override
    public void onBindViewHolder(CouponRecyclerViewAdapter.ItemViewHolder holder, int position) {
        holder.cCompany.setText(mItems.get(position).getCompany());
        holder.cName.setText(mItems.get(position).getcName()+" - "+mItems.get(position).getPrice()+"원" );
        holder.cPeriod.setText(mItems.get(position).getDeadline()+"까지");
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
        private TextView cName;
        private TextView cCompany;
        private TextView cPeriod;
        public ItemViewHolder(View itemView) {
            super(itemView);
            cName = (TextView) itemView.findViewById(R.id.itemCname);
            cCompany =(TextView)itemView.findViewById(R.id.itemCompany);
            cPeriod = (TextView)itemView.findViewById(R.id.itemPeriod);
        }
    }
}
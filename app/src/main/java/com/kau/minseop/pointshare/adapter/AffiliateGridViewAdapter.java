package com.kau.minseop.pointshare.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kau.minseop.pointshare.R;

import java.util.ArrayList;

/**
 * Created by khanj on 2018-07-05.
 */

public class AffiliateGridViewAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList affname;
    ArrayList affimg;
    Context context;
    public AffiliateGridViewAdapter(ArrayList affname, ArrayList affimg,Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.affname = affname;
        this.affimg = affimg;
        this.context=context;
        Log.d("TAG","시발");

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return affimg.size();    //그리드뷰에 출력할 목록 수
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return affimg.get(position);    //아이템을 호출할 때 사용하는 메소드
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;    //아이템의 아이디를 구할 때 사용하는 메소드
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.model_affiliate_viewitem, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.custom_list_image);
        TextView textView = (TextView) convertView.findViewById(R.id.affiliatename);
        Glide.with(context).load(affimg.get(position)).into(imageView);

        textView.setText(affname.get(position).toString());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 이미지를 터치했을때 동작하는 곳
            }
        });
        return convertView;
    }
}
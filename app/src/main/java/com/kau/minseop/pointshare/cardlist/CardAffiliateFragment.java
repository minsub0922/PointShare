package com.kau.minseop.pointshare.cardlist;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kau.minseop.pointshare.BaseFragment;
import com.kau.minseop.pointshare.R;


public class CardAffiliateFragment extends BaseFragment {
    private String qrCode;
    private TextView cardNum;
    private ImageView Im_qrCode;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference couponRef = database.getReference("coupons");
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_card_affiliate, container, false);
        cardNum = (TextView)v.findViewById(R.id.cardnum);
        Im_qrCode = (ImageView)v.findViewById(R.id.qrCode);

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



        return v;
    }

    class GridViewAdapter extends BaseAdapter {
        Context context;
        int layout;
        int img[];
        LayoutInflater inf;

        public GridViewAdapter(Context context, int layout, int[] img) {
            this.context = context;
            this.layout = layout;
            this.img = img;
            inf = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return img.length;
        }

        @Override
        public Object getItem(int position) {
            return img[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null)
                convertView = inf.inflate(layout, null);
            //ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
            //iv.setImageResource(img[position]);

            return convertView;
        }
    }
}

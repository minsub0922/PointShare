package com.kau.minseop.pointshare.cardlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kau.minseop.pointshare.R;

public class CardListViewActivity extends AppCompatActivity {

    TextView cardType;
    TextView cardNum;
    ImageView Im_qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list_view);
        cardType = (TextView)findViewById(R.id.cardtype);
        cardNum = (TextView)findViewById(R.id.cardnum);

        Intent intent = getIntent();
        String cType = intent.getExtras().getString("cardtype");
        String cNum = intent.getExtras().getString("cardnum");
        String cPassward = intent.getExtras().getString("cardPassward");
        String qrCode = cNum+cPassward;

        Bitmap bitmap=getIntent().getParcelableExtra("pic");
        cardType.setText(cType);
        cardNum.setText(cNum);
    }
}

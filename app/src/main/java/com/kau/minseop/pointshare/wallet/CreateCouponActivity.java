package com.kau.minseop.pointshare.wallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kau.minseop.pointshare.R;

public class CreateCouponActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference couponRef = database.getReference("coupons");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coupon);

        EditText cName = (EditText)findViewById(R.id.etcName);
        EditText company = (EditText)findViewById(R.id.etcompany);
        EditText deadline = (EditText)findViewById(R.id.etdeadline);
        EditText price = (EditText)findViewById(R.id.etprice);

        Button btn_click = (Button)findViewById(R.id.btn_click);


        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponRef.child(company.getText().toString()).child(cName.getText().toString()).child("cName").setValue(cName.getText().toString());
                couponRef.child(company.getText().toString()).child(cName.getText().toString()).child("company").setValue(company.getText().toString());
                couponRef.child(company.getText().toString()).child(cName.getText().toString()).child("deadline").setValue(deadline.getText().toString());
                couponRef.child(company.getText().toString()).child(cName.getText().toString()).child("price").setValue(price.getText().toString());
            }
        });
    }
}

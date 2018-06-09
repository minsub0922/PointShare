package com.kau.minseop.pointshare.cardlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.helper.CardListDBHelper;

import java.nio.BufferUnderflowException;

public class AddCardListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_list);
        final CardListDBHelper dbHelper = new CardListDBHelper(getApplicationContext(), "CardList.db", null, 1);
        final EditText etCardtype = (EditText)findViewById(R.id.cardtype);
        final EditText etCardNum = (EditText)findViewById(R.id.cardnum);
        final EditText etCardPeriod = (EditText)findViewById(R.id.cardperiod);
        final EditText etCardCVC = (EditText)findViewById(R.id.cardcvc);
        final EditText etCardPassward = (EditText)findViewById(R.id.cardpassward);
        final TextView result = (TextView) findViewById(R.id.result);


        Button insert = (Button)findViewById(R.id.insert);
        Button delete = (Button)findViewById(R.id.delete);
        result.setText(dbHelper.getResult());
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardType = etCardtype.getText().toString();
                String cardNum = etCardNum.getText().toString();
                String cardPeriod = etCardPeriod.getText().toString();
                String cardCVC = etCardCVC.getText().toString();
                String cardPassward = etCardPassward.getText().toString();

                dbHelper.insert(cardType,cardNum,cardPeriod,cardCVC,cardPassward);
                result.setText(dbHelper.getResult());

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardType = etCardtype.getText().toString();
                dbHelper.delete(cardType);
                result.setText(dbHelper.getResult());
            }
        });

    }
}

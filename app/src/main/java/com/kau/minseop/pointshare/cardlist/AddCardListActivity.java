package com.kau.minseop.pointshare.cardlist;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.helper.CardListDBHelper;

import java.nio.BufferUnderflowException;

public class AddCardListActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton rb1, rb2, rb3;
    private String cardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_list);
        final CardListDBHelper dbHelper = new CardListDBHelper(getApplicationContext(), "CardList.db", null, 1);
//        final EditText etCardtype = (EditText)findViewById(R.id.cardtype);
        final EditText etCardNum = (EditText)findViewById(R.id.cardnum);
        final EditText etCardPeriod = (EditText)findViewById(R.id.cardperiod);
        final EditText etCardCVC = (EditText)findViewById(R.id.cardcvc);
        final EditText etCardPassward = (EditText)findViewById(R.id.cardpassward);
        final TextView result = (TextView) findViewById(R.id.result);

        buildRadioButtons();

        Button insert = (Button)findViewById(R.id.insert);
        Button delete = (Button)findViewById(R.id.delete);
        result.setText(dbHelper.getResult());
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String cardType = etCardtype.getText().toString();
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
           //     String cardType = etCardtype.getText().toString();
                dbHelper.delete(cardType);
                result.setText(dbHelper.getResult());
            }
        });
    }

    private void buildRadioButtons(){
        radioGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.btn_radio1);
        rb2 = findViewById(R.id.btn_radio2);
        rb3 = findViewById(R.id.btn_radio2);

        /*rb1.setOnClickListener(radioButtonClickListener);
        rb2.setOnClickListener(radioButtonClickListener);
        rb3.setOnClickListener(radioButtonClickListener);*/
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
    }

    /*RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener(){
        @Override
        public void onClick(View view) {

            Toast.makeText(MainActivity.this, "r_btn1 : "+r_btn1.isChecked() + "r_btn2 : " +r_btn2.isChecked() , Toast.LENGTH_SHORT).show();
        }
    };*/

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
         if(i == R.id.btn_radio1)   cardType = "HappyPoint";
         else if(i == R.id.btn_radio2)  cardType = "CU";
         else if (i == R.id.btn_radio3) cardType ="CJONE";
        Log.d("TAG",cardType);
      }
  };
}

package com.kau.minseop.pointshare.cardlist;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kau.minseop.pointshare.MainActivity;
import com.kau.minseop.pointshare.R;
import com.kau.minseop.pointshare.helper.CardListDBHelper;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

public class AddCardListActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton rb1, rb2, rb3;
    private String cardType;
    private Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference clistRef = database.getReference("carlist");
    private List<String> spinnerlist;
    private String [] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_list);
        final CardListDBHelper dbHelper = new CardListDBHelper(getApplicationContext(), "CardList.db", null, 1);
        //final EditText etCardtype = (EditText)findViewById(R.id.cardtype);
        final EditText etCardNum = (EditText)findViewById(R.id.cardnum);
        final EditText etCardPeriod = (EditText)findViewById(R.id.cardperiod);
        final EditText etCardCVC = (EditText)findViewById(R.id.cardcvc);
        final EditText etCardPassward = (EditText)findViewById(R.id.cardpassward);
        final TextView result = (TextView) findViewById(R.id.result);

        spinnerlist = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,spinnerlist);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cardType = spinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        clistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                  spinnerlist.add(snapshot.getValue().toString());
                }
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        etCardNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    // auto insert dashes while user typing their ssn
                    if (start == 3 || start == 8|| start == 13) {
                    etCardNum.setText(etCardNum.getText() + "-");
                    etCardNum.setSelection(etCardNum.getText().length());
                     }
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCardPeriod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    // auto insert dashes while user typing their ssn
                    if (start == 1) {
                        etCardPeriod.setText(etCardPeriod.getText() + "/");
                        etCardPeriod.setSelection(etCardPeriod.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView insert = findViewById(R.id.txt_insert);
        //result.setText(dbHelper.getResult());
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String cardType = etCardtype.getText().toString();
                if(etCardNum.getText().toString().length() ==0){
                    Toast.makeText(AddCardListActivity.this, "카드번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etCardNum.requestFocus();
                    return;
                }
                else if(etCardPeriod.getText().toString().length() ==0){
                    Toast.makeText(AddCardListActivity.this, "카드 유효기간을 입력하세요!", Toast.LENGTH_SHORT).show();
                    etCardNum.requestFocus();
                    return;
                }
                else if(etCardCVC.getText().toString().length() ==0){
                    Toast.makeText(AddCardListActivity.this, "카드cvc번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etCardNum.requestFocus();
                    return;
                }
                else if(etCardPassward.getText().toString().length() ==0){
                    Toast.makeText(AddCardListActivity.this, "카드비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                    etCardNum.requestFocus();
                    return;
                }
                String cardNum = etCardNum.getText().toString();
                String cardPeriod = etCardPeriod.getText().toString();
                String cardCVC = etCardCVC.getText().toString();
                String cardPassward = etCardPassward.getText().toString();

                dbHelper.insert(cardType,cardNum,cardPeriod,cardCVC,cardPassward);
                result.setText(dbHelper.getResult());
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        findViewById(R.id.img_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}

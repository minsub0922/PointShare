package com.kau.minseop.pointshare.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by khanj on 2018-06-10.
 */

public class CardListDBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public CardListDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        db.execSQL("CREATE TABLE CARDLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, cardType TEXT NOT NULL, cardNum TEXT NOT NULL, cardPeriod TEXT NOT NULL,cardCVC TEXT NOT NULL,cardPassward TEXT NOT NULL);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String cardType, String cardNum,String cardPeriod,String cardCVC,String cardPassward ) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO CARDLIST VALUES(null, '" + cardType + "', '" + cardNum + "', '" +cardPeriod + "', '" + cardCVC + "', '" + cardPassward + "');");
        db.close();
    }

    public void update(String item, int price) {
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("UPDATE CARDLIST SET price=" + price + " WHERE item='" + item + "';");
        db.close();
    }

    public void delete(String cardType) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM CARDLIST WHERE cardType='" + cardType + "';");
        db.close();
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM CARDLIST", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(1)
                    + "@"
                    + cursor.getString(2)
                    + "@"
                    + cursor.getString(3)
                    + "@"
                    + cursor.getString(4)
                    + "@"
                    + cursor.getString(5)
                    + "%" ;
        }
        return result;
    }
}



package com.kau.minseop.pointshare.loading;

/**
 * Created by khanj on 2018-06-11.
 */


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.test.ActivityTestCase;

public class BaseActivity extends AppCompatActivity {


    public void progressON() {
        BaseApplication.getInstance().progressON(this, null);
    }

    public void progressON(Activity activity, String message) {
        BaseApplication.getInstance().progressON(activity, message);
    }

    public void progressOFF() {
        BaseApplication.getInstance().progressOFF();
    }

}
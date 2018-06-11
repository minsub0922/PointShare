package com.kau.minseop.pointshare.handler;

import android.app.Activity;

import com.kau.minseop.pointshare.shop.QRActivity;

/**
 * Created by khanj on 2018-06-11.
 */

public class BackPressHandler {
    private long backKeyPressedTime = 0;

    private Activity activity;

    public BackPressHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed(QRActivity qrActivity) {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.moveTaskToBack(true);
        }
    }

    public void showGuide() {
    }
}


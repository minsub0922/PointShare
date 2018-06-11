package com.kau.minseop.pointshare.handler;

import android.app.Activity;

/**
 * Created by khanj on 2018-06-11.
 */

public class BackPressHandler {
    private long backKeyPressedTime = 0;

    private Activity activity;

    public BackPressHandler(Activity context) {
        this.activity = context;
    }

    public int onBackPressed(Activity qrActivity) {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            return 0;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            return 1;
        }
        return 1;
    }

    public void showGuide() {
    }
}


package com.kau.minseop.pointshare;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by minseop on 2018-11-22.
 */

public class PointShareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

    }
}

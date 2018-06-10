package com.kau.minseop.pointshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.kau.minseop.pointshare.cardlist.CardListFragment;
import com.kau.minseop.pointshare.event.ActivityResultEvent;
import com.kau.minseop.pointshare.shop.ShopFragment;
import com.kau.minseop.pointshare.wallet.BusProvider;
import com.kau.minseop.pointshare.wallet.WalletFragment;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private MenuItem preitem;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item==preitem) return false;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    replaceViewPager(new ShopFragment());
                    preitem = item;
                    return true;
                case R.id.nav_cardlist:
                    replaceViewPager(new CardListFragment());
                    preitem = item;
                    return true;
                case R.id.nav_mypage:
                    replaceViewPager(new WalletFragment());
                    preitem = item;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        replaceViewPager(new ShopFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private boolean replaceViewPager(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment)
                .commit();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BusProvider.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
    }
}

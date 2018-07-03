package com.kau.minseop.pointshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kau.minseop.pointshare.cardlist.CardListFragment;
import com.kau.minseop.pointshare.event.ActivityResultEvent;
import com.kau.minseop.pointshare.handler.BackPressHandler;
import com.kau.minseop.pointshare.shop.ShopFragment;
import com.kau.minseop.pointshare.utils.FragmentHistory;
import com.kau.minseop.pointshare.utils.Utils;
import com.kau.minseop.pointshare.views.FragNavController;
import com.kau.minseop.pointshare.wallet.BusProvider;
import com.kau.minseop.pointshare.wallet.WalletFragment;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {
    private TextView tb_title;
    private MenuItem preitem;
    String[] TABS;

    private Toolbar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    switchTab(0);
                    if (item==preitem) {
                        mNavController.clearStack();
                        return false;
                    }
                    preitem = item;
                    fragmentHistory.push(0);
                    return true;
                case R.id.nav_cardlist:
                    switchTab(1);
                    if (item==preitem) {
                        mNavController.clearStack();
                        return false;
                    }
                    fragmentHistory.push(1);
                    preitem = item;

                    return true;
                case R.id.nav_mypage:
                    switchTab(2);
                    if (item==preitem) {
                        mNavController.clearStack();
                        return false;
                    }
                    fragmentHistory.push(2);
                    preitem = item;
                    return true;
            }
            return false;
        }
    };

    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;
    private  BottomNavigationView bottomTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildView();

        initToolbar();

        fragmentHistory = new FragmentHistory();
        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.main_activity_fragment_container)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();

        switchTab(0);

    }

    private void buildView(){
        TABS  = new String[]{getString(R.string.title_home), getString(R.string.title_cardlist), getString(R.string.title_mypage)};

        setContentView(R.layout.activity_main);
        bottomTabLayout = (BottomNavigationView) findViewById(R.id.navigation);
        bottomTabLayout.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar = findViewById(R.id.toolbar);

        tb_title = findViewById(R.id.tb_title);

        Realm.init(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BusProvider.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
    }

    private void switchTab(int position) {
        mNavController.switchTab(position);
       // updateToolbarTitle(position);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();

        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();

            } else {

                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();
                    switchTab(position);
                    //updateTabSelection(position);

                } else {
                    switchTab(0);
                    //updateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case FragNavController.TAB1:
                return new ShopFragment();
            case FragNavController.TAB2:
                return new CardListFragment();
            case FragNavController.TAB3:
                return new WalletFragment();
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            updateToolbar();
        }
    }

    private void updateToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }


    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {
            updateToolbar();
        }
    }

    public void updateToolbarTitle(String title) {
       // getSupportActionBar().setTitle(title);
        tb_title.setText(title);
    }

    public void initToolbar(Toolbar toolbar, boolean isBackEnabled) {
        setSupportActionBar(toolbar);

        if(isBackEnabled) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    public void initToolbar(Toolbar toolbar, String title, boolean isBackEnabled) {
        setSupportActionBar(toolbar);
        if (isBackEnabled) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        getSupportActionBar().setTitle(title);
    }
}

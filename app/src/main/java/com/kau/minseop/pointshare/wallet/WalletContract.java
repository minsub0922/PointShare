package com.kau.minseop.pointshare.wallet;

import com.kau.minseop.pointshare.BasePresenter;
import com.kau.minseop.pointshare.BaseView;

/**
 * Created by eirlis on 29.06.17.
 */

public class WalletContract {

    interface View extends BaseView<Presenter> {

        void showBalance();

        void showWalletAddress();
    }

    interface Presenter extends BasePresenter {

        void getWalletBalance();

        void getWalletAddress();
    }
}

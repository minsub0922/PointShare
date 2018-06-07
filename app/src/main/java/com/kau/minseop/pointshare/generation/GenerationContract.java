package com.kau.minseop.pointshare.generation;

import com.kau.minseop.pointshare.BasePresenter;
import com.kau.minseop.pointshare.BaseView;

/**
 * Created by eirlis on 29.06.17.
 */

public interface GenerationContract {

    interface View extends BaseView<Presenter> {

        void showGeneratedWallet(String walletAddress);
    }

    interface Presenter extends BasePresenter {

        void generateWallet(String password);
    }


}

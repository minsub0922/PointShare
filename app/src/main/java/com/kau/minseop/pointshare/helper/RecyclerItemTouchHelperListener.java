package com.kau.minseop.pointshare.helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by minseop on 2018-06-09.
 */

interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolderm, int direction, int position);
}

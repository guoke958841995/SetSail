package com.sxhalo.PullCoal.ui.pullrecyclerview.view.cardview;


import android.support.v7.widget.CardView;

public interface CardAdapter {

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}

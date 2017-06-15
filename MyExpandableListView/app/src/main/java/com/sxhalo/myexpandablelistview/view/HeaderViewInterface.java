package com.sxhalo.myexpandablelistview.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

import java.util.List;

public abstract class HeaderViewInterface<T> {

    protected Activity mContext;
    protected LayoutInflater mInflate;
    protected T mEntity;

    public HeaderViewInterface(Activity context) {
        this.mContext = context;
        mInflate = LayoutInflater.from(context);
    }

    public boolean fillView(T t, ExpandableListView listView) {
        if (t == null) {
            return false;
        }
        if ((t instanceof List) && ((List) t).size() == 0) {
            return false;
        }
        this.mEntity = t;
        getView(t, listView);
        return true;
    }

    protected abstract void getView(T t, ExpandableListView listView);

}

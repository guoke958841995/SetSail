package com.sxhalo.PullCoal.ui.pullrecyclerview.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;


public abstract class BaseView extends LinearLayout
{
    protected int viewCode = 0;

    protected Activity mContext;

    protected BaseModule module;

    public BaseView(Activity context)
    {
        super(context);
        this.mContext = context;
    }

    public BaseView(Activity context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        this.mContext = context;
    }

    protected void onItemClick(BaseModule module)
    {
//        CategoryUtil.jumpByTargetLink(mContext, module, viewCode);
    }

    public BaseModule getModule()
    {
        return this.module;
    }

    public Context getmContext()
    {
        return this.mContext;
    }

    public abstract void setData(BaseModule module);

    public abstract void fillData(BaseModule module);

    public abstract void addTemplateView();

    public abstract void reFresh();
}

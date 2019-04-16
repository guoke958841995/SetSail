package com.sxhalo.PullCoal.ui.spinerpop;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by amoldZhang on 2017/4/26.
 */

public class PopWindowUtils {

    private Activity mContext;
    private SpinerPopWindow mSpinerPopWindow;
    private WindowManager.LayoutParams layoutParams;
    private View view;

    public PopWindowUtils(Activity mContext){
        this.mContext = mContext;
        mSpinerPopWindow = new SpinerPopWindow(mContext);
    }

    private void setData(final ArrayList<String> nameList,int pos) {
        mSpinerPopWindow.refreshData(nameList, pos);
        mSpinerPopWindow.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setHero(pos,nameList);
            }
        });
    }

    public void showSpinWindow(ArrayList<String> nameList,int pos,View view){
        this.view = view;
        setData(nameList,pos);
        mSpinerPopWindow.setWidth(view.getWidth());
        mSpinerPopWindow.showAsDropDown(view);

        layoutParams = mContext.getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        layoutParams.alpha=0.9f;
        mContext.getWindow().setAttributes(layoutParams);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        mSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                layoutParams = mContext.getWindow().getAttributes();
                layoutParams.alpha=1f;
                mContext.getWindow().setAttributes(layoutParams);
                if (myDismissListener != null) {
                    myDismissListener.onDismiss();
                }
            }
        });
    }



    private void setHero(int pos,ArrayList<String> nameList){
        if (pos >= 0 && pos <= nameList.size()){
            String value = nameList.get(pos);
            if (dataBack != null){
                dataBack.setdata(value,view);
            }
        }
    }

    public DataBack dataBack;
    public void getData(DataBack dataBack){
        this.dataBack = dataBack;
    }
    public interface DataBack<T>{
        void setdata(String text,T view);
    }

    /**
     * 添加点击外接接口
     */
    private MyDismissListener myDismissListener;

    public void setMyDismissListener(MyDismissListener myDismissListener) {
        this.myDismissListener = myDismissListener;
    }
    public interface MyDismissListener {
        void onDismiss();
    }
}

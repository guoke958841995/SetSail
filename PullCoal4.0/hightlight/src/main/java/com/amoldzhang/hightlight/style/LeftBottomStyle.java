package com.amoldzhang.hightlight.style;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.amoldzhang.hightlight.LayoutStyle;
import com.amoldzhang.hightlight.ViewInfo;


/**
 * Created by zhaozhibo on 2017/11/6.
 * 在高亮区域正左方
 */

public class LeftBottomStyle extends LayoutStyle {
    public LeftBottomStyle(int layoutRes, int offset) {
        super(layoutRes,offset);
    }

    public LeftBottomStyle(int layoutRes) {
        super(layoutRes);
    }

    public LeftBottomStyle(View decoView, int offset) {
        super(decoView, offset);
    }

    public LeftBottomStyle(View decoView) {
        super(decoView);
    }

    @Override
    public void showDecorationOnScreen(final ViewInfo viewInfo, ViewGroup parent) {
        if (decoView==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            decoView = inflater.inflate(layoutRes, parent, false);
        }
        parent.addView(decoView);
        decoView.setVisibility(View.INVISIBLE);
        decoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    decoView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    decoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                FrameLayout.LayoutParams child_params = (FrameLayout.LayoutParams) decoView.getLayoutParams();
                child_params.leftMargin = viewInfo.offsetX -decoView.getWidth()-offset;
                child_params.topMargin = viewInfo.offsetY +viewInfo.height+offset;
                decoView.requestLayout();
                //多一次布局监听，防止view闪烁
                decoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < 16) {
                            decoView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            decoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        decoView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}

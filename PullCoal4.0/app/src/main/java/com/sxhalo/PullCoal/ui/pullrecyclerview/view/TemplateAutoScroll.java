package com.sxhalo.PullCoal.ui.pullrecyclerview.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.InformationListActivity;
import com.sxhalo.PullCoal.model.Article;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;
import com.sxhalo.PullCoal.ui.upmarqueeview.UPMarqueeView;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  资讯滚动列表
 * Created by amoldZhang on 2018/12/18.
 */
public class TemplateAutoScroll extends BaseView{

    @Bind(R.id.up_marquee_view)
    UPMarqueeView upMarqueeView;
    @Bind(R.id.layout_root)
    LinearLayout layoutRoot;

    private View mRootView;
    List<View> views = new ArrayList<View>();

    public TemplateAutoScroll(Activity context) {
        super(context);
        initView();
    }

    private void initView() {
        try {
            mRootView = View.inflate(mContext, R.layout.header_auto_scroll_layout, null);
            ButterKnife.bind(this, mRootView);

            addTemplateView();
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }

    @Override
    public void setData(BaseModule module) {
        fillData(module);
    }

    @Override
    public void fillData(BaseModule module) {
        if (module == null || !(module instanceof HomeData))
        {
            return;
        }

        if (((HomeData) module).getInformationsList().size() != 0){
            dealWithTheView(((HomeData) module).getInformationsList());
        }
//        this.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onItemClick(module);
//            }
//        });
        invalidate();
    }

    @Override
    public void addTemplateView() {
//        LayoutParams lp = new LayoutParams(-1, (int) (mContext.getResources().getDisplayMetrics().density * 80));
//        mRootView.setLayoutParams(lp);
        addView(mRootView);
    }

    @Override
    public void reFresh() {

    }

    private void dealWithTheView(final List<Article> lsit) {

        try {
            for (int i = 0; i < lsit.size(); i = i + 2) {
                final int position = i;
                //设置滚动的单个布局
                LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_view, null);
                //初始化布局的控件
                TextView tv1 = (TextView) moreView.findViewById(R.id.tv1);
                TextView tv2 = (TextView) moreView.findViewById(R.id.tv2);

                /**
                 * 设置监听
                 */
//                moreView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(myActivity, position + "你点击了" + lsit.get(position).toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
                //进行对控件赋值
                tv1.setText(lsit.get(i).getTitle());
                if (lsit.size() > i + 1) {
                    //因为淘宝那儿是两条数据，但是当数据是奇数时就不需要赋值第二个，所以加了一个判断，还应该把第二个布局给隐藏掉
                    tv2.setText(lsit.get(i + 1).getTitle().toString());
                    /**
                     * 设置监听
                     */
//                    moreView.findViewById(R.id.rl2).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (lsit.size() > position + 1) {
//                                //因为淘宝那儿是两条数据，但是当数据是奇数时就不需要赋值第二个，所以加了一个判断，还应该把第二个布局给隐藏掉
//                                Toast.makeText(myActivity, position + "你点击了" + lsit.get(position + 1).toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
                } else {
                    moreView.findViewById(R.id.rl2).setVisibility(View.GONE);
                }

                //添加到循环滚动数组里面去
                views.add(moreView);
            }
            upMarqueeView.setViews(views);
            layoutRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.jumpAct(mContext, InformationListActivity.class, false);
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("功能列表", e.toString());
        }
    }
}

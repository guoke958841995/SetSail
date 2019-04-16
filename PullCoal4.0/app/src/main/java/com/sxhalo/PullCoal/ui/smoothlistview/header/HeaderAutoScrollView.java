package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.InformationListActivity;
import com.sxhalo.PullCoal.model.Article;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.upmarqueeview.UPMarqueeView;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HeaderAutoScrollView extends HeaderViewInterface<List<Article>> {

    private FragmentActivity myActivity;

    private View view;

    List<View> views = new ArrayList<View>();

    @Bind(R.id.up_marquee_view)
    UPMarqueeView upMarqueeView;
    @Bind(R.id.layout_root)
    LinearLayout layoutRoot;

    public HeaderAutoScrollView(FragmentActivity myActivity) {
        super(myActivity);
        this.myActivity = myActivity;
    }


    @Override
    protected void getView(List<Article> lsit, ListView listView) {
        try {
            view = mInflate.inflate(R.layout.header_auto_scroll_layout, listView, false);
            ButterKnife.bind(this, view);
            dealWithTheView(lsit);
            listView.addHeaderView(view);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }

    public View getView(List<Article> lsit){
        view = mInflate.inflate(R.layout.header_auto_scroll_layout, null, false);
        ButterKnife.bind(this, view);
        dealWithTheView(lsit);
        return view;
    }

    private void dealWithTheView(final List<Article> lsit) {

        try {
            for (int i = 0; i < lsit.size(); i = i + 2) {
                final int position = i;
                //设置滚动的单个布局
                LinearLayout moreView = (LinearLayout) LayoutInflater.from(myActivity).inflate(R.layout.item_view, null);
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

//    /**
//     * 添加点击外接接口
//     */
//    private ADOnClickListener adOnClickListener;
//
//    public interface ADOnClickListener {
//        void adItemOnClickListener(View view, int position);
//    }
//
//    public void setADItemOnClickListener(ADOnClickListener imageOnClickListener) {
//        this.adOnClickListener = imageOnClickListener;
//    }

}

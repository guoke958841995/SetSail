package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2016/12/15.
 */
public class HeaderIntervalView extends HeaderViewInterface<List<String>> {

    @Bind(R.id.recommend_title)
    TextView recommendTitle;
    @Bind(R.id.home_recommend_lv)
    CustomListView homeRecommendLv;
    private View view;
    private int insdex;

    public HeaderIntervalView(Activity context,int insdex) {
        super(context);
        this.insdex = insdex;
    }

    @Override
    protected void getView(List<String> strings, ListView listView) {
        try {
            view = mInflate.inflate(R.layout.home_pager_item, listView, false);
            ButterKnife.bind(this, view);

            dealWithTheView(strings);
            listView.addHeaderView(view);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }

    private void dealWithTheView(List<String> strings) {
        try {
            recommendTitle.setText(strings.get(insdex));
            homeRecommendLv.setVisibility(View.GONE);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }

    @OnClick({R.id.more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more:
                //TODO 点击后跳转到信息部列表界面
                ((BaseActivity)mContext).displayToast(("资讯更多被点击..."));
                break;
        }
    }
}

package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.LegalConsultingActivity;
import com.sxhalo.PullCoal.activity.PlaceOrderActivity;
import com.sxhalo.PullCoal.activity.SearchPunctuationActivity;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.ActivityData;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liz on 2017/11/15.
 */
public class HeaderToolView extends HeaderViewInterface<ActivityData> {

    @Bind(R.id.imageView0)
    ImageView imageView0;
    @Bind(R.id.imageView1)
    ImageView imageView1;
    @Bind(R.id.imageView2)
    ImageView imageView2;
    @Bind(R.id.imageView3)
    ImageView imageView3;
    @Bind(R.id.imageView4)
    ImageView imageView4;
    @Bind(R.id.imageView5)
    ImageView imageView5;

    private ActivityData mData;
    protected Activity mContext;
    private int[] res = {R.drawable.icon_map,R.drawable.icon_calculator,R.drawable.icon_mine_mouth,  R.drawable.icon_information_department,  R.drawable.icon_legal_advice,R.drawable.icon_find_check_in};

    public HeaderToolView(Activity context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void getView(ActivityData data, ListView listView) {
        View view = mInflate.inflate(R.layout.header_tool_view, listView, false);
        ButterKnife.bind(this, view);
        this.mData = data;
        initView();
        listView.addHeaderView(view);
    }

    private void initView() {
        imageView0.setImageResource(res[0]);
        imageView1.setImageResource(res[1]);
        imageView2.setImageResource(res[2]);
        imageView3.setImageResource(res[3]);
        imageView4.setImageResource(res[4]);
        imageView5.setImageResource(res[5]);
    }

    private void doItemClick(int position) {
        switch (position) {
            case 0:
                //跳转地图导航
                UIHelper.showFindPunctuation(mContext, SearchPunctuationActivity.class, "地图导航", "");
                break;
            case 1:
                //跳转运费计算
                UIHelper.showFreightCharge(mContext, null, 0);
                break;
            case 2:
                //跳转查找矿口
                UIHelper.showFindPunctuation(mContext, SearchPunctuationActivity.class, "查找矿口", "");
                break;
            case 3:
                //跳转查找信息部
                UIHelper.showFindPunctuation(mContext, SearchPunctuationActivity.class, "查信息部", "");
                break;
            case 4:
                //法律援助
                UIHelper.jumpAct(mContext, LegalConsultingActivity.class, false);
                break;
            case 5:
                //入住加盟
                String URL = new Config().getCOUPONS_JOINRULE();
                UIHelper.showWEB(mContext, URL, "入驻加盟");
                break;
        }
    }

    @OnClick({R.id.imageView0, R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4,R.id.imageView5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageView0:
                doItemClick(0);
                break;
            case R.id.imageView1:
                doItemClick(1);
                break;
            case R.id.imageView2:
                doItemClick(2);
                break;
            case R.id.imageView3:
                doItemClick(3);
                break;
            case R.id.imageView4:
                doItemClick(4);
                break;
            case R.id.imageView5:
                doItemClick(5);
                break;
        }
    }
}

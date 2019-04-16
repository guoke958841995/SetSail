package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.guide.GuidePage;
import com.sxhalo.PullCoal.ui.guide.GuidePageManager;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 法律咨询
 * Created by liz on 2018/1/6.
 */

public class LegalConsultingActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_legal_consulting);
        if(GuidePageManager.hasNotShowed(this, LegalConsultingActivity.class.getSimpleName())){
            new GuidePage.Builder(this)
                    .setLayoutId(R.layout.view_find_legal_counsulting_guide_page_001)
                    .setKnowViewId(R.id.btn_home_act_enter_know)
                    .setPageTag(LegalConsultingActivity.class.getSimpleName())
                    .builder()
                    .apply();
        }
    }

    @Override
    protected void initTitle() {
        title.setText("法律咨询");
    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.title_bar_left, R.id.layout_phone_service, R.id.layout_appointment, R.id.layout_leave_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_phone_service:
//                Map<String,String> map = new HashMap<String, String>();
//                map.put("tel",getResources().getString(R.string.service_hot_line));
//                UIHelper.showCollTel(this,map,false);
//                break;
            case R.id.layout_appointment:
                if ("-1".equals(SharedTools.getStringValue(this, "userId", "-1"))) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(this, false);
                } else {
                    Map<String,String> map1 = new HashMap<String, String>();
                    map1.put("tel",getResources().getString(R.string.service_hot_line));
                    map1.put("callType", Constant.CALE_TYPE_LEGAL_ADVICE);
                    map1.put("targetId", SharedTools.getStringValue(this, "userId", "-1"));
                    UIHelper.showCollTel(this,map1,true);
                }
                break;
            case R.id.layout_leave_message:
                // 留言
                if ("-1".equals(SharedTools.getStringValue(this, "userId", "-1"))) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(this, false);
                } else {
                    UIHelper.jumpAct(this, MessageListActivity.class, false);
                }
                break;
        }
    }
}

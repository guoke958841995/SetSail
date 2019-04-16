package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 服务中心
 * Created by liz on 2017/4/1.
 */
public class CustomerServiceActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.layout_registration)
    LinearLayout layoutRegistration;

    private String roleId;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_customer_service);
    }

    @Override
    protected void initTitle() {
        title.setText("服务中心");
         roleId = getIntent().getStringExtra("roleId");
        //运营专员 5 和运营管理员2 可以看到司机登记
        if ((!StringUtils.isEmpty(roleId) && roleId.equals("2")) ||
                (!StringUtils.isEmpty(roleId) && roleId.equals("5"))) {
            layoutRegistration.setVisibility(View.VISIBLE);
        }else{
            layoutRegistration.setVisibility(View.GONE);
        }
    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.title_bar_left,R.id.layout_qq_service, R.id.layout_hot_line, R.id.layout_question_other, R.id.layout_register_driver,R.id.layout_guest_registration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_qq_service:
                if (checkAppExist("com.tencent.mobileqq")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + getResources().getString(R.string.service_qq) + "&version=1")));
                } else {
                    displayToast("本机未安装QQ应用！");
                }
                break;
            case R.id.layout_hot_line:
                Map<String,String> map = new HashMap<String, String>();
                map.put("tel",getResources().getString(R.string.service_hot_line));
                UIHelper.showCollTel(this,map,false);
                break;
            case R.id.layout_question_other:
                String URL = new Config().getCOMMON_PROBLEM();
                UIHelper.showWEB(CustomerServiceActivity.this,URL,"常见问题帮助");
                break;
            case R.id.layout_register_driver:
                UIHelper.jumpAct(this,DriverRegistrationActivity.class,false);
                break;
            case R.id.layout_guest_registration:
                UIHelper.jumpAct(this,GuestRegistrationActivity.class,roleId,false);
                break;

        }
    }

    private boolean checkAppExist (String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}

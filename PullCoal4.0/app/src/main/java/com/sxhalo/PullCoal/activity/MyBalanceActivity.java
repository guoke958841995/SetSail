package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.ui.StatusBarUtils;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.StringUtils;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 余额界面
 * Created by liz on 2018/3/15.
 */

public class MyBalanceActivity extends BaseActivity {

    @Bind(R.id.layout_header)
    RelativeLayout layoutHeader;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_balance)
    TextView tvBalance;

    private UserEntity userEntity;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_balance);
        setStatusBar(0,-1);

        // 设置固定大小的占位符
        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) layoutHeader.getLayoutParams(); //取控件View当前的布局参数
        linearParams.height = StatusBarUtils.getStatusBarHeight(this) + BaseUtils.dip2px(this,50f);// 控件的高强制设成当前手机状态栏高度
        layoutHeader.setLayoutParams(linearParams); //使设置好的布局参数应用到控件</pre>
    }

    @Override
    protected void initTitle() {
        title.setText("余额");
    }

    @Override
    protected void getData() {
        userEntity = (UserEntity)getIntent().getSerializableExtra("UserEntity");
        tvBalance.setText(StringUtils.setNumLenth(Float.valueOf(userEntity.getPocketAmount())/100, 2));
    }

    @OnClick({R.id.title_bar_left, R.id.layout_top_up, R.id.layout_transaction})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_top_up:
                //提现
                Intent intent = new Intent(this, WithdrawActivity.class);
                intent.putExtra("UserEntity", userEntity);
                startActivityForResult(intent, Constant.FINISH_ACTIVITY_CODE);
                break;
            case R.id.layout_transaction:
                //流水
                Intent intent1 = new Intent(this, TransactionActivity.class);
                intent1.putExtra("UserEntity", userEntity);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.FINISH_ACTIVITY_CODE) {
            finish();
        }
    }
}

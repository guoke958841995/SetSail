package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.SharedTools;

import butterknife.Bind;
import butterknife.OnClick;

/**
 *  关于拉煤宝
 * Created by liz on 2017/4/1.
 */
public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Bind(R.id.tv_declare)
    TextView tvDeclare;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_us);
    }

    @Override
    protected void initTitle() {
        title.setText("关于拉煤宝");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("已同意《拉煤宝平台用户协议》、《用户隐私政策》和《免责声明》");
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                gotoWEB(new Config().getABOUT_MY(), "用户协议");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                gotoWEB(new Config().getPRIVACY_POLICY(), "隐私政策");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                gotoWEB(new Config().getDISCLAIMER_OF_LIABILITY(), "免责声明");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        //设置点击字体的颜色
        spannableStringBuilder.setSpan(clickableSpan1, 3, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(clickableSpan2, 15, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(clickableSpan3, 24, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.app_title_text_color));
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.app_title_text_color));
        ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(getResources().getColor(R.color.app_title_text_color));
        spannableStringBuilder.setSpan(colorSpan1, 3, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(colorSpan2, 15, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(colorSpan3, 24, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDeclare.setText(spannableStringBuilder);
        tvDeclare.setHighlightColor(getResources().getColor(android.R.color.transparent));//方法重新设置文字背景为透明色。
        tvDeclare.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void getData() {
        try {
            tvVersion.setText("拉煤宝 "+ BaseUtils.getVersionName(this));
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    @OnClick({R.id.title_bar_left, R.id.pull_coal_introduce, R.id.layout_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.pull_coal_introduce: //拉煤宝简介
                gotoWEB(new Config().getABOUT_MY(), "拉煤宝简介");
                break;
            case R.id.layout_feedback:
                //反馈建议
                if ("-1".equals(SharedTools.getStringValue(this, "userId", "-1"))) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(this,false);
                } else {
                    UIHelper.jumpAct(this, FeedbackActivity.class, false);
                }
                break;
        }
    }

    private void gotoWEB(String url, String title) {
        Intent intent = new Intent(AboutUsActivity.this, WebViewActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

}

package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * 意向未达成
 * Created by amoldZhang on 2018/5/14.
 */
public class NoIntentionOfSubmissionActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.iv_reach)
    ImageView ivReach;
    @Bind(R.id.iv_no_reach)
    ImageView ivNoReach;

    @Bind(R.id.et_automatic_refund)
    EditText etAutomaticRefund;
    @Bind(R.id.et_refunds)
    EditText etRefunds;


    private String selectChecked = ""; //当前用户选择直接退款 0  或申请平台介入退款 1

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_no_intention_submission);
    }

    @Override
    protected void initTitle() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// 设置输入软键盘隐藏
        title.setText("申请退款");
    }

    @Override
    protected void getData() {

    }



    @OnClick({R.id.title_bar_left, R.id.iv_reach, R.id.iv_no_reach, R.id.tv_cancel, R.id.tv_determine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.iv_reach:
                ivReach.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_selected));
                ivNoReach.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_normal));
                selectChecked = "0";
                break;
            case R.id.iv_no_reach:
                ivReach.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_normal));
                ivNoReach.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_selected));
                selectChecked = "1";
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_determine:
                if ("0".equals(selectChecked)){
                    setAutomaticRefund();
                }else if ("1".equals(selectChecked)){
                    setRefunds();
                }else{
                 displayToast("请选择后再点击确认按钮！");
                }
                break;
        }
    }

    /**
     * 双方协商退款
     */
    private void setAutomaticRefund() {
        if (StringUtils.isEmpty(etAutomaticRefund.getText().toString().trim())){
             displayToast("您退款原因尚未填写，请填写后再提交");
             return;
        }
        UserDemand userDemand = (UserDemand) getIntent().getSerializableExtra("UserDemand");
        userDemand.setFlage("0");
        userDemand.setReasonString(etAutomaticRefund.getText().toString().trim());
        Intent intent = new Intent();
        intent.putExtra("UserDemand",userDemand);
        setResult(RESULT_OK,intent);
        finish();
    }

    /**
     * 平台介入退款
     */
    private void setRefunds() {
        if (StringUtils.isEmpty(etRefunds.getText().toString().trim())){
            displayToast("您申请平台介入原因尚未填写，请填写后再提交");
            return;
        }
        UserDemand userDemand = (UserDemand) getIntent().getSerializableExtra("UserDemand");
        userDemand.setFlage("1");
        userDemand.setReasonString(etRefunds.getText().toString().trim());

        Intent intent = new Intent();
        intent.putExtra("UserDemand",userDemand);
        setResult(RESULT_OK,intent);
        finish();
    }
}

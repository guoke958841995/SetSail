package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 人工介入
 * Created by amoldZhang on 2018/5/24.
 */

public class ArtificialInterventionActivity extends BaseActivity {

    @Bind(R.id.title_bar_left)
    ImageView titleBarLeft;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.et_refunds)
    EditText etRefunds;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_intention_submission);
    }

    @Override
    protected void initTitle() {
        titleBarLeft.setImageDrawable(getResources().getDrawable(R.mipmap.icon_close));
        title.setText("平台介入");
    }

    @Override
    protected void getData() {

    }


    @OnClick({R.id.title_bar_left, R.id.tv_determine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.tv_determine:
                setRefunds();
                break;
        }
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

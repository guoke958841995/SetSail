package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 反馈建议
 * Created by liz on 2018/1/6.
 */

public class FeedbackActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.et_content)
    EditText etContent;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feedback);
    }

    @Override
    protected void initTitle() {
        title.setText("反馈建议");
    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.title_bar_left, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.btn_submit:
                String content = etContent.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    displayToast("请输入正确的问题描述！");
                    return;
                }
                commit(content);
                break;
        }
    }

    /**
     * 提交反馈建议
     * @param content
     */
    private void commit(String content) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("messageType", "1");
            params.put("message", content);
            new DataUtils(this, params).commitContent(new DataUtils.DataBack<APPData<Object>>() {
                @Override
                public void getData(APPData<Object> data) {
                    try {
                        displayToast(data.getMessage());
                        finish();
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }
                @Override
                public void getError(Throwable e) {
                    super.getError(e);
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }
}

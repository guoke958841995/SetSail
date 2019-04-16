package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UserMessage;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2017/5/9.
 * 系统消息详情页
 */

public class SystemtMessageDetailActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_message_title)
    TextView tvMessageTitle;
    @Bind(R.id.tv_message_content)
    TextView tvMessageContent;
    @Bind(R.id.tv_message_time)
    TextView tvMessageTime;

    private UserMessage massage;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_system_message);
    }

    @Override
    protected void initTitle() {
        title.setText(getIntent().getStringExtra("title"));
        massage = (UserMessage) getIntent().getSerializableExtra("UserMessage");
        setData();
    }

    @Override
    protected void getData() {

    }

    private void setData() {
        tvMessageTitle.setText(massage.getHeader());
        tvMessageContent.setText(massage.getContent());
        tvMessageTime.setText(massage.getPushTime());
    }

    @OnClick(R.id.title_bar_left)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
        }
    }
}

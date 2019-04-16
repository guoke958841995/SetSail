package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.UserMessage;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 消息中心界面
 */
public class MessageCenterActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.layout_coal_message)
    RelativeLayout layoutCoalMessage;
    @Bind(R.id.layout_freight_message)
    RelativeLayout layoutFreightMessage;
    @Bind(R.id.layout_system_message)
    RelativeLayout layoutSystemMessage;
    @Bind(R.id.iv_red_point_coal)
    TextView ivRedPointCoal;
    @Bind(R.id.iv_red_point_transport)
    TextView ivRedPointTransport;
    @Bind(R.id.iv_red_point_system)
    TextView ivRedPointSystem;


    private List<UserMessage> list = new ArrayList<UserMessage>();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_message_center);
    }

    @Override
    protected void initTitle() {
        title.setText("消息中心");
    }

    @Override
    protected void getData() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        ivRedPointCoal.setVisibility(View.GONE);
        ivRedPointTransport.setVisibility(View.GONE);
        ivRedPointSystem.setVisibility(View.GONE);
        if (!SharedTools.getStringValue(this,"userId","-1").equals("-1")){
            getUnReadMessageCount();
        }
    }

    private void getUnReadMessageCount () {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this, params).getPushMessageUnreadCount(new DataUtils.DataBack<APPData>() {
                @Override
                public void getData(APPData dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        String orderMsgCount = StringUtils.isEmpty(dataMemager.getOrderMsgCount())?"0":dataMemager.getOrderMsgCount();
                        String transMsgCount = StringUtils.isEmpty(dataMemager.getTransMsgCount())?"0":dataMemager.getTransMsgCount();
                        String sysMsgCount = StringUtils.isEmpty(dataMemager.getSysMsgCount())?"0":dataMemager.getSysMsgCount();
                        if (!StringUtils.isEmpty(dataMemager.getOrderMsgCount()) && !orderMsgCount.equals("0")){
                            ivRedPointCoal.setVisibility(View.VISIBLE);
                            ivRedPointCoal.setText(orderMsgCount);
                        }
                        if (!StringUtils.isEmpty(dataMemager.getTransMsgCount()) && !transMsgCount.equals("0") ){
                            ivRedPointTransport.setVisibility(View.VISIBLE);
                            ivRedPointTransport.setText(transMsgCount);
                        }
                        if (!StringUtils.isEmpty(dataMemager.getSysMsgCount()) && !sysMsgCount.equals("0")){
                            ivRedPointSystem.setVisibility(View.VISIBLE);
                            ivRedPointSystem.setText(sysMsgCount);
                        }
                    } catch (Exception e) {
                        GHLog.e("消息中心赋值", e.toString());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.title_bar_left, R.id.layout_coal_message, R.id.layout_freight_message, R.id.layout_system_message})
    public void onClick(View view) {
        String userId = SharedTools.getStringValue(this,"userId","-1");
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_coal_message:
                // 判断是否登录
                if (!userId.equals("-1")) { // 已登录
//                    Intent intent = new Intent(this, CoalMessageListActivity.class);
                    Intent intent = new Intent(this, FreightMessageListActivity.class);
                    intent.putExtra("logType", 1);
                    startActivity(intent);
                }else{
                    UIHelper.jumpActLogin(this, false);
                }
                break;
            case R.id.layout_freight_message:
                // 判断是否登录
                if (!userId.equals("-1")) { // 已登录
                    Intent intent = new Intent(this, FreightMessageListActivity.class);
                    intent.putExtra("logType", 2);
                    startActivity(intent);
                }else{
                    UIHelper.jumpActLogin(this, false);
                }
                break;
            case R.id.layout_system_message:
                Intent intent = new Intent(this, FreightMessageListActivity.class);
                intent.putExtra("logType",100);
                startActivity(intent);
                break;
        }
    }
}

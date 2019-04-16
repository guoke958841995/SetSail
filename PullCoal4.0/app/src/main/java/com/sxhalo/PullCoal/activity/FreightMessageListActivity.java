package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserMessage;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.tools.DeleteMessage;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.TowButtonDialog;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 货运通知/系统消息列表
 */
public class FreightMessageListActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<UserMessage> {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.freight_message_list)
    SmoothListView freightMessageList;
    @Bind(R.id.invitation)
    TextView invitation;
    @Bind(R.id.listview_ementy)
    LinearLayout listviewEmenty;
    @Bind(R.id.listview_no_net)
    LinearLayout listviewNoNet;

    private BaseAdapterUtils<UserMessage> baseAdapterUtils;

    private int currentPage = 1;//当前页

    private int logType;// 2-货运消息   100-系统消息

    private List<UserMessage> list = new ArrayList<UserMessage>();
    private DeleteMessage deleteMessage;
    private int pos;
    private String typeName = "";

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_freight_message_list);
    }

    @Override
    protected void initTitle() {
        //消息类型1订单2货运单100系统消息
        logType = getIntent().getIntExtra("logType", -1);
        if (logType == 1){
            title.setText("煤炭预约通知");
            typeName = "订单";
            invitation.setVisibility(View.VISIBLE);
        }else if (logType == 2) {
            title.setText("货运通知");
            typeName = "货运";
            invitation.setVisibility(View.VISIBLE);
        } else if (logType == 100){
            title.setText("系统通知");
            typeName = "系统";
            invitation.setVisibility(View.GONE);
        }
        initListView();
    }

    @Override
    protected void getData() {
        if (!SharedTools.getStringValue(this,"userId","-1").equals("-1")){
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("logType", logType +"");
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this,params).getPushMessageList(new DataUtils.DataBack<APPDataList<UserMessage>>() {
                @Override
                public void getData(APPDataList<UserMessage> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }

                        baseAdapterUtils.refreshData(dataMemager.getList());
                        list = baseAdapterUtils.getListData();

                        if (list.size() != 0 || logType == 100){
                            invitation.setVisibility(View.GONE);
                        }else{
                            invitation.setVisibility(View.VISIBLE);
                        }
                        showEmptyView(baseAdapterUtils.getCount(),listviewEmenty,freightMessageList);
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        GHLog.e("系统消息列表赋值", e.toString());
                        e.printStackTrace();
                    }
                }
            });
        }else{
            if (logType == 100){
                List<UserMessage> userMessages = OrmLiteDBUtil.getQueryAll(UserMessage.class);
                baseAdapterUtils.refreshData(userMessages);
                list = baseAdapterUtils.getListData();
            }
            invitation.setVisibility(View.GONE);
            showEmptyView(baseAdapterUtils.getCount(),listviewEmenty,freightMessageList);
        }
    }

    /**
     * 删除消息后右上角删除按钮状态更新
     */
    private void updateDelete() {
        invitation.setVisibility(View.GONE);
        for(int i=0;i<list.size();i++){
            if (!(list.get(i).getLogType().equals("6"))){
                invitation.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void initListView() {
        deleteMessage = new DeleteMessage((BaseActivity) mContext);
        deleteMessage.setCoalBack(new DeleteMessage.CoalBack() {
            @Override
            public void setBack(String data, int type) {
                if (!StringUtils.isEmpty(data)) {
                    displayToast(data);
                    if (type == 0) {
                        baseAdapterUtils.deleteData(pos);
                    } else {
                        baseAdapterUtils.onRefresh();
                    }
                    updateDelete();
                    GHLog.i("当前列表的长度", baseAdapterUtils.getCount() + "");
                } else {
                    displayToast(getString(R.string.delete_failed));
                }
            }
        });
        baseAdapterUtils = new BaseAdapterUtils<UserMessage>(mContext, freightMessageList);
        baseAdapterUtils.setViewItemData(R.layout.freight_message_item, list);
        baseAdapterUtils.setBaseAdapterBack(this);
        freightMessageList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                pos = i - 1;
                UserMessage massage = baseAdapterUtils.getAdapter().getItem(i - 1);
                if (massage.getLogType().equals("1")) {
                    String itemId = massage.getLogId();
                    deleteMessage.setDeleteMessage(getString(R.string.delete_coal_tips), 0, itemId);
                } else if (massage.getLogType().equals("2")) {
                    String itemId = massage.getLogId();
                    deleteMessage.setDeleteMessage(getString(R.string.delete_transport_tips), 0, itemId);
                } else if (massage.getLogType().equals("100")){
                    showDialog();
                }
                return true;
            }
        });
    }

    private void showDialog() {
        new TowButtonDialog(this, getString(R.string.delete_system_tips), "知道了",
                "", new TowButtonDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
            }
        }).show();
    }

    @OnClick({R.id.title_bar_left, R.id.invitation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.invitation: //消息清空
                if (logType == 1){
                    deleteMessage.setDeleteMessage(getString(R.string.clear_all_coal_tips), logType, null);
                }else if (logType == 2){
                    deleteMessage.setDeleteMessage(getString(R.string.clear_all_transport_tips), logType, null);
                }else if (logType == 100){
                    showDialog();
                }
                break;
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, UserMessage massage, int pos) {
        //是否已读 0否 1是
        if (massage.getIsRead().equals("0")){
            TextView  header = (TextView)helper.getView().findViewById(R.id.tv_title);
            Drawable drawable= getResources().getDrawable(R.mipmap.sin);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            header.setCompoundDrawables(null,null,drawable,null);
        }else{
            TextView  header = (TextView)helper.getView().findViewById(R.id.tv_title);
            Drawable drawable= getResources().getDrawable(R.color.transparent);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            header.setCompoundDrawables(null,null,drawable,null);
        }

        helper.setText(R.id.tv_title, massage.getHeader());

        helper.setText(R.id.tv_time, massage.getPushTime());
        helper.setText(R.id.tv_content, massage.getContent());
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserMessage> mAdapter) {
        String userId = SharedTools.getStringValue(this,"userId","-1");
        //当在非登录并且是系统消息时，只改变本地数据的已读未读，其他状况都需要联网更改已读未读
        if(logType == 100 && userId.equals("-1")){
            UserMessage userMessage = mAdapter.getItem(position - 1);
            userMessage.setIsRead("1");
            OrmLiteDBUtil.deleteWhere(UserMessage.class,"pre_id",new String[]{userMessage.getPreId()});
            OrmLiteDBUtil.insert(userMessage);
            setReading(userMessage);
        }else {
            setReadMessage(mAdapter.getItem(position - 1));
        }
    }

    /**
     * 联网改变
     * @param userMessage
     */
    private void setReadMessage(final UserMessage<TransportMode> userMessage){
        LinkedHashMap<String,String> params =  new LinkedHashMap<String,String>();
        params.put("logId",userMessage.getLogId());
        new DataUtils(this,params).getPushMessageReadMessage(new DataUtils.DataBack<UserMessage>() {
            @Override
            public void getData(UserMessage data) {
                if (data != null){
                    setReading(userMessage);
                }
            }
        });
    }

    private void setReading(UserMessage userMessage) {
        switch (logType) {
            case 1:
                // 跳转到货运单详情页
//                Intent intent = new Intent(this, MyTransportOrderDetailActivity.class);
//                intent.putExtra("waybilNumber", userMessage.getPreId());
//                startActivity(intent);
                Intent intent0 = new Intent();
                intent0.setClass(this, CoalOrderDetailActivity.class);
                intent0.putExtra("orderNumber", userMessage.getPreId());
                startActivity(intent0);
                break;
            case 2:
                // 跳转到货运单详情页
                Intent intent = new Intent(this, MyTransportOrderDetailActivity.class);
                intent.putExtra("waybilNumber", userMessage.getPreId());
                startActivity(intent);
                break;
            case 100:
                //根据logType 来跳转不同的详情页
                // logType 4-司机认证   5-实名认证   6-广播 13 投诉系统回复
                if ("13".equals(userMessage.getLogType())) {
                    // 投诉回复
                    UIHelper.jumpAct(this, ComplaintActivity.class,userMessage.getPreId(), false);
                } else{
                    //系统广播
                    if (!StringUtils.isEmpty(userMessage.getLinkAddress())) {
                        Intent intent1 = new Intent(this, WebViewActivity.class);
                        intent1.putExtra("URL", userMessage.getLinkAddress());
                        intent1.putExtra("title", "系统通知");
                        startActivity(intent1);
                    } else {
                        Intent intent1 = new Intent(this, SystemtMessageDetailActivity.class);
                        intent1.putExtra("UserMessage", userMessage);
                        intent1.putExtra("title", "系统通知");
                        startActivity(intent1);
                    }
                }
                break;
        }
    }

    @Override
    public void getOnRefresh(int page) {
        currentPage = page;
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        currentPage = page;
        getData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        baseAdapterUtils.onRefresh();
    }

    /**
     * 界面跳转
     *
     * @param goClass 要去的界面
     * @param flage   是否要做登录判断
     */
    private void setGoto(Class goClass, boolean flage) {
        String userId = SharedTools.getStringValue(this, "userId", "-1");
        if (flage) {
            if (userId.equals("-1")) {
                //未登录点击跳转登录界面
                UIHelper.jumpActLogin(this, false);
            } else {
                //登陆后点击跳转个人信息界面
                UIHelper.jumpAct(this, goClass, false);
            }
        } else {
            //登陆后点击跳转个人信息界面
            UIHelper.jumpAct(this, goClass, false);
        }
    }


}

package com.sxhalo.PullCoal.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.CoalOrderStatusEntity;
import com.sxhalo.PullCoal.model.DelivererInformationDepartment;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 预约采购详情
 * Created by amoldZhang on 2016/12/21.
 */
public class DetailPurchasingReservationActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;
    @Bind(R.id.publisher_data_view)
    LinearLayout publisherDataView; //发布人布局信息
    @Bind(R.id.iv_head)
    CircleImageView ivHead;//头像
    @Bind(R.id.tv_name)
    TextView tvName;//昵称
    @Bind(R.id.tv_status)
    TextView tvStatus;//认证状态
    @Bind(R.id.ratingbar)
    ResetRatingBar ratingBar;//信用等级
    @Bind(R.id.tv_area)
    TextView tvArea;//地区
    @Bind(R.id.tv_cash)
    TextView tvCash;//保证金金额
    @Bind(R.id.tv_cash_deposit)
    TextView tvCashDeposit;//保证金布局

    @Bind(R.id.select_information_view)
    LinearLayout selectInformationView; //已经选择的信息部数据展示
    @Bind(R.id.selsct_infor_name)
    TextView selsctInforName;//名称
    @Bind(R.id.selsct_infor_address)
    TextView selsctInforAddress;//地址
    @Bind(R.id.selsct_infor_user_name)
    TextView selsctInforUserName;//发布人姓名
    @Bind(R.id.selsct_infor_user_moble)
    TextView selsctInforUserMoble;//发布人电话


    @Bind(R.id.goto_payment)
    TextView gotoPayment;   // 当有保证金，没有支付时的去支付按钮

    @Bind(R.id.layout_margin_process)
    LinearLayout layoutMarginProcess; //支付了保证金流程界面展示
    @Bind(R.id.tv_balance)
    TextView tvBalance;  //保证金金额

    @Bind(R.id.iv_normal_status_2)
    ImageView ivNormalStatus2;
    @Bind(R.id.tv_normal_status_2)
    TextView tvNormalStatus2;     // 确认流程
    @Bind(R.id.iv_normal_status_3)
    ImageView ivNormalStatus3;
    @Bind(R.id.tv_normal_status_3)
    TextView tvNormalStatus3;     // 磋商流程
    @Bind(R.id.iv_normal_status_4)
    ImageView ivNormalStatus4;
    @Bind(R.id.tv_normal_status_4)
    TextView tvNormalStatus4;     // 退款流程
    @Bind(R.id.iv_normal_status_5)
    ImageView ivNormalStatus5;
    @Bind(R.id.tv_normal_status_5)
    TextView tvNormalStatus5;     // 完成流程

    @Bind(R.id.layout_validation_process)
    LinearLayout layoutValidationProcess; //确认流程布局
    @Bind(R.id.tv_select)
    TextView tvSelect; //确认按钮
    @Bind(R.id.list_view_passing)
    CustomListView listViewPassing; //确认流程布局


    @Bind(R.id.layout_refund_process)
    LinearLayout layoutRefundProcess; //退款流程布局
    @Bind(R.id.content_listview)
    CustomListView contentListView; //退款详情展示布局
    @Bind(R.id.iv_refund_process)
    ImageView ivRefundProcess; //退款详情展示布局
    @Bind(R.id.tv_more)
    TextView tvMore;//查看更多


    @Bind(R.id.layout_intention_view)
    LinearLayout layoutIntentionView; //意向达成布局
    @Bind(R.id.tv_intention_not)
    TextView tvIntentionNot; // 意向未达成
    @Bind(R.id.tv_intention)
    TextView tvIntention; // 意向达成
    @Bind(R.id.layout_more)
    LinearLayout layoutMore; // 查看更多布局

    @Bind(R.id.layout_select_infor)
    LinearLayout layoutSelectInfor; //确认选定按钮
    @Bind(R.id.tv_delivery_end_time)
    TextView tvDeliceryEndTime; // 过期时间

    @Bind(R.id.tv_purchase_name)
    TextView tvPurchaseName;//名称
    @Bind(R.id.purchase_coal_type)
    TextView purchaseCoalType;//煤种
    //    @Bind(R.id.tv_time)
//    TextView tvTime;//时间
    @Bind(R.id.tv_calorific)
    TextView tvCalorific;//发热量
    @Bind(R.id.tv_sulphur_content)
    TextView tvSulphurContent;//含硫量
    @Bind(R.id.tv_num)
    TextView tvNum;//预约量
    @Bind(R.id.tv_price)
    TextView tvPrice;//价钱
    @Bind(R.id.purchase_remarks)
    TextView purchaseRemarks;
    @Bind(R.id.layout_remark)
    LinearLayout layoutRemark;
    @Bind(R.id.divider_remark)
    View dividerRemark;

    private String demandId;
    private String phoneNumber;
    private Map<String, String> coalTypeMap = new HashMap<String, String>();
    private Integer demandState; //发布状态
    private UserDemand userDemand;
    private DelivererInformationDepartment informationDepartment;

    private VerticalAdapter verticalAdapter;//垂直方向时间轴adapter
    private boolean showMore = false;//默认不是查看更多


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_purchase_detail);
    }

    @Override
    protected void initTitle() {
        title.setText("求购详情");
        queryCoalType();
    }

    private void queryCoalType() {
        Dictionary dictionaryList = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100002"}).get(0);
        for (FilterEntity filterEntity : dictionaryList.list){
            coalTypeMap.put(filterEntity.dictCode, filterEntity.dictValue);
        }
    }

    @Override
    protected void getData() {
        try {
            demandId = getIntent().getStringExtra("demandId");
            LinkedHashMap<String, String> praram = new LinkedHashMap<String, String>();
            praram.put("demandId",demandId);
            new DataUtils(this,praram).getUserDemandInfo(new DataUtils.DataBack<UserDemand>() {
                @Override
                public void getData(UserDemand data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        userDemand = data;
                        initView();
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2130003".equals(e.getMessage())){
                        displayToast(e.getCause().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    private void initView() {
        String userId = SharedTools.getStringValue(this,"userId","-1");
        if (!userDemand.getUserId().equals(userId)){
            initUserData(userDemand);
        }else{
            //根据当前状态来处理界面逻辑
            setSwithView();
        }
        initCoalData(userDemand);
    }

    QuickAdapter<DelivererInformationDepartment> adapter;
    /**
     *  根据当前状态来处理界面逻辑
     *  状态：01、未发布；10、已发布（待投递，确认）；20、待选定；30、磋商；40、未达成意向；41、已达成意向；50、退款；60、完成；00、取消
     */
    private void setSwithView() {
        tvBalance.setText(StringUtils.setNumLenth(Float.valueOf(userDemand.getBond())/100, 2).replace(".00","") + "元");
        if ("0.00元".equals(tvBalance.getText().toString().trim())|| "0元".equals(tvBalance.getText().toString().trim())){
            mapType.setText("取消发布");
            mapType.setVisibility(View.VISIBLE);
            layoutMarginProcess.setVisibility(View.GONE);
            return;
        }
        demandState = Integer.valueOf((StringUtils.isEmpty(userDemand.getDemandState())?"01" : userDemand.getDemandState()));
        if ( demandState == 01 ){
            layoutMarginProcess.setVisibility(View.GONE);
            gotoPayment.setVisibility(View.VISIBLE);

            mapType.setText("删除发布");
            mapType.setVisibility(View.VISIBLE);
        }else{
            gotoPayment.setVisibility(View.GONE);
            layoutMarginProcess.setVisibility(View.VISIBLE);
            if (demandState > 20){
                selectInformationView.setVisibility(View.VISIBLE);
                setInForData();
            }

            layoutRefundProcess.setVisibility(View.VISIBLE);
            if (userDemand.getEntityList() != null && userDemand.getEntityList().size() <= 1){
                layoutMore.setVisibility(View.GONE);
            }else {
                layoutMore.setVisibility(View.VISIBLE);
            }
            if (userDemand.getEntityList() != null && userDemand.getEntityList().size() != 0){
                verticalAdapter = new VerticalAdapter(this, userDemand.getEntityList());
                contentListView.setAdapter(verticalAdapter);
            }
            switch (demandState){
                case 10:
                    if ("即将到期".equals(DateUtil.dateDiff(userDemand.getDeliveryEndTime()))){
                        tvDeliceryEndTime.setText("已经到期");
                    }else if ("已过期".equals(DateUtil.dateDiff(userDemand.getDeliveryEndTime()))){
                        tvDeliceryEndTime.setText("已过期");
                    }else {
                        tvDeliceryEndTime.setText(dateDiff(userDemand.getDeliveryEndTime()));
                    }

                    tvDeliceryEndTime.setVisibility(View.VISIBLE);

                    mapType.setText("取消发布");
                    mapType.setVisibility(View.VISIBLE);
                    break;
                case 20:
                    if ("即将到期".equals(DateUtil.dateDiff(userDemand.getDeliveryEndTime()))){
                        tvDeliceryEndTime.setText("已经到期");
                    }else if ("已过期".equals(DateUtil.dateDiff(userDemand.getDeliveryEndTime()))){
                        tvDeliceryEndTime.setText("已过期");
                    }else {
                        tvDeliceryEndTime.setText(dateDiff(userDemand.getDeliveryEndTime()));
                    }
                    tvDeliceryEndTime.setVisibility(View.VISIBLE);

                    ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));

                    if (userDemand.getInformationDepartmentList() != null && userDemand.getInformationDepartmentList().size() != 0){
                        layoutValidationProcess .setVisibility(View.VISIBLE);
                        tvDeliceryEndTime.setVisibility(View.VISIBLE);
                        layoutSelectInfor.setVisibility(View.VISIBLE);

                        setSelectInFor();
                    }
                    mapType.setText("取消发布");
                    mapType.setVisibility(View.VISIBLE);
                    break;
                case 30:
                    ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));

                    tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus3.setTextColor(getResources().getColor(R.color.app_title_text_color));

                    mapType.setVisibility(View.GONE);
                    layoutValidationProcess.setVisibility(View.GONE);
                    tvDeliceryEndTime.setVisibility(View.GONE);
                    layoutSelectInfor.setVisibility(View.GONE);

                    layoutIntentionView.setVisibility(View.VISIBLE);

                    for (CoalOrderStatusEntity coalOrderStatusEntity : userDemand.getEntityList()){
                        if ("50".equals(coalOrderStatusEntity.getDemandFlow())){
                            layoutIntentionView.setVisibility(View.GONE);
                            //是否是本人发起的 0、否；1、是
                            if (userDemand.getIfSelfRefund().equals("0")){
                                //信息部未达成意向，申请退款
                                layoutIntentionView.setVisibility(View.VISIBLE);
                                tvIntentionNot.setText("人工介入");
                                tvIntention.setText("同意退款");
                            }else{
                                layoutSelectInfor .setVisibility(View.GONE);
                            }
                            return;
                        }
                        if ("41".equals(coalOrderStatusEntity.getDemandFlow())){
                            // 是否本人发起意向达成 0、否；1、是
                            if (userDemand.getIfSelfRefund().equals("0")){
                                //信息部达成意向，申请退款
                                layoutIntentionView.setVisibility(View.VISIBLE);
                                tvIntentionNot.setText("不同意确认");
                                tvIntention.setText("同意确认");
                            }else{
                                layoutIntentionView.setVisibility(View.GONE);
                            }
                            for (CoalOrderStatusEntity coalEntity : userDemand.getEntityList()){
                                if ("40".equals(coalEntity.getDemandFlow())){
                                    ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                                    tvNormalStatus4.setTextColor(getResources().getColor(R.color.app_title_text_color));

                                    layoutIntentionView.setVisibility(View.GONE);
                                    layoutSelectInfor .setVisibility(View.VISIBLE);
                                    tvSelect.setText("申请退款");
                                    return;
                                }
                            }
                            return;
                        }else if ("40".equals(coalOrderStatusEntity.getDemandFlow())){
                            // 是否本人发起意向达成 0、否；1、是
                            if (userDemand.getIfSelfRefund().equals("0")){
                                //信息部未达成意向，申请退款
                                layoutIntentionView.setVisibility(View.VISIBLE);
                                tvIntentionNot.setText("人工介入");
                                tvIntention.setText("同意退款");
                            }else{
                                layoutIntentionView.setVisibility(View.GONE);
                            }
                            for (CoalOrderStatusEntity coalEntity : userDemand.getEntityList()){
                                if ("41".equals(coalEntity.getDemandFlow())){
                                    ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                                    tvNormalStatus4.setTextColor(getResources().getColor(R.color.app_title_text_color));

                                    layoutIntentionView.setVisibility(View.GONE);
                                    layoutSelectInfor .setVisibility(View.VISIBLE);
                                    tvSelect.setText("申请退款");
                                    return;
                                }
                            }
                            return;
                        }
                    }
                    break;
                case 40:  // 40、未达成意向；41、已达成意向
                    ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));

                    tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus3.setTextColor(getResources().getColor(R.color.app_title_text_color));

                    for (CoalOrderStatusEntity coalOrderStatusEntity : userDemand.getEntityList()){
                        if ("50".equals(coalOrderStatusEntity.getDemandFlow())){

                            layoutIntentionView.setVisibility(View.GONE);
                            //是否是本人发起的 0、否；1、是
                            if (userDemand.getIfSelfRefund().equals("0")){
                                //信息部未达成意向，申请退款
                                layoutIntentionView.setVisibility(View.VISIBLE);
                                tvIntentionNot.setText("人工介入");
                                tvIntention.setText("同意退款");
                            }else{
                                layoutSelectInfor .setVisibility(View.GONE);
                            }
                            return;
                        }else{
                            layoutIntentionView.setVisibility(View.GONE);
                            layoutSelectInfor .setVisibility(View.VISIBLE);
                            tvSelect.setText("申请退款");
                        }
                    }
                    break;
                case 41:
                    ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));

                    tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus3.setTextColor(getResources().getColor(R.color.app_title_text_color));

                    for (CoalOrderStatusEntity coalOrderStatusEntity : userDemand.getEntityList()){
                        if ("50".equals(coalOrderStatusEntity.getDemandFlow())){

                            layoutIntentionView.setVisibility(View.GONE);
                            layoutSelectInfor .setVisibility(View.VISIBLE);
                            //是否是本人发起的  0、否；1、是
                            if (userDemand.getIfSelfRefund().equals("0")){
                                tvSelect.setText("同意退款");
                            }else{
                                layoutSelectInfor .setVisibility(View.GONE);
                            }
                            return;
                        }else{
                            layoutIntentionView.setVisibility(View.GONE);
                            layoutSelectInfor .setVisibility(View.VISIBLE);
                            tvSelect.setText("申请退款");
                        }
                    }
                    break;
                case 50:
                    layoutSelectInfor .setVisibility(View.GONE);
                    layoutValidationProcess .setVisibility(View.GONE);
                    layoutIntentionView.setVisibility(View.GONE);

                    if (StringUtils.isEmpty(userDemand.getSelectedSalesId())){
                        selectInformationView.setVisibility(View.GONE);
                    }

                    ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));

                    tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus3.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus4.setTextColor(getResources().getColor(R.color.app_title_text_color));

                    break;
                case 60:
                    if (StringUtils.isEmpty(userDemand.getSelectedSalesId())){ //取消后的退款成功后的完成状态
                        ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                        ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                        ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                        ivNormalStatus5.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));

                        tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));
                        tvNormalStatus3.setTextColor(getResources().getColor(R.color.app_title_text_color));
                        tvNormalStatus4.setTextColor(getResources().getColor(R.color.app_title_text_color));
                        tvNormalStatus5.setTextColor(getResources().getColor(R.color.app_title_text_color));
                        if (userDemand.getInformationDepartmentList() != null && userDemand.getInformationDepartmentList().size() != 0){

                        }else{
                            findViewById(R.id.rl_normal_status_2).setVisibility(View.GONE);
                        }
                        findViewById(R.id.rl_normal_status_3).setVisibility(View.GONE);
                        selectInformationView.setVisibility(View.GONE);
                    }else {  //正常完成状态
                        ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                        ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                        ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                        ivNormalStatus5.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));

                        tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));
                        tvNormalStatus3.setTextColor(getResources().getColor(R.color.app_title_text_color));
                        tvNormalStatus4.setTextColor(getResources().getColor(R.color.app_title_text_color));
                        tvNormalStatus5.setTextColor(getResources().getColor(R.color.app_title_text_color));

                        selectInformationView.setVisibility(View.VISIBLE);
                        layoutIntentionView.setVisibility(View.GONE);
                        setInForData();
                    }
                    break;
                case 00:
                    selectInformationView.setVisibility(View.GONE);
                    mapType.setVisibility(View.GONE);
                    if (userDemand.getInformationDepartmentList() != null && userDemand.getInformationDepartmentList().size() != 0){
                        layoutValidationProcess.setVisibility(View.GONE);
                        tvDeliceryEndTime.setVisibility(View.GONE);
                        layoutSelectInfor.setVisibility(View.GONE);
                    }else{
                        findViewById(R.id.rl_normal_status_2).setVisibility(View.GONE);
                    }

                    findViewById(R.id.rl_normal_status_3).setVisibility(View.GONE);
                    findViewById(R.id.rl_normal_status_4).setVisibility(View.GONE);

                    ivNormalStatus5.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    tvNormalStatus5.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus5.setText("取消");

                    ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                    ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));

                    tvNormalStatus2.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus3.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    tvNormalStatus4.setTextColor(getResources().getColor(R.color.app_title_text_color));

                    break;
            }
        }
    }

    /**
     * 初始化用户选定信息部信息
     */
    private void setInForData() {
        for (DelivererInformationDepartment informationDepartment : userDemand.getInformationDepartmentList()){
            if (userDemand.getSelectedSalesId().equals(informationDepartment.getCoalSalesId())){
                selsctInforName.setText("已选定" + informationDepartment.getCompanyName());
                selsctInforAddress.setText("地址："+ informationDepartment.getAddress());
                selsctInforUserName.setText("发布人："+ informationDepartment.getRealName());
                selsctInforUserMoble.setText("电话："+ informationDepartment.getUserMobile());
            }
        }
    }

    /**
     * 选定意向信息部布局
     */
    private void setSelectInFor() {
        adapter = new QuickAdapter<DelivererInformationDepartment>(this,R.layout.purchasing_reseration_list_item,userDemand.getInformationDepartmentList()) {
            @Override
            protected void convert(BaseAdapterHelper helper,final DelivererInformationDepartment item,final int position) {
                helper.setText(R.id.name,item.getCompanyName());
                helper.setText(R.id.address,"地址："+item.getAddress());
                helper.setText(R.id.deliverer_name,item.getRealName());
                helper.setText(R.id.deliverer_tel,item.getUserMobile());

                ImageView ivSelsct = (ImageView)helper.getView().findViewById(R.id.iv_select);
                if (item.isSelectInfor()){
                    ivSelsct.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_selected));
                }else{
                    ivSelsct.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_normal));
                }
            }
        };
        listViewPassing.setAdapter(adapter);
        listViewPassing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0;i<userDemand.getInformationDepartmentList().size();i++){
                    if (i == position){
                        adapter.getItem(i).setSelectInfor(true);
                    }else{
                        adapter.getItem(i).setSelectInfor(false);
                    }
                }
                adapter.notifyDataSetChanged();
                informationDepartment = adapter.getItem(position);
            }
        });
    }

    /**
     * 初始化煤炭信息
     * @param userDemand
     */
    private void initCoalData(UserDemand userDemand) {
        //名称
        tvPurchaseName.setText(userDemand.getCoalName());
        //煤种
        purchaseCoalType.setText(coalTypeMap.get(userDemand.getCategoryId()));
        //时间
//        tvTime.setText(userDemand.getCreateTime());
        //发热量
        tvCalorific.setText(userDemand.getCalorificValue() + "kCal/kg");
        //含硫量
        tvSulphurContent.setText(userDemand.getTotalSulfur() + "%");
        //预约量
        tvNum.setText(userDemand.getNumber() + "吨");
        //价钱
        tvPrice.setText(userDemand.getPrice() + "元/吨");
        String illustrate = userDemand.getIllustrate();
        if (!StringUtils.isEmpty(illustrate)) {
            purchaseRemarks.setText(illustrate);
            layoutRemark.setVisibility(View.VISIBLE);
            dividerRemark.setVisibility(View.VISIBLE);
        }else{
            layoutRemark.setVisibility(View.GONE);
            dividerRemark.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化用户信息
     */
    private void initUserData(UserDemand userDemand) {
        publisherDataView.setVisibility(View.VISIBLE);
        //联系电话
        phoneNumber = userDemand.getContactPhone();
        if(userDemand.getRealnameAuthState().equals("1")) {
            // 认证 显示昵称
            tvName.setText(StringUtils.setName(userDemand.getContactPerson()));
            tvStatus.setText("已认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_green));
        } else {
            //未认证 显示电话号码
            tvName.setText(StringUtils.setPhoneNumber(phoneNumber));
            tvStatus.setText("未认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_gray));
        }
        // 头像
        getImageManager().loadUrlImageView(userDemand.getHeadPic(), ivHead);
        // 信用等级
        ratingBar.setStar(Integer.valueOf(StringUtils.isEmpty(userDemand.getCreditRating())?"1":userDemand.getCreditRating()));
        //地区
        tvArea.setText(userDemand.getRegionName());

        String bond = StringUtils.setNumLenth(Float.valueOf(userDemand.getBond())/100, 2);
        if ("0.00".equals(bond)){
            tvCash.setVisibility(View.GONE);
            tvCashDeposit.setVisibility(View.GONE);
        }else{
            tvCash.setText(bond);
        }
    }

    @OnClick({R.id.title_bar_left, R.id.iv_phone, R.id.map_type,R.id.goto_payment,R.id.layout_select_infor,R.id.tv_intention_not,R.id.tv_intention,R.id.layout_more,R.id.select_information_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                String message =  "" ;
                if ("0.00元".equals(tvBalance.getText().toString().trim()) || "0元".equals(tvBalance.getText().toString().trim())){
                    message = "请确认取消本条预约！";
                }else{
                    if (demandState == 01){   // 选择保证金 没有支付 删除
                        message = "请确认删除本条求购！";
                    }else  if (demandState == 10){
                        float bond = (Float.valueOf(userDemand.getBond()) * 2 /100);
                        message = "您正在取消已交保证金信息，如确定取消，则将扣除 "+ StringUtils.setNumLenth(Float.valueOf(bond)/100, 2) +"元 管理费！" ;
                    }else  if (demandState == 20){
                        int size = 0;
                        if (userDemand.getInformationDepartmentList() != null && userDemand.getInformationDepartmentList().size() != 0){
                            size = userDemand.getInformationDepartmentList().size();
                        }
                        float bond =  Float.valueOf(userDemand.getBond()) * (2+(Float.valueOf( 5 * size)/10))/100;
                        message = "您正在取消已有信息部用户交保证金投递意向的求购信息，如确定取消，则将扣除 "+ StringUtils.setNumLenth(Float.valueOf(bond)/100, 2) + "元 的费用，并全额退还信息部的保证金。" ;
                    }
                }
                showDialog(message, getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                    @Override
                    public void onLeftClick() {
                    }
                    @Override
                    public void onRightClick() {
                        if ("0.00元".equals(tvBalance.getText().toString().trim())|| "0元".equals(tvBalance.getText().toString().trim())){
                            //未交保证金时发布取消
//                            displayToast("未交保证金时发布取消！");
                            delatData(demandId);
                        }else {
                            //选择保证金 没有支付 删除
                            if (demandState == 00) {
                                delatData(demandId);
                            } else if (demandState == 10) { //尚未有信息部投递意向时 取消
                                getUserDemandCancel();
                            } else if (demandState == 20) { // 已有信息部投递意向时 取消
                                getUserDemandCancel();
                            }
                        }
                    }
                });
                break;
            case R.id.iv_phone:
                String name = "";
                if (BaseUtils.isAppInstalled(this, "com.kmbao")) {
                    name = "去打开";
                } else {
                    name = getString(R.string.dialog_right_text);
                }
                showDialog(getString(R.string.dialog_tips), getString(R.string.dialog_left_text), name, new RLAlertDialog.Listener() {
                    @Override
                    public void onLeftClick() {
                    }
                    @Override
                    public void onRightClick() {
                        if (BaseUtils.isAppInstalled(DetailPurchasingReservationActivity.this, "com.kmbao")) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.putExtra("userId", SharedTools.getStringValue(DetailPurchasingReservationActivity.this, "user_mobile", "-1"));
                            intent.putExtra("demandId", userDemand.getDemandId());
                            ComponentName cn = new ComponentName("com.kmbao", "com.kmbao.MainActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            intent.setComponent(cn);
                            startActivity(intent);
                        } else {
                            // 快煤宝没安装
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse("http://www.17lm.com/web/appDownload.html");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    }
                });
                break;
            case R.id.layout_more:
                //查看更多
                if (userDemand.getEntityList().size() <= 1){

                }else {
                    showMore = !showMore;
                    verticalAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.goto_payment:
                Intent intent = new Intent(DetailPurchasingReservationActivity.this,MarginPaymentActivity.class);
                intent.putExtra("UserDemand",userDemand);
                startActivityForResult(intent, Constant.PAYMENT_CODE);
                break;
            case R.id.layout_select_infor:
                if (demandState == 20){
                    if (informationDepartment == null){
                        displayToast("请选定意向信息部后再点击按钮");
                    }else{
                        showDialog("您已经选定 \"" + informationDepartment.getCompanyName()+ " \" 确认意向，确认后，其他信息部将不能继续参与预约，并且退还对方保证金。",
                                getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                                    @Override
                                    public void onLeftClick() {

                                    }

                                    @Override
                                    public void onRightClick() {
                                        getUserDemandDelivervSelected();
                                    }
                                });
                    }
                }else if ( demandState == 30 || demandState == 40 ){
                    Intent intent1 = new Intent(DetailPurchasingReservationActivity.this,NoIntentionOfSubmissionActivity.class);
                    intent1.putExtra("UserDemand",userDemand);
                    startActivityForResult(intent1, Constant.SELECT_ADDRESS);
                }else if (demandState == 41){
                    showDialog("是否确认申请退款？",
                            getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                                @Override
                                public void onLeftClick() {
                                }

                                @Override
                                public void onRightClick() {
                                    LinkedHashMap<String, String> praram = new LinkedHashMap<>();
                                    praram.put("demandId",userDemand.getDemandId());
                                    setIntentionToInfor(praram);
                                }
                            });
                }
                break;
            case R.id.tv_intention_not:
                if ("不同意确认".equals(tvIntentionNot.getText().toString())){
                    showDialog("是否不同意确认？",
                            getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                                @Override
                                public void onLeftClick() {
                                }

                                @Override
                                public void onRightClick() {
                                    LinkedHashMap<String, String> praram = new LinkedHashMap<>();
                                    praram.put("demandId",demandId);
                                    praram.put("demandState","40");
                                    getuserDemandDeliveryConfing(tvIntentionNot.getText().toString(),praram);
                                }
                            });
                }else if ("人工介入".equals(tvIntentionNot.getText().toString())){
                    Intent intent1 = new Intent(this,ArtificialInterventionActivity.class);
                    intent1.putExtra("UserDemand",userDemand);
                    startActivityForResult(intent1, Constant.SELECT_ADDRESS);
                }else if ("意向未达成".equals(tvIntentionNot.getText().toString())){
                    showDialog("是否确认意向未达成？",
                            getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                                @Override
                                public void onLeftClick() {
                                }

                                @Override
                                public void onRightClick() {
                                    LinkedHashMap<String, String> praram = new LinkedHashMap<>();
                                    praram.put("demandId",userDemand.getDemandId());
                                    praram.put("demandState","40");
                                    getuserDemandDeliveryConfing(tvIntentionNot.getText().toString(),praram);
                                }
                            });
                }
                break;
            case R.id.tv_intention:
                if ("意向达成".equals(tvIntention.getText().toString())){
                    showDialog("您正在确认 \"意向达成\"  确定后等待信息部确认。双方确认完成后，退还保证金。",
                            getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                                @Override
                                public void onLeftClick() {
                                }

                                @Override
                                public void onRightClick() {
                                    LinkedHashMap<String, String> praram = new LinkedHashMap<>();
                                    praram.put("demandId",demandId);
                                    praram.put("demandState","41");
                                    getuserDemandDeliveryConfing(tvIntention.getText().toString(),praram);

                                }
                            });
                }else if ("同意确认".equals(tvIntention.getText().toString())){
                    showDialog("是否同意确认？",
                            getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                                @Override
                                public void onLeftClick() {
                                }

                                @Override
                                public void onRightClick() {
                                    LinkedHashMap<String, String> praram = new LinkedHashMap<>();
                                    praram.put("demandId",demandId);
                                    praram.put("demandState","41");
                                    getuserDemandDeliveryConfing(tvIntention.getText().toString(),praram);
                                }
                            });
                }else if ("同意退款".equals(tvIntention.getText().toString())){
                    showDialog("是否确认同意退款？",
                            getString(R.string.cancel), getString(R.string.sure), new RLAlertDialog.Listener() {
                                @Override
                                public void onLeftClick() {
                                }

                                @Override
                                public void onRightClick() {
                                    LinkedHashMap<String, String> praram = new LinkedHashMap<>();
                                    praram.put("demandId",userDemand.getDemandId());
                                    setIntentionToInfor(praram);
                                }
                            });
                }
                break;
            case R.id.select_information_view:
                Intent intent2 = new Intent(this,DetaileInformationDepartmentActivity.class);
                intent2.putExtra("InfoDepartId",userDemand.getSelectedSalesId());
                startActivity(intent2);
                break;
        }
    }



    /**
     * 投递意向是否达成
     */
    private void getuserDemandDeliveryConfing(final String messageText,final LinkedHashMap<String, String> praram) {
        try {
            new DataUtils(this,praram).getuserDemandDeliveryConfing(new DataUtils.DataBack<UserDemand>() {
                @Override
                public void getData(UserDemand data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        if ("意向未达成".equals(messageText) || "不同意确认".equals(messageText)){
//                            Intent intent1 = new Intent(DetailPurchasingReservationActivity.this,NoIntentionOfSubmissionActivity.class);
//                            intent1.putExtra("UserDemand",userDemand);
//                            startActivityForResult(intent1, Constant.SELECT_ADDRESS);
//                            DetailPurchasingReservationActivity.this.getData();
//                            displayToast("意向未达成！");
                        }else{
                            displayToast("申请提交成功！");
                        }
                        userDemand = data;
                        initView();
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }
                @Override
                public void getError(Throwable e) {
                    if ("2130003".equals(e.getMessage())){
                        displayToast(e.getCause().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }


    /**
     * 保证金申请退款
     */
    private void setIntentionToInfor(final LinkedHashMap<String, String> praram) {
        try {


            new DataUtils(this,praram).getuserDemandBondRefundRecordCreate(new DataUtils.DataBack<UserDemand>() {
                @Override
                public void getData(UserDemand data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        if (30 == demandState){
//                            displayToast(tvIntention.getText().toString());
                        }else if (50 == demandState){
//                            displayToast(tvSelect.getText().toString().trim() + "成功！");
                        }
                        userDemand = data;
                        initView();
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2130003".equals(e.getMessage())){
                        displayToast(e.getCause().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    /**
     * 确认意向信息部
     */
    private void getUserDemandDelivervSelected(){
        try {
            LinkedHashMap<String, String> praram = new LinkedHashMap<String, String>();
            praram.put("demandId",demandId);
            praram.put("selected",informationDepartment.getUserId());
            new DataUtils(this,praram).getUserDemandDelivervSelected(new DataUtils.DataBack<UserDemand>() {
                @Override
                public void getData(UserDemand data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        displayToast("确认意向信息部成功！");
                        userDemand = data;
                        initView();
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2130004".equals(e.getMessage())){
                        displayToast(e.getCause().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  取消发布
     */
    private void getUserDemandCancel(){
        try {
            LinkedHashMap<String, String> praram = new LinkedHashMap<String, String>();
            praram.put("demandId",demandId);
            new DataUtils(this,praram).getUserDemandCancel(new DataUtils.DataBack<UserDemand>() {
                @Override
                public void getData(UserDemand data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        displayToast("取消发布成功！");
                        userDemand = data;
                        initView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2130003".equals(e.getMessage())){
                        displayToast(e.getCause().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.PAYMENT_CODE: //支付界面的回调
                    if (data == null) {
                        //支付成功回调
                        getData();
                    } else {
                        //取消支付回调
                        displayToast("取消支付");
                    }
                    break;
                case Constant.SELECT_ADDRESS: //意向未达成界面的回调
                    if (data == null){
                        getData();
                        return;
                    }
                    UserDemand userDemand = (UserDemand)data.getSerializableExtra("UserDemand");
                    LinkedHashMap<String, String> praram = new LinkedHashMap<>();
                    praram.put("demandId",userDemand.getDemandId());
                    if (30 == demandState || 40 == demandState){
                        if ("0".equals(userDemand.getFlage())){  //平台不介入 申请退款
                            praram.put("memo",userDemand.getReasonString());
                        }else if ("1".equals(userDemand.getFlage())){ //平台介入 申请退款
                            praram.put("reason",userDemand.getReasonString());
                        }
                        setIntentionToInfor(praram);
                    }
                    break;
            }
        }
    }

    private void showDialog(String tips, String leftString, String rightString, RLAlertDialog.Listener listener) {
        new RLAlertDialog(this, getString(R.string.dialog_title), tips, leftString, rightString, listener).show();
    }

    /**
     * 删除买煤求购
     */
    private void delatData(String demandId) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("demandId", demandId);
            new DataUtils(this, params).getUserDemandDelete(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String dataMemager) {
                    try {
                        displayToast(getString(R.string.delete_procurement_success));
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class VerticalAdapter extends BaseAdapter {
        private Context mContext;
        private List<CoalOrderStatusEntity> mList = new ArrayList<>();

        public VerticalAdapter(Context context, List<CoalOrderStatusEntity> list) {
            this.mContext = context;
            this.mList = list;
        }

        @Override
        public int getCount() {
            if (!showMore) {
                return mList == null ? 0 : 1;
            } else {
                return mList == null ? 0 : mList.size();
            }
        }

        @Override
        public CoalOrderStatusEntity getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.time_line_item_virtual, parent, false);
            }
            TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            TextView tvTimeSS = (TextView) convertView.findViewById(R.id.tv_time_ss);
            TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            ImageView ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);
            View ivLineTop = convertView.findViewById(R.id.view_line_top);
            View ivLineBottom = convertView.findViewById(R.id.view_line_bottom);
            ivLineTop.setVisibility(View.GONE);
            ivLineBottom.setVisibility(View.GONE);
            CoalOrderStatusEntity coalOrderStatusEntity = getItem(position);

            String timeString = coalOrderStatusEntity.getCreateTime();
            tvTime.setText(DateUtil.strToStrMYType(timeString,"MM-dd"));
            tvTimeSS.setText(DateUtil.strToStrMYType(timeString,"HH:mm"));

            tvContent.setText(coalOrderStatusEntity.getMemo());
            ViewGroup.LayoutParams layoutParams = ivStatus.getLayoutParams();
            ivStatus.setImageResource(R.drawable.icon_status_2);
            layoutParams.width = DeviceUtil.dp2px(mContext, 15);
            layoutParams.height = DeviceUtil.dp2px(mContext, 15);
            ivStatus.setLayoutParams(layoutParams);
            if(position == 0) {
                try {
                    if (showMore) {
                        ivLineBottom.setVisibility(View.VISIBLE);
                        tvMore.setText("求购流程");
                        ivRefundProcess.setImageDrawable(getResources().getDrawable(R.drawable.sort_common_up));
                    }  else {
                        ivLineBottom.setVisibility(View.GONE);
                        tvMore.setText("求购流程");
                        ivRefundProcess.setImageDrawable(getResources().getDrawable(R.drawable.sort_common_down));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ivLineTop.setVisibility(View.GONE);
                tvTime.setTextColor(mContext.getResources().getColor(R.color.actionsheet_blue));
                tvTimeSS.setTextColor(mContext.getResources().getColor(R.color.actionsheet_blue));
                tvContent.setTextColor(mContext.getResources().getColor(R.color.actionsheet_blue));
            } else {
                tvTime.setTextColor(mContext.getResources().getColor(R.color.gary));
                tvTimeSS.setTextColor(mContext.getResources().getColor(R.color.gary));
                tvContent.setTextColor(mContext.getResources().getColor(R.color.gary));
                if (position == mList.size() - 1) {
                    ivLineTop.setVisibility(View.VISIBLE);
                    ivLineBottom.setVisibility(View.GONE);
                } else {
                    ivLineTop.setVisibility(View.VISIBLE);
                    ivLineBottom.setVisibility(View.VISIBLE);
                }

                ivStatus.setImageResource(R.drawable.icon_status_0);
                layoutParams.width = DeviceUtil.dp2px(mContext, 10);
                layoutParams.height = DeviceUtil.dp2px(mContext, 10);
                ivStatus.setLayoutParams(layoutParams);
            }
            return convertView;
        }
    }


    private long mDay = 10;
    private long mHour = 10;
    private long mMin = 30;
    private long mSecond = 00;// 天 ,小时,分钟,秒
    private boolean isRun = true;
    private Handler timeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 ) {
                computeTime();
                tvDeliceryEndTime.setText(mDay+"天" + mHour+"时" + mMin+"分"+ mSecond+"秒"+ "后过期");
                if (mDay==0&&mHour==0&&mMin==0&&mSecond==0) {
                    tvDeliceryEndTime.setVisibility(View.GONE);
                }
            }
        }
    };

    private  String dateDiff(String endTime) {
        String strTime = null;
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sd.format(curDate);
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime()
                    - sd.parse(str).getTime();
            mDay = diff / nd;// 计算差多少天
            mHour = diff % nd / nh;// 计算差多少小时
            mMin = diff % nd % nh / nm;// 计算差多少分钟
            mSecond = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (mDay >= 1) {
                strTime = mDay + "天" + mHour + "时"+ mMin + "分";
            } else {
                if (mHour >= 1) {
                    strTime = mDay + "天" + mHour + "时" + mMin + "分";
                } else {
                    if (mSecond >= 1) {
                        strTime = mDay + "天" + mHour + "时" + mMin + "分" + mSecond + "秒";
                    } else {
                        strTime = "即将到期";
                    }
                }
            }
            startRun();
            return strTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 开启倒计时
     */
    private void startRun() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isRun) {
                    try {
                        Thread.sleep(1000); // sleep 1000ms
                        Message message = Message.obtain();
                        message.what = 1;
                        timeHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 倒计时计算
     */
    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mMin--;
            mSecond = 59;
            if (mMin < 0) {
                mMin = 59;
                mHour--;
                if (mHour < 0) {
                    // 倒计时结束
                    mHour = 23;
                    mDay--;
                }
            }
        }
    }
}

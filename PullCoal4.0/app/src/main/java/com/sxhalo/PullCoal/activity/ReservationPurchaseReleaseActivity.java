package com.sxhalo.PullCoal.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.SelectedGrideViewAdapter;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.map.NaviRoutePlanning;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.popwin.CommonPopupWindow;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.RegexpUtils;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 买煤求购发布
 * Created by amoldZhang on 2016/12/22.
 */
public class ReservationPurchaseReleaseActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.coal_type_grid)
    NoScrollGridView coalTypeGrid;//煤种
    @Bind(R.id.purchase_name_et)
    EditText purchaseNameEt;//货物名称
    @Bind(R.id.et_calorific_value)
    EditText etCalorificValue;//发热量
    @Bind(R.id.et_sulphur_content)
    EditText etSuplhurContent;//含硫量
    @Bind(R.id.bookings_num_et)
    EditText bookingsNumEt;//预定量
    @Bind(R.id.bookings_price_et)
    EditText bookingsPriceEt;//预定价格
    @Bind(R.id.remarks_et)
    EditText remarksEt;
    @Bind(R.id.receiving_area)
    TextView receivingArea;

    @Bind(R.id.rl_bond_view)
    RelativeLayout rlBondView;  //保证金布局
    @Bind(R.id.bond_select_num)
    TextView bondSelectNum;  //保证金金额

    @Bind(R.id.rl_participant_view)
    RelativeLayout rlParticipantView;
    @Bind(R.id.participant_num)
    TextView participantNum;  //最多参与信息部个数


    private UserDemand userDemand;
    private ArrayList<String> bondList;//保证金集合
    private ArrayList<String> participantList;//可参与信息部个数集合

    private CommonPopupWindow popupWindow;

    List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
    private SelectedGrideViewAdapter selectedAdapter;
    //当前选中的煤种
    private int pos = 0;
    private String toCityCode = "-1";
    private LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_purchase_release);
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected void initTitle() {
        title.setText("发布求购");
    }

    @Override
    protected void getData() {
        bondList = getBondData();
        participantList = getParticipantData();
        getDataPath();
        initView();
        setNavi();
    }

    private void setNavi() {
        new PermissionUtil().requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                showProgressDialog("定位中，请稍后...");
                //定位
                new NaviRoutePlanning(ReservationPurchaseReleaseActivity.this, new NaviRoutePlanning.NaviDataCoalBack() {
                    @Override
                    public void getNaviData(AMapLocation loc) {
                        dismisProgressDialog();
                        toCityCode = loc.getAdCode() + "000";
                        receivingArea.setText(loc.getProvince() + loc.getCity() + loc.getDistrict());
                    }
                }).startLocation();
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }

    /**
     * 界面空件初始化
     */
    private void initView() {
        coalTypeGrid.setNumColumns(2);
        selectedAdapter = new SelectedGrideViewAdapter(this, mapList);
        coalTypeGrid.setAdapter(selectedAdapter);
        coalTypeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAdapter.setSeclection(position);
                pos = position;
            }
        });
        etSuplhurContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String string = etSuplhurContent.getText().toString().trim();
                if (!StringUtils.isEmpty(string) && !hasFocus) {
                    float f1 =Float.parseFloat(string);
                    etSuplhurContent.setText(f1 + "");
                }
            }
        });
        etSuplhurContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //删除“.”后面超过2位后的数据
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etSuplhurContent.setText(s);
                        etSuplhurContent.setSelection(s.length()); //光标移到最后
                    }
                }

                //如果"."在起始位置,则起始位置自动补0
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etSuplhurContent.setText(s);
                    etSuplhurContent.setSelection(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    @OnClick({R.id.cancel, R.id.submit, R.id.title_bar_left, R.id.rl_area,R.id.rl_bond_view,R.id.rl_participant_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.rl_area:
                Intent intent = new Intent(this, SelectAreaActivity.class);
//                intent.putExtra("level", 2);
                startActivityForResult(intent, Constant.AREA_CODE);
                break;
            case R.id.rl_bond_view: //保证金额选择空件
                initPopWindow(bondSelectNum);
                break;
            case R.id.rl_participant_view: //最多参与信息部选择空件
                initPopWindow(participantNum);
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.submit:
                //为防止重复提交 当点击间隔小于1秒时 当做同一点击事件
                //只有点击时间间隔在1秒及以上时 才认为是两次事件
                if ("0".equals(toCityCode)) {
                    displayToast(getString(R.string.select_area_tips));
                    return;
                }
                long curClickTime = System.currentTimeMillis();
                if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                    lastClickTime = curClickTime;
                    checkEmpty();
                }
                break;
        }
    }

    /**
     * 初始化弹框
     * @param showWiew
     */
    private void initPopWindow(final TextView showWiew){
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
                //设置PopupWindow布局
                .setView(R.layout.purchase_release_popwin_view)
                //设置宽高
                .setWidthAndHeight(showWiew.getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT)
//                //设置动画
//                .setAnimationStyle(R.style.AnimDown)
                //设置背景颜色，取值范围0.0f-1.0f 值越小越暗 1.0f为透明
                .setBackGroundLevel(0.5f)
                //设置PopupWindow里的子View及点击事件
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        ArrayList<String> data;
                        if (showWiew == bondSelectNum){
                            data = bondList;
                        }else{
                            data = participantList;
                        }
                        setChildView(showWiew,view,data);
                    }
                })
                //设置外部是否可点击 默认是true
                .setOutsideTouchable(true)
                //开始构建
                .create();
        int height;
        if (showWiew == bondSelectNum){
            height = - bondList.size() * (popupWindow.getHeight() + showWiew.getTop() ) + 100;
        }else{
            height = - participantList.size() * (popupWindow.getHeight() + showWiew.getTop());
        }
//        //弹出PopupWindow
        popupWindow.showAsDropDown(showWiew,0,height);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if ("无保证金".equals(bondSelectNum.getText().toString().trim())){
                    rlParticipantView.setVisibility(View.GONE);
                }else{
                    rlParticipantView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 弹框数据展示布局
     * @param showWiew
     * @param view
     * @param data
     */
    private void setChildView(final TextView showWiew,View view,final ArrayList<String> data) {
        //获得PopupWindow布局里的View
        ListView listView = (ListView)view.findViewById(R.id.listview);
        QuickAdapter mAdapter = new QuickAdapter<String>(this, R.layout.purchase_release_popwin_view_item,data) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item, int pos) {
                try{
                    if ("0".equals(item) || "".equals(item)){
                        helper.setText(R.id.text_view,"无保证金");
                    }else{
                        helper.setText(R.id.text_view,item);
                    }

                }catch (Exception e){
                    GHLog.e(getClass().getName(),e.toString());
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                }
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (popupWindow != null){
                    if (showWiew == bondSelectNum){
                        if ("0".equals(data.get(position)) || "".equals(data.get(position))){
                            showWiew.setText("无保证金");
                        }else{
                            showWiew.setText(data.get(position) + "元");
                        }
                    }else{
                        showWiew.setText(data.get(position) + "");
                    }
                    popupWindow.dismiss();
                }
            }
        });
    }


    /**
     * 获取保证金额度数目列表
     * @return
     */
    private ArrayList<String> getBondData(){
        //筛选数据
        ArrayList<String> bondList = new ArrayList<String>();
        bondSelectNum.setText("无保证金");
        bondList.add("0");
        bondList.add("100");
        bondList.add("200");
        bondList.add("300");
        bondList.add("500");
        return bondList;
    }

    /**
     * 获取最多参与信息部列表
     * @return
     */
    private ArrayList<String> getParticipantData(){
        //筛选数据
        ArrayList<String> participantList = new ArrayList<String>();
        Dictionary sys100009 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100009"}).get(0);
        int maxDelivery = 1;
        for (FilterEntity filterEntity : sys100009.list){
            if ("max_delivery".equals(filterEntity.dictCode)){
                maxDelivery = Integer.valueOf(filterEntity.dictValue);
                break;
            }
        }
        participantNum.setText("1"+"/家");
        for (int i = 1;i <= maxDelivery;i++){
            participantList.add(i + "/家");
        }
        return participantList;
    }

    /**
     * 校验必填项
     */
    private void checkEmpty () {
        if (StringUtils.isEmpty(purchaseNameEt.getText().toString().trim())) {
            //校验名称是否为空
            displayToast(getString(R.string.input_procurement_name));
            return;
        }
        if (StringUtils.isEmpty(etCalorificValue.getText().toString().trim())) {
            //校验发热量是否为空
            displayToast(getString(R.string.input_calorific_value));
            return;
        }
        if (!RegexpUtils.validatePositiveInteger(etCalorificValue.getText().toString().trim())) {
            //请输入正确的发热量
            displayToast(getString(R.string.input_correct_calorific_value));
            return;
        }
        if (StringUtils.isEmpty(etSuplhurContent.getText().toString().trim())) {
            //校验含硫量是否为空
            displayToast(getString(R.string.input_suplhur_content));
            return;
        }
        String text = etSuplhurContent.getText().toString().trim();
        if (text.equals("0") || text.equals("0.0") || text.equals("0.00")) {
            //请输入正确的含硫量
            displayToast(getString(R.string.input_correct_suplhur_content));
            return;
        }
        if (StringUtils.isEmpty(bookingsNumEt.getText().toString().trim())) {
            //校验预定量是否为空
            displayToast(getString(R.string.input_procurement_number));
            return;
        }
        int num = Integer.parseInt(bookingsNumEt.getText().toString().trim());
        if (num < 1) {
            //校验预定量是否合法
            displayToast(getString(R.string.procurement_number_min));
            return;
        }
        if (bookingsNumEt.getText().toString().trim().length()>8) {
            //校验预定量是否合法
            displayToast(getString(R.string.procurement_number_max));
            return;
        }
        if (StringUtils.isEmpty(bookingsPriceEt.getText().toString().trim())) {
            //校验预定价格是否为空
            displayToast(getString(R.string.input_procurement_price));
            return;
        }
        int price = Integer.parseInt(bookingsPriceEt.getText().toString().trim());
        if (price <= 0) {
            //校验预定价格是否合法
            displayToast(getString(R.string.input_legally_price));
            return;
        }
        if (bookingsPriceEt.getText().toString().trim().length() > 8 ) {
            //校验预定价格是否合法
            displayToast(getString(R.string.procurement_price_max));
            return;
        }
        if (toCityCode.equals("-1")) {
            //校验收货地区是否为空
            displayToast(getString(R.string.input_consignee_area));
            return;
        }
        if ("无保证金".equals(bondSelectNum.getText().toString())){
            dataRelease();
        }else{ //有保证金
            //联网获取认证状态
            getSelfAuthentication();
        }
    }

    /**
     * 买家认证实时获取
     */
    private void getSelfAuthentication() {
        try {
            new DataUtils(this, new LinkedHashMap<String, String>()).getUserRealnameAuthInfo(new DataUtils.DataBack<UserAuthenticationEntity>() {
                @Override
                public void getData(UserAuthenticationEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        checkStatus(StringUtils.isEmpty(dataMemager.getAuthState()) ? "100" : dataMemager.getAuthState());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("煤种赋值", e.toString());
        }
    }

    /**
     * 校验用户状态 只有实名用户才可以委托货运
     */
    private void checkStatus(String cerrtificationState) {
        //0审核中1审核成功2审核失败 100未提交
        switch (Integer.valueOf(cerrtificationState)) {
            case 100: //未提交
                showDaiLog(this, getString(R.string.unable_real_name_the_authentication));
                break;
            case 0: //审核中
                displayToast(getString(R.string.under_review));
                break;
            case 1: //审核成功
                showBondDlog(bondSelectNum.getText().toString());
                break;
            case 2: //审核失败
                showDaiLog(this, getString(R.string.submit_under_review_failed));
                break;
        }
    }

    private void showDaiLog(Activity mActivity, String message) {
        new RLAlertDialog(mActivity, "系统提示", message, "关闭",
                "立刻前往", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                startActivity(new Intent(ReservationPurchaseReleaseActivity.this, BuyerCertificationActivity.class));
            }
        }).show();
    }

    /**
     * 获取系统煤种名称
     */
    private void getDataPath() {
        try {
            Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100002"}).get(0);

            for (int i = 0; i < coalTypes.list.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", coalTypes.list.get(i).dictValue);
                map.put("id", coalTypes.list.get(i).dictCode);
                mapList.add(map);
            }
        } catch (Exception e) {
            GHLog.e("煤种赋值", e.toString());
        }
    }

    /**
     * 买煤求购信息联网提交
     */
    private void dataRelease() {
        try {
            setParameter();
            new DataUtils(this,params).getUserDemandCreate(new DataUtils.DataBack<UserDemand>() {
                @Override
                public void getData(UserDemand dataMemager) {
                    try {
                        if (dataMemager == null) {
                            displayToast(getString(R.string.submit_procurement_failed));
                            return;
                        }
                        userDemand = dataMemager;
                        if ("无保证金".equals(bondSelectNum.getText().toString())){
                            displayToast(getString(R.string.submit_procurement_success));
                            setResult(RESULT_OK);
                            sendBroadcast();
                            finish();
                        }else{
                            //有保证金
                            Intent intent = new Intent(ReservationPurchaseReleaseActivity.this,MarginPaymentActivity.class);
                            intent.putExtra("UserDemand",dataMemager);
                            startActivityForResult(intent,Constant.PAYMENT_CODE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    boolean flage = false; // 保证金规则是否已经阅读
    private void showBondDlog(String num) {
        LayoutInflater inflater1 = this.getLayoutInflater();
        View layout = inflater1.inflate(R.layout.layout_bond_dialog, null);
        TextView bondTv =  ((TextView) layout.findViewById(R.id.bond_text));
        String message = "您已选择支付  “"+num+"”  保证金发布买煤求购。确认信息部时，请尽快先电话联络。发布买煤求购信息有时效限制，具体参考《保证金规则须知》。";
        SpannableString spannableStringIntent = new SpannableString(message);
        ForegroundColorSpan foregroundColorSpanNum=new ForegroundColorSpan(Color.parseColor("#ff5b46"));
        spannableStringIntent.setSpan(foregroundColorSpanNum,9,13,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpanIntent = new MyClickableSpan("《保证金规则须知》");
        spannableStringIntent.setSpan(clickableSpanIntent,message.length() - 10,message.length()-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan foregroundColorSpanIntent=new ForegroundColorSpan(Color.parseColor("#1e7aea"));
        spannableStringIntent.setSpan(foregroundColorSpanIntent,message.length() - 10,message.length()-1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        bondTv.setText(spannableStringIntent);
        bondTv.setMovementMethod(LinkMovementMethod.getInstance());
        final RLAlertDialog rlAlertDialog =  new RLAlertDialog(this,"用户提示",layout,"不同意",
                "同意", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                if (flage){
                    dataRelease();
                }else{
                    displayToast("您未阅读保证金规则须知");
                }
            }
        });
        final CheckBox checkbox =  ((CheckBox) layout.findViewById(R.id.checkbox));
        final TextView userRegister =  ((TextView) layout.findViewById(R.id.register_user_agreement));
        checkbox.setChecked(false);
//        rlAlertDialog.getRightBtn().setTextColor(getResources().getColor(R.color.gary));
//        rlAlertDialog.getRightBtn().setEnabled(false);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    displayToast("已阅读");
                    userRegister.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    flage = true;
//                    rlAlertDialog.getRightBtn().setTextColor(getResources().getColor(R.color.app_title_text_color));
//                    rlAlertDialog.getRightBtn().setEnabled(true);
                }else{
                    displayToast("未阅读");
                    userRegister.setTextColor(getResources().getColor(R.color.gary));
                    flage = false;
//                    rlAlertDialog.getRightBtn().setTextColor(getResources().getColor(R.color.gary));
//                    rlAlertDialog.getRightBtn().setEnabled(false);
                }
            }
        });
        rlAlertDialog.show();
    }

    class MyClickableSpan extends ClickableSpan{

        private String content;

        public MyClickableSpan(String content){
            this.content=content;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);//去掉下划线
        }

        @Override
        public void onClick(View view) {
            Intent intent=new Intent(ReservationPurchaseReleaseActivity.this,WebViewActivity.class);
            intent.putExtra("URL", new Config().getCOUPONS_NOTICE_RULE_OF_MARGIN());
            intent.putExtra("title", "保证金规则须知");
            startActivity(intent);
        }
    }

    /**
     * 获取用户输入信息
     */
    private void setParameter() {
        params.clear();
        //收货地区
        params.put("regionCode", toCityCode);
        //煤种
        params.put("categoryId", mapList.get(pos).get("id"));
        //货物名称
        params.put("coalName", purchaseNameEt.getText().toString().trim());
        //发热量
        params.put("calorificValue", etCalorificValue.getText().toString().trim());
        //含硫量
        params.put("totalSulfur", etSuplhurContent.getText().toString().trim());
        //预定量
        params.put("number", bookingsNumEt.getText().toString().trim());
        //预定价格
        params.put("price", bookingsPriceEt.getText().toString().trim());
        //保证金
        if ("无保证金".equals(bondSelectNum.getText().toString())){
            //保证金
//            params.put("bond", "0");
        }else{
            //保证金
            params.put("bond", bondSelectNum.getText().toString().replace("元","00"));
            //最多参与信息部
            params.put("maxDeliveryNum", participantNum.getText().toString().replace("/家",""));
        }

        //购买说明
        if (!TextUtils.isEmpty(remarksEt.getText())) {
            params.put("illustrate", remarksEt.getText().toString().trim());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            switch (requestCode) {
                case Constant.AREA_CODE:
                    if (data != null) {
                        toCityCode = data.getStringExtra("code");
                        receivingArea.setText(data.getStringExtra("name"));
                    }
                    break;
                case Constant.PAYMENT_CODE: //支付界面的回调
                    if (data == null) {
                    //支付成功回调
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        flage = false;
                    //取消支付回调
                        deleteOrder(userDemand);
                    }
                    break;
            }
        }
    }

    /**
     * 删除买煤求购
     */
    private void deleteOrder(final UserDemand userDemandEntity) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("demandId", userDemandEntity.getDemandId());
            new DataUtils(ReservationPurchaseReleaseActivity.this, params).getUserDemandDelete(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String dataMemager) {
                    //因为没有支付保证金，所以此处需要将该生成的订单删除
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交成功，发送广播，订单列表刷新
     */
    private void sendBroadcast() {
        Intent intent = new Intent(Constant.UPDATE_MY_RELEASE_FRAGMENT + "");
        sendBroadcast(intent);
    }
}

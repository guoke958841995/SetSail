package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.EscrowAccount;
import com.sxhalo.PullCoal.model.Orders;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.NoLineClickSpan;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.SELECT_ADDRESS;

/**
 * 下订单界面
 * Created by liz on 2018/5/8.
 */

public class PlaceOrderActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    /*****************************/
    @Bind(R.id.tv_information_department)
    TextView tvInformationDepartmentName;//信息部名称
    @Bind(R.id.coal_image)
    ImageView coalImage;//煤炭图片
    @Bind(R.id.tv_coal_name)
    TextView tvCoalName;//煤炭名称
    @Bind(R.id.tv_calorific_value)
    TextView tvCaloroficValue;//发热量
    @Bind(R.id.tv_mouth_value)
    TextView tvmouthValue;//矿口名称
    /*****************************/
    @Bind(R.id.layout_unselecte)
    RelativeLayout layoutUnselect;//默认的选择地址的视图
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;//是否协助找车
    @Bind(R.id.layout_selecte)
    RelativeLayout layoutSelect;//选择过地址后的视图
    @Bind(R.id.divider_address)
    View dividerAddress;//分割线
    @Bind(R.id.tv_warehouse)
    TextView tvWarehouse;//地址仓库标签
    @Bind(R.id.tv_receive_address)
    TextView tvReceiveAddress;//收货地址
    /*****************************/
    @Bind(R.id.tv_order_price)
    TextView tvOrderPrice;//预订价
    @Bind(R.id.et_order_number)
    EditText etOrderNumber;//预定量
    @Bind(R.id.tv_order_toal_price)
    TextView tvOrderToalPrice;//预订金额
    @Bind(R.id.tv_coal_balance)
    TextView tvCoalBalance;//煤款余额
    @Bind(R.id.coal_balance_view)
    LinearLayout coalBalanceView;//煤款不足，前去充值
    @Bind(R.id.tv_coal_empower_amount)
    TextView tvEmpowerAmount;//煤款白条
    @Bind(R.id.empower_amount_view)
    LinearLayout empowerAmountView;//煤款白条布局
    /*****************************/
    @Bind(R.id.tv_receiver)
    TextView tvReciver;//收货人
    @Bind(R.id.tv_receive_phone)
    TextView tvReceivePhone;//收货电话
    /*****************************/
    @Bind(R.id.et_remark)
    EditText etRemark;//备注
    /*****************************/
    @Bind(R.id.tv_declare_5)
    TextView tvDeclare5;//说明

    //控制选择收货地址显示的样式  0 表示仅显示 选择收货地址的视图  1 表示显示仓库及详细地址的视图
    private int showStatus = 0;

    private boolean isSelfCar = false;//是否协助找车的标记 默认为false
    private Coal coal;//煤炭数据实体
    private UserAddress userAddress;//选择收货地址返回的数据实体
    private String receiver;//sp文件或选择地址后返回的姓名
    private String receivePhone;//sp文件或选择地址后返回的电话号码
    private String orderNumber = "";//预定量

    private String coalBalance = "0.00";  //煤款
    private String empowerAmount = "0.00";  //煤款白条
    private double orderToalPrice = 0.00;   // 预订金额

    private boolean orderDoing = false;  //获取有没有进行中的订单

    public APPDataList<Orders> dataOrders; // 所有进行中的订单

    private Handler handler = new Handler();

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            if (!StringUtils.isEmpty(orderNumber)){
                Double number = Double.valueOf(orderNumber);
                Double pice = Double.valueOf(coal.getOneQuote());
                orderToalPrice = number * pice;
                tvOrderToalPrice.setText(StringUtils.fmtMicrometer((StringUtils.setNumLenth(Float.valueOf(orderToalPrice + ""), 2)))+ " 元");
                checkOrderBalance();
            }else{
                tvOrderToalPrice.setText("0.00"+ " 元");
            }
        }
    };

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_place_order);
    }

    @Override
    protected void initTitle() {
        title.setText("下预订单");
        coal = (Coal) getIntent().getSerializableExtra("Coal");
        initView();
    }

    private void initView() {
        tvInformationDepartmentName.setText(coal.getCompanyName());

            if (Utils.isImagesTrue(coal.getImageUrl()) == 200 ){
                getImageManager().loadCoalTypeUrlImage(coal.getImageUrl(),coal.getCategoryImage(),coalImage);
            }else{
                getImageManager().loadCoalTypeUrlImage(coal.getCategoryImage(),coalImage);
            }

        tvCoalName.setText(coal.getCoalName());
        tvCaloroficValue.setText(coal.getCalorificValue() + " kCal/kg");
        tvmouthValue.setText(coal.getMineMouthName());


        tvOrderPrice.setText(coal.getOneQuote() + " 元/吨");

        setTextVeiew();

        etOrderNumber.addTextChangedListener(new TextWatcher() {
            int mPreviousLength; //监听输入框内容改变之前的文本长度
            int index ;//标记edittext的光标位置
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mPreviousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                etOrderNumber.removeTextChangedListener(this);// 解除文字改变事件
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                index = etOrderNumber.getSelectionStart();// 获取光标位置
                //检测用户是否点击了删除操作
                boolean mBackSpace = mPreviousLength > s.length();
                //当光标处在输入框最后位置处，并且用户做了删除操作
                if (!StringUtils.isEmpty(s.toString()) && !s.toString().contains("吨") && mBackSpace){
                    index = index - 1;
                    orderNumber = orderNumber.substring(0,s.toString().length() - 1);
                }else{
                    orderNumber = s.toString().replace("吨","").trim();
                }

                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 800);
                //重新给输入框赋值
                if (StringUtils.isEmpty(orderNumber)){
                    etOrderNumber.setText("");
                }else{
                    etOrderNumber.setText(orderNumber+"吨");
                }
                etOrderNumber.setSelection(index);// 重新设置光标位置
                etOrderNumber.addTextChangedListener(this);// 重新绑定事件
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_yes:
                        //协助派车
                        if (isSelfCar) {
                            return;
                        }
                        new RLAlertDialog(mContext, "温馨提示", "订单受理后将发布一条货运信息", "取消",
                                "确定", new RLAlertDialog.Listener() {
                            @Override
                            public void onLeftClick() {
                                ((RadioButton)findViewById(R.id.rb_no)).setChecked(true);
                            }

                            @Override
                            public void onRightClick() {
                                isSelfCar = true;
                                if (showStatus == 0) {
                                    layoutUnselect.setVisibility(View.VISIBLE);
                                } else {
                                    layoutSelect.setVisibility(View.VISIBLE);
                                }
                                dividerAddress.setVisibility(View.VISIBLE);
                            }
                        }).show();
                        break;
                    case R.id.rb_no:
                        //不协助
                        if (!isSelfCar) {
                            return;
                        }
                        isSelfCar = false;
                        layoutSelect.setVisibility(View.GONE);
                        layoutUnselect.setVisibility(View.GONE);
                        dividerAddress.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @Override
    protected void getData() {
        getUserEscrowAccount();
        getUserData();
        getOdrderDoing();
    }

    /**
     * 获取当前用户信息
     */
    private void getUserData() {
        try {
            new DataUtils(mContext).getUserInfo(new DataUtils.DataBack<UserEntity>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        receiver = dataMemager.getRealName();
                        receivePhone = dataMemager.getUserMobile();

                        tvReciver.setText(receiver);
                        tvReceivePhone.setText(receivePhone);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网获取当前用户状态", e.toString());
        }
    }


    /**
     * 获取当前用户有没有进行中的订单
     */
    private void getOdrderDoing() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("orderState", "0");
            params.put("currentPage","1"); //
            params.put("pageSize", "-1"); // -1 返回所有  10 当前页数的条数
            new DataUtils(this, params).getUserCoalOrderList(new DataUtils.DataBack<APPDataList<Orders>>() {

                @Override
                public void getData(APPDataList<Orders> dataOrders) {
                    try {
                        if (dataOrders == null) {
                            return;
                        }
                        PlaceOrderActivity.this.dataOrders = dataOrders;
                        if (dataOrders.getList() != null && dataOrders.getList().size() != 0){
                            orderDoing = true;
                            empowerAmountView.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网获取当前用户状态", e.toString());
        }
    }


    /**
     * 获取当前用户煤款数
     */
    private void getUserEscrowAccount() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this, params).getUserEscrowAccount(new DataUtils.DataBack<EscrowAccount>() {
                @Override
                public void getData(EscrowAccount appData) {
                    try {
                        if (appData == null) {
                            return;
                        }

                        //煤款
                        coalBalance = (StringUtils.setNumLenth(Float.valueOf(appData.getUncashAmount())/100, 2));
                        String coalBalance1 = StringUtils.fmtMicrometer(coalBalance);
                        if ("0.00".equals(coalBalance1) || coalBalance1.contains("-")){
                            coalBalanceView.setVisibility(View.VISIBLE);
                        }else{
                            coalBalanceView.setVisibility(View.GONE);
                        }
                        tvCoalBalance.setText(coalBalance1 + "元");

                        //白条
                        empowerAmount = (StringUtils.setNumLenth(Float.valueOf(appData.getEmpowerAmount())/100, 2));
                        String empowerAmountView1 = StringUtils.fmtMicrometer(empowerAmount);
                        if (!"0.00".equals(empowerAmountView1) && !coalBalance.contains("-")){
                            empowerAmountView.setVisibility(View.VISIBLE);
                            tvEmpowerAmount.setText(empowerAmountView1 + "元");
                        }else{
                            empowerAmountView.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        GHLog.e("煤款托管账户", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
//                    if (!"".equals(e.getMessage())){
//                        displayToast(e.getCause().getMessage());
//                    }
                    GHLog.e("煤款托管账户", e.toString());
                }
            },true);
        } catch (Exception e) {
            GHLog.e("联网获取当前用户状态", e.toString());
        }
    }

    @OnClick({R.id.title_bar_left, R.id.place_order_submission, R.id.coal_balance_view, R.id.layout_unselecte, R.id.layout_selecte})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.place_order_submission:
                // 提交订单
                if (checkOrderBalance()){
                    //进行中的订单中是否有白条订单
                    boolean flage = false;
                    for (Orders orders : dataOrders.getList()){
                        if ("1".equals(orders.getIouUse())){
                            flage = true;
                        }
                    }
                    // 有进行中的白条订单，不能下单
                    if (flage){
                        new RLAlertDialog(this, "消息提示", "您有进行中的白条订单，请完成后再来！", "确定",
                                "", new RLAlertDialog.Listener() {
                            @Override
                            public void onLeftClick() {
                                finish();
                            }

                            @Override
                            public void onRightClick() {

                            }
                        }).show();
                    }else{
                        if (!checkParams()) {
                            return;
                        }
                    }
                }
                break;
            case R.id.coal_balance_view: //煤款不足，前去充值
                checkOrderBalance();
                break;
            case R.id.layout_unselecte:
            case R.id.layout_selecte:
                // 跳转至选择地址界面
                startActivityForResult(new Intent(this, UpDataAddressActivity.class), SELECT_ADDRESS);
                break;
        }
    }



    /**
     * 校验是否和当前的煤炭存在未完成或者未取消的订单
     */
    private void checkOrderStatus() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("releaseNum", coal.getReleaseNum());
            new DataUtils(this, params).chackCoalOrderStatus(new DataUtils.DataBack<APPData>() {
                @Override
                public void getData(APPData data) {
                    try {
                        submitOrder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2080004".equals(e.getMessage())) {
                        showDialog();
                    } else if ("2060032".equals(e.getMessage())) {
                        displayToast("账户异常，请联系客服！");
                    }else{
                        displayToast("网络连接异常");
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }


    /**
     * 检验煤款是否充足
     *
     *  private String coalBalance;  //煤款 + 煤款白条
     *  private double orderToalPrice;   // 预订金额
     * @return
     */
    private boolean checkOrderBalance(){
        Double coalBalance1;
        //1.当有进行中的订单时，不能使用白条
        if (orderDoing || coalBalance.contains("-")){
            coalBalance1 = Double.valueOf(coalBalance);
            empowerAmountView.setVisibility(View.GONE);
        }else{
            coalBalance1 = Double.valueOf(coalBalance)+ Double.valueOf(empowerAmount);
        }

        if (orderToalPrice <= coalBalance1 && 0.00 != coalBalance1){
            coalBalanceView.setVisibility(View.INVISIBLE);
            return true;
        }else{
            coalBalanceView.setVisibility(View.VISIBLE);
            new RLAlertDialog(this, "消息提示", "您的煤款不足，请充值！", "取消",
                    "联系客服", new RLAlertDialog.Listener() {
                @Override
                public void onLeftClick() {
                }

                @Override
                public void onRightClick() {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tel", getResources().getString(R.string.service_hot_line));
                    map.put("callType", Constant.CALE_TYPE_COAL_RECHARGE);
                    map.put("targetId", SharedTools.getStringValue(mContext,"userId","-1"));
                    UIHelper.showCollTel(mContext, map, true);
                }
            }).show();
        }
        return false;
    }

    /**
     * 提交订单
     */
    private void submitOrder() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            setParams(params);
            new DataUtils(this, params).getUserCoalGoodsCreate(new DataUtils.DataBack<List<Orders>>() {
                @Override
                public void getData(List<Orders> dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
                        Orders data = dataList.get(0);
                        Intent intent = new Intent();
                        intent.setClass(PlaceOrderActivity.this, CoalOrderDetailActivity.class);
                        intent.putExtra("orderNumber", data.getOrderNumber());
                        startActivity(intent);
                        sendBroadcast();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2060030".equals(e.getMessage())){
                        displayToast("账户异常，请联系客服！");
                    } else {
                        displayToast("网络连接异常");
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    private void setParams(LinkedHashMap<String, String> params) {
        // 煤炭发布编号
        params.put("releaseNum", coal.getReleaseNum());
        //预定量
        params.put("tradingVolume", orderNumber);
        //联系人
        params.put("contactPerson", receiver);
        //联系电话
        params.put("contactPhone", receivePhone);
        //是否协助派车 01 是  00 否
        if (isSelfCar) {
            params.put("transportType", "01");
            //协助派车时需要传收货地行政区划和收货详细地址两个参数
            params.put("regionCode", userAddress.getRegionCode());
            params.put("receiptAddress", userAddress.getAddress());
        } else {
            params.put("transportType", "00");
        }
        //备注
        params.put("remark", etRemark.getText().toString().trim());
    }

    /**
     * 校验下订单请求参数是否完整
     */
    private boolean checkParams() {
        boolean canSubmit = true;
//        orderNumber = etOrderNumber.getText().toString().replace("吨","").trim();
        if (StringUtils.isEmpty(orderNumber)) {
            displayToast(getString(R.string.input_order_number));
            return false;
        }
        int number = Integer.parseInt(orderNumber);
        if (number <= 0) {
            displayToast(getString(R.string.illegally_order_number));
            return false;
        }
        if (isSelfCar && userAddress == null) {
            displayToast(getString(R.string.select_area_first));
            return false;
        }
        if (canSubmit){
            new RLAlertDialog(this,
                    "温馨提示",
                    "订单提交后，平台将自动冻结您本次煤炭预订金额 "+ StringUtils.fmtMicrometer((StringUtils.setNumLenth(Float.valueOf(orderToalPrice + ""), 2)))+ " 元，是否继续？", "取消", "继续",
                    new RLAlertDialog.Listener() {
                        @Override
                        public void onLeftClick() {
                        }

                        @Override
                        public void onRightClick() {
                            checkOrderStatus();
                        }
                    }).show();
        }
        return canSubmit;
    }

    /**
     * 给底部说明文字中设置样式及点击事件
     */
    private void setTextVeiew() {
        SpannableString spannableString = new SpannableString(getString(R.string.string_coal_order_tips_5));
        NoLineClickSpan noLineClickSpan = new NoLineClickSpan(mContext);
        noLineClickSpan.setMyClickListener(new NoLineClickSpan.MyClickListener() {
            @Override
            public void onClick() {
                String URL = new Config().getCOUPONS_NOTICE_BUY_COAL_LONLINE();
                UIHelper.showWEB(PlaceOrderActivity.this, URL, "在线买煤常见问题说明");
            }

            @Override
            public void setTextColorAndUnderline(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.actionsheet_blue));// 设置字体颜色
                ds.setUnderlineText(false); //去掉下划线
            }
        });
        spannableString.setSpan(noLineClickSpan, 18, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDeclare5.setText(spannableString);
        tvDeclare5.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 重复订单时，给用户提示
     */
    private void showDialog() {
        new RLAlertDialog(this, "温馨提示", "您和卖家的这个煤炭有进行中的订单，是否继续？", "取消", "继续",
                new RLAlertDialog.Listener() {
                    @Override
                    public void onLeftClick() {

                    }

                    @Override
                    public void onRightClick() {
                        submitOrder();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case SELECT_ADDRESS:
                    showStatus = 1;
                    layoutUnselect.setVisibility(View.GONE);
                    layoutSelect.setVisibility(View.VISIBLE);
                    userAddress = (UserAddress) data.getSerializableExtra("addressEntity");
                    tvReceiveAddress.setText(userAddress.getAddress());//详细地址
                    receiver = userAddress.getContactPerson();
                    receivePhone = userAddress.getContactPhone();
                    tvReciver.setText(receiver);
                    tvReceivePhone.setText(receivePhone);
                    tvWarehouse.setText(userAddress.getAddressName());//仓库标签
                    break;
            }
        }
    }

    /**
     * 提交成功，发送广播，订单列表刷新
     */
    private void sendBroadcast() {
        Intent intent = new Intent(Constant.UPDATE_COAL_ORDER_FRAGMENT + "");
        sendBroadcast(intent);
        AppManager.getAppManager().finishActivity(mContext);
        AppManager.getAppManager().finishActivity(DetailsWebActivity.class);
        AppManager.getAppManager().finishActivity(BuyCoalActivity.class);
    }

}

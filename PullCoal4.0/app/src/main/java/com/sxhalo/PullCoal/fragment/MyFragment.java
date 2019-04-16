package com.sxhalo.PullCoal.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.AccountDetailsActivity;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.BuyerCertificationActivity;
import com.sxhalo.PullCoal.activity.BuyerRecordsActivity;
import com.sxhalo.PullCoal.activity.CustomerServiceActivity;
import com.sxhalo.PullCoal.activity.DriverCertificationActivity;
import com.sxhalo.PullCoal.activity.DriverRegistrationActivity;
import com.sxhalo.PullCoal.activity.FriendActivity;
import com.sxhalo.PullCoal.activity.GuestRegistrationActivity;
import com.sxhalo.PullCoal.activity.InformationDepartmentActivity;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.MessageCenterActivity;
import com.sxhalo.PullCoal.activity.MyBalanceActivity;
import com.sxhalo.PullCoal.activity.MyCouponsActivity;
import com.sxhalo.PullCoal.activity.PaymentRecordActivity;
import com.sxhalo.PullCoal.activity.PersonUpDataActivity;
import com.sxhalo.PullCoal.activity.RegisterAddLoginActivity;

import com.sxhalo.PullCoal.activity.SettingActivity;
import com.sxhalo.PullCoal.activity.UpDataAddressActivity;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.StatusBarUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullHeadViewLayout;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.amoldzhang.library.ObservableScrollView;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.PullToRefreshScrollView;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liz on 2017/4/11.
 */
public class MyFragment extends Fragment {

    @Bind(R.id.my_nested_scroll_view)
    PullToRefreshScrollView myNestedScrollView;

    @Bind(R.id.layout_header)
    RelativeLayout layoutHeader;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_message)
    ImageView ivMessage;
    @Bind(R.id.iv_red_point)
    ImageView ivRedPoint;
    @Bind(R.id.view_header)
    View viewHeader;

    @Bind(R.id.iv_head)
    CircleImageView ivHead;
    @Bind(R.id.tv_register)
    TextView tvRegister;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_nick_name)
    TextView tvNickName;
    //    @Bind(R.id.iv_arrow)
//    ImageView ivArrow;
    @Bind(R.id.tv_coal_money)
    TextView tvCoalMoney;
    @Bind(R.id.tv_freezing_amount)
    TextView tvFreezingAmount;
    @Bind(R.id.tv_refundable_amount)
    TextView tvRefundableAmount;

    @Bind(R.id.tv_coupons_number)
    TextView tvCouponsNumber;
    @Bind(R.id.tv_balance)
    TextView tvBalance;
    @Bind(R.id.layout)
    RelativeLayout layout;

    @Bind(R.id.tv_real_name_status)
    TextView tvRealNameStatus;
    @Bind(R.id.tv_real_name_tips)
    TextView tvRealNameStatusTips;
    @Bind(R.id.tv_driver_status)
    TextView tvDriverStatus;
    @Bind(R.id.tv_driver_status_tips)
    TextView tvDriverStatusTips;

    private String userId;
    private BaseActivity mActivity;
    public UserEntity users;

    private int myNestedScrollViewScrollY = 0; //myNestedScrollView 的当前滑动距离

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        mActivity = (BaseActivity) getActivity();
        // 设置固定大小的占位符
        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) layoutHeader.getLayoutParams(); //取控件View当前的布局参数
        linearParams.height = StatusBarUtils.getStatusBarHeight(mActivity) + BaseUtils.dip2px(mActivity,50f);// 控件的高强制设成当前手机状态栏高度
        layoutHeader.setLayoutParams(linearParams); //使设置好的布局参数应用到控件</pre>
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userId = SharedTools.getStringValue(mActivity, "userId", "-1");
        setInItView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            if (!userId.equals("-1")) {
                getPersonal();
            }
        }
    }

    @SuppressLint("NewApi")
    private void setInItView(){
        myNestedScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        myNestedScrollView.setHeaderLayout(new PullHeadViewLayout(mActivity));

        myNestedScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPersonal();
                        myNestedScrollView.onRefreshComplete();  //数据加载完成后，关闭header,footer
                    }
                }, 1500);
            }
        });

        //y轴滑动监听
        myNestedScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int scrollY, int oldX, int oldY) {
                if (scrollY <= 0) {
                    layoutHeader.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
                    tvTitle.setTextColor(Color.argb(0, 51, 51, 51));
                    ivMessage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_message_whrat));
                    // 设置固定大小的占位符
                    FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) layoutHeader.getLayoutParams(); //取控件View当前的布局参数
                    linearParams.height = StatusBarUtils.getStatusBarHeight(mActivity) + BaseUtils.dip2px(mActivity,50f);// 控件的高强制设成当前手机状态栏高度
                    layoutHeader.setLayoutParams(linearParams); //使设置好的布局参数应用到控件</pre>
                    ((MainActivity)mActivity).setStatusBar(0,-1);
                    mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_VISIBLE);
                    viewHeader.setVisibility(View.GONE);
                } else if (scrollY > 0 && scrollY <= BaseUtils.dip2px(mActivity,70f)) {
                    float scale = (float) scrollY / BaseUtils.dip2px(mActivity,70f);
                    float alpha = (225 * scale);
                    // 只是layout背景透明
                    layoutHeader.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                    tvTitle.setTextColor(Color.argb((int) alpha, 51, 51, 51));
//                    Utils.initStatusView(getActivity(),Color.rgb(30-(int)(30 * scale), 122-(int)(122 * scale),234-(int)(234 * scale)));
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                    }
                    viewHeader.setVisibility(View.GONE);
                } else {
                    layoutHeader.setBackgroundColor(Color.argb((int) 255, 255, 255, 255));
                    tvTitle.setTextColor(Color.argb(255, 51, 51, 51));
                    ivMessage.setImageDrawable(getResources().getDrawable(R.mipmap.message_center_icon));
                    viewHeader.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }else{
                        // 设置固定大小的占位符
                        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) layoutHeader.getLayoutParams(); //取控件View当前的布局参数
                        linearParams.height = BaseUtils.dip2px(mActivity,50f);// 控件的高强制设成当前手机状态栏高度
                        layoutHeader.setLayoutParams(linearParams); //使设置好的布局参数应用到控件</pre>
                        ((MainActivity)mActivity).setStatusBar(-1,-1);
                    }
                }
                myNestedScrollViewScrollY = scrollY;
            }
        });
    }

    private void initView(){
        if (myNestedScrollViewScrollY <= 0) {
            tvTitle.setTextColor(Color.argb(0, 51, 51, 51));
            ivMessage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_message_whrat));
        } else if (myNestedScrollViewScrollY > 0 && myNestedScrollViewScrollY <= BaseUtils.dip2px(mActivity,70f)) {
            float scale = (float) myNestedScrollViewScrollY / BaseUtils.dip2px(mActivity,70f);
            float alpha = (225 * scale);
            tvTitle.setTextColor(Color.argb((int) alpha, 51, 51, 51));
        } else {
            tvTitle.setTextColor(Color.argb(255, 51, 51, 51));
            ivMessage.setImageDrawable(getResources().getDrawable(R.mipmap.message_center_icon));
        }
        userId = SharedTools.getStringValue(mActivity, "userId", "-1");
        if (userId.equals("-1")) {
            //未登录 显示"点击登陆"
            ivHead.setImageResource(R.mipmap.icon_login);
            tvRegister.setVisibility(View.VISIBLE);
//            ivArrow.setVisibility(View.GONE);
            tvNickName.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
            tvBalance.setText("0");
            tvCoalMoney.setText("0");
            tvCouponsNumber.setText("无可用");
            tvCouponsNumber.setTextColor(getResources().getColor(R.color.font_black_5));
            tvFreezingAmount.setText("0");
            tvRefundableAmount.setText("0");
            //未登录 默认显示未认证
            tvRealNameStatus.setText("未认证");
            tvRealNameStatus.setTextColor(getResources().getColor(R.color.light_yellow));
            tvRealNameStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_yellow));
            tvRealNameStatusTips.setText("完成实名认证，获取更多服务和信任");
            tvRealNameStatusTips.setTextColor(getResources().getColor(R.color.light_yellow));
            tvDriverStatus.setText("未认证");
            tvDriverStatus.setTextColor(getResources().getColor(R.color.light_yellow));
            tvDriverStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_yellow));
            tvDriverStatusTips.setText("完成司机认证，接单拉货方便快捷");
            tvDriverStatusTips.setTextColor(getResources().getColor(R.color.light_yellow));
        } else {
//            ivArrow.setVisibility(View.VISIBLE);
            tvNickName.setVisibility(View.VISIBLE);
            tvPhone.setVisibility(View.VISIBLE);
            getPersonal();
        }
    }

    /**
     * 挡在登陆状态下联网获取当前用户状态，并更新数据库
     */
    private void getPersonal() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(getActivity(), params).getUserInfo(new DataUtils.DataBack<UserEntity>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        users = dataMemager;
                        SharedTools.putBooleanValue(mActivity, "isWXBind", StringUtils.isEmpty(users.getWeChatCredential())?false :true);
                        UIHelper.saveUserData(mActivity,dataMemager);
                        setDataView(dataMemager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网获取当前用户状态", e.toString());
        }
    }

    private void setDataView(UserEntity data) {
        tvPhone.setText("账号：" + data.getUserMobile());
        tvNickName.setText(StringUtils.isEmpty(data.getNickname()) ? "煤宝" + data.getUserId() : data.getNickname());

//                        int escrow = Integer.valueOf(appData.getAmount());
//                        if (escrow >= 1000000){  // 字体上万自动缩小
//                            tvCoalMoney.setTextSize(16);
//                        }else{
//                            tvCoalMoney.setTextSize(20);
//                        }
        //煤款
        tvCoalMoney.setText(StringUtils.fmtMicrometer((StringUtils.setNumLenth(Float.valueOf(data.getEscrowAmount())/100, 2)) + "")); //煤款

//                        int freezeUncashAmount = Integer.valueOf(appData.getFreezeUncashAmount());
//                        if (freezeUncashAmount >= 1000000){  // 字体上万自动缩小
//                            tvFreezingAmount.setTextSize(16);
//                        }else{
//                            tvFreezingAmount.setTextSize(20);
//                        }
        // 冻结金额
        tvFreezingAmount.setText(StringUtils.fmtMicrometer(StringUtils.setNumLenth(Float.valueOf(data.getFreezeUncashAmount())/100, 2)));

//                        int uncashAmount = Integer.valueOf(appData.getUncashAmount());
//                        if (uncashAmount >= 1000000){  // 字体上万自动缩小
//                            tvRefundableAmount.setTextSize(16);
//                        }else{
//                            tvRefundableAmount.setTextSize(20);
//                        }
        // 可退款金额
        tvRefundableAmount.setText(StringUtils.fmtMicrometer(StringUtils.setNumLenth(Float.valueOf(data.getUncashAmount())/100, 2)));

        tvBalance.setText((StringUtils.setNumLenth(Float.valueOf(data.getPocketAmount())/100, 2)) + "元");//余额

        //代金券可使用张数
        if (!"0".equals(data.getAvailableNumber())){
            tvCouponsNumber.setText(data.getAvailableNumber());
            tvCouponsNumber.setTextColor(mActivity.getResources().getColor(R.color.label_one_background));
        }else{
            tvCouponsNumber.setText("无可用");
            tvCouponsNumber.setTextColor(mActivity.getResources().getColor(R.color.gary));
        }

        tvRegister.setVisibility(View.GONE);
//        ivArrow.setVisibility(View.VISIBLE);
        if (StringUtils.isEmpty(data.getHeadPic())) {
            ivHead.setImageResource(R.mipmap.icon_login);
        } else {
            mActivity.getImageManager().loadItemUrlImage(data.getHeadPic(), ivHead);//个人圆图加载
        }

        //实名认证
        if (data.getRealNameReviewStatus().equals("1")) {
            //审核中用realNameReviewStatus来进行判断
            tvRealNameStatus.setText("审核中");
            tvRealNameStatus.setTextColor(getResources().getColor(R.color.actionsheet_blue));
            tvRealNameStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_blue));
            tvRealNameStatusTips.setText("完成实名认证，获取更多服务和信任");
            tvRealNameStatusTips.setTextColor(getResources().getColor(R.color.light_yellow));
        } else {
            //不是审核中状态 用realNameAuthState来判断 只有两个状态 0 未认证 其他已认证
            if (data.getRealnameAuthState().equals("0")) {  //未认证
                tvRealNameStatus.setText("未认证");
                tvRealNameStatus.setTextColor(getResources().getColor(R.color.light_yellow));
                tvRealNameStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_yellow));
                tvRealNameStatusTips.setText("完成实名认证，获取更多服务和信任");
                tvRealNameStatusTips.setTextColor(getResources().getColor(R.color.light_yellow));
            } else{  //已认证
                tvRealNameStatus.setText("已认证");
                tvRealNameStatus.setTextColor(getResources().getColor(R.color.search_text_color_green));
                tvRealNameStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_green));
                tvRealNameStatusTips.setText("已通过实名认证");
                tvRealNameStatusTips.setTextColor(getResources().getColor(R.color.actionsheet_gray));
            }
        }

        //司机认证
        if (data.getDriverReviewStatus().equals("1")) {
            //审核中用driverReviewStatus来进行判断
            tvDriverStatus.setText("审核中");
            tvDriverStatus.setTextColor(getResources().getColor(R.color.actionsheet_blue));
            tvDriverStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_blue));
            if ("0".equals(data.getPercentage())) {
                tvDriverStatusTips.setText("完成司机认证，接单拉货方便快捷");
            } else {
                tvDriverStatusTips.setText("已通过司机认证，资料完成度" + data.getPercentage() + "%");
            }
            tvDriverStatusTips.setTextColor(getResources().getColor(R.color.light_yellow));
        } else {
            //不是审核中状态 用driverAuthState来判断 只有两个状态 0 未认证 其他已认证
            if (data.getDriverAuthState().equals("0")) {
                tvDriverStatus.setText("未认证");
                tvDriverStatus.setTextColor(getResources().getColor(R.color.light_yellow));
                tvDriverStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_yellow));
                tvDriverStatusTips.setText("完成司机认证，接单拉货方便快捷");
                tvDriverStatusTips.setTextColor(getResources().getColor(R.color.light_yellow));
            } else{
                tvDriverStatus.setText("已认证");
                tvDriverStatus.setTextColor(getResources().getColor(R.color.search_text_color_green));
                tvDriverStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_green));
                if ("100".equals(data.getPercentage())) {
                    tvDriverStatusTips.setText("已通过司机认证，资料完成度100%");
                    tvDriverStatusTips.setTextColor(getResources().getColor(R.color.actionsheet_gray));
                } else {
                    tvDriverStatusTips.setText("已通过司机认证，资料完成度" + data.getPercentage() + "%");
                    tvDriverStatusTips.setTextColor(getResources().getColor(R.color.light_yellow));
                }
            }
        }
    }

    @OnClick({R.id.iv_message,R.id.layout,R.id.layout_account_details, R.id.layout_authentication_buyer,
            R.id.layout_authentication_deiver, R.id.layout_information_fee_record, R.id.layout_my_balance,
            R.id.layout_my_coupons,  R.id.layout_my_focus_department, R.id.layout_my_friend,R.id.layout_my_address,
            R.id.layout_customer_service,R.id.layout_setting,R.id.layout_my_sampling_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_message:
                // 点击后跳入消息中心
                UIHelper.jumpAct(mActivity, MessageCenterActivity.class, false);
                SharedTools.putBooleanValue(mActivity,"ivRedPoint",false);
                setIvRedPoint();
                break;
            case R.id.layout:
                //个人界面
                userId = SharedTools.getStringValue(mActivity, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpAct(mActivity, RegisterAddLoginActivity.class,false);
                } else {
                    //登陆后点击跳转个人信息界面
                    UIHelper.jumpAct(mActivity, PersonUpDataActivity.class, users, false);
                }
                break;
            case R.id.layout_account_details:
                userId = SharedTools.getStringValue(mActivity, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    setGoto(RegisterAddLoginActivity.class, true);
                } else {
                    //已登录 直接跳转界面
                    UIHelper.jumpAct(mActivity,AccountDetailsActivity.class,false);
                }
                break;
            case R.id.layout_my_address:
                //我的地址管理
//                mActivity.displayToast("我的地址管理");
                userId = SharedTools.getStringValue(mActivity, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    setGoto(RegisterAddLoginActivity.class, true);
                } else {
                    //已登录 直接跳转界面
                    UIHelper.jumpAct(mActivity, UpDataAddressActivity.class, "MyFragment",false);
                }
                break;
            case R.id.layout_authentication_buyer:
                //实名认证
                setGoto(BuyerCertificationActivity.class, true);
                break;
            case R.id.layout_authentication_deiver:
                //司机认证
                try {
                    setGoto(DriverCertificationActivity.class, true);
                } catch (Exception e) {
                    UIHelper.jumpActLogin(mActivity, false);
                }
                break;
            case R.id.layout_my_balance:
                //我的余额
                userId = SharedTools.getStringValue(mActivity, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(mActivity, false);
                } else {
                    //登陆后跳转到要去的界面
                    Intent intent = new Intent(mActivity, MyBalanceActivity.class);
                    intent.putExtra("UserEntity", users);
                    startActivity(intent);
                }
                break;
            case R.id.layout_my_coupons:
                //我的代金券
                setGoto(MyCouponsActivity.class, true);
                break;
            case R.id.layout_information_fee_record:
                //资讯费支付记录
                setGoto(PaymentRecordActivity.class, true);
                break;
            case R.id.layout_my_focus_department:
                //关注信息部
                setGoto(InformationDepartmentActivity.class, true);
                break;
            case R.id.layout_my_sampling_test:
                //采样化验订单
                setGoto(BuyerRecordsActivity.class, true);
                break;
            case R.id.layout_my_friend: // 我的好友界面
                setGoto(FriendActivity.class, true);
                break;
            case R.id.layout_customer_service: //服务中心
                Intent intent1 = new Intent(mActivity, CustomerServiceActivity.class);
                if (!"-1".equals(SharedTools.getStringValue(mActivity, "userId", "-1"))) {
                    intent1.putExtra("roleId", users.getRoleId());
                }
                startActivity(intent1);
                break;
            case R.id.layout_setting: //设置
                setGoto(SettingActivity.class, false);
                break;

        }
    }

    /**
     * 设置红点显示信息
     */
    public void setIvRedPoint(){
        if (ivRedPoint != null ){
            if (SharedTools.getBooleanValue(mActivity,"ivRedPoint",false)){
                ivRedPoint.setVisibility(View.VISIBLE);
            }else{
                ivRedPoint.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 界面跳转
     *
     * @param goClass 要去的界面
     * @param flage   是否要做登录判断
     */
    private void setGoto(Class goClass, boolean flage) {
        userId = SharedTools.getStringValue(mActivity, "userId", "-1");
        if (flage) {
            if (userId.equals("-1")) {
                //未登录点击跳转登录界面
                UIHelper.jumpActLogin(mActivity, false);
            } else {
                if (goClass == RegisterAddLoginActivity.class){
                    UIHelper.jumpActLogin(mActivity, false);
                }else{
                    //登陆后跳转到要去的界面
                    UIHelper.jumpAct(mActivity, goClass, false);
                }
            }
        } else {
            //登陆后跳转到要去的界面
            UIHelper.jumpAct(mActivity, goClass, false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

package com.sxhalo.PullCoal.common.base;

/**
 * Created by amoldZhang on 2016/11/3.
 */

public class Constant {
    public static String TIME_BETWEEN_DAY = "day";
    public static String TIME_BETWEEN_HOUR = "hour";
    public static String TIME_BETWEEN_MINUTE = "minute";
    public static String TIME_BETWEEN_SECONDS = "seconds";


    public static final int BUYER_CERTIFICATION_CODE = 0x01;
    public static final int FINISH_ACTIVITY_CODE = 0x02;
    public static final int REFRESH_CODE = 0x03;
    public static final int JUMP_TO_FREIGHT_TRANSPORT_FRAGMENT = 0x04;
    public static final int AREA_CODE = 0x05;
    public static final int PAYMENT_CODE = 0x06;
    public static final int SELECT_ADDRESS = 0x07;
    public static final int UPDATE_COAL_ORDER_FRAGMENT = 0x08;//刷新CoalOrderFragment页面
    public static final int UPDATE_COAL_ORDER_DETAIL = 0x09;//刷新CoalOrderDetailActivity页面
    public static final int UPDATE_SEND_CAR_ORDER_FRAGMENT = 0x10;//刷新SendCarOrderFragment页面
    public static final int UDPATE_SEND_CAR_DETAIL = 0x11;//刷新SendCarDetailActivity页面
    public static final int UPDATE_MY_RELEASE_FRAGMENT = 0x12;//刷新MyReleaseFragment页面
    public static final int UPDATE_SEND_CAR_LIST = 0x13;//刷新SendCarActivity中手动添加的派车数据
    public static final int UPDATE_SEND_CAR_ENTITY = 0x14;//刷新SendCarActivity中手动编辑的的派车数据
    public static final int UPDATE_MY_TRANSPORT_ORDER = 0x15;//刷新我的货运单列表界面数据
    public static final int AREA_CODE_FROM = 0x16;   //选择出发地
    public static final int AREA_CODE_TO = 0x17;    //选择目的地
    public static final int AREA_CODE_COAL_DETAILS = 0x18; //煤炭详情返回刷新
    public static final int AREA_CODE_COAL_NAME_CHOICE_PAY = 0x19; //采样或化验单支付成功返回
    public static final int AREA_CODE_COAL_NAME = 0x20; //采样或化验更换煤名称返回
    public static final int AREA_CODE_MINE = 0x21; //采样或化验更换矿名称返回
    public static final int ACTION_MANAGE_UNKNOWN_APP_SOURCES = 0x22; //从系统设置界面返回
//    public static final int REFRESH_DETAILS_WEB = 0x13;//刷新DetailsWebActivity页面
//    public static final int REFRESH_DRIVER_CERTIFICATION = 0x14;//刷新DriverCertificationActivity页面
//    public static final int REFRESH_GOING_TO_PULLCOAL = 0x15;//刷新GoingToPullCoalActivity页面
//    public static final int REFRESH_MESSAGE_LIST = 0x16;//刷新MessageListActivity页面
//    public static final int REFRESH_MY_TRANSPORT_ORDER_DETAIL = 0x17;//刷新MyTransportOrderDetailActivity页面
//    public static final int REFRESH_PAYMENT_CONFIRMATION = 0x18;//刷新PaymentConfirmationActivity页面

    //接收到信息的通知
    public static final String RECEIVE_MESSAGE = "com.sxhalo.PullCoal.MESSAGE_RECEIVED_ACTION";
    //账号冲突的通知
    public static final String ACCOUNT_CONFLICT = "com.sxhalo.PullCoal.HIDE_RED_POINT_ACTION";

    //煤炭信息搜索
    public static final int Search_Coal = 0;
    //货运信息搜索
    public static final int Search_Freight = 1;
    //司机搜索
    public static final int Search_Driver = 2;
    //买煤求购搜索
    public static final int Search_Purchase = 3;
//    //好友搜索
//    public static final int Search_friend = 3;
//    //信息部搜索
//    public static final int Search_inforDepart = 4;
//    //预订单搜索
//    public static final int Search_order = 5;
//    //货运单搜索
//    public static final int Search_waybill = 6;
//    //邀请司机
//    public static final int Search_invitation_driver = 7;


    //通过平台电话的内容类型
    public static final String CALE_TYPE_MINE_MOUTH = "0";  // 矿口
    public static final String CALE_TYPE_INFORMATION_DEPARTENT = "1";  // 信息部
    public static final String CALE_TYPE_DRIVER = "2";  // 司机
    public static final String CALE_TYPE_FREIGHT = "3";  // 货运
    public static final String CALE_TYPE_COAL = "4";  // 煤炭
    public static final String CALE_TYPE_NEWS = "5";  // 新闻
    public static final String CALE_TYPE_RESERVATION_PURCHASE_INFORMATION = "6";  // 预约采购信息
    public static final String CALE_TYPE_LEGAL_ADVICE = "7";  // 法律咨询
    public static final String CALE_TYPE_COMPLAINT = "8";  // 资讯费支付投诉电话
    public static final String CALE_TYPE_PULL_COAL = "9";  // 我要拉煤电话上报
    public static final String CALE_TYPE_COAL_RECHARGE = "10";  // 我要充值电话上报
    public static final String CALE_TYPE_COAL_ORDER = "11";  // 订单异常
    public static final String CALE_TYPE_COAL_SEND_CAR_ORDER = "12";  // 派车单电话上报

}

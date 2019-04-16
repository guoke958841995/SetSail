package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.ListCoalEntity;
import com.sxhalo.PullCoal.model.SearchEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.CustomeProgressBar;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.addrightview.LayoutContrastView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

/**
 * 搜索界面
 * Created by amoldZhang on 2017/1/5.
 */
public class SearchActivity<T> extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.et_search)
    EditText seachforEdits;
    @Bind(R.id.seach_list)
    SmoothListView seachList;
    @Bind(R.id.listview_no_net)
    LinearLayout listviewNoNet;
    @Bind(R.id.listview_ementy)
    LinearLayout listviewEment;
    @Bind(R.id.text_ementy)
    TextView textEment;

    @Bind(R.id.ll_contrast)
    LayoutContrastView llContrast;

    private int TAG;
    private boolean refresh = false;
    private QuickAdapter<T> mAdapter;
    private List<T> tList = new ArrayList<T>();
    private Map<String, String> coalTypeMap = new HashMap<String, String>();

    //刷新时消息列表
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private String searchText;
    private int page = 1;
    private MyReceiver myReceiver;

    private Serializable serializable;
//    private int type;  //标记当前点击的类型

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_seach);
    }

    @Override
    protected void initTitle() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_ACTION);
        registerReceiver(myReceiver, filter);
        queryCoalType();
        initData();
        setView();
    }

    private void queryCoalType() {
        Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100002"}).get(0);
        for (int i = 0; i < coalTypes.list.size(); i++) {
            coalTypeMap.put(coalTypes.list.get(i).dictCode, coalTypes.list.get(i).dictValue);
        }
    }

    @Override
    protected void getData() {
        searchText = getIntent().getStringExtra("keyword");
        if (!StringUtils.isEmpty(searchText)) {
            seachforEdits.setText(searchText);
            seachforEdits.setSelection(searchText.length());
            getSeachData(searchText);
        } else {
            performSearch();
        }
    }

    private void setView() {
        seachforEdits.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//6
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    protected void performSearch() {
        try {
            String s = seachforEdits.getText().toString().trim();
            if (!TextUtils.isEmpty(s)) {
                seachList.setVisibility(View.VISIBLE);
                listviewNoNet.setVisibility(View.GONE);
                if (s.toString() != null) {
                    tList.clear();
                    mAdapter.replaceAll(tList);
                    searchText = s.toString();
                    getSeachData(searchText);
                }
                seachList.setLoadMoreEnable(true);
            } else {
                seachList.setVisibility(View.GONE);
                listviewNoNet.setVisibility(View.GONE);
                listviewEment.setVisibility(View.VISIBLE);
                textEment.setText("搜索您感兴趣的内容");
                seachList.setLoadMoreEnable(false);
                tList.clear();
                mAdapter.replaceAll(tList);
            }
        } catch (Exception e) {
            Log.i("货运搜索", e.toString());
        }
    }

    /**
     * 界面控件初始化
     */
    private void initData() {
        TAG = getIntent().getIntExtra("TAG", -1);
        int layout = getLayout();
        mAdapter = new QuickAdapter<T>(this, layout, tList) {
            @Override
            protected void convert(BaseAdapterHelper helper, T t, int pos) {
                try {
                    setDataView(helper, t, pos);
                } catch (Exception e) {
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        // 设置ListView数据
        seachList.setRefreshEnable(false);
        seachList.setLoadMoreEnable(true);
        seachList.setDrawingCacheEnabled(true);
        seachList.setSmoothListViewListener(this);
        seachList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        seachList.setAdapter(mAdapter);

        seachList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                setToGo(intent, position);
            }
        });
        seachList.setVisibility(View.GONE);
        listviewNoNet.setVisibility(View.GONE);

        if (TAG == Constant.Search_Coal){
            llContrast.initView(new LayoutContrastView.CoalBack(){
                @Override
                public void setLayout(boolean flage){
                    if (flage) {
                        llContrast.setVisibility(View.VISIBLE);
                    }else {
                        llContrast.setVisibility(View.GONE);
                    }
                    mAdapter.replace();
                }
            });
        }
    }

    /**
     * 根据需求给相对应的item赋值
     *
     * @param helper
     * @param t
     * @param pos
     */
    private void setDataView(BaseAdapterHelper helper, T t, final int pos) {
        switch (TAG) {
            case Constant.Search_Coal:
                try {
                    setSearchCoal(helper, t, pos);
                } catch (Exception e) {
                    GHLog.e("煤炭展示", e.toString());
                }
                break;
            case Constant.Search_Freight:
                try {
                    setSearchFreight(helper, t, pos);
                } catch (Exception e) {
                    GHLog.e("货运展示", e.toString());
                }
                break;
            case Constant.Search_Driver:
                try {
                    setSearchDriver(helper, t, pos);
                } catch (Exception e) {
                    GHLog.e("司机展示", e.toString());
                }
                break;
            case Constant.Search_Purchase:
                try {
                    //买煤求购
                    setSearchPurchase(helper, t, pos);
                } catch (Exception e) {
                    GHLog.e("买煤求购", e.toString());
                }
                break;
        }
    }

    /**
     * 买煤求购
     * @param helper
     * @param t
     * @param pos
     */
    private void setSearchPurchase(BaseAdapterHelper helper, T t, int pos) {
        UserDemand entity = (UserDemand) t;
        View dividerRough = helper.getView().findViewById(R.id.layout_divider_rough);
        View dividerThin = helper.getView().findViewById(R.id.layout_divider_thin);
        if (pos == 0) {
            dividerRough.setVisibility(View.GONE);
            dividerThin.setVisibility(View.VISIBLE);
        } else {
            dividerRough.setVisibility(View.VISIBLE);
            dividerThin.setVisibility(View.GONE);
        }
        CircleImageView ivHead = (CircleImageView) helper.getView().findViewById(R.id.iv_head);
        if (!StringUtils.isEmpty(entity.getHeadPic())) {
            // 头像
            getImageManager().loadUrlImageView(entity.getHeadPic(), ivHead);
        } else {
            ivHead.setImageResource(R.mipmap.main_tab_item);
        }
        String coalType = coalTypeMap.get(entity.getCategoryId());
        TextView tvStatus = (TextView) helper.getView().findViewById(R.id.tv_status);
        if (entity.getRealnameAuthState().equals("1")) {
            //已认证 显示名字 隐藏中间
            helper.setText(R.id.tv_name, StringUtils.setName(entity.getContactPerson()));
            tvStatus.setText("已认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_green));
        } else {
            //未认证 显示电话号码隐藏中间4位
            helper.setText(R.id.tv_name, StringUtils.setPhoneNumber(entity.getContactPhone()));
            tvStatus.setText("未认证");
            tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_gray));
        }

        TextView tvCash = (TextView) helper.getView().findViewById(R.id.tv_cash);
        TextView tvCashDeposit = (TextView) helper.getView().findViewById(R.id.tv_cash_deposit);
        if ("0".equals(entity.getBond())) {
            tvCash.setVisibility(View.GONE);
            tvCashDeposit.setVisibility(View.GONE);
        } else {
            tvCash.setVisibility(View.VISIBLE);
            tvCashDeposit.setVisibility(View.VISIBLE);
            tvCash.setText("¥" + StringUtils.setNumLenth(Float.valueOf(entity.getBond())/100, 2));
        }
        helper.setText(R.id.tv_title, "求购基低位发热量 " + entity.getCalorificValue() + "Kcal/Kg " + entity.getCoalName());
        helper.setText(R.id.tv_coal_name, coalType);
        helper.setText(R.id.tv_area, entity.getRegionName());
        helper.setText(R.id.tv_num, entity.getNumber());
        helper.setText(R.id.tv_price, StringUtils.isEmpty(entity.getPrice()) ? "无" : entity.getPrice());
        helper.setText(R.id.tv_time, entity.getCreateTime());
    }

    /**
     * 给搜索出的司机列表赋值
     *
     * @param helper
     * @param t
     * @param pos
     */
    private void setSearchDriver(BaseAdapterHelper helper, final T t, int pos) {
        final UserEntity item = (UserEntity) t;
        GHLog.i("司机展示", "赋值开始");
        try {
            String derverName = "";
            derverName = item.getRealName();
            helper.setText(R.id.drivert_name_num, derverName);
            ImageView imageView = (ImageView) helper.getView().findViewById(R.id.recommend_drivder_image);
            // 给 ImageView 设置一个 tag
            imageView.setTag(item.getHeadPic());
            // 预设一个图片
            imageView.setImageResource(R.mipmap.main_tab_item);
            // 通过 tag 来防止图片错位
            if (imageView.getTag() != null && imageView.getTag().equals(item.getHeadPic())) {
                ((BaseActivity) mContext).getImageManager().loadUrlDerverView(item.getHeadPic(), imageView);
            }

            //设置车牌号码
            TextView tvLicencePlate = (TextView) helper.getView().findViewById(R.id.tv_licence_plate);
            if (StringUtils.isEmpty(item.getPlateNumber())) {
                tvLicencePlate.setBackgroundDrawable(null);
                tvLicencePlate.setText("未填写");
                tvLicencePlate.setTextColor(getResources().getColor(R.color.setting_alt_text_normal));
            } else {
                tvLicencePlate.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_plate_number));
                tvLicencePlate.setText(StringUtils.setPlateNumber(item.getPlateNumber()));
                tvLicencePlate.setTextColor(getResources().getColor(R.color.text_color_plate_number));
            }

            //设置认证百分比
            CustomeProgressBar myProgressBar = (CustomeProgressBar) helper.getView().findViewById(R.id.text_progressbar);
            myProgressBar.setShowText(true);
            myProgressBar.setProgress(Integer.parseInt(StringUtils.isEmpty(item.getPercentage()) ? "0" : item.getPercentage()));
            ResetRatingBar dirver_ratingBar = (ResetRatingBar) helper.getView().findViewById(R.id.recommend_drivder_ratingbar1);
            dirver_ratingBar.setStar(Integer.valueOf(StringUtils.isEmpty(item.getCreditRating()) ? "1" : item.getCreditRating()));

            String carLength = item.getVehicleLength();
            if (StringUtils.isEmpty(carLength)) {
                carLength = "--  ";
            } else {
                carLength = carLength + "米  ";
            }

            Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
            String vehicleModels = "";
            for (FilterEntity filterEntity : sys100005.list) {
                if (filterEntity.dictCode.equals(item.getVehicleMode())) {
                    vehicleModels = filterEntity.dictValue;
                    break;
                }
            }
            if (StringUtils.isEmpty(vehicleModels)) {
                vehicleModels = "--  ";
            } else {
                vehicleModels = vehicleModels + "  ";
            }

            String carLoad = item.getVehicleLoad();
            if (StringUtils.isEmpty(carLoad)) {
                carLoad = "--";
            } else {
                carLoad = "载重" + carLoad + "吨";
            }
            //车长 、车型、载重
            helper.setText(R.id.drivert_car_content, carLength + vehicleModels + "  " + carLoad);

            ImageView driverStatus = (ImageView) helper.getView().findViewById(R.id.driver_current_status);
            if (item.getDriverState().equals("2")) { //忙碌
                driverStatus.setImageResource(R.mipmap.be_busy);
            } else { //空闲
                driverStatus.setImageResource(R.mipmap.free);
            }

            //是否是专线司机
            if (item.getSpecialLine().equals("1")) {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.VISIBLE);
            } else {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.GONE);
            }

            helper.setText(R.id.drivert_common_singular, item.getCumulative());

            helper.getView().findViewById(R.id.derver_tel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tel", item.getUserMobile());
                    map.put("callType", Constant.CALE_TYPE_DRIVER);
                    map.put("targetId", item.getUserId());
                    UIHelper.showCollTel(mContext, map, true);
                }
            });

            //  好友状态判断
            TextView friendIV = (TextView) helper.getView().findViewById(R.id.tv_is_friend);
            if (item.getIsFriend().equals("0")) {
                friendIV.setVisibility(View.GONE);
            } else {
                friendIV.setVisibility(View.VISIBLE);
            }
        } catch (NumberFormatException e) {
            GHLog.i("搜索司机赋值", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 给搜索出的货运列表赋值
     *
     * @param helper
     * @param t
     * @param pos
     */
    private void setSearchFreight(BaseAdapterHelper helper, final T t, int pos) {
        final TransportMode item = (TransportMode) t;
        final Activity myActivity = this;
        try {
            View view_line = helper.getView().findViewById(R.id.layout_divider);
            if (pos == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }
            // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
            String transportType = item.getTransportType();
            if (transportType.equals("0")) {
                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
                helper.setText(R.id.freight_type, " / 煤炭");
            } else if (transportType.equals("1")) {
                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.GONE);
                helper.setText(R.id.freight_type, "    长期货运 / 煤炭");
            } else if (transportType.equals("2")) {
                helper.setText(R.id.freight_type, " / 普货");

                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
            }
            helper.setText(R.id.pubilshTime, item.getDifferMinute());
            helper.setText(R.id.tv_source, item.getCompanyName());
            helper.setText(R.id.tv_transport_cost, item.getCost());
            helper.setText(R.id.tv_start_address, item.getFromPlace());
            helper.setText(R.id.tv_end_address, item.getToPlace());

            final String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
            helper.getView().findViewById(R.id.iv_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getConsultingFee().equals("0")) {
                        //免费信息或者已支付 可以直接打电话
                        callPhone(item);
                    } else {
                        //不免费 先判断是否登录
                        if (userId.equals("-1")) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(myActivity,false);
                        } else {
                            //已登录 判断是否支付
                            if (item.getIsPay().equals("1")) {
                                //已支付 直接打电话
                                callPhone(item);
                            } else {
                                //未支付 弹框提示
                                ((BaseActivity) myActivity).showPayDialog(item, "1");
                            }
                        }
                    }
                }
            });
            /****************************信息费********************************/
            String payName;
            int payColor;
            //当资讯信息是免费的
            if (item.getConsultingFee().equals("0")) {
                payName = "免费信息";
                payColor = R.color.blue;
                //免费信息 隐藏信息提示 显示货运标签
                helper.getView().findViewById(R.id.ll_information_free).setVisibility(View.GONE);
                //设置货运标签
                TextView publishTag0 = (TextView) helper.getView().findViewById(R.id.publishTag0);
                TextView publishTag1 = (TextView) helper.getView().findViewById(R.id.publishTag1);
                TextView publishTag2 = (TextView) helper.getView().findViewById(R.id.publishTag2);
                TextView publishTag3 = (TextView) helper.getView().findViewById(R.id.publishTag3);
                String publishTag = item.getPublishTag() == "" ? null : item.getPublishTag();
                if (!StringUtils.isEmpty(publishTag)) {
                    String[] strings = publishTag.split(",");
                    int size = strings.length;
                    if (strings.length > 0) {
                        if (size >= 1 && !StringUtils.isEmpty(strings[0])) {
                            publishTag0.setVisibility(View.VISIBLE);
                            publishTag0.setText(strings[0]);
                        } else {
                            publishTag0.setVisibility(View.GONE);
                        }
                        if (size >= 2 && !StringUtils.isEmpty(strings[1])) {
                            publishTag1.setVisibility(View.VISIBLE);
                            publishTag1.setText(strings[1]);
                        } else {
                            publishTag1.setVisibility(View.GONE);
                        }
                        if (size >= 3 && !StringUtils.isEmpty(strings[2])) {
                            publishTag2.setVisibility(View.VISIBLE);
                            publishTag2.setText(strings[2]);
                        } else {
                            publishTag2.setVisibility(View.GONE);
                        }
                        if (size >= 4 && !StringUtils.isEmpty(strings[3])) {
                            publishTag3.setVisibility(View.VISIBLE);
                            publishTag3.setText(strings[3]);
                        } else {
                            publishTag3.setVisibility(View.GONE);
                        }
                    }
                } else {
                    publishTag0.setVisibility(View.GONE);
                    publishTag1.setVisibility(View.GONE);
                    publishTag2.setVisibility(View.GONE);
                    publishTag3.setVisibility(View.GONE);
                }
            } else {
                ImageView typeImage = (ImageView)helper.getView().findViewById(R.id.free_type_image);
                TextView typeText = (TextView)helper.getView().findViewById(R.id.free_type);
                //当前登录用户和发布人同属于一个信息部
                if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(item.getCoalSalesId())){
                    payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                    typeText.setText("发布人："+ item.getPublishUser());
                }else
                    //0未支付，1已支付
                    if (item.getIsPay().equals("1")) {
                        //当资讯信息已经付过费
                        payName = "(已支付)收费信息";
                        payColor = R.color.actionsheet_red;
                        typeImage.setImageResource(R.mipmap.icon_buyer_app);
                        typeText.setText(item.getLicenseMinute());
                    } else {
                        //当资讯信息尚未付费
                        payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                        payColor = R.color.actionsheet_red;
                        typeImage.setImageResource(R.mipmap.icon_about_app);
                        helper.setText(R.id.free_type, item.getLicenseMinute());
                    }
                //收费信息 隐藏货运标签 显示信息提示
                helper.getView().findViewById(R.id.ll_information_free).setVisibility(View.VISIBLE);
                //隐藏货运标签
                helper.getView().findViewById(R.id.publishTag0).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag1).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag2).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag3).setVisibility(View.GONE);
            }
            helper.setText(R.id.information_free, payName);
            ((TextView) helper.getView().findViewById(R.id.information_free)).setTextColor(getResources().getColor(payColor));
        } catch (Exception e) {
            GHLog.i("货运赋值", e.toString());
        }
    }

    /**
     * 货运列表中的拨打电话
     *
     * @param transportMode
     */
    private void callPhone(TransportMode transportMode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", transportMode.getPublishUserPhone());
        map.put("callType", Constant.CALE_TYPE_FREIGHT);
        map.put("targetId", transportMode.getTransportId());
        UIHelper.showCollTel(mContext, map, true);
    }

    /**
     * 给搜索出的煤炭列表赋值
     */
    private void setSearchCoal(BaseAdapterHelper helper, T t, int pos) {
        final Coal coals = (Coal) t;
        try {
                if (Utils.isImagesTrue(coals.getImageUrl()) == 200 ){
                    getImageManager().loadCoalTypeUrlImage(coals.getImageUrl(),coals.getCategoryImage(),(ImageView)helper.getView(R.id.iv_com_head));
                }else{
                    getImageManager().loadCoalTypeUrlImage(coals.getCategoryImage(),(ImageView)helper.getView(R.id.iv_com_head));
                }

            helper.setText(R.id.coal_item_name, coals.getCoalName());
            helper.setText(R.id.coal_price, coals.getOneQuote());
            helper.setText(R.id.tv_com_calorificvalue, coals.getCalorificValue() + "kCal/kg");
            helper.setText(R.id.tv_com_storagerate, coals.getMineMouthName());
            helper.setText(R.id.information_name, coals.getCompanyName());
            if (StringUtils.isEmpty(coals.getCoalReportPicUrl())) {
                helper.getView().findViewById(R.id.iv_test).setVisibility(View.GONE);
            } else {
                helper.getView().findViewById(R.id.iv_test).setVisibility(View.VISIBLE);
            }
            helper.setText(R.id.push_time, coals.getDifferMinute());
            String payName;
            int payColor;
            //当资讯信息是免费的
            if (coals.getConsultingFee().equals("0")) {
                payName = "免费信息";
                payColor = R.color.blue;
                helper.getView().findViewById(R.id.coal_price).setVisibility(View.VISIBLE);
                helper.getView().findViewById(R.id.coal_price_image).setVisibility(View.VISIBLE);
                helper.getView().findViewById(R.id.free_type).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.free_type_image).setVisibility(View.GONE);
            } else {
                ImageView typeImage = (ImageView)helper.getView().findViewById(R.id.free_type_image);
                TextView typeText = (TextView)helper.getView().findViewById(R.id.free_type);
                typeImage.setVisibility(View.VISIBLE);
                typeText.setVisibility(View.VISIBLE);

                if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())){
                    payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    helper.getView().findViewById(R.id.coal_price).setVisibility(View.GONE);
                    helper.getView().findViewById(R.id.coal_price_image).setVisibility(View.GONE);
                    typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                    typeText.setText("发布人："+ coals.getPublishUser());
                }else
                    //0未支付，1已支付
                    if (coals.getIsPay().equals("1")&& coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {
                        //当资讯信息已经付过费
                        payName = "(已支付)收费信息";
                        payColor = R.color.actionsheet_red;
                        helper.getView().findViewById(R.id.coal_price).setVisibility(View.VISIBLE);
                        helper.getView().findViewById(R.id.coal_price_image).setVisibility(View.VISIBLE);
                        typeImage.setImageResource(R.mipmap.icon_buyer_app);
                        typeText.setText(coals.getLicenseMinute());
                    } else {
                        //当资讯信息尚未付费
                        payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + "收费信息";
                        payColor = R.color.actionsheet_red;
                        helper.getView().findViewById(R.id.coal_price).setVisibility(View.GONE);
                        helper.getView().findViewById(R.id.coal_price_image).setVisibility(View.GONE);
                        typeImage.setImageResource(R.mipmap.icon_about_app);
                        if (coals.getLicenseMinute().contains("已失效")){
                            typeText.setText("支付后可查看更多");
                        }else{
                            typeText.setText(coals.getLicenseMinute().contains("已失效")?"支付后可查看更多":coals.getLicenseMinute());
                        }
                    }
            }
            helper.setText(R.id.information_free, payName);
            ((TextView) helper.getView().findViewById(R.id.information_free)).setTextColor(getResources().getColor(payColor));
            helper.setText(R.id.coal_price, coals.getOneQuote());


            TextView contrastText = (TextView)helper.getView(R.id.contrast_text);
            contrastText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                    //先判断是否登录
                    if (userId.equals("-1")) {
                        //未登录 先去登录
                        UIHelper.jumpActLogin(SearchActivity.this,false);
                    } else {
                        if (coals.getConsultingFee().equals("0") ||
                                SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())) {
                            //免费信息或者已支付 直接对比
                            llContrast.setAddContrastView(coals);
                        } else {
                            //已登录 判断是否支付
                            if (coals.getIsPay().equals("1")&& coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {
                                //已支付 直接查看
                                //免费信息或者已支付 直接对比
                                llContrast.setAddContrastView(coals);
                            } else {
                                //未支付 弹框提示
                                showPayDialog(coals, "0");
                            }
                        }
                    }
                }
            });

            if (llContrast.getifContrast(coals)){
                contrastText.setBackgroundResource(R.drawable.button_shape_pressed);
                contrastText.setTextColor(getResources().getColor(R.color.white));
            }else{
                contrastText.setBackgroundResource(R.drawable.bull_send_car);
                contrastText.setTextColor(getResources().getColor(R.color.actionsheet_blue));
            }
        } catch (Exception e) {
            GHLog.e("煤炭赋值", e.toString());
        }
    }



    /**
     * 选择对应跳转的界面
     *
     * @param intent
     * @param position
     */
    private void setToGo(Intent intent, int position) {
        switch (TAG) {
            case Constant.Search_Coal:
                String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(mContext, false);
                } else {
//                type = 0;
                    Coal coal = (Coal) mAdapter.getItem(position - 1);
                    intent.setClass(SearchActivity.this, DetailsWebActivity.class);
                    intent.putExtra("inforDepartId", "");
                    intent.putExtra("Coal", coal);
                    serializable = coal;
                    if (SharedTools.getStringValue(mContext, "coalSalesId", "-1").equals(coal.getCoalSalesId())) {
                        startActivity(intent);
                    } else {
                        nextMove(coal.getConsultingFee(), coal.getIsPay(), intent, "0");
                    }
                }
                break;
            case Constant.Search_Driver:
//                type = 1;
                intent.setClass(SearchActivity.this, DetaileFriendActivity.class);
                intent.putExtra("UserEntity", (UserEntity) mAdapter.getItem(position - 1));
                intent.putExtra("driver", "driver");
                startActivity(intent);
                break;
            case Constant.Search_Freight:
                String userId1 = SharedTools.getStringValue(mContext, "userId", "-1");
                if (userId1.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpAct(mContext, RegisterAddLoginActivity.class,false);
                } else {
//                type = 0;
                    TransportMode transportMode = (TransportMode) mAdapter.getItem(position - 1);
                    intent.setClass(SearchActivity.this, TransportDetailActivity.class);
                    intent.putExtra("waybillId", transportMode.getTransportId()); //货运id
                    serializable = transportMode;
                    if (SharedTools.getStringValue(mContext, "coalSalesId", "-1").equals(transportMode.getCoalSalesId())) {
                        startActivity(intent);
                    } else {
                        nextMove(transportMode.getConsultingFee(), transportMode.getIsPay(), intent, "1");
                    }
                }
                break;
            case Constant.Search_Purchase:
//                type = 2;
                //跳转到预约详情
                intent.setClass(mContext, DetailPurchasingReservationActivity.class);
                intent.putExtra("demandId", ((UserDemand) mAdapter.getItem(position - 1)).getDemandId());
                startActivity(intent);
                break;
        }
    }

    /**
     * 点击信息后下一步操作
     *
     * @param consultingFee
     * @param isPay
     * @param intent
     * @param type
     */
    private void nextMove(String consultingFee, String isPay, Intent intent, String type) {
        String userId = SharedTools.getStringValue(this, "userId", "-1");
        //先判断是否登录
        if (userId.equals("-1")) {
            //未登录 先去登录
            UIHelper.jumpActLogin(this,false);
        } else {
            if (consultingFee.equals("0")) {
                //免费信息或者已支付 可以直接查看详情
                startActivity(intent);
            } else {

                    //已登录 判断是否支付
                    if (isPay.equals("1")) {
                        //已支付 直接查看
                        startActivity(intent);
                    } else {
                        //未支付 弹框提示
                        showPayDialog(serializable, type);
                    }
                }
            }
        }

        /**
         * 搜索数据联网
         *
         * @param search
         */
    public void getSeachData(String search) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("searchValue", search);
            params.put("currentPage", page + "");
            params.put("pageSize", "10");
            switch (TAG) {
                case Constant.Search_Coal:
                    params.put("firstPrice", "0");
                    params.put("freeInfor", "0");
                    params.put("viewType", "0");
                    new DataUtils(this, params).getCoalGoodsList(new DataUtils.DataBack<ListCoalEntity>() {
                        @Override
                        public void getData(ListCoalEntity listCoalEntity) {
                            if (listCoalEntity == null) {
                                return;
                            }
                            SearchEntity searchEntity = new SearchEntity();
                            searchEntity.setCoalProductList(listCoalEntity.getCoalList());
                            getJSON(searchEntity);
                        }
                    },true);
                    break;
                case Constant.Search_Driver:
                    try {
                        new DataUtils(this, params).getDriverList(new DataUtils.DataBack<APPDataList<UserEntity>>() {
                            @Override
                            public void getData(APPDataList<UserEntity> data) {
                                if (data == null) {
                                    return;
                                }
                                SearchEntity searchEntity = new SearchEntity();
                                searchEntity.setDriverList(data.getList());
                                getJSON(searchEntity);
                            }
                        },true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constant.Search_Freight:
                    try {
                        new DataUtils(this, params).getCoalTransportList(new DataUtils.DataBack<APPDataList<TransportMode>>() {
                            @Override
                            public void getData(APPDataList<TransportMode> dataList) {
                                if (dataList == null) {
                                    return;
                                }
                                SearchEntity searchEntity = new SearchEntity();
                                searchEntity.setTransportModeList(dataList.getList());
                                getJSON(searchEntity);
                            }
                        },false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constant.Search_Purchase:
                    try {
                        // 搜索买煤求购
                        new DataUtils(this, params).getUserDemandList(new DataUtils.DataBack<List<UserDemand>>() {
                            @Override
                            public void getData(List<UserDemand> dataList) {
                                try {
                                    if (dataList == null) {
                                        return;
                                    }
                                    SearchEntity searchEntity = new SearchEntity();
                                    searchEntity.setUserDemandEntityList(dataList);
                                    getJSON(searchEntity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } catch (Exception e) {
            seachList.setVisibility(View.GONE);
            listviewNoNet.setVisibility(View.VISIBLE);
            GHLog.e("货运列表联网", e.toString());
        }
    }

    /**
     * 联网数据解析
     *
     * @param data
     */
    private void getJSON(SearchEntity data) {
        try {
            if (refresh) {
                tList.clear();
                for (int i = 0; i < setSwitch(data).size(); i++) {
                    tList.add(i, setSwitch(data).get(i));
                }
                mAdapter.replaceAll(tList);
            } else {
                tList.addAll(setSwitch(data));
                mAdapter.addAll(setSwitch(data));
            }
            if (tList.size() < 10) {
                seachList.setLoadMoreEnable(false);
            } else {
                seachList.setLoadMoreEnable(true);
            }
            GHLog.i("实际的个数", tList.size() + "");
            GHLog.i("当前显示个数", mAdapter.getCount() + "");
            if (mAdapter.getCount() == 0) {
                seachList.setVisibility(View.GONE);
                listviewNoNet.setVisibility(View.GONE);
                listviewEment.setVisibility(View.VISIBLE);
                textEment.setText("未查找到您搜索的相关内容，请重新搜索");
            } else {
                seachList.setVisibility(View.VISIBLE);
                listviewNoNet.setVisibility(View.GONE);
                listviewEment.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            GHLog.e("", e.toString());
        }
    }

    /**
     * 根据不同的需求取出相应的实体
     *
     * @param data
     * @return
     */
    private List<T> setSwitch(SearchEntity data) {
        List<T> list = new ArrayList<T>();
        switch (TAG) {
            case Constant.Search_Coal:
                list = (List<T>) data.getCoalProductList();
                break;
            case Constant.Search_Freight:
                list = (List<T>) data.getTransportModeList();
                break;
            case Constant.Search_Driver:
                list = (List<T>) data.getDriverList();
                break;
            case Constant.Search_Purchase:
                // 买煤求购
                list = (List<T>) data.getUserDemandEntityList();
                break;
        }
        return list;
    }

    /**
     * 根据不同的需求加载不同的item
     *
     * @return
     */
    public int getLayout() {
        int layout = 0;
        switch (TAG) {
            case Constant.Search_Coal:
                layout = R.layout.fragment_coal_list_item;
                break;
            case Constant.Search_Freight:
                layout = R.layout.freight_transport_item;
                break;
            case Constant.Search_Driver:
                layout = R.layout.driver_item;
                break;
            case Constant.Search_Purchase:
                //买煤求购
                layout = R.layout.purchase_orders_list_item;
                break;
        }
        return layout;
    }


    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                seachList.stopRefresh();
                refresh = true;
                getSeachData(searchText);
                seachList.setRefreshTime("刚刚");
            }
        }, 500);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                refresh = false;
                getSeachData(searchText);
                seachList.stopLoadMore();
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    /**
     *
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RECEIVED_ACTION.equals(intent.getAction()) && intent != null) {
                try {
//                    if (0 == type) {
                    onRefresh();
//                    }
//                    if (1 == type) {
//                        String friendId = intent.getStringExtra("friendId");
//                        for (int i = 0; i < mAdapter.getCount(); i++) {
//                            UserEntity userEntity = (UserEntity) mAdapter.getItem(i);
//                                if (userEntity.getUserId().equals(friendId)) {
//                                    userEntity.setIsFriend("1");
//                                    mAdapter.set(i, (T) userEntity);
//                                    mAdapter.notifyDataSetChanged();
//                                    break;
//                            }
//                        }
//                    }
                } catch (Exception e) {
                    GHLog.e("MyReceiver", e.toString());
                }
            }
        }
    }
}

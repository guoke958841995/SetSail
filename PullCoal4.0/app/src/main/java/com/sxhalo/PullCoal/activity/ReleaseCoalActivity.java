package com.sxhalo.PullCoal.activity;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.model.UserDemandBBS;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.popwin.SelectAreaPopupWindow;
import com.sxhalo.PullCoal.ui.popwin.popwindow.DatePickerPopWin;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.RegexpUtils;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我要拉煤发布
 * Created by amoldZhang on 2018/4/23.
 */
public class ReleaseCoalActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.from_time_view)
    View fromTimeView;
    @Bind(R.id.tv_from_time)
    TextView tvFromTime;
    @Bind(R.id.iv_from_time)
    ImageView ivFromTime;

    @Bind(R.id.state_view)
    View stateView;
    @Bind(R.id.tv_from_area)
    TextView tvFromArea;
    @Bind(R.id.iv_from)
    ImageView ivFrom;

    @Bind(R.id.to_time_view)
    View toTimeView;
    @Bind(R.id.tv_to_time)
    TextView tvToTime;
    @Bind(R.id.iv_to_time)
    ImageView ivToTime;

    @Bind(R.id.end_view)
    View endView;
    @Bind(R.id.tv_to_area)
    TextView tvToArea;
    @Bind(R.id.iv_to)
    ImageView ivTo;

    @Bind(R.id.car_mode_view)
    View carModeView;
    @Bind(R.id.tv_car_mode)
    TextView tvCarMode;
    @Bind(R.id.iv_car_mode)
    ImageView ivCarMode;

    @Bind(R.id.et_car_length)
    EditText etCarLength;
    @Bind(R.id.et_car_load)
    EditText etCarLoad;
    @Bind(R.id.et_contacts)
    EditText etContacts;
    @Bind(R.id.et_phone)
    EditText etPhone;


    private SelectAreaPopupWindow areaPopupWindow;
    private int currentPage = 1;
    private String startCode;
    private String endCode;
    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地
    private int currentType = 0;//用来判断点击的是出发地还是目的地


    //车型选择
    private CustomListView listView;
    private PopupWindow popupWindow;
    private QuickAdapter<FilterEntity> mAdapter;
    private List<FilterEntity> carModeList = new ArrayList<FilterEntity>();
    private String selectedId;
    private String currentUserName;
    private String currentUserPhone;

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    private LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_release_coal);
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected void initTitle() {
//         /*
//         * 防止键盘挡住输入框
//         * 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
//         * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
//         */
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //设置Activity的SoftInputMode属性值为adjustResize
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        title.setText("我要拉煤发布");
        currentUserName = SharedTools.getStringValue(this, "user_real_name", "");
        currentUserPhone = SharedTools.getStringValue(this, "user_mobile", "");
        etContacts.setText(currentUserName);
        etPhone.setText(currentUserPhone);
    }

    @Override
    protected void getData() {
        createAreaWindow();
    }


    @OnClick({R.id.title_bar_left,R.id.rl_from_time,R.id.rl_from_area,R.id.rl_to_time, R.id.rl_to_area,R.id.rl_car_mode,R.id.cancel,R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.rl_from_time:
                showTimePopWin(tvFromTime,ivFromTime,fromTimeView);
                break;
            case R.id.rl_from_area:
                currentType = TYPE_START;
                areaPopupWindow.showPopupWindow(tvFromArea, currentType, this.stateView);
                ivFrom.setImageResource(R.mipmap.icon_arrow_up);
                break;
            case R.id.rl_to_time:
                showTimePopWin(tvToTime,ivToTime,toTimeView);
                break;
            case R.id.rl_to_area:
                currentType = TYPE_END;
                areaPopupWindow.showPopupWindow(tvToArea, currentType, this.endView);
                ivTo.setImageResource(R.mipmap.icon_arrow_up);
                break;
            case R.id.rl_car_mode:
                // 选择车型
                ivCarMode.setImageResource(R.mipmap.icon_arrow_up);
                showCarModeView();
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.submit:
                if ("0".equals(startCode)) {
                    displayToast("出发地不能选全国");
                    return;
                }
                if ("0".equals(endCode)) {
                    displayToast("目的地不能选全国");
                    return;
                }

                //为防止重复提交 当点击间隔小于1秒时 当做同一点击事件
                //只有点击时间间隔在1秒及以上时 才认为是两次事件
                long curClickTime = System.currentTimeMillis();
                if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                    lastClickTime = curClickTime;
                    getViewData();
                }
                break;
        }
    }

    public void getViewData() {
        if (StringUtils.isEmpty(tvFromTime.getText().toString().trim()) || tvFromTime.getText().toString().trim().contains("请选择")) {
            //校验用户是否选择出发时间
            displayToast(getString(R.string.input_from_time));
            return;
        }
        //出发时间不能小于当前时间
        long mTime = DateUtil.dateDiff(tvFromTime.getText().toString().trim(),DateUtil.dateToStr(new Date()) + "","yyyy-MM-dd","m");
        if (mTime > 0) {
            //校验用户是否选择出发时间
            displayToast(getString(R.string.input_from_time_tatyday));
            return;
        }

        if (StringUtils.isEmpty(tvFromArea.getText().toString().trim()) || tvFromArea.getText().toString().trim().contains("请选择")) {
            //校验用户是否选择出发地区
            displayToast(getString(R.string.input_from_area));
            return;
        }

        if (RegexpUtils.validatePositiveInteger(tvToTime.getText().toString().trim()) || tvToTime.getText().toString().trim().contains("请选择")) {
            //校验用户是否选择到达时间
            displayToast(getString(R.string.input_to_time));
            return;
        }
        // 到达时间不能小于出发时间
        long mTime1 = DateUtil.dateDiff(tvToTime.getText().toString().trim(),tvFromTime.getText().toString().trim(),"yyyy-MM-dd","m");
        if ( mTime1 > 0 ) {
            //校验用户是否选择出发地区
            displayToast(getString(R.string.input_from_time_day));
            return;
        }

        if (StringUtils.isEmpty(tvToArea.getText().toString().trim()) || tvToArea.getText().toString().trim().contains("请选择")) {
            //校验用户是否选择目的地
            displayToast(getString(R.string.input_to_area));
            return;
        }
        if (StringUtils.isEmpty(tvCarMode.getText().toString().trim())|| tvCarMode.getText().toString().trim().contains("请选择")) {
            //校验用户是否选择车辆类型
            displayToast(getString(R.string.input_car_mode));
            return;
        }

        if (StringUtils.isEmpty(etCarLength.getText().toString().trim())) {
            //校验用户是否输入车辆长度
            displayToast("请输入车辆长度");
            return;
        }
        String carLength = etCarLength.getText().toString().trim();
        if (carLength.equals("0") || carLength.equals("0.0") || carLength.equals("0.00")) {
            //车辆长度
            displayToast("请输入正确的车辆长度");
            return;
        }

        if (StringUtils.isEmpty(etCarLength.getText().toString().trim())) {
            //校验载重
            displayToast(getString(R.string.input_procurement_number));
            return;
        }
        String carload = etCarLoad.getText().toString().trim();
        if (carload.equals("0") || carload.equals("0.0") || carload.equals("0.00")) {
            //请输入正确的载重量
            displayToast("请输入正确的载重量");
            return;
        }

        if (StringUtils.isEmpty(etContacts.getText().toString().trim())) {
            //校验联系人
            displayToast("请输入联系人");
            return;
        }

        if (StringUtils.isEmpty(etPhone.getText().toString().trim())) {
            //校验联系人
            displayToast("请输入联系人电话");
            return;
        }
        if (BaseUtils.isMobileNO(etPhone.getText().toString().trim()) != true) {
            //校验联系人
            displayToast("请输入正确的联系人电话");
            return;
        }
        dataRelease();
    }

    /**
     * 发布联网提交
     */
    private void dataRelease() {
        try {
            setParameter();
            new DataUtils(this,params).getUserDemandBBSCreate(new DataUtils.DataBack<APPData>() {
                @Override
                public void getData(APPData data) {
                    try {
                        if (data != null) {
                            displayToast(data.getMessage());
                        }
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    /**
     * 获取用户输入信息
     */
    private void setParameter() {
        params.clear();
        // 出发时间
        params.put("startTime", tvFromTime.getText().toString().trim());
        // 出发地区
        params.put("fromPlace", startCode);
        // 到达时间
        params.put("endTime", tvToTime.getText().toString().trim());
        // 到达地区
        params.put("toPlace", endCode);
        // 车型
        params.put("vehicleModels", selectedId);
        // 车长
        params.put("vehicleLength", etCarLength.getText().toString().trim());
        // 载重
        params.put("vehicleLoad", etCarLoad.getText().toString().trim());
        // 联系人姓名
        params.put("contactPerson", etContacts.getText().toString().trim());
        // 联系人电话
        params.put("contactPhone", etPhone.getText().toString().trim());

    }

    private void showCarModeView () {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_car_mode_popup_window, null);
        initListView(view);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.showAsDropDown(carModeView);
    }

    private void initListView (View view){
        /**
         * 初始化车型选择器
         */
        Dictionary dictionary = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100005"}).get(0);
        carModeList = dictionary.list;
        listView = (CustomListView) view.findViewById(R.id.listview);
        mAdapter = new QuickAdapter<FilterEntity>(mContext, R.layout.item_car_mode_popup_window, carModeList) {
            @Override
            protected void convert(BaseAdapterHelper helper, FilterEntity item, int position) {
                if (position == 0) {
                    helper.getView().findViewById(R.id.devider).setVisibility(View.GONE);
                } else {
                    helper.getView().findViewById(R.id.devider).setVisibility(View.VISIBLE);
                }
                helper.setText(R.id.tv_item, item.dictValue);
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvCarMode.setText(carModeList.get(position).dictValue);
                selectedId = carModeList.get(position).dictCode;
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    ivCarMode.setImageResource(R.mipmap.icon_arrow_down);
                }
            }
        });
    }

    private void showTimePopWin(final TextView TV,final ImageView IV,View view){
        String dufaultTime = TV.getText().toString().trim();
        if (dufaultTime.contains("请选择")){
            dufaultTime = DateUtil.getNewTimeType("yyyy-MM-dd");
        }
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                TV.setText(dateDesc);
                displayToast(dateDesc);
                IV.setImageResource(R.mipmap.icon_arrow_up);
            }
            @Override
            public void onDismissPopWin() {
                IV.setImageResource(R.mipmap.icon_arrow_down);
            }
        }).textConfirm("确定") //text of confirm button
                .textCancel("取消") //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(20) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#1e7aea"))//color of confirm button
                .minYear(Integer.valueOf(DateUtil.getNewTimeType("yyyy"))) //min year in loop
                .maxYear(2550) // max year in loop
                .dateChose(dufaultTime) // date chose when init popwindow  2013-11-11
                .build();
        pickerPopWin.showPopupWindow(this,view);
    }

    private void createAreaWindow() {
        areaPopupWindow = new SelectAreaPopupWindow(this, 0);
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                switch (currentType) {
                    case TYPE_START:
                        ivFrom.setImageResource(R.mipmap.icon_arrow_down);
                        break;
                    case TYPE_END:
                        ivTo.setImageResource(R.mipmap.icon_arrow_down);
                        break;
                }
                startCode = areaPopupWindow.getStartCode();
                endCode = areaPopupWindow.getEndCode();
            }
        });
    }

}

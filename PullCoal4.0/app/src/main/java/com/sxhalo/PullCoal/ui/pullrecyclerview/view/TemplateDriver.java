package com.sxhalo.PullCoal.ui.pullrecyclerview.view;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.DetaileFriendActivity;
import com.sxhalo.PullCoal.activity.DriverListAcrivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.CustomeProgressBar;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 司机展示列表
 * Created by amoldZhang on 2018/12/18.
 */
public class TemplateDriver extends BaseView{

    @Bind(R.id.recommend_title)
    TextView recommendTitle;
    @Bind(R.id.home_recommend_lv)
    CustomListView homeRecommendLv;
    private View mRootView;
    private List<UserEntity> myListData = new ArrayList();
    private QuickAdapter<UserEntity> mAdapter;

    public TemplateDriver(Activity context) {
        super(context);
        initView();
    }

    private void initView() {
        mRootView = View.inflate(mContext, R.layout.home_pager_item, null);
        ButterKnife.bind(this, mRootView);
        dealWithTheView();

        addTemplateView();
    }

    @Override
    public void setData(BaseModule module) {
        fillData(module);
    }

    @Override
    public void fillData(BaseModule module) {
        if (module == null || !(module instanceof HomeData))
        {
            return;
        }

        if (((HomeData) module).getDriverList().size() != 0){
            mAdapter.replaceAll(((HomeData) module).getDriverList());
        }
//        this.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onItemClick(module);
//            }
//        });
        invalidate();
    }

    @Override
    public void addTemplateView() {
//        LayoutParams lp = new LayoutParams(-1, (int) (mContext.getResources().getDisplayMetrics().density * 40));
//        mRootView.setLayoutParams(lp);
        addView(mRootView);
    }

    @Override
    public void reFresh() {

    }

    private void dealWithTheView() {
        GHLog.i("明星司机展示", myListData.size() + "");
        recommendTitle.setText("明星司机");
        mAdapter = new QuickAdapter<UserEntity>(mContext, R.layout.driver_item, myListData) {
            @Override
            protected void convert(BaseAdapterHelper helper, final UserEntity item, int position) {
                try {
                    String derverName = "";
                    derverName = item.getRealName();
                    helper.setText(R.id.drivert_name_num, derverName);

                    ((BaseActivity) mContext).getImageManager().loadUrlDerverListView(item.getHeadPic(), (ImageView) helper.getView().findViewById(R.id.recommend_drivder_image));

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
                        carLoad = carLoad + "吨";
                    }

                    //是否专线司机
                    if ("1".equals(item.getSpecialLine())) {
                        helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.VISIBLE);
                    } else {
                        helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.GONE);
                    }

                    //车长 、车型、载重
                    helper.setText(R.id.drivert_car_content, carLength + vehicleModels + "  " + carLoad);

                    ImageView driverStatus = (ImageView) helper.getView().findViewById(R.id.driver_current_status);
                    if (item.getDriverState().equals("2")) { //忙碌
                        driverStatus.setImageResource(R.mipmap.be_busy);
                    } else{ //空闲
                        driverStatus.setImageResource(R.mipmap.free);
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
                    TextView isFriend = (TextView) helper.getView().findViewById(R.id.tv_is_friend);
                    if (item.getIsFriend().equals("0")) {
                        isFriend.setVisibility(View.GONE);
                    } else {
                        isFriend.setVisibility(View.VISIBLE);
                    }

                    //设置车牌号码
                    TextView tvLicencePlate = (TextView)helper.getView().findViewById(R.id.tv_licence_plate);
                    if (StringUtils.isEmpty(item.getPlateNumber())) {
                        tvLicencePlate.setBackgroundDrawable(null);
                        tvLicencePlate.setText("未填写");
                        tvLicencePlate.setTextColor(mContext.getResources().getColor(R.color.setting_alt_text_normal));
                    } else {
                        tvLicencePlate.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_plate_number));
                        tvLicencePlate.setText(StringUtils.setPlateNumber(item.getPlateNumber()));
                        tvLicencePlate.setTextColor(mContext.getResources().getColor(R.color.text_color_plate_number));
                    }

                    CustomeProgressBar myProgressBar = (CustomeProgressBar) helper.getView().findViewById(R.id.text_progressbar);
                    myProgressBar.setShowText(true);
                    myProgressBar.setProgress(Integer.parseInt(StringUtils.isEmpty(item.getPercentage()) ? "0" : item.getPercentage()));
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("明星司机展示", e.toString());
                }
            }
        };
        homeRecommendLv.setAdapter(mAdapter);
        homeRecommendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(mContext,DetaileFriendActivity.class);
                intent.putExtra("UserEntity",mAdapter.getDataList().get(position));
                mContext.startActivityForResult(intent, Constant.REFRESH_CODE);
            }
        });
    }

    @OnClick({R.id.more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more:
                //点击后跳转到司机列表界面
                Intent myIntent = new Intent();
                myIntent.setClass(mContext, DriverListAcrivity.class);
                mContext.startActivity(myIntent);
                break;
        }
    }
}

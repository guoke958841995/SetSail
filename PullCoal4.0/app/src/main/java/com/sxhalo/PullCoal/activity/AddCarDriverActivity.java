package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.CustomeProgressBar;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 从好友列表中添加派车司机
 * Created by amoldZhang on 2018/5/21.
 */

public class AddCarDriverActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<UserEntity> {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.my_friend_list)
    SmoothListView listView;
    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;

    @Bind(R.id.botton_ll)
    LinearLayout bottonLL;

    private BaseAdapterUtils<UserEntity> baseAdapterUtils;
    private ArrayList<SendCarEntity> listFromIntent = new ArrayList<>();//从前一个界面带来的数据
    private ArrayList<UserEntity> listSelect = new ArrayList<>();//当前界面选择的数据
    private ArrayList<SendCarEntity> tempList = new ArrayList<>();//临时数据

    public List<UserEntity> friends = new ArrayList<>();
    private int currentPage = 1;
    private int number; // 还需派车数

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_car_driver);
    }

    @Override
    protected void initTitle() {
        title.setText("可派车好友");
        listFromIntent = (ArrayList<SendCarEntity>) getIntent().getSerializableExtra("list");
        number = getIntent().getIntExtra("number",0);
        initView();
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(this, listView);
        baseAdapterUtils.settingList(false, true);
        baseAdapterUtils.setViewItemData(R.layout.driver_item, friends);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("isCanSend", "1");  //  1：可派车好友 0：所有好友
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this, params).getUserFriendList(new DataUtils.DataBack<APPDataList<UserEntity>>() {
                @Override
                public void getData(APPDataList<UserEntity> dataMemager) {

                    if (dataMemager == null) {
                        return;
                    }
                    resetData(dataMemager.getList());
                    if (friends.size() > 0) {
                        bottonLL.setVisibility(View.VISIBLE);
                    }
                    baseAdapterUtils.refreshData(friends);
                    showEmptyView(baseAdapterUtils.getListData().size(), relativeLayout, listView);
                }

                @Override
                public void getError(Throwable e) {
                    showEmptyView(baseAdapterUtils.getListData().size(), relativeLayout, listView);
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    private void resetData(List<UserEntity> list) {
        if (listFromIntent.size() == 0) {
            //前一个界面没有数据
            friends = list;
        } else {
            //前一个界面有数据 判断是否和司机列表是否有重复数据
            for (UserEntity userEntity : list) {
                for (SendCarEntity sendCarEntity : listFromIntent) {
                    if (sendCarEntity.getDriverPhone().equals(userEntity.getUserMobile()) && sendCarEntity.getPlateNumber().equals(userEntity.getPlateNumber())) {
                        userEntity.setSelectUser(true);
                        listSelect.add(userEntity);
                    }
                }
                friends.add(userEntity);
            }
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, final UserEntity userEntity, int pos) {
        try {
            String derverName = "";
            derverName = userEntity.getRealName();
            helper.setText(R.id.drivert_name_num, derverName);
            ImageView imageView = (ImageView) helper.getView().findViewById(R.id.recommend_drivder_image);
            // 给 ImageView 设置一个 tag
            imageView.setTag(userEntity.getHeadPic());
            // 预设一个图片
            imageView.setImageResource(R.mipmap.main_tab_item);
            // 通过 tag 来防止图片错位
            if (imageView.getTag() != null && imageView.getTag().equals(userEntity.getHeadPic())) {
                ((BaseActivity) mContext).getImageManager().loadUrlDerverView(userEntity.getHeadPic(), imageView);
            }

            // 设置认证百分比
            CustomeProgressBar myProgressBar = (CustomeProgressBar) helper.getView().findViewById(R.id.text_progressbar);
            myProgressBar.setShowText(true);
            myProgressBar.setProgress(Integer.parseInt(StringUtils.isEmpty(userEntity.getPercentage()) ? "0" : userEntity.getPercentage()));

            ResetRatingBar dirver_ratingBar = (ResetRatingBar) helper.getView().findViewById(R.id.recommend_drivder_ratingbar1);
//            dirver_ratingBar.setStar(Integer.valueOf(StringUtils.isEmpty(userEntity.getCreditRating()) ? "1" : userEntity.getCreditRating()));
            dirver_ratingBar.setVisibility(View.GONE);

            String carLength = userEntity.getVehicleLength();
            if (StringUtils.isEmpty(carLength)) {
                carLength = "--  ";
            } else {
                carLength = carLength + "米  ";
            }

            Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
            String vehicleModels = "";
            for (FilterEntity filterEntity : sys100005.list) {
                if (filterEntity.dictCode.equals(userEntity.getVehicleMode())) {
                    vehicleModels = filterEntity.dictValue;
                    break;
                }
            }
            if (StringUtils.isEmpty(vehicleModels)) {
                vehicleModels = "--  ";
            } else {
                vehicleModels = vehicleModels + "  ";
            }

            String carLoad = userEntity.getVehicleLoad();
            if (StringUtils.isEmpty(carLoad)) {
                carLoad = "--";
            } else {
                carLoad = "载重" + carLoad + "吨";
            }
            //车长 、车型、载重
            helper.setText(R.id.drivert_car_content, carLength + vehicleModels + "  " + carLoad);

            ImageView driverStatus = (ImageView) helper.getView().findViewById(R.id.driver_current_status);
            if (userEntity.getDriverState().equals("2")) { //忙碌
                driverStatus.setImageResource(R.mipmap.be_busy);
            } else { //空闲
                driverStatus.setImageResource(R.mipmap.free);
            }

            //是否是专线司机
            if (userEntity.getSpecialLine().equals("1")) {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.VISIBLE);
            } else {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.GONE);
            }

            //设置车牌号码
            TextView tvLicencePlate = (TextView)helper.getView().findViewById(R.id.tv_licence_plate);
            if (StringUtils.isEmpty(userEntity.getPlateNumber())) {
                tvLicencePlate.setBackgroundDrawable(null);
                tvLicencePlate.setText("未填写");
                tvLicencePlate.setTextColor(mContext.getResources().getColor(R.color.setting_alt_text_normal));
            } else {
                tvLicencePlate.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_plate_number));
                tvLicencePlate.setText(StringUtils.setPlateNumber(userEntity.getPlateNumber()));
                tvLicencePlate.setTextColor(mContext.getResources().getColor(R.color.text_color_plate_number));
            }

            helper.setText(R.id.drivert_common_singular, userEntity.getCumulative());

            //电话按钮隐藏
            helper.getView().findViewById(R.id.derver_tel).setVisibility(View.GONE);

            //  好友状态判断  直接隐藏
            TextView friendIV = (TextView) helper.getView().findViewById(R.id.tv_is_friend);
            friendIV.setVisibility(View.GONE);

            helper.getView().findViewById(R.id.select_ll_view).setVisibility(View.VISIBLE);
            ImageView IV = (ImageView) helper.getView().findViewById(R.id.iv_select);
            if (userEntity.isSelectUser()) {
                IV.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_selected));
            } else {
                IV.setImageDrawable(getResources().getDrawable(R.mipmap.checkbox_normal));
            }
        } catch (NumberFormatException e) {
            GHLog.i("搜索司机赋值", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserEntity> mAdapter) {
        UserEntity userEntity = mAdapter.getItem(position - 1);
        if (userEntity.isSelectUser()) {
            userEntity.setSelectUser(false);
            listSelect.remove(userEntity);
        } else {
            userEntity.setSelectUser(true);
            listSelect.add(userEntity);
        }
        baseAdapterUtils.getListData().set(position - 1, userEntity);
        mAdapter.replaceAll(baseAdapterUtils.getListData());
    }

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        this.currentPage = page;
        getData();
    }


    @OnClick({R.id.title_bar_left, R.id.select_car_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.select_car_tv:
                try {
                    if (listSelect.size() == 0) {
                        displayToast("您还没有选择需要添加增派的司机");
                        return;
                    }
                    if (listSelect.size() > number){
                        displayToast("您当前选择派车数已经远超出预订吨数，请减少后再提交");
                        return;
                    }
                    SendCarEntity sendCarEntity;
                    for (UserEntity userEntity : listSelect) {
                        sendCarEntity = new SendCarEntity();
                        sendCarEntity.setDriverPhone(userEntity.getUserMobile());
                        sendCarEntity.setDriverId(userEntity.getUserId());
                        sendCarEntity.setDriverRealName(userEntity.getRealName());
                        sendCarEntity.setPlateNumber(userEntity.getPlateNumber());
                        sendCarEntity.setDriverAuthState(userEntity.getDriverAuthState());
                        listFromIntent.add(sendCarEntity);
                    }
                    Set<SendCarEntity> set = new HashSet<>(listFromIntent);
                    listFromIntent.clear();
                    listFromIntent.addAll(set);
                    Intent intent = new Intent();
                    intent.putExtra("list", listFromIntent);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

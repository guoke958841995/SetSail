package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.CustomeProgressBar;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenu;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuCreator;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuItem;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuListView;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的好友列表
 * Created by amoldZhang on 2017/1/19.
 */

public class FriendActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<UserEntity> {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.my_friend_list)
    SmoothListView listView;
    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;

    private BaseAdapterUtils<UserEntity> baseAdapterUtils;

    public List<UserEntity> friends = new ArrayList<>();
    private int currentPage = 1;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friend);
    }

    @Override
    protected void initTitle() {
        title.setText("我的好友");
        initView();
    }

    @Override
    protected void getData() {
        try{
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("currentPage", currentPage + "");
            params.put("isCanSend", "0");
            params.put("pageSize", "10");
            new DataUtils(this,params).getUserFriendList(new DataUtils.DataBack<APPDataList<UserEntity>>() {
                @Override
                public void getData(APPDataList<UserEntity> dataMemager) {

                    if (dataMemager == null) {
                        return;
                    }
                    if (dataMemager.getList() != null) {
                        baseAdapterUtils.refreshData(dataMemager.getList());
                        friends = baseAdapterUtils.getListData();
                    }
                    showEmptyView(friends.size(), relativeLayout, listView);
                }
            });
        }catch (Exception e){
            GHLog.e("联网错误", e.toString());
        }

    }


    private void initView() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DeviceUtil.dp2px(FriendActivity.this, 80));
                deleteItem.setTitle("刪除");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                FriendActivity.this.delatData(position, friends.get(position).getUserId());
                return false;
            }
        });
        baseAdapterUtils = new BaseAdapterUtils(this, listView);
        baseAdapterUtils.setViewItemData(R.layout.driver_item, friends);
        baseAdapterUtils.setBaseAdapterBack(this);

    }

    @OnClick({R.id.title_bar_left, R.id.title_bar_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
        }
    }

    /**
     * 删除好友
     * @param position
     */
    private void delatData(final int position,final String friendId) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("friendId",friendId);
            new DataUtils(this,params).getUserFriendDelete(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String dataMemager) {
                    try {
                        if (StringUtils.isEmpty(dataMemager)){
                            return;
                        }
                        displayToast(dataMemager);
                        baseAdapterUtils.onRefresh();
                        FriendActivity.this.getData();
                        Intent intent = new Intent(HomePagerFragment.RECEIVED_ACTION);
                        intent.putExtra("friendId", friendId);
                        mContext.sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
            CustomeProgressBar myProgressBar = (CustomeProgressBar)helper.getView().findViewById(R.id.text_progressbar);
            myProgressBar.setShowText(true);
            myProgressBar.setProgress(Integer.parseInt(StringUtils.isEmpty(userEntity.getPercentage())?"0":userEntity.getPercentage()));
            ResetRatingBar dirver_ratingBar = (ResetRatingBar) helper.getView().findViewById(R.id.recommend_drivder_ratingbar1);
            dirver_ratingBar.setStar(Integer.valueOf(StringUtils.isEmpty(userEntity.getCreditRating())?"1":userEntity.getCreditRating()));

            String carLength = userEntity.getVehicleLength();
            if (StringUtils.isEmpty(carLength)) {
                carLength = "--  ";
            } else {
                carLength = carLength + "米  ";
            }

            Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100005"}).get(0);
            String vehicleModels = "";
            for (FilterEntity filterEntity : sys100005.list){
                if (filterEntity.dictCode.equals(userEntity.getVehicleMode())){
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
            helper.setText(R.id.drivert_car_content,carLength + vehicleModels + "  " + carLoad);

            ImageView driverStatus = (ImageView) helper.getView().findViewById(R.id.driver_current_status);
            if (userEntity.getDriverState().equals("2")) { //忙碌
                driverStatus.setImageResource(R.mipmap.be_busy);
            } else{ //空闲
                driverStatus.setImageResource(R.mipmap.free);
            }

            //是否是专线司机
            if (userEntity.getSpecialLine().equals("1")) {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.VISIBLE);
            } else {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.GONE);
            }

            helper.setText(R.id.drivert_common_singular, userEntity.getCumulative());

            helper.getView().findViewById(R.id.derver_tel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("tel",userEntity.getUserMobile());
                    map.put("callType", Constant.CALE_TYPE_DRIVER);
                    map.put("targetId",userEntity.getUserId());
                    UIHelper.showCollTel(mContext, map,true);
                }
            });

            //  好友状态判断
            TextView friendIV = (TextView) helper.getView().findViewById(R.id.tv_is_friend);
            friendIV.setVisibility(View.VISIBLE);

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

        } catch (NumberFormatException e) {
            GHLog.i("搜索司机赋值", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserEntity> mAdapter) {
        if (friends.size() != 0) {
            Intent intent = new Intent();
            intent.setClass(FriendActivity.this, DetaileFriendActivity.class);
            intent.putExtra("UserEntity", mAdapter.getItem(position - 1));
            startActivity(intent);
        }
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
}

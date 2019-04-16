package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.CustomeProgressBar;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 司机详情
 * Created by amoldZhang on 2016/12/16.
 */
public class DetaileFriendActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.recommend_drivder_image)
    CircleImageView recommendDrivderImage;
    @Bind(R.id.driver_status)
    ImageView driverStatus;
    @Bind(R.id.drivder_ratingbar)
    ResetRatingBar drivderRatingbar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_is_friend)
    TextView tvIsFriend;
    @Bind(R.id.drivert_common_singular)
    TextView drivertCommonSingular;
    @Bind(R.id.tv_plate_number)
    TextView tvPlateNumber;
    @Bind(R.id.tv_vehicle_brand)
    TextView tvVehicleBrand;
    @Bind(R.id.tv_vehicle_type)
    TextView tvVehicleType;
    @Bind(R.id.tv_vehicle_length_and_load)
    TextView tvVehicleLengthAndLoad;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.region_v)
    TextView regionV;
    @Bind(R.id.add_friend)
    TextView addFriend;
    @Bind(R.id.view_top)
    View viewTop;
    @Bind(R.id.ll_special_line)
    LinearLayout specialLine;
    @Bind(R.id.view_bottom)
    View viewBottom;
    @Bind(R.id.tv_special_line_from)
    TextView tvSpecialLineFrom;
    @Bind(R.id.tv_special_line_to)
    TextView tvSpecialLineTo;
    @Bind(R.id.tv_progress)
    TextView tvProgress;
    @Bind(R.id.progressbar)
    CustomeProgressBar progressBar;
    @Bind(R.id.tv_tips)
    TextView tvTips;


    private UserEntity userEntity;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_friend_business_card);
        userEntity = (UserEntity) getIntent().getSerializableExtra("UserEntity");
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("targetId", userEntity.getUserId());
            params.put("browseType", "2");
            new DataUtils(this,params).getUserBrowseRecordCreate(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initTitle() {
        title.setText("司机详情");
    }

    @Override
    protected void getData() {
        try {
            userEntity = (UserEntity) getIntent().getSerializableExtra("UserEntity");
            String userId = SharedTools.getStringValue(this, "userId", "-1");
            if (!userId.equals("-1")) {
                String friendState = userEntity.getIsFriend() == null ? "0" : userEntity.getIsFriend();
                if (friendState.equals("1") || userEntity.getUserId().equals(userId)) {
                    //是好友或者自己的時候不显示
                    addFriend.setVisibility(View.GONE);
                } else {
                    addFriend.setVisibility(View.VISIBLE);
                }
            } else {
                addFriend.setVisibility(View.GONE);
            }
            setDriverView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDriverView() {
        try {
            getImageManager().loadUrlImageView(userEntity.getHeadPic(), recommendDrivderImage);
            tvName.setText(userEntity.getRealName());
            if (userEntity.getDriverState().equals("2")) { //忙碌
                driverStatus.setImageResource(R.mipmap.be_busy);
            } else{ //空闲
                driverStatus.setImageResource(R.mipmap.free);
            }

            drivderRatingbar.setStar(Integer.valueOf(StringUtils.isEmpty(userEntity.getCreditRating()) ? "1" : userEntity.getCreditRating()));
            drivderRatingbar.setVisibility(View.VISIBLE);

            if (!userEntity.getIsFriend().equals("0")) {
                tvIsFriend.setVisibility(View.VISIBLE);
            }

            //设置认证百分比
            int percentage = Integer.parseInt(StringUtils.isEmpty(userEntity.getPercentage())?"0":userEntity.getPercentage());
            progressBar.setShowText(false);
            progressBar.setProgress(percentage);

            if (percentage <= 40) {
                //信任值偏低
                tvProgress.setTextColor(getResources().getColor(R.color.dark_yellow));
                tvTips.setText("信任值偏低");
                tvTips.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else if (percentage == 60) {
                //信任值良好
                tvTips.setText("信任值良好");
                tvTips.setTextColor(getResources().getColor(R.color.search_text_color_green));
                tvProgress.setTextColor(getResources().getColor(R.color.search_text_color_green));
            } else{
                //信任值较高
                tvTips.setText("信任值较高");
                tvTips.setTextColor(getResources().getColor(R.color.search_text_color_green));
                tvProgress.setTextColor(getResources().getColor(R.color.search_text_color_green));
            }
            tvProgress.setText(percentage + "%");
            drivertCommonSingular.setText(userEntity.getCumulative());
            drivertCommonSingular.setVisibility(View.VISIBLE);
            //车牌号码
            if (StringUtils.isEmpty(userEntity.getPlateNumber())){
                tvPlateNumber.setText("未提供");
            } else {
                tvPlateNumber.setText(StringUtils.setPlateNumber(userEntity.getPlateNumber()));
                tvPlateNumber.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_plate_number));
            }
            //车辆品牌
            tvVehicleBrand.setText(StringUtils.isEmpty(userEntity.getBrandName())?"未提供":userEntity.getBrandName());
            Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
            String vehicleModel = "未提供";
            for (FilterEntity filterEntity : sys100005.list) {
                if (filterEntity.dictCode.equals(userEntity.getVehicleMode())) {
                    vehicleModel = filterEntity.dictValue;
                    break;
                }
            }
            tvVehicleType.setText(vehicleModel);
            String carLong = userEntity.getVehicleLength();
            if (TextUtils.isEmpty(carLong)) {
                carLong = "未提供 米  ";
            } else {
                carLong = carLong + "米  ";
            }
            String carLoad = userEntity.getVehicleLoad();
            if (TextUtils.isEmpty(carLoad)) {
                carLoad = "未提供 吨";
            } else {
                carLoad = carLoad + "吨";
            }
            tvVehicleLengthAndLoad.setText(carLong + carLoad);

            tvPhone.setText(StringUtils.setPhoneNumber(userEntity.getUserMobile()));

            if (userEntity.getSpecialLine().equals("1")) {
                //1 是专线司机
                viewTop.setVisibility(View.VISIBLE);
                specialLine.setVisibility(View.VISIBLE);
                viewBottom.setVisibility(View.VISIBLE);
                tvSpecialLineFrom.setText(userEntity.getStartRegionName());
                tvSpecialLineTo.setText(userEntity.getEndRegionName());
            } else {
                //0 不是专线司机
                viewTop.setVisibility(View.GONE);
                specialLine.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
            }
            regionV.setText(StringUtils.isEmpty(userEntity.getRegionName()) ? "未设置" : userEntity.getRegionName());
        } catch (Exception e) {
            GHLog.e("司机赋值", e.toString());
        }
    }


    @OnClick({R.id.title_bar_left, R.id.recommend_drivder_image, R.id.add_friend, R.id.tv_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.recommend_drivder_image:
                if (!StringUtils.isEmpty(userEntity.getHeadPic())) {
                    Intent intent = new Intent();
                    intent.putExtra("url", userEntity.getHeadPic());
                    intent.setClass(this, PicturePreviewActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.add_friend:
                addFriend(userEntity.getUserId());
                break;
            case R.id.tv_phone:
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tel", userEntity.getUserMobile());
                    map.put("callType", Constant.CALE_TYPE_DRIVER);
                    map.put("targetId", userEntity.getUserId());
                    UIHelper.showCollTel(DetaileFriendActivity.this, map, true);
                } catch (Exception e) {
                    displayToast("尚未填写电话");
                    GHLog.e("打电话", e.toString());
                }
                break;
//            case R.id.rl_photo:
//                //人车合照
//                Intent intent = new Intent();
//                intent.putExtra("url", userEntity.getCarPhotoUrl());//认证图片
//                intent.setClass(this, PicturePreviewActivity.class);
//                startActivity(intent);
//                break;
        }
    }

    /**
     * 添加好友
     */
    private void addFriend(final String friendId) {
        try {
            String userId = SharedTools.getStringValue(this, "userId", "-1");
            if (userId.equals("-1")) {
                UIHelper.jumpActLogin(DetaileFriendActivity.this,false);
                return;
            }
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("friendId", friendId);
            new DataUtils(this, params).getUserFriendCreate(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String dataMemager) {
                    try {
                        if (StringUtils.isEmpty(dataMemager)) {
                            return;
                        }
                        addFriend.setVisibility(View.GONE);
                        displayToast(dataMemager);
                        tvIsFriend.setVisibility(View.VISIBLE);
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

}

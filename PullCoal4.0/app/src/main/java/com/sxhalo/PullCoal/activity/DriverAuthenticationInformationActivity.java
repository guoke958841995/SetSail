package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 司机认证情况展示界面
 * Created by amoldZhang on 2018/1/8.
 */
public class DriverAuthenticationInformationActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.completion_degree)
    TextView completionDegree;
    @Bind(R.id.image_authentication_prompt)
    ImageView imageAuthenticationPrompt;
    @Bind(R.id.driver_license_image)
    ImageView driverLicenseImage;
    @Bind(R.id.real_name)
    TextView realName;
    @Bind(R.id.driver_license_id)
    TextView driverLicenseId;
    @Bind(R.id.issue_date)
    TextView issueDate;

    @Bind(R.id.vehicle_license_attached)
    ImageView vehicleLicenseAttached;
    @Bind(R.id.plate_number)
    TextView plateNumber;
    @Bind(R.id.owner_of_vehicle)
    TextView ownerOfVehicle;
    @Bind(R.id.vehicle_mode)
    TextView vehicleMode;
    @Bind(R.id.tv_vehicle_length)
    TextView tvVehicleLength;
    @Bind(R.id.tv_vehicle_load)
    TextView tvVehicleLoad;
    @Bind(R.id.registration_of_driving_license)
    TextView registrationOfDrivingLicense;

    private APPData<UserAuthenticationEntity> appData;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_driver_authentication_information);
    }

    @Override
    protected void initTitle() {
        title.setText("司机认证信息");
        appData = (APPData<UserAuthenticationEntity>) getIntent().getSerializableExtra("Entity");
    }

    @Override
    protected void getData() {
        initView();
    }

    private void initView() {
        try {
            if (appData != null){
                //司机认证状态 0 未认证
                if ("0".equals(appData.getEntity().getDriverAuthState())){
                    completionDegree.setText("司机认证尚未通过，资料完善度 "+ appData.getEntity().getPercentage() + " %");
                    imageAuthenticationPrompt.setImageResource(R.mipmap.icon_authentication_prompt);
                }else{
                    completionDegree.setText("司机认证已通过，资料完善度 "+ appData.getEntity().getPercentage() + " %");
                    imageAuthenticationPrompt.setImageResource(R.mipmap.icon_about_app);
                }

                String driverLicenseUrl = appData.getEntity().getDriverLicenseUrl();
                if (!StringUtils.isEmpty(driverLicenseUrl)){
                    getImageManager().loadUrlImageAuthentication(driverLicenseUrl , driverLicenseImage); //驾驶证照片
                }
//                realName.setText(StringUtils.isEmpty(appData.getEntity().getRealName())?"未完善":appData.getEntity().getRealName());
                if (StringUtils.isEmpty(appData.getEntity().getRealName())) {
                    realName.setText("未完善");
                    realName.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    realName.setText(appData.getEntity().getRealName());
                    realName.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }

//                driverLicenseId.setText(StringUtils.isEmpty(appData.getEntity().getDriverLicenseId())?"未完善":appData.getEntity().getDriverLicenseId());
                //实名认证未通过时
                if ("0".equals(appData.getEntity().getRealnameAuthState())){
                    if (StringUtils.isEmpty(appData.getEntity().getDriverLicenseId())) {
                        driverLicenseId.setText("未完善");
                        driverLicenseId.setTextColor(getResources().getColor(R.color.actionsheet_red));
                    } else {
                        driverLicenseId.setText(appData.getEntity().getDriverLicenseId());
                        driverLicenseId.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                    }
                }else{  //实名认证通过时
                    driverLicenseId.setText(appData.getEntity().getIdentitycardId());
                    driverLicenseId.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }


//                issueDate.setText(StringUtils.isEmpty(appData.getEntity().getDriverLicenseInitialTime())?"未完善":appData.getEntity().getDriverLicenseInitialTime());
                if (StringUtils.isEmpty(appData.getEntity().getDriverLicenseInitialTime())) {
                    issueDate.setText("未完善");
                    issueDate.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    issueDate.setText(appData.getEntity().getDriverLicenseInitialTime());
                    issueDate.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }
                String vehicleLicenseHomeUrl = StringUtils.isEmpty(appData.getEntity().getVehicleLicenseHomeUrl())?"":appData.getEntity().getVehicleLicenseHomeUrl();
                if (!vehicleLicenseHomeUrl.equals("")){
                    getImageManager().loadUrlImageAuthentication(vehicleLicenseHomeUrl, vehicleLicenseAttached); //行驶证照片
                }

//                plateNumber.setText(StringUtils.isEmpty(appData.getEntity().getPlateNumber())?"未完善":appData.getEntity().getPlateNumber());
                if (StringUtils.isEmpty(appData.getEntity().getPlateNumber())) {
                    plateNumber.setText("未完善");
                    plateNumber.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    plateNumber.setText(appData.getEntity().getPlateNumber());
                    plateNumber.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }

//                ownerOfVehicle.setText(StringUtils.isEmpty(appData.getEntity().getOwner())?"未完善":appData.getEntity().getOwner());
                if (StringUtils.isEmpty(appData.getEntity().getOwner())) {
                    ownerOfVehicle.setText("未完善");
                    ownerOfVehicle.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    ownerOfVehicle.setText(appData.getEntity().getOwner());
                    ownerOfVehicle.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }

//                vehicleMode.setText(StringUtils.isEmpty(appData.getEntity().getVehicleModeName())?"未完善":appData.getEntity().getVehicleModeName());
                if (StringUtils.isEmpty(appData.getEntity().getVehicleModeName())) {
                    vehicleMode.setText("未完善");
                    vehicleMode.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    vehicleMode.setText(appData.getEntity().getVehicleModeName());
                    vehicleMode.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }

                if (StringUtils.isEmpty(appData.getEntity().getVehicleLength())) {
                    tvVehicleLength.setText("未完善");
                    tvVehicleLength.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    tvVehicleLength.setText(appData.getEntity().getVehicleLength() + "米");
                    tvVehicleLength.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }
                if (StringUtils.isEmpty(appData.getEntity().getVehicleLoad())) {
                    tvVehicleLoad.setText("    未完善");
                    tvVehicleLoad.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    tvVehicleLoad.setText("    " + appData.getEntity().getVehicleLoad() + "吨");
                    tvVehicleLoad.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }

//                registrationOfDrivingLicense.setText(StringUtils.isEmpty(appData.getEntity().getEndRegion())?"未完善":appData.getEntity().getEndRegion());
                if (StringUtils.isEmpty(appData.getEntity().getRegistDate())) {
                    registrationOfDrivingLicense.setText("未完善");
                    registrationOfDrivingLicense.setTextColor(getResources().getColor(R.color.actionsheet_red));
                } else {
                    registrationOfDrivingLicense.setText(appData.getEntity().getRegistDate());
                    registrationOfDrivingLicense.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                }
            }
        } catch (Exception e) {
            GHLog.e("数据赋值",e.toString());
        }
    }


    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }
}

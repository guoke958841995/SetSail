package com.sxhalo.PullCoal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.Result;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.APPMessage;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.ImageUtils;
import com.sxhalo.PullCoal.tools.PhotoSelection;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.IDCardUtil;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 司机认证界面
 */
public class DriverCertificationSubmissionActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;

    @Bind(R.id.drivar_name_text)
    TextView drivarNameText;
    @Bind(R.id.driver_license_number_text)
    TextView driverLicenseNumberText;

    @Bind(R.id.drivar_name)
    EditText drivarName;
    @Bind(R.id.driver_license_number)
    EditText driverLicenseNumber;
    @Bind(R.id.driver_text)
    TextView driverText;
    @Bind(R.id.driver_image)
    ImageView driverImage;

    @Bind(R.id.vehicle_audit)
    LinearLayout vehicleAudit;
    @Bind(R.id.car_num)
    EditText plateNumber;
    @Bind(R.id.car_mode)
    TextView carMode;
    @Bind(R.id.layout_car_mode)
    LinearLayout layoutCarMode;
    @Bind(R.id.car_vehicle_load)
    EditText carVehicleLoad;
    @Bind(R.id.vehicle_load)
    EditText vehicleLoad;
    @Bind(R.id.car_length)
    EditText carLength;

    @Bind(R.id.driving_text)
    TextView drivingText;
    @Bind(R.id.driving_image)
    ImageView drivingImage;

    @Bind(R.id.driver_car_content)
    LinearLayout driverCarContent;
    @Bind(R.id.bottom_ll_view)
    LinearLayout bottomView;

    public   static int DRIVER_REGISTRATION = 10;
    public static int REGISTRATION = 2;
    public static int REGIST = 3;

    private  APPData<UserAuthenticationEntity> appData;
    private PhotoSelection photoSelection;  // 图片选择器
    private Bitmap bmp;  //提示要显示的图片
    private String fileName;  //图片上传时的图片名称

    private String vehicleLicenseHomePicCode ;
    private String vehicleLicenseHomeURL ;

    private String driverLicensePicCode ;
    private String driverLicenseUrl;

    //车型选择
    private CustomListView listView;
    private PopupWindow popupWindow;
    private QuickAdapter<FilterEntity> mAdapter;
    private List<FilterEntity> carModeList = new ArrayList<FilterEntity>();
    private String selectedId;

    private UserEntity userEntity = new UserEntity();
    private String userId;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_driver_certification_submission);
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected void initTitle() {
        appData = (APPData<UserAuthenticationEntity>)getIntent().getSerializableExtra("Entity");
        userId = SharedTools.getStringValue(DriverCertificationSubmissionActivity.this,"userId","-1");
        String dataString =  SharedTools.getStringValue(this,userId+"driver_certific","-1");
        //司机审核状态0审核完成或未进行、1审核中
        if (!dataString.equals("-1")){
            Gson gson = new Gson();
            Type type = new TypeToken<APPData<UserAuthenticationEntity>>(){}.getType();
            appData = gson.fromJson(dataString,type);
        }
        userEntity.setStartRegion(StringUtils.isEmpty(appData.getEntity().getStartRegion())? ""  : appData.getEntity().getStartRegion());
        userEntity.setStartRegionName(StringUtils.isEmpty(appData.getEntity().getStartRegionName())? ""  : appData.getEntity().getStartRegionName());

        userEntity.setEndRegion(StringUtils.isEmpty(appData.getEntity().getEndRegion())? ""  : appData.getEntity().getEndRegion());
        userEntity.setEndRegionName(StringUtils.isEmpty(appData.getEntity().getEndRegionName())? ""  : appData.getEntity().getEndRegionName());

        title.setText("司机资料完善");
        mapType.setText("提交");
        mapType.setVisibility(View.VISIBLE);

        initView();
    }

    /**
     *  界面功能初始化
     */
    private void initView() {
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

        plateNumber.addTextChangedListener(new TextWatcher() {
            int index = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable s) {
                plateNumber.removeTextChangedListener(this);// 解除文字改变事件
                index = plateNumber.getSelectionStart();// 获取光标位置
                String text = s.toString();
                plateNumber.setText(text.toUpperCase());// 转换
//                if (!text.equals("")) {
//                    next_btn.setEnabled(true);
//                    next_btn.setBackgroundResource(R.drawable.button_shape);
//                } else {
//                    next_btn.setEnabled(false);
//                    next_btn.setBackgroundResource(R.drawable.button_shape_normal);
//                }
                plateNumber.setSelection(index);// 重新设置光标位置
                plateNumber.addTextChangedListener(this);// 重新绑定事件
            }
        });

    }


    @Override
    protected void getData() {
        /**
         * 初始化车型选择器
         */
        Dictionary dictionary = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100005"}).get(0);
        carModeList = dictionary.list;
        mAdapter.replaceAll(carModeList);

        setDriverData(appData.getEntity());
        setVehicleData(appData.getEntity());
    }

    /**
     * 驾驶员资料赋值
     */
    private void setDriverData(UserAuthenticationEntity data) {
        try {

            if ("1".equals(data.getRealnameAuthState())){
                // 实名认证成功赋值
                drivarNameText.setText(data.getRealName() == null ? "" : data.getRealName());
                driverLicenseNumberText.setText(StringUtils.isEmpty(data.getIdentitycardId())? "" : data.getIdentitycardId());
                drivarName.setVisibility(View.GONE);
                driverLicenseNumber.setVisibility(View.GONE);
            }else{
                // 实名认证成功赋值
                drivarName.setText(StringUtils.isEmpty(data.getRealName()) ? "" : data.getRealName());
                drivarName.setSelection(drivarName.getText().toString().length());

                driverLicenseNumber.setText(StringUtils.isEmpty(data.getDriverLicenseId()) ? "" : data.getDriverLicenseId());
                driverLicenseNumber.setSelection(driverLicenseNumber.getText().toString().length());

                drivarNameText.setVisibility(View.GONE);
                driverLicenseNumberText.setVisibility(View.GONE);
            }

            if (StringUtils.isEmpty(data.getDriverLicenseUrl())){
                driverText.setVisibility(View.VISIBLE);
                driverImage.setVisibility(View.GONE);
            }else{
                driverText.setVisibility(View.GONE);
                driverImage.setVisibility(View.VISIBLE);
                getImageManager().loadUrlImageAuthentication(data.getDriverLicenseUrl(),driverImage); //驾驶证显示
                driverLicensePicCode = data.getDriverLicensePicCode();
            }

        } catch (Exception e) {
            GHLog.e("驾驶员资料赋值",e.toString());
        }
    }

    /**
     * 车辆资料赋值
     */
    private void setVehicleData(UserAuthenticationEntity data) {
        try {
            // 司机认证成功赋值
            plateNumber.setText(data.getPlateNumber() == null ? "" : data.getPlateNumber());
            plateNumber.setSelection(plateNumber.getText().toString().length());
            carMode.setText(data.getVehicleModeName() == null ? "" : data.getVehicleModeName());

            carVehicleLoad.setText(data.getVehicleWeight() == null ? "" : data.getVehicleWeight());
            carVehicleLoad.setSelection(carVehicleLoad.getText().toString().length());

            vehicleLoad.setText(data.getVehicleLoad() == null ? "" : data.getVehicleLoad());
            vehicleLoad.setSelection(vehicleLoad.getText().toString().length());

            carLength.setText(data.getVehicleLength() == null ? "" : data.getVehicleLength());
            carLength.setSelection(carLength.getText().toString().length());

            if (!StringUtils.isEmpty(data.getVehicleMode())){
                selectedId = data.getVehicleMode();
            }

            if (StringUtils.isEmpty(data.getVehicleLicenseHomeUrl())){
                drivingText.setVisibility(View.VISIBLE);
                drivingImage.setVisibility(View.GONE);
            }else{
                drivingText.setVisibility(View.GONE);
                drivingImage.setVisibility(View.VISIBLE);
                getImageManager().loadUrlImageAuthentication(data.getVehicleLicenseHomeUrl(),drivingImage);//正面
                vehicleLicenseHomePicCode = data.getVehicleLicenseHomePicCode();
            }
        } catch (Exception e) {
            GHLog.e("车辆资料赋值",e.toString());
        }
    }


    @OnClick({R.id.title_bar_left, R.id.map_type, R.id.layout_car_mode, R.id.layout_bottom,R.id.toback_address,R.id.ll_drivers_licence_front ,R.id.ll_driving_licence_front})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                onBack();
                break;
            case R.id.map_type:
                setSubmitted();
                break;
            case R.id.layout_car_mode:
                // 选择车型
                showCarModeView();
                break;
            case R.id.layout_bottom:
                String URL = new Config().getTRUCKSKE_DAND_QUESTIONS();
                UIHelper.showWEB(this, URL, "司机认证说明");
                break;
            case R.id.toback_address:
                Intent intent = new Intent(this,DriverAddressUpDataActivity.class) ;
                intent.putExtra("UserEntity",userEntity);
                startActivityForResult(intent,REGISTRATION);
                break;
            case R.id.ll_drivers_licence_front:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.drivers_licence_front_image);
                selection(driverImage, driverText,bmp, APIConfig.DRIVER_PIC);
                break;
            case R.id.ll_driving_licence_front:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.driving_licence_front_image);
                selection(drivingImage,drivingText, bmp,APIConfig.VEHICLE_HOME_PIC);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
        super.onBackPressed();
    }

    private void onBack(){
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params = setParamsData(params);
        if (params.size() == 0){
            finish();
        }else{
            appData.getEntity().setPlateNumber(plateNumber.getText().toString().trim());
            showDaiLog(this,"您编辑的内容尚未提交，是否需要保存?");
        }
    }

    private void showDaiLog(Activity mActivity, String TEXT){
//        LayoutInflater inflater1 = mActivity.getLayoutInflater();
//        View layout = inflater1.inflate(R.layout.dialog_updata, null);
//        ((TextView)layout.findViewById(R.id.updata_message)).setText(TEXT);
        new RLAlertDialog(mActivity, "系统提示" ,TEXT, "取消",
                "保存", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                finish();
            }
            @Override
            public void onRightClick() {
                Gson gson = new Gson();
                String dataString = gson.toJson(appData);
                SharedTools.putStringValue(DriverCertificationSubmissionActivity.this,userId+"driver_certific",dataString);
                finish();
            }
        }).show();
    }

    /**
     * 提交资料
     *
     */
    private void setSubmitted() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params = setParams(params);
            if (params.size() != 0){
                //当身份证号是不可编辑状态时 不进入此判断
                if (!StringUtils.isEmpty(driverLicenseNumber.getText().toString().trim())){
                    Result result = IDCardUtil.validateIDNum(driverLicenseNumber.getText().toString().trim());
                    if (!result.isLegal()) {
                        displayToast(result.getError());
                        return;
                    }
                    params.put("driverLicenseId",driverLicenseNumber.getText().toString().trim());
                }
                if (StringUtils.isEmpty(plateNumber.getText().toString().trim())){
                    displayToast(getString(R.string.input_licence_plate_number));
                    return;
                }else if (BaseUtils.isPlateNumberNO(plateNumber.getText().toString().trim()) == false) {
                    displayToast(getString(R.string.wrong_licence_plate_number));
                    return;
                }else{
                    params.put("plateNumber",plateNumber.getText().toString().trim());
                }
                new DataUtils(this, params).getUserDriverAuthCreate(new DataUtils.DataBack<APPData>() {
                    @Override
                    public void getData(APPData dataMemager) {
                        try {
                            if (dataMemager == null) {
                                return;
                            }
                            displayToast(dataMemager.getMessage());
                            SharedTools.putStringValue(DriverCertificationSubmissionActivity.this,userId+"driver_certific","-1");
    //                    Intent intent = new Intent(DriverCertificationSubmissionActivity.this, DriverCertificationActivity.class);
    //                    setResult(DriverCertificationActivity.DRIVER_REGISTRATION, intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LinkedHashMap<String, String> setParams(LinkedHashMap<String, String> params) {
        //实名认证通过 1 不通过 0
        if ("0".equals(appData.getEntity().getRealnameAuthState())){
            if (!StringUtils.isEmpty(drivarName.getText().toString().trim())){
                params.put("realName",drivarName.getText().toString().trim());
            }
            if (!StringUtils.isEmpty(driverLicenseNumber.getText().toString().trim())){
                params.put("driverLicenseId",driverLicenseNumber.getText().toString().trim());
            }
            if (StringUtils.isEmpty(params.get("realName")) && StringUtils.isEmpty(params.get("driverLicenseId"))){
                displayToast("您司机姓名或身份证号尚未编辑，不能提交");
                return new LinkedHashMap<String, String>();
            }
        } else {
            params.put("realName",appData.getEntity().getRealName());
            params.put("driverLicenseId",appData.getEntity().getIdentitycardId());
        }

        if (!StringUtils.isEmpty(carMode.getText().toString().trim())){
            params.put("vehicleMode",selectedId + "");
        }
        if (!StringUtils.isEmpty(carVehicleLoad.getText().toString().trim())){
            params.put("vehicleWeight",carVehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(vehicleLoad.getText().toString().trim())){
            params.put("vehicleLoad",vehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(carLength.getText().toString().trim())){
            params.put("vehicleLength",carLength.getText().toString().trim());
        }

        if (!StringUtils.isEmpty(driverLicensePicCode)){
            params.put("driverLicensePicCode",driverLicensePicCode);
        }

        if (!StringUtils.isEmpty(vehicleLicenseHomePicCode)){
            params.put("vehicleLicenseHomePicCode",vehicleLicenseHomePicCode);
        }

        if (userEntity != null){
            if (!StringUtils.isEmpty(userEntity.getStartRegion())){
                params.put("startRegion",userEntity.getStartRegion());
            }
            if (!StringUtils.isEmpty(userEntity.getEndRegion())){
                params.put("endRegion",userEntity.getEndRegion());
            }
        }
        return params;
    }


    private LinkedHashMap<String, String> setParamsData(LinkedHashMap<String, String> params) {
        //实名认证状态 1 通过 0 不通过
        if ("1".equals(appData.getEntity().getRealNameComplete())){

        }else{
            if (!StringUtils.isEmpty(drivarName.getText().toString().trim()) && !(drivarName.getText().toString().trim()).equals(appData.getEntity().getRealName())){
                params.put("realName",drivarName.getText().toString().trim());
                appData.getEntity().setRealName(drivarName.getText().toString().trim());
            }
            if (!StringUtils.isEmpty(driverLicenseNumber.getText().toString().trim()) && !(driverLicenseNumber.getText().toString().trim()).equals(appData.getEntity().getDriverLicenseId())){
                params.put("driverLicenseId",driverLicenseNumber.getText().toString().trim());
                appData.getEntity().setDriverLicenseId(driverLicenseNumber.getText().toString().trim());
            }
        }

        if (!StringUtils.isEmpty(carMode.getText().toString().trim()) && !(carMode.getText().toString().trim()).equals(appData.getEntity().getVehicleModeName())){
            params.put("vehicleMode",selectedId + "");
            appData.getEntity().setVehicleMode(selectedId + "");
            appData.getEntity().setVehicleModeName(carMode.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(carVehicleLoad.getText().toString().trim()) && !(carVehicleLoad.getText().toString().trim()).equals(appData.getEntity().getVehicleWeight())){
            params.put("vehicleWeight",carVehicleLoad.getText().toString().trim());
            appData.getEntity().setVehicleWeight(carVehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(vehicleLoad.getText().toString().trim()) && !(vehicleLoad.getText().toString().trim()).equals(appData.getEntity().getVehicleLoad())){
            params.put("vehicleLoad",vehicleLoad.getText().toString().trim());
            appData.getEntity().setVehicleLoad(vehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(carLength.getText().toString().trim()) && !(carLength.getText().toString().trim()).equals(appData.getEntity().getVehicleLength())){
            params.put("vehicleLength",carLength.getText().toString().trim());
            appData.getEntity().setVehicleLength(carLength.getText().toString().trim());
        }


        if (!StringUtils.isEmpty(driverLicensePicCode) && !(driverLicensePicCode).equals(appData.getEntity().getDriverLicensePicCode())){
            params.put("driverLicensePicCode",driverLicensePicCode);
            appData.getEntity().setDriverLicenseUrl(driverLicenseUrl);
        }

        if (!StringUtils.isEmpty(vehicleLicenseHomePicCode) && !(vehicleLicenseHomePicCode).equals(appData.getEntity().getVehicleLicenseHomePicCode())){
            params.put("vehicleLicenseHomePicCode",vehicleLicenseHomePicCode);
            appData.getEntity().setVehicleLicenseHomeUrl(vehicleLicenseHomeURL);
        }

        if (userEntity != null){
            if (!StringUtils.isEmpty(userEntity.getStartRegion()) && !(userEntity.getStartRegion()).equals(appData.getEntity().getStartRegion())){
                params.put("startRegion",userEntity.getStartRegion());
                appData.getEntity().setStartRegion(userEntity.getStartRegion());
                appData.getEntity().setStartRegionName(userEntity.getStartRegionName());
            }
            if (!StringUtils.isEmpty(userEntity.getEndRegion()) && !(userEntity.getEndRegion()).equals(appData.getEntity().getEndRegion())){
                params.put("endRegion",userEntity.getEndRegion());
                appData.getEntity().setEndRegion(userEntity.getEndRegion());
                appData.getEntity().setEndRegionName(userEntity.getEndRegionName());
            }
        }
        return params;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                //添加图片返回
                if (data != null && requestCode == PhotoSelection.SELECT_IMAGE_RESULT_CODE) {
                    photoSelection.setUploadImage(data, fileName);
                }
            }

            if (requestCode == REGISTRATION){
                if (data != null) {
                    userEntity = (UserEntity) data.getSerializableExtra("UserEntity");
                }
            }
        } catch (Exception e) {
            GHLog.i("onActivityResult", e.toString());
        }
    }

    private void selection(final ImageView imageView, final TextView textView,Bitmap bm, String fileName) {
        this.fileName = fileName;
        photoSelection = new PhotoSelection(this,1, false);
        photoSelection.setSelection(true, bm);
        photoSelection.onCallBack(new PhotoSelection.Callback<APPMessage>() {
            @Override
            public void onCallBack(APPMessage data, String fileUrl) {
                try {
                    if (data == null){
                        return;
                    }
                    setDataView(data,imageView);
                    //获取图片缩略图，避免OOM
                    Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(getApplicationContext()) , ImageUtils.getWidth(getApplicationContext()) );
                    if (imageView != null){
                        imageView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                        imageView.setImageBitmap(bitmap);
                    }
                    displayToast(getString(R.string.upload_pic_success));
                } catch (Exception e) {
                    GHLog.i("图片提交反回",e.toString());
                }
            }
            @Override
            public void onError(Throwable e, String imagePhth) {
                displayToast("图片上传失败！");
            }
        });
    }

    private void setDataView(APPMessage data, ImageView imageView) {
        try {
            if (imageView == driverImage){
                if (StringUtils.isEmpty(data.getDriverLicenseUrl())){
                    driverText.setVisibility(View.VISIBLE);
                    driverImage.setVisibility(View.GONE);
                }else{
                    driverLicensePicCode = data.getDriverLicensePicCode();
                    driverLicenseUrl = data.getDriverLicenseUrl();
                    driverText.setVisibility(View.GONE);
                    driverImage.setVisibility(View.VISIBLE);
                }
            }else{
                if (StringUtils.isEmpty(data.getVehicleLicenseHomeUrl())){
                    drivingText.setVisibility(View.VISIBLE);
                    drivingImage.setVisibility(View.GONE);
                }else{
                    drivingText.setVisibility(View.GONE);
                    drivingImage.setVisibility(View.VISIBLE);
                    vehicleLicenseHomePicCode = data.getVehicleLicenseHomePicCode();
                    vehicleLicenseHomeURL = data.getVehicleLicenseHomeUrl();
                }
            }
        } catch (Exception e) {
            GHLog.e("图片上传成功后更新界面",e.toString());
        }
    }

    private void showCarModeView () {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_car_mode_popup_window, null);
        listView = (CustomListView) view.findViewById(R.id.listview);
        initListView();
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.showAsDropDown(layoutCarMode);
    }

    private void initListView (){
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                carMode.setText(carModeList.get(position).dictValue);
                selectedId = carModeList.get(position).dictCode;
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

    }
}

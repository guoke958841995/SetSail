package com.sxhalo.PullCoal.activity;

import android.annotation.SuppressLint;
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

import com.lzy.imagepicker.ImagePicker;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.APPMessage;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.ImageUtils;
import com.sxhalo.PullCoal.tools.PhotoSelection;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 车辆添加界面
 * Created by amoldZhang on 2018/1/18.
 */
public class VehicleInformationEditingActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;

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
    @Bind(R.id.drivingText)
    TextView drivingText;
    @Bind(R.id.drivingImage)
    ImageView drivingImage;


    private APPData<UserAuthenticationEntity> appData;
    //车型选择
    private CustomListView listView;
    private PopupWindow popupWindow;
    private QuickAdapter<FilterEntity> mAdapter;
    private List<FilterEntity> carModeList = new ArrayList<FilterEntity>();
    private String selectedId = "";
    private Bitmap bmp;
    private PhotoSelection photoSelection;
    private String fileName;
    private int position;

    private String vehicleLicenseHomePicCode ;
    private String vehicleLicenseHomeURL ;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_vehicle_information_editing);
    }

    @Override
    protected void initTitle() {
        position = getIntent().getIntExtra("position",-1);
        if (position == -1){
            title.setText("添加车辆信息");
        }else{
            appData = (APPData<UserAuthenticationEntity>)getIntent().getSerializableExtra("Entity");
            String  titleText = getIntent().getStringExtra("title");
            title.setText(titleText);
        }
        mapType.setText("保存");
        mapType.setVisibility(View.VISIBLE);
        initView();
    }

    @Override
    protected void getData() {
        /**
         * 初始化车型选择器
         */
        Dictionary dictionary = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100005"}).get(0);
        carModeList = dictionary.list;
        mAdapter.replaceAll(carModeList);

        if (appData != null && position != -1) {
            setVehicleData(appData.getList().get(position));
        }
    }

    /**
     * 车辆资料赋值
     */
    private void setVehicleData(UserAuthenticationEntity data) {
        // 司机认证成功赋值
        plateNumber.setText(data.getPlateNumber() == null ? "" : data.getPlateNumber());
        plateNumber.setSelection(plateNumber.getText().toString().length());

        carMode.setText(data.getVehicleModeName() == null ? "" : data.getVehicleModeName());
        selectedId = StringUtils.isEmpty(data.getVehicleMode())?"":data.getVehicleMode();

        carVehicleLoad.setText(data.getVehicleWeight() == null ? "" : data.getVehicleWeight());
        carVehicleLoad.setSelection(carVehicleLoad.getText().toString().length());

        vehicleLoad.setText(data.getVehicleLoad() == null ? "" : data.getVehicleLoad());
        vehicleLoad.setSelection(vehicleLoad.getText().toString().length());

        carLength.setText(data.getVehicleLength() == null ? "" : data.getVehicleLength());
        carLength.setSelection(carLength.getText().toString().length());

        if (StringUtils.isEmpty(data.getVehicleLicenseHomeUrl())){
            drivingText.setVisibility(View.VISIBLE);
            drivingImage.setVisibility(View.GONE);
        }else{
            drivingText.setVisibility(View.GONE);
            drivingImage.setVisibility(View.VISIBLE);
            getImageManager().loadUrlImageAuthentication(data.getVehicleLicenseHomeUrl(),drivingImage); //司机认证图片展示显示
            vehicleLicenseHomePicCode = data.getVehicleLicenseHomePicCode();
        }
    }

    /**
     * 界面功能初始化
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

    private void showCarModeView() {
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

    private void initListView() {
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


    @OnClick({R.id.title_bar_left, R.id.map_type,R.id.layout_car_mode,R.id.ll_driving_licence_front})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                setSubmitted();
                break;
            case R.id.layout_car_mode:
                // 选择车型
                showCarModeView();
                break;
            case R.id.ll_driving_licence_front:
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.driving_licence_front_image);
                selection(drivingImage,drivingText, bmp, APIConfig.VEHICLE_HOME_PIC);
                break;
        }
    }

    private void selection(final ImageView imageView, final TextView textView, Bitmap bm, String fileName) {
        this.fileName = fileName;
        photoSelection = new PhotoSelection(this, 1, false);
        photoSelection.setSelection(true, bm);
        photoSelection.onCallBack(new PhotoSelection.Callback<APPMessage>() {
            @Override
            public void onCallBack(APPMessage data, String fileUrl) {
                try {
                    if (data == null) {
                        return;
                    }

                //获取图片缩略图，避免OOM
                    Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(getApplicationContext()) , ImageUtils.getWidth(getApplicationContext()) );
                    if (imageView != null) {
                        imageView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.GONE);
                        imageView.setImageBitmap(bitmap);

                    }
                    if (StringUtils.isEmpty(data.getVehicleLicenseHomeUrl())){
                        drivingText.setVisibility(View.VISIBLE);
                        drivingImage.setVisibility(View.GONE);
                    }else{
                        drivingText.setVisibility(View.GONE);
                        drivingImage.setVisibility(View.VISIBLE);
                        vehicleLicenseHomePicCode = data.getVehicleLicenseHomePicCode();
                        vehicleLicenseHomeURL = data.getVehicleLicenseHomeUrl();
                    }
                    displayToast(getString(R.string.upload_pic_success));
                } catch (Exception e) {
                    GHLog.i("图片提交反回", e.toString());
                }
            }

            @Override
            public void onError(Throwable e, String imagePhth) {
                displayToast("图片上传失败！");
            }
        });
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
        } catch (Exception e) {
            GHLog.i("onActivityResult", e.toString());
        }
    }


    /**
     * 提交资料
     */
    private void setSubmitted() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            if (position == -1){
                params = setParams(params);  // 添加
            }else{
                params = setParamsAppData(params);  // 编辑
            }
            if (params.size() != 0) {
                if (StringUtils.isEmpty(plateNumber.getText().toString().trim())){
                    displayToast(getString(R.string.input_licence_plate_number));
                    return ;
                }else if (BaseUtils.isPlateNumberNO(plateNumber.getText().toString().trim()) == false) {
                    displayToast(getString(R.string.wrong_licence_plate_number));
                    return ;
                }else{
                    params.put("plateNumber",plateNumber.getText().toString().trim());
                }
                new DataUtils(this, params).getUserVehiclesCreate(new DataUtils.DataBack<APPData>() {
                    @Override
                    public void getData(APPData dataMemager) {
                        try {
                            if (dataMemager == null) {
                                return;
                            }
                            displayToast(dataMemager.getMessage());
                            setResult(RESULT_OK);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }else{
                displayToast("您还未编辑内容，不能保存！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private LinkedHashMap<String, String> setParams(LinkedHashMap<String, String> params) {

        if (!StringUtils.isEmpty(plateNumber.getText().toString().trim()) ){
            params.put("plateNumber",plateNumber.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(carMode.getText().toString().trim()) && !StringUtils.isEmpty(selectedId)){
            params.put("vehicleMode",selectedId + "");
        }
        if (!StringUtils.isEmpty(carVehicleLoad.getText().toString().trim()) ){
            params.put("vehicleWeight",carVehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(vehicleLoad.getText().toString().trim()) ){
            params.put("vehicleLoad",vehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(carLength.getText().toString().trim()) ){
            params.put("vehicleLength",carLength.getText().toString().trim());
        }

        if (!StringUtils.isEmpty(vehicleLicenseHomePicCode)){
            params.put("vehicleLicenseHomePicCode",vehicleLicenseHomePicCode);
        }
        return params;
    }

    public LinkedHashMap<String,String> setParamsAppData(LinkedHashMap<String,String> paramsAppData) {
        if (!StringUtils.isEmpty(plateNumber.getText().toString().trim()) && !(plateNumber.getText().toString().trim()).equals(appData.getEntity().getPlateNumber())){
            paramsAppData.put("plateNumber",plateNumber.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(carMode.getText().toString().trim()) && !(carMode.getText().toString().trim()).equals(appData.getEntity().getVehicleModeName())&& !StringUtils.isEmpty(selectedId)){
            paramsAppData.put("vehicleMode",selectedId + "");
        }
        if (!StringUtils.isEmpty(carVehicleLoad.getText().toString().trim()) && !(carVehicleLoad.getText().toString().trim()).equals(appData.getEntity().getVehicleWeight())){
            paramsAppData.put("vehicleWeight",carVehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(vehicleLoad.getText().toString().trim()) && !(vehicleLoad.getText().toString().trim()).equals(appData.getEntity().getVehicleLoad())){
            paramsAppData.put("vehicleLoad",vehicleLoad.getText().toString().trim());
        }
        if (!StringUtils.isEmpty(carLength.getText().toString().trim()) && !(carLength.getText().toString().trim()).equals(appData.getEntity().getVehicleLength())){
            paramsAppData.put("vehicleLength",carLength.getText().toString().trim());
        }

        if (!StringUtils.isEmpty(vehicleLicenseHomePicCode)){
            paramsAppData.put("vehicleLicenseHomePicCode",vehicleLicenseHomePicCode);
        }
        return paramsAppData;
    }
}

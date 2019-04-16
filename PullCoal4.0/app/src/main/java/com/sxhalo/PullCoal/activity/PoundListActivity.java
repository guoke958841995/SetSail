package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.ImageUtils;
import com.sxhalo.PullCoal.tools.PhotoSelection;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2017/10/24.
 */

public class PoundListActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.iv_pound_list_pic)
    ImageView ivPoundListPic;
    @Bind(R.id.iv_take_photo)
    ImageView ivTakePhoto;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.et_cargo_suttle)
    EditText etCargoSuttle;//净重
    @Bind(R.id.divider)
    View divider;//分割线
    @Bind(R.id.btn_submit)
    Button btnSubmit;//提交

    private PhotoSelection photoSelection;
    private TransportMode transport;
    private SendCarEntity sendCarEntity;
    private int type;//type为0 表示货运单上传磅单 1表示派车单上传磅单
    private boolean canSubmit = false;//是否可以提交

    private String image = ""; //图片上传后的图片路径

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_pound_list);
    }

    @Override
    protected void initTitle() {
        title.setText(getString(R.string.upload_list));
        etCargoSuttle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etCargoSuttle.removeTextChangedListener(this);// 解除文字改变事件
                int index = etCargoSuttle.getSelectionStart();// 获取光标位置
                String cargoSuttle = s.toString().replace("吨","").trim();
                if (StringUtils.isEmpty(cargoSuttle)){
                    etCargoSuttle.setText("");
                }else{
                    etCargoSuttle.setText(cargoSuttle+"吨");
                }
                etCargoSuttle.setSelection(index);// 重新设置光标位置
                etCargoSuttle.addTextChangedListener(this);// 重新绑定事件
            }
        });
    }

    @Override
    protected void getData() {
        type = getIntent().getIntExtra("type", -1);
        if (type == -1) {
            return;
        }
        switch (type) {
            case 0:
                transport = (TransportMode) getIntent().getSerializableExtra("entity");
                break;
            case 1:
                sendCarEntity = (SendCarEntity) getIntent().getSerializableExtra("entity");
                divider.setVisibility(View.VISIBLE);
                break;
        }
        ViewTreeObserver vto2 = ivPoundListPic.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                switch (type) {
                    case 0:
                        if (!StringUtils.isEmpty(transport.getCarryWeightDocPicUrl()) && !canSubmit) {
                            getImageManager().loadUrlImageCertificationView(transport.getCarryWeightDocPicUrl(), ivPoundListPic); //磅单图片展示
                            etCargoSuttle.setText(transport.getCarryWeight());
                            etCargoSuttle.extendSelection(etCargoSuttle.getText().toString().length());
                            textView.setText(getString(R.string.upload_pound_list_again));
                            canSubmit = true;
                        }
                        break;
                    case 1:
                        if (!StringUtils.isEmpty(sendCarEntity.getCarryWeightDocPicUrl()) && !canSubmit) {
                            getImageManager().loadUrlImageCertificationView(sendCarEntity.getCarryWeightDocPicUrl(), ivPoundListPic);//磅单图片展示
                            etCargoSuttle.setText(sendCarEntity.getCarryWeight());
                            etCargoSuttle.extendSelection(etCargoSuttle.getText().toString().length());
                            textView.setText(getString(R.string.upload_pound_list_again));
                            canSubmit = true;
                        }
                        break;
                }
            }
        });
    }

    @OnClick({R.id.title_bar_left, R.id.iv_pound_list_pic, R.id.iv_take_photo, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                switch (type) {
                    case 0:
                        if (!image.equals(StringUtils.isEmpty(transport.getCarryWeightDocPicUrl()) ? "":transport.getCarryWeightDocPicUrl())){
                            setResult(RESULT_OK);
                        }
                        break;
                    case 1:
                        if (!image.equals(StringUtils.isEmpty(sendCarEntity.getCarryWeightDocPicUrl()) ? "":sendCarEntity.getCarryWeightDocPicUrl())){
                            setResult(RESULT_OK);
                        }
                        break;
                }
                finish();
                break;
            case R.id.iv_pound_list_pic:
            case R.id.iv_take_photo:
                selectPic();
                break;
            case R.id.btn_submit:
                if (!canSubmit) {
                    displayToast(getString(R.string.please_upload_picture));
                    return;
                }
                if (StringUtils.isEmpty(etCargoSuttle.getText().toString().trim().replace("吨",""))) {
                    displayToast(getString(R.string.please_input_cargo_weight));
                    return;
                }
                String cargoSuttle = etCargoSuttle.getText().toString().trim().replace("吨","");
                if (cargoSuttle.equals("0")||cargoSuttle.equals("0.0")||cargoSuttle.equals("0.00")) {
                    displayToast(getString(R.string.please_input_correct_cargo_weight));
                    return;
                }
                if (type == 1) {
//                    if (StringUtils.isEmpty(etRoughWeight.getText().toString().trim())) {
//                        displayToast(getString(R.string.please_input_cargo_rough_weight));
//                        return;
//                    }
//                    if (etRoughWeight.getText().toString().trim().equals("0")||etRoughWeight.getText().toString().trim().equals("0.0")||etRoughWeight.getText().toString().trim().equals("0.00")) {
//                        displayToast(getString(R.string.please_input_correct_cargo_rough_weight));
//                        return;
//                    }
//                    Double roughWeight = Double.valueOf(etRoughWeight.getText().toString().trim());
//                    Double suttleWeight = Double.valueOf(etCargoSuttle.getText().toString().trim());
//                    if (roughWeight <= suttleWeight) {
//                        displayToast(getString(R.string.wrong_weight));
//                        return;
//                    }
                }
                submitPoundList();
                break;
        }
    }

    private void submitPoundList() {
        try {
            LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
            params.put("carryWeight",etCargoSuttle.getText().toString().trim().replace("吨",""));
            switch (type) {
                case 0:
                    params.put("transportOrderCode",transport.getTransportOrderCode());
                    new DataUtils(this,params).getUserTransportOrderCarryWeight(new DataUtils.DataBack<List<TransportMode>>() {
                        @Override
                        public void getData(List<TransportMode> transportModeList) {
                            if (transportModeList == null){
                                return;
                            }
                            displayToast("提交成功！");
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void getError(Throwable e) {
                            if ("2080011".equals(e.getMessage())){
//                                displayToast("网络连接失败，请稍后再试！");
                                displayToast(e.getCause().getMessage());
                            }else{
                                displayToast(e.getCause().getMessage());
                            }
                        }
                    });
                    break;
                case 1:
                    params.put("transportOrderCode",sendCarEntity.getTransportOrderCode());
//                    params.put("grossWeight",etRoughWeight.getText().toString().trim());
                    new DataUtils(this,params).getUserCoalOrderCarryWeight(new DataUtils.DataBack<SendCarEntity>() {
                        @Override
                        public void getData(SendCarEntity sendCarEntity) {
                            if (sendCarEntity == null){
                                return;
                            }
                            displayToast("提交成功！");
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void getError(Throwable e) {
                            if ("2080011".equals(e.getMessage())){
//                                displayToast("网络连接失败，请稍后再试！");
                                displayToast(e.getCause().getMessage());
                            }else{
                                displayToast(e.getCause().getMessage());
                            }
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectPic() {
        photoSelection = new PhotoSelection(this,1, false);
        photoSelection.setSelection(false, null);
        photoSelection.onCallBack(new PhotoSelection.Callback<List<APPData<Map<String,Object>>>>() {
            @Override
            public void onCallBack(List<APPData<Map<String,Object>>> datalist, String fileUrl) {

                switch (type){
                    case 0:
                        Type type = new TypeToken<List<APPData<TransportMode>>>() {}.getType();
                        List<APPData<TransportMode>> dataS = new ArrayList<APPData<TransportMode>>();
                        if (datalist.size() != 0){
                            try {
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(datalist);
                                dataS = gson.fromJson(jsonString,type);
                            } catch (Exception e) {
                                GHLog.e("派车单详情解析",e.toString());
                            }
                        }
                        List<TransportMode> dataSs = new ArrayList<TransportMode>();
                        for (APPData<TransportMode> data : dataS) {
                            if (data.getDataType().equals("coal090004")) { //货运单信息
                                dataSs.add(0, data.getEntity());
                            }
                            if (data.getDataType().equals("coal020009")) { //信息部信息
                                dataSs.add(1, data.getEntity());
                            }
                        }
                        displayPci(dataSs.get(0), fileUrl);
                        break;
                    case 1:
                        SendCarEntity sendCarEntity = new SendCarEntity().getEntity(datalist);
                        displayPci(sendCarEntity, fileUrl);
                        break;
                }

            }

            @Override
            public void onError(Throwable e, String imagePhth) {
                displayToast("图片上传失败！");
            }
        });
    }

    private void displayPci(SendCarEntity sendCarEntity, String fileUrl) {
        try {
            if (sendCarEntity != null) {
                if (StringUtils.isEmpty(sendCarEntity.getCarryWeightDocPicUrl())) {
                    ivPoundListPic.setImageResource(R.mipmap.upload_photo_bg);
                    displayToast(getString(R.string.modify_head_pic_failed));
                } else {
                    image = sendCarEntity.getCarryWeightDocPicUrl();
                    canSubmit = true;
                    //获取图片缩略图，避免OOM
                    Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(getApplicationContext()), ImageUtils.getWidth(getApplicationContext()));
                    ivPoundListPic.setImageBitmap(bitmap);
                    displayToast(getString(R.string.upload_pound_list_success));
                    textView.setText(getString(R.string.upload_pound_list_again));
                }
            } else {
                canSubmit = false;
                displayToast(getString(R.string.modify_head_pic_failed));
            }
        } catch (Exception e) {
            canSubmit = false;
            dismisProgressDialog();
            displayToast(getString(R.string.save_head_pic_failed));
            Log.e("上传失败", e.toString());
        }
    }
    private void displayPci(TransportMode transport, String fileUrl) {
        try {
            if (transport != null) {
                if (StringUtils.isEmpty(transport.getCarryWeightDocPicUrl())) {
                    ivPoundListPic.setImageResource(R.mipmap.upload_photo_bg);
                    displayToast(getString(R.string.modify_head_pic_failed));
                } else {
                    image = transport.getCarryWeightDocPicUrl();
                    canSubmit = true;
                    //获取图片缩略图，避免OOM
                    Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(getApplicationContext()), ImageUtils.getWidth(getApplicationContext()));
                    ivPoundListPic.setImageBitmap(bitmap);
                    displayToast(getString(R.string.upload_pound_list_success));
                    textView.setText(getString(R.string.upload_pound_list_again));
                }
            } else {
                canSubmit = false;
                displayToast(getString(R.string.modify_head_pic_failed));
            }
        } catch (Exception e) {
            canSubmit = false;
            dismisProgressDialog();
            displayToast(getString(R.string.save_head_pic_failed));
            Log.e("上传失败", e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case PhotoSelection.SELECT_IMAGE_RESULT_CODE:// 如果是调用相机拍照时
                    String transportOrderCode = "";
                    switch (type) {
                        case 0:
                            transportOrderCode = transport.getTransportOrderCode();
                            break;
                        case 1:
                            transportOrderCode = sendCarEntity.getTransportOrderCode();
                            break;
                    }
                    if (data != null) {
                        photoSelection.setUploadPoundList(data, transportOrderCode, type);
                    }
                    break;
            }
        } catch (Exception e) {
            Log.i("onActivityResult", e.toString());
        }
    }
}

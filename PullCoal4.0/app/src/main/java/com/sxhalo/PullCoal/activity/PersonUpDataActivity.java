package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPMessage;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.ImageUtils;
import com.sxhalo.PullCoal.tools.PhotoSelection;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2017/1/11.
 */
public class PersonUpDataActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.logout_icon)
    CircleImageView logoutIcon;
    @Bind(R.id.text_name)
    TextView textName;
    @Bind(R.id.text_accont)
    TextView textAccont;
    @Bind(R.id.text_address)
    TextView textAddress;
    @Bind(R.id.password_name)
    TextView passwordName;

    /*  修改昵称*/
    public final static int per_area = 10;
    /*  修改地区*/
    public final static int PERSON_HOME = 20;
    private PhotoSelection photoSelection;
    private int Type;
    private UserEntity users;
    private String nick;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_personal_updata);
    }
    @Override
    protected void initTitle() {
        title.setText("个人信息");
    }

    @Override
    protected void getData() {
        try {
            users = (UserEntity)getIntent().getSerializableExtra("Entity");
            textAccont.setText(users.getUserName());

            textName.setText(StringUtils.isEmpty(users.getNickname())?"煤宝" + users.getUserId():users.getNickname());
            String address = users.getRegionName();
            if (address.equals("") || address == null || address.equals("null")){
                address = SharedTools.getStringValue(this, "city", "未设置");
            }
            textAddress.setText(address);
            if(StringUtils.isEmpty(users.getHeadPic())){
                logoutIcon.setImageResource(R.mipmap.icon_login);
            }else{
                getImageManager().loadItemUrlImage(users.getHeadPic(),logoutIcon); //个人圆图加载
            }
            if (StringUtils.isEmpty(users.getUserPwd())){
                passwordName.setText("添加登录密码");
            }else{
                passwordName.setText("修改密码");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.title_bar_left, R.id.collection_my, R.id.bt_name, R.id.bt_address,R.id.bt_modify_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.collection_my:
                Type = 0;
                setPicImage();
                break;
            case R.id.bt_name:
                Intent intent = new Intent(this, PersonEditActivity.class);
                intent.putExtra("Nickname", textName.getText().toString().trim());
                startActivityForResult(intent, per_area);
                break;
            case R.id.bt_address:
                Type = 2;
                startActivityForResult(new Intent(this,SelectAreaActivity.class), Constant.AREA_CODE);
                break;
            case R.id.bt_modify_password:
                if (StringUtils.isEmpty(users.getUserPwd())){
                    Intent intent1 = new Intent(this, SettingUserNewPasswordActivity.class);
                    intent1.putExtra("type", "0");
                    startActivity(intent1);
                }else{
                    UIHelper.jumpAct(this, ModifyPasswordActivity.class,users, false);
                }
                break;
        }
    }

    /**
     * 照片上传
     */
    private void setPicImage() {
        photoSelection = new PhotoSelection(PersonUpDataActivity.this,1, true);
        photoSelection.setSelection(false,null);
        photoSelection.onCallBack(new PhotoSelection.Callback<APPMessage>() {
            @Override
            public void onCallBack(APPMessage data,String fileUrl) {
                getPicId(data);
                //获取图片缩略图，避免OOM
                Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(getApplicationContext()) , ImageUtils.getWidth(getApplicationContext()) );
                if (logoutIcon != null){
                    logoutIcon.setImageBitmap(bitmap);
                }
//                displayToast("图片上传成功！");
            }
            @Override
            public void onError(Throwable e, String imagePhth) {
                displayToast(getResources().getString(R.string.save_pic_failed));
            }
        });
    }

    /**
     * 照片上传成功时返回
     */
    protected void getPicId(APPMessage data) {
        try {
            if (data != null) {
                if(StringUtils.isEmpty(data.getHeadPic())){
                    logoutIcon.setImageResource(R.mipmap.icon_login);
                }else{
                    getImageManager().loadItemUrlImage(data.getHeadPic(),logoutIcon); //个人圆图加载
                    displayToast(getString(R.string.modify_head_pic_success));
                }
            } else {
                displayToast(getString(R.string.modify_head_pic_failed));
            }
        } catch (Exception e) {
            dismisProgressDialog();
            displayToast(getString(R.string.save_head_pic_failed));
            Log.e("上传失败", e.toString());
        }
    }

    private void setUsetData(String drivePic){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            switch (Type){
                case 1:
                    params.put("nickname", drivePic);
                    break;
                case 2:
                    params.put("regionCode", drivePic);
                    break;
            }
            getHttp(params);
        } catch (Exception e) {
            GHLog.e("参数错误", e.toString());
        }
    }

    private void getHttp(final LinkedHashMap<String, String> params){
        try {
            new DataUtils(this,params).getUserUpdata(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    if(dataMemager == null){
                        return;
                    }
                    if (Type == 2){
                        displayToast(getString(R.string.modify_area));
                        textAddress.setText(dataMemager.getRegionName());
                    }else if (Type == 1){
                        displayToast(getString(R.string.modify_nick_name_success));
                        textName.setText(dataMemager.getNickname());
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case PhotoSelection.SELECT_IMAGE_RESULT_CODE:// 如果是调用相机拍照时
//                    if (resultCode == RESULT_OK) {
//                        String fileName = APIConfig.HEAD_PIC;
//                        photoSelection.setUploadImage(data,fileName);
//                    }
                    String fileName = APIConfig.HEAD_PIC;
                    if (data != null) {
                        photoSelection.setUploadImage(data, fileName);
                    }
                    break;
                case per_area:
                    if (data == null){
                        return;
                    }
                    Type = 1;
                    nick = data.getStringExtra("Nickname");
                    setUsetData(nick);
                    break;
                case Constant.AREA_CODE:
                    String code = data.getStringExtra("code");
                    setUsetData(code);
                    break;
            }
        } catch (Exception e) {
            Log.i("onActivityResult", e.toString());
        }
    }
}

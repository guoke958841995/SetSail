package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPMessage;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.ImageUtils;
import com.sxhalo.PullCoal.tools.PhotoSelection;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 注册成功后首次进入的个人资料设置界面
 * Created by liz on 2018/1/9.
 */

public class FirstSetupPersonalInformationActivity extends BaseActivity {
    @Bind(R.id.title_bar_left)
    ImageView titleBarLeft;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.iv_head)
    CircleImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.ll_password)
    LinearLayout llPassword;
    @Bind(R.id.ll_my_referral)
    LinearLayout llMyReferral;
    @Bind(R.id.tv_my_referral)
    TextView tvMyReferral;

    /*  修改昵称*/
    public final static int MODIFY_NAME = 10;
    /*  添加推荐人*/
    public final static int MODIFY_TEL = 11;
    /*  修改地区*/
    public final static int MODIFY_AREA = 20;

    public final static int MODIFY_HOME = 100;

    private PhotoSelection photoSelection;

    private int Type;// 区分提交接口的字段
    private String registerType;
    private boolean flage; //推荐人是否可修改

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_first_setup_personal_information);
    }

    @Override
    protected void initTitle() {
        title.setText("个人资料设置");
        registerType = getIntent().getStringExtra("registerType");
    }

    @Override
    protected void getData() {
        if (registerType.equals("0")) {
            llPassword.setVisibility(View.GONE);
        } else if (registerType.equals("1")) {
            llPassword.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (CallBack.callback != null){
//            CallBack.callback.onCallBack();
//        }
//        finish();
//    }

    @OnClick({R.id.title_bar_left, R.id.iv_head, R.id.ll_name, R.id.ll_area, R.id.ll_password, R.id.ll_my_referral, R.id.tv_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
//                if (CallBack.callback != null){
//                    CallBack.callback.onCallBack();
//                }
                finish();
                break;
            case R.id.iv_head:
                Type = 0;
                setlectHeadPic();
                break;
            case R.id.ll_name:
                Intent intent = new Intent(this, PersonEditActivity.class);
                intent.putExtra("Nickname", (tvName.getText().toString().trim()) .equals("未设置") ? "" : (tvName.getText().toString().trim()));
                intent.putExtra("type", 0);
                startActivityForResult(intent, MODIFY_NAME);
                break;
            case R.id.ll_area:
                Type = 2;
                startActivityForResult(new Intent(this, SelectAreaActivity.class), MODIFY_AREA);
                break;
            case R.id.ll_password:
                Intent intent1 = new Intent(this, SettingUserNewPasswordActivity.class);
                intent1.putExtra("type", "0");
                startActivity(intent1);
                break;
            case R.id.ll_my_referral:
                if (flage) {
                    displayToast("推荐人只能修改一次");
                } else {
                    Intent intent2 = new Intent(this, SettingUserNewPasswordActivity.class);
                    intent2.putExtra("type", "1");
                    startActivityForResult(intent2, MODIFY_TEL);
                }
                break;
            case R.id.tv_skip:
                finish();
                break;
        }
    }

    /**
     * 头像选择或者拍照
     */
    private void setlectHeadPic() {
        photoSelection = new PhotoSelection(FirstSetupPersonalInformationActivity.this, 1, true);
        photoSelection.setSelection(false, null);
        photoSelection.onCallBack(new PhotoSelection.Callback<APPMessage>() {
            @Override
            public void onCallBack(APPMessage data, String fileUrl) {
                getPicId(data);
                //获取图片缩略图，避免OOM
                Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(getApplicationContext()), ImageUtils.getWidth(getApplicationContext()));
                if (ivHead != null) {
                    ivHead.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onError(Throwable e, String imagePhth) {
                displayToast("图片上传失败！");
            }
        });

    }

    /**
     * 照片上传成功时返回
     */
    protected void getPicId(APPMessage data) {
        try {
            if (data != null) {
                if (StringUtils.isEmpty(data.getHeadPic())) {
                    ivHead.setImageResource(R.mipmap.icon_login);
                } else {
                    getImageManager().loadItemUrlImage(data.getHeadPic(), ivHead);  //修改图像成功后展示
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case PhotoSelection.SELECT_IMAGE_RESULT_CODE:// 如果是调用相机拍照时
                        String fileName = APIConfig.HEAD_PIC;
                        if (data != null) {
                            photoSelection.setUploadImage(data, fileName);
                        }
                    break;
                case MODIFY_NAME:
                    try {
                        Type = 1;
                        String nickName = data.getStringExtra("Nickname");
                        uploadPersonalData(nickName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MODIFY_TEL:
                    try {
                        Type = 3;
                        String recommend = data.getStringExtra("recommend");
                        uploadPersonalData(recommend);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MODIFY_AREA:
                    try {
                        String code = data.getStringExtra("code");
                        uploadPersonalData(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }catch (Exception e) {
            Log.i("onActivityResult", e.toString());
        }
    }

    /**
     * 修改成功后提交到后台
     *
     * @param string
     */
    private void uploadPersonalData(final String string) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            switch (Type) {
                case 1:
                    params.put("nickname", string);
                    break;
                case 2:
                    params.put("regionCode", string);
                    break;
                case 3:
                    params.put("recommend", string);
                    break;
            }
            new DataUtils(this, params).getUserUpdata(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    if (dataMemager == null) {
                        return;
                    }
                    if (Type == 2) {
                        displayToast(getString(R.string.modify_area));
                        tvAddress.setText(dataMemager.getRegionName());
                    } else if (Type == 1) {
                        displayToast(getString(R.string.modify_nick_name_success));
                        tvName.setText(dataMemager.getNickname());
                    } else if (Type == 3) {
                        displayToast(getString(R.string.modify_relmib_success));
                        tvMyReferral.setText(string);
                        flage = true;
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("服务繁忙，请稍候再试！");
                }
            });
        } catch (Exception e) {
            GHLog.e("参数错误", e.toString());
        }
    }
}

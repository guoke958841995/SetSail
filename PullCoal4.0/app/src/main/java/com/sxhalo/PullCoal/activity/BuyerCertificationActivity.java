package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.APPMessage;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.ImageUtils;
import com.sxhalo.PullCoal.tools.PhotoSelection;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 买家认证界面
 * Created by amoldZhang on 2017/1/4.
 */
public class BuyerCertificationActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_id_card_front)
    ImageView ivIdCardFront;
    @Bind(R.id.iv_id_card_opposite)
    ImageView ivIdCardOpposite;
    @Bind(R.id.tv_apply)
    TextView tvApply;
    @Bind(R.id.authenticate_state_text)
    TextView authenticateStateText;
    @Bind(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @Bind(R.id.layout_declare)
    LinearLayout layoutDeclare;
    @Bind(R.id.scroll_view)
    ScrollView scrollView;

    @Bind(R.id.individual_name)
    TextView individualName;
    @Bind(R.id.individual_number)
    TextView individualNumber;
    @Bind(R.id.individual_status_time)
    TextView individualStatusTime;
    @Bind(R.id.authenticate_state_content)
    TextView authenticateStateContent;
    @Bind(R.id.individual_update)
    TextView individualUpdate;
    @Bind(R.id.individual_audit)
    LinearLayout individualAudit;


    private PhotoSelection photoSelection;

    private Map<String, String> popMap = new HashMap<String, String>();
    private Bitmap bmp;
    private String authenticateState;
    private String fileName;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_buyer_certification);
    }

    @Override
    protected void initTitle() {
        tvTitle.setText("买家认证");
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this, params).getUserRealnameAuthInfo(new DataUtils.DataBack<UserAuthenticationEntity>() {
                @Override
                public void getData(UserAuthenticationEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        setView(dataMemager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    /**
     * 根据认证状态设置 界面显示
     * 认证状态：0、未审核；1、审核通过；2、审核未通过；100、未提交审核
     *
     * @param data
     */
    private void setView(UserAuthenticationEntity data) {
        authenticateState = StringUtils.isEmpty(data.getAuthState()) ? "100" : data.getAuthState();  //买家审核状态
        layoutDeclare.setVisibility(View.VISIBLE);
        switch (Integer.valueOf(authenticateState)) {
            case 100: //资料未提交
                authenticateStateText.setText("未认证");
                authenticateStateContent.setText("请上传资料后，提交认证申请！审核通过后即可开始买煤。");
                scrollView.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.VISIBLE);
                tvApply.setEnabled(false);

                authenticateStateText.setText("未提交审核");
                authenticateStateContent.setText("请上传资料后，提交认证申请！审核通过后即可开始买煤。");
                scrollView.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.VISIBLE);
                setDataView(data);
                if (!StringUtils.isEmpty(data.getIdentitycardFrontUrl())) {
                    getImageManager().loadUrlImageCertificationView(data.getIdentitycardFrontUrl(), ivIdCardFront); //身份证正面
                }
                if (!StringUtils.isEmpty(data.getIdentitycardBackUrl())) {
                    getImageManager().loadUrlImageCertificationView(data.getIdentitycardBackUrl(), ivIdCardOpposite); //身份证反面
                }
                break;
            case 0: //审核中
                authenticateStateText.setText("审核中");
                authenticateStateContent.setText("您提交的资料正在审核中，请耐心等待!");
                scrollView.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.VISIBLE);
                setDataView(data);
                if (!StringUtils.isEmpty(data.getIdentitycardFrontUrl())) {
                    getImageManager().loadUrlImageCertificationView(data.getIdentitycardFrontUrl(), ivIdCardFront); //身份证正面
                }
                if (!StringUtils.isEmpty(data.getIdentitycardBackUrl())) {
                    getImageManager().loadUrlImageCertificationView(data.getIdentitycardBackUrl(), ivIdCardOpposite); //身份证反面
                }
                tvApply.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                tvApply.setEnabled(false);
                break;
            case 1: //审核成功
                authenticateStateText.setText("审核成功");
                authenticateStateContent.setText("审核成功,现在您可以下单买煤了！");
                layoutBottom.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                tvApply.setVisibility(View.GONE);
                individualAudit.setVisibility(View.VISIBLE);
                setAuthenticateData(data);
                break;
            case 2: //审核失败
                authenticateStateText.setText("审核失败");
                authenticateStateContent.setText("您没有通过买家认证，请调整后重新提交!!");
                scrollView.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.VISIBLE);
                setDataView(data);
                if (!StringUtils.isEmpty(data.getIdentitycardFrontUrl())) {
                    getImageManager().loadUrlImageCertificationView(data.getIdentitycardFrontUrl(), ivIdCardFront); //身份证正面
                }
                if (!StringUtils.isEmpty(data.getIdentitycardBackUrl())) {
                    getImageManager().loadUrlImageCertificationView(data.getIdentitycardBackUrl(), ivIdCardOpposite); //身份证反面
                }
                tvApply.setEnabled(false);
                tvApply.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                break;
        }

    }


    @OnClick({R.id.ib_back, R.id.tv_apply, R.id.iv_id_card_front, R.id.layout_bottom,
//            R.id.iv_take_id_card_front,R.id.iv_take_id_card_opposite,
            R.id.iv_id_card_opposite})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.tv_apply:  //资料提交
                setSubmitted();
                break;
//            case R.id.iv_take_id_card_front:
            case R.id.iv_id_card_front:  //身份证正面
                if (authenticateState.equals("0")) {
                    displayToast(getString(R.string.under_review));
                } else {
                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.id_card_opposite_image);
                    selection(ivIdCardFront, bmp, APIConfig.IDENTITY_FRONT_PIC);
                }
                break;
//            case R.id.iv_take_id_card_opposite:
            case R.id.iv_id_card_opposite:  //身份证反面
                if (authenticateState.equals("0")) {
                    displayToast(getString(R.string.under_review));
                } else {
                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.id_card_front_image);
                    selection(ivIdCardOpposite, bmp, APIConfig.IDENTITY_BACK_PIC);
                }
                break;
            case R.id.layout_bottom:
                String URL = new Config().getUSERASKE_DAND_QUESTIONS();
                UIHelper.showWEB(this, URL, "买家认证说明");
                break;
        }
    }

    /**
     * 提交审核资料
     * 认证状态：0、未审核；1、审核通过；2、审核未通过；100、未提交审核
     */
    private void setSubmitted() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this, params).getUserRealnameAuthCreate(new DataUtils.DataBack<UserAuthenticationEntity>() {
                @Override
                public void getData(UserAuthenticationEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        displayToast(getString(R.string.submit_under_review_success));
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
//            if (requestCode == PhotoSelection.SELECT_IMAGE_RESULT_CODE && resultCode == this.RESULT_OK) {
//                photoSelection.setUploadImage(data, fileName);
//            }
            if (data != null && requestCode == PhotoSelection.SELECT_IMAGE_RESULT_CODE) {
                photoSelection.setUploadImage(data, fileName);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            Log.i("onActivityResult", e.toString());
        }
    }

    private void selection(final ImageView IV, Bitmap bm, final String fileName) {
        this.fileName = fileName;
        photoSelection = new PhotoSelection(this,1, false);
        photoSelection.setSelection(true, bm);
        photoSelection.onCallBack(new PhotoSelection.Callback<APPMessage>() {
            @Override
            public void onCallBack(APPMessage data, String fileUrl) {
                if (data == null) {
                    displayToast(getString(R.string.upload_pic_failed));
                    return;
                }
                //获取图片缩略图，避免OOM
                Bitmap bitmap = ImageUtils.getImageThumbnail(fileUrl, ImageUtils.getHeight(getApplicationContext()), ImageUtils.getWidth(getApplicationContext()));
                if (IV != null) {
                    IV.setImageBitmap(bitmap);
                }
                setDataView(data);
                displayToast(getString(R.string.upload_pic_success));
            }

            @Override
            public void onError(Throwable e, String imagePhth) {
                displayToast("图片上传失败！");
            }
        });
    }


    /**
     * 给界面图片赋值
     *
     * @param dataView
     */
    public void setDataView(APPMessage dataView) {
        if (!StringUtils.isEmpty(dataView.getIdentitycardFrontUrl())) {
            popMap.put(APIConfig.IDENTITY_FRONT_PIC, APIConfig.IDENTITY_FRONT_PIC);
        }
        if (!StringUtils.isEmpty(dataView.getIdentitycardBackUrl())) {
            popMap.put(APIConfig.IDENTITY_BACK_PIC, APIConfig.IDENTITY_BACK_PIC);
        }
        if (popMap.size() > 1) {
            tvApply.setTextColor(getResources().getColor(R.color.app_title_text_color));
            tvApply.setEnabled(true);
        } else {
            tvApply.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
            tvApply.setEnabled(false);
        }
    }

    /**
     * 给界面图片赋值
     *
     * @param dataView
     */
    public void setDataView(UserAuthenticationEntity dataView) {
        if (!StringUtils.isEmpty(dataView.getIdentitycardFrontUrl())) {
            popMap.put(APIConfig.IDENTITY_FRONT_PIC, APIConfig.IDENTITY_FRONT_PIC);
        }
        if (!StringUtils.isEmpty(dataView.getIdentitycardBackUrl())) {
            popMap.put(APIConfig.IDENTITY_BACK_PIC, APIConfig.IDENTITY_BACK_PIC);
        }
        if (popMap.size() > 1) {
            tvApply.setTextColor(getResources().getColor(R.color.app_title_text_color));
            tvApply.setEnabled(true);
        } else {
            tvApply.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
            tvApply.setEnabled(false);
        }
    }

    /**
     * 买家认证审核成功界面赋值
     *
     * @param result
     */
    private void setAuthenticateData(UserAuthenticationEntity result) {
        individualName.setText(result.getRealName());
        individualNumber.setText(result.getIdentitycardId());
        if (!StringUtils.isEmpty(result.getCreateTime())) {
            String createTime = DateUtil.strToStr(result.getCreateTime());
            individualStatusTime.setText(createTime);
        }
        if (!StringUtils.isEmpty(result.getVerifyTime())) {
            String updataTime = DateUtil.strToStr(result.getVerifyTime());
            individualUpdate.setText(updataTime);
        }
    }

}

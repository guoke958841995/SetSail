package com.sxhalo.PullCoal.tools;

import android.app.Activity;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.SaleManager;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.LinkedHashMap;


/**
 * 短息验证工具类
 *  amoldZhang on 2016/11/22.
 */
public class SMSSdkTools {


    private  Activity mActivity;
    private SaleManager data;
    public SMSSdkTools(Activity mActivity){
        this.mActivity = mActivity;
    }

    /**
     * 请求验证码
     * @param phoneNum
     */
    public void getCode(String phoneNum){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber",phoneNum);  //手机号
            new DataUtils(mActivity,params).getSendSMS(new DataUtils.DataBack<SaleManager>() {
                @Override
                public void getData(SaleManager dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        data = dataMemager;
                        if (coalBack != null){
                            coalBack.getData(data.getPhoneNumber(),data.getPhoneNumber());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    /**
     * 提交验证码
     * @param phoneNum   用户手机号
     * @param verificationCode    用户提交的验证码
     * @param flage      当前验证码是否失效
     */
    public void setInputCode(String phoneNum, String verificationCode,boolean flage){
        if (data != null ){
            if (flage){
                if (coalBack != null){
                    coalBack.getEorr(mActivity.getString(R.string.invalid_no_code));
                }
                return;
            }
            String phoneNumber = data.getPhoneNumber();
            if(phoneNumber.equals(phoneNum)){
                getVerificationCode(phoneNumber,verificationCode);
            }else{
                if (coalBack != null){
                    coalBack.getEorr(mActivity.getString(R.string.not_match));
                }
            }
        }else{
            if (coalBack != null){
                coalBack.getEorr(mActivity.getString(R.string.wrong_no_code));
            }
        }
    }

    /**
     * 联网校验验证码
     * @param phoneNum
     */
    public void getVerificationCode(String phoneNum,String verificationCode){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber",phoneNum);  //手机号
            params.put("verificationCode",verificationCode);  //用户输入验证码
            new DataUtils(mActivity,params).getVerificationCode(new DataUtils.DataBack<SaleManager>() {
                @Override
                public void getData(SaleManager dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        if (coalBack != null){
                            coalBack.getData("+86",dataMemager.getPhoneNumber());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    public CoalBack coalBack;

    public interface CoalBack{
        void getData(String codeNum ,String phone);
        void getEorr(String showText);
    }
    public void setCoalBack(CoalBack coalBack){
        this.coalBack = coalBack;
    }
}

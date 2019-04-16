package com.sxhalo.PullCoal.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.utils.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.utils.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.weight.dialog.RLAlertDialog;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.core.app.ActivityCompat;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * Created by amoldZhang on 2019/4/11.
 */
public class UIHelper {

    public final static String TAG = "UIHelper";

    public final static int RESULT_OK = 0x00;
    public final static int REQUEST_CODE = 0x01;

    /**
     * 跳转登录activity
     *
     * @param context
     */
    public static void jumpActLogin(Activity context) {
        showDaiLogLogin(context, "您当前尚未登录，是否去登录？");
    }

    /**
     * 弹框
     *
     * @param context
     * @param message
     */
    private static void showDaiLogLogin(final Activity context, String message) {
        new RLAlertDialog(context, "系统提示", message, "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                // TODO 需要跳转至登录界面
//                Intent intent = new Intent(context, RegisterAddLoginActivity.class);
//                context.startActivity(intent);
//                if (needClose && context instanceof Activity) {
//                    context.finish();
//                }
            }
        }).show();
    }

    /**
     * 跳转activity
     *
     * @param context
     * @param to        跳转到的activity
     * @param needClose 是否关闭
     */
    public static void jumpAct(Activity context, Class to, boolean needClose) {
        Intent intent = new Intent(context, to);
        context.startActivity(intent);
        if (needClose && context instanceof Activity) {
            context.finish();
        }
    }

    /**
     * 跳转activity
     *
     * @param context
     * @param to                 跳转到的activity
     * @param stringSerializable 需要传递的参数
     * @param needClose          是否关闭
     */
    public static void jumpAct(Activity context, Class to, Map<String, Serializable> stringSerializable, boolean needClose) {
        Intent intent = new Intent(context, to);
        if (stringSerializable != null && stringSerializable.size() > 0) {
            Set<String> keys = stringSerializable.keySet();
            for (String headerKey : keys) {
                intent.putExtra(headerKey, stringSerializable.get(headerKey));
            }
        }
        context.startActivity(intent);
        if (needClose && context instanceof Activity) {
            context.finish();
        }
    }

    /**
     * 返回跳转activity
     *
     * @param context
     * @param to
     * @param requestCode 请求码
     */
    public static <T> void jumpActForResult(Activity context, Class to, Map<String, Serializable> stringSerializable, int requestCode) {
        Intent intent = new Intent(context, to);
        if (stringSerializable != null && stringSerializable.size() > 0) {
            Set<String> keys = stringSerializable.keySet();
            for (String headerKey : keys) {
                intent.putExtra(headerKey, stringSerializable.get(headerKey));
            }
        }
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 返回跳转activity
     *
     * @param context
     * @param to
     * @param requestCode 请求码
     */
    public static <T> void jumpActForResult(Activity context, Class to, int requestCode) {
        Intent intent = new Intent(context, to);
        context.startActivityForResult(intent, requestCode);
    }

    public static void showFindPunctuation(final Activity context, final Class to, final Map<String, Serializable> stringSerializable) {
        new PermissionUtil().requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                jumpAct(context, to, stringSerializable, false);
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }

    /**
     * 拨打电话
     * @param mActivity 当前所在的activity
     * @param map   需要传递的参数
     * @param flage  是否需要做电话统计
     */
    public static void showCollTel(final Activity mActivity,final Map<String,String> map,final boolean flage) {
        try {
            new PermissionUtil().requestPermissions(mActivity,new String[]{Manifest.permission.CALL_PHONE}, new PermissionListener() {
                @Override
                public void onGranted() { //用户同意授权
                    showDaiLog(mActivity, map,flage);
                }

                @Override
                public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                }
            });
        } catch (Exception e) {
            ((BaseActivity) mActivity).displayToast(mActivity.getString(R.string.checkout_sim_card));
        }
    }
    private static void showDaiLog(final Activity mActivity,final Map<String,String> map,final boolean flage) {
        new RLAlertDialog(mActivity, mActivity.getString(R.string.call_tips),map.get("tel"), "取消",
                "呼叫", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                try {
                    String hintMessage = BaseUtils.getSIM(mActivity.getApplication());
                    if (hintMessage.equals("Ready")) {
                        if (flage){
                            //添加统计时长
//                        PhoneStateReceive phoneStateReceive = new PhoneStateReceive(mActivity,map);
                            //直接上报
                            setCallRecord(mActivity,map);
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + map.get("tel")));
//                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+tel));
//                    intent.setData(Uri.parse("tel:"+tel));//蓝色固定
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ((BaseActivity)mActivity).displayToast(mActivity.getString(R.string.forbidden_permission));
                            return;
                        }
                        mActivity.startActivity(intent);
                    } else {
                        ((BaseActivity) mActivity).displayToast(mActivity.getString(R.string.no_sim_card));
                    }
                }catch (Exception e){
                    ((BaseActivity)mActivity).displayToast(mActivity.getString(R.string.checkout_sim_card));
                }
            }
        }).show();
    }

    /**
     * 电话上报
     * @param context
     * @param map
     */
    private static void setCallRecord(Context context,Map<String,String> map){
        try {
            LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
            params.put("longitude", PaperUtil.get("longitude", "0.0"));
            params.put("latitude",  PaperUtil.get("latitude", "0.0"));
            params.put("regionCode", PaperUtil.get("adCode", ""));

            params.put("callType", map.get("callType"));
            params.put("targetId", StringUtils.isEmpty(map.get("targetId"))?"":map.get("targetId"));

            // TODO 这里需要联网将需要电话上报服务器
//            new DataUtils((BaseActivity)context,params).getUserCallRecordCreate(new DataUtils.DataBack<UserCallRecord>() {
//                @Override
//                public void getData(UserCallRecord userCallRecord) {
//                    if (userCallRecord == null){
//                        return;
//                    }
//
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

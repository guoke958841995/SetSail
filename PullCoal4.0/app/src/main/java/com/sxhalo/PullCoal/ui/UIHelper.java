package com.sxhalo.PullCoal.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.FreightChargeActivity;
import com.sxhalo.PullCoal.activity.MapActivity;
import com.sxhalo.PullCoal.activity.RegisterAddLoginActivity;
import com.sxhalo.PullCoal.activity.RouteActivity;
import com.sxhalo.PullCoal.activity.SearchActivity;
import com.sxhalo.PullCoal.activity.SearchResultActivity;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserCallRecord;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * Created by amoldZhang on 2016/12/6.
 */
public class UIHelper {

    public final static String TAG = "UIHelper";

    public final static int RESULT_OK = 0x00;
    public final static int REQUEST_CODE = 0x01;

    public static void ToastMessage(Context cont, String msg) {
        if (cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        if (cont == null || msg <= 0) {
            return;
        }
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        if (cont == null || msg == null) {
            return;
        }
        Toast.makeText(cont, msg, time).show();
    }

    /**
     * 当用户登录成功时，缓存必要数据
     * @param context  当前的活动界面
     * @param userEntity  用户登录成功返回实体
     */
    public static void saveUserData(Context context, UserEntity userEntity){
        SharedTools.putStringValue(context, "userId",userEntity.getUserId()); //用户id
        SharedTools.putStringValue(context, "roleId",userEntity.getRoleId()); //角色id
        SharedTools.putStringValue(context,"user_real_name",StringUtils.isEmpty(userEntity.getRealName())? "未认证":userEntity.getRealName()); //用户真实姓名
        SharedTools.putStringValue(context,"user_mobile",userEntity.getUserMobile()); //用户手机号
        SharedTools.putStringValue(context,"coalSalesId",StringUtils.isEmpty(userEntity.getCoalSalesId())? "-1":userEntity.getCoalSalesId()); //用户所在的信息部id
    }

    /**
     * 当用户退出登录时，清空缓存必要数据
     * @param context  当前的活动界面
     */
    public static void cleanUserData(Context context){
        SharedTools.putStringValue(context, "userId","-1"); //用户id
        SharedTools.putStringValue(context, "roleId","-1"); //角色id
        SharedTools.putStringValue(context,"user_real_name","-1"); //用户真实姓名
        SharedTools.putStringValue(context,"user_mobile","-1"); //用户手机号
        SharedTools.putStringValue(context,"coalSalesId","-1"); //用户所在的信息部id
    }

    public static void showRoutNavi(final Activity context,final String latitude,final String longitude,final String endAddress) {
        new PermissionUtil().requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("endAddress",endAddress);
                context.startActivity(intent);
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });

    }

    public static void showRoutNavi(final Activity context,final Serializable dataInfor) {
        new PermissionUtil().requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra("Entity", dataInfor);
                context.startActivity(intent);
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }

    /**
     * 跳转到定位界面
     * @param context  当前界面
     * @param dataInfor 要发送的对象
     * @param type  MineMouth 是 从矿口跳转   InformationDepartment 是从信息部跳转
     */
    public static void showMap(final Activity context,final Serializable dataInfor,final String type) {
        new PermissionUtil().requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("Entity", dataInfor);
                intent.putExtra("Type", type);
                context.startActivity(intent);
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }

    public static void showSearch(Activity context, int TAG) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("TAG", TAG);
        context.startActivity(intent);
    }

    public static void showFreightCharge(Activity context, Serializable t, int type) {
        Intent intent = new Intent(context, FreightChargeActivity.class);
        intent.putExtra("type", type);//用来区分是从服务跳转过去还是从货运详情跳转过去  0 服务  1货运详情
        intent.putExtra("Entity", t);
        context.startActivity(intent);
    }

    public static void showSearchResult(Activity context, String type) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    /**
     * 跳转登录activity
     * @param context
     * @param needClose
     */
    public static void jumpActLogin(Activity context, boolean needClose) {
        showDaiLogLogin(context,"您当前尚未登录，是否去登录？",needClose);
    }

    private static void showDaiLogLogin(final Activity context,String message,final boolean needClose) {
//        LayoutInflater inflater1 = context.getLayoutInflater();
//        View layout = inflater1.inflate(R.layout.dialog_updata, null);
//        ((TextView) layout.findViewById(R.id.updata_message)).setText(message);
        new RLAlertDialog(context, "系统提示",message, "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(context, RegisterAddLoginActivity.class);
                context.startActivity(intent);
                if (needClose && context instanceof Activity) {
                    context.finish();
                }
            }
        }).show();
    }
    /**
     * 跳转activity
     * @param context
     * @param to
     * @param needClose
     */
    public static void jumpAct(Activity context, Class to, boolean needClose) {
        Intent intent = new Intent(context, to);
        context.startActivity(intent);
        if (needClose && context instanceof Activity) {
            context.finish();
        }
    }

    public static void jumpAct(Activity context, Class to, String url, String title, boolean needClose) {
        Intent intent = new Intent(context, to);
        intent.putExtra("URL", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
        if (needClose && context instanceof Activity) {
            context.finish();
        }
    }

    /**
     * 跳转activity
     * @param context
     * @param to
     * @param needClose
     */
    public static <T> void jumpAct(Activity context, Class to,Serializable t, boolean needClose) {
        Intent intent = new Intent(context, to);
        intent.putExtra("Entity",t);
        context.startActivity(intent);
        if (needClose && context instanceof Activity) {
            context.finish();
        }
    }

    /**
     * 返回跳转activity
     * @param context
     * @param to
     * @param requestCode 请求码
     */
    public static <T> void jumpActForResult(Activity context, Class to,Serializable t, int requestCode) {
        Intent intent = new Intent(context, to);
        intent.putExtra("Entity",t);
        context.startActivityForResult(intent,requestCode);
    }
    public static void showFindPunctuation(final Activity context,final Class to,final String TYPE,final String search) {
        new PermissionUtil().requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                Intent intent = new Intent(context, to);
                intent.putExtra("TYPE", TYPE);
                intent.putExtra("search", search);
                context.startActivity(intent);
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }

    public static void showSearch(Activity context, int TAG, TransportMode transport) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("TAG", TAG);
        intent.putExtra("TransportMode", transport);
        context.startActivity(intent);
    }

    public static void togoSubmitAuthentication(Activity myActivity, Class toActivity) {
        Intent myIntent = new Intent();
        myIntent.setClass(myActivity, toActivity);
        myActivity.startActivity(myIntent);
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

    public static void showWEB(Activity mActivity,String url, String title) {
        Intent intent = new Intent(mActivity, WebViewActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("title", title);
        mActivity.startActivity(intent);
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

    private static void setCallRecord(Context context,Map<String,String> map){
        try {
            LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
            params.put("longitude", SharedTools.getStringValue(context, "longitude", "0.0"));
            params.put("latitude", SharedTools.getStringValue(context, "latitude", "0.0"));
            params.put("regionCode", SharedTools.getStringValue(context, "adCode", ""));

            params.put("callType", map.get("callType"));
            params.put("targetId", StringUtils.isEmpty(map.get("targetId"))?"":map.get("targetId"));
            new DataUtils((BaseActivity)context,params).getUserCallRecordCreate(new DataUtils.DataBack<UserCallRecord>() {
                @Override
                public void getData(UserCallRecord userCallRecord) {
                    if (userCallRecord == null){
                        return;
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
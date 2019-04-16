package com.sxhalo.PullCoal.tools.permissionutil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import java.util.List;

public class PermissionUtil {

    private static final String TAG = PermissionUtil.class.getSimpleName();

    private Activity mActivity;
    private PermissionListener listener;

    private RLAlertDialog settingDialog;
    private RLAlertDialog againDialog;

    String[] PERMISSIONS_GROUP = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 读取SD卡内容
            Manifest.permission.ACCESS_FINE_LOCATION,  //网络卫星定位
            Manifest.permission.CALL_PHONE,   //打电话权限
            Manifest.permission.CAMERA,  //相机权限
            Manifest.permission.READ_PHONE_STATE,  //读取手机状态
            Manifest.permission.REQUEST_INSTALL_PACKAGES,  //安装未知来源程序
    };
    String[] messageString = {
             "拉煤宝需要您的同意，才能使用您相册里的图片来设置您的头像和上传图片。",
             "拉煤宝会在定位，路径规划和导航等服务中使用您的位置信息。",
             "拉煤宝需要您的同意，才能使用打电话功能。",
             "拉煤宝需要您的同意，才能拍摄图片来设置您的头像和上传图片。",
             "拉煤宝需要您的同意，才能读取手机状态来为您提供更准确的服务。",
             "安装应用需要打开未知来源权限，请去设置中开启权限。"
    };


    public PermissionUtil() {
    }

    /**
     * 外部使用 申请权限
     * @param permissions 申请授权的权限
     * @param listener 授权回调的监听
     */
    public void requestPermissions(final Activity mActivity,String[] permissions, PermissionListener listener) {
        this.mActivity = mActivity;
        this.listener = listener;
        if (Build.VERSION.SDK_INT >= 23){
            AndPermission.with(mActivity)
                    .permission(permissions)
                    .rationale(new Rationale() {
                        @Override
                        public void showRationale(Context context, final List<String> permissions,final RequestExecutor executor) {
                            // 这里使用一个Dialog询问用户是否继续授权。
                            String message = getMessage(permissions,0);
                            if (againDialog == null){
                                LayoutInflater inflater1 = mActivity.getLayoutInflater();
                                View layout = inflater1.inflate(R.layout.dialog_updata, null);
                                ((TextView)layout.findViewById(R.id.updata_message)).setText(message);
                                againDialog = new RLAlertDialog(mActivity, "系统提示" , layout, "稍候提示",
                                        "继续授权", new RLAlertDialog.Listener() {
                                    @Override
                                    public void onLeftClick() {
                                        // 如果用户中断：
                                        executor.cancel();
                                        permissionHasDenied(mActivity,permissions);
                                    }
                                    @Override
                                    public void onRightClick() {
                                        // 如果用户继续：
                                        executor.execute();
                                    }
                                });
                                againDialog.show();
                            }
                        }
                    })
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            permissionAllGranted();
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(final List<String> permissions) {
                            // 这些权限被用户总是拒绝。
                            if (AndPermission.hasAlwaysDeniedPermission(mActivity, permissions)) {
                                // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。
                                final SettingService settingService = AndPermission.permissionSetting(mActivity);
                                String message = getMessage(permissions,1);
                                if (settingDialog == null) {
                                    LayoutInflater inflater1 = mActivity.getLayoutInflater();
                                    View layout = inflater1.inflate(R.layout.dialog_updata, null);
                                    ((TextView) layout.findViewById(R.id.updata_message)).setText(message);
                                    settingDialog = new RLAlertDialog(mActivity, "系统提示", layout, "取消授权",
                                            "手动设置", new RLAlertDialog.Listener() {
                                        @Override
                                        public void onLeftClick() {
                                            // 如果用户不同意去设置：
                                            settingService.cancel();
                                            permissionHasDenied(mActivity,permissions);
                                        }

                                        @Override
                                        public void onRightClick() {
                                            // 如果用户同意去设置：
                                            settingService.execute();
                                        }
                                    });
                                    settingDialog.show();
                                }
                            }
                        }
                    })
                    .start();
        }else{
            permissionAllGranted();
        }
    }

    /**
     * 获取需要给用户提示的文字信息
     * @param permissions
     * @param tag
     * @return
     */
    private String getMessage(List<String> permissions,int tag) {
        int select = 0;
        String message = "";
        List<String> permissionNames = Permission.transformText(mActivity, permissions);
        for (String permission : permissions){
            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){   // 读取SD卡内容
                select = 0;
            }else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){  //网络卫星定位
                select = 1;
            }else if(permission.equals(Manifest.permission.CALL_PHONE)){  //打电话权限
                select = 2;
            }else if(permission.equals(Manifest.permission.CAMERA)){  //相机权限
                select = 3;
            }else if(permission.equals(Manifest.permission.READ_PHONE_STATE)){  //读取手机状态
                select = 4;
            }else if(permission.equals(Manifest.permission.REQUEST_INSTALL_PACKAGES)){  //位置来源程序安装
                select = 5;
            }
        }
//         message = "您已经拒绝了\"" + TextUtils.join(",\n", permissionNames)+"\"权限，";
         message = message + messageString[select];
        if (tag == 0){
            message = message + "\n是否继续授权？";
        }else{
            message = message +  "\n请点击\"手动设置\"-\"权限\"-打开所需权限。\n" +
                    "最后点击两次后退按钮即可返回。";
        }
        return message;
    }



    /**
     * 权限全部已经授权
     */
    private void permissionAllGranted() {
        if (listener != null) {
            listener.onGranted();
        }
    }

    /**
     * 权限被拒绝
     * @param deniedList 被拒绝的权限List
     */
    private void permissionHasDenied(Context context, List<String> deniedList) {
        if (listener != null) {
            listener.onDenied(context,deniedList);
        }
    }


}

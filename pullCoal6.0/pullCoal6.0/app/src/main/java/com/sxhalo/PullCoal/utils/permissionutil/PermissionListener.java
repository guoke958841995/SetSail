package com.sxhalo.PullCoal.utils.permissionutil;

import android.content.Context;

import java.util.List;

public interface PermissionListener {

    /**
     * 所有权限都已授权
     */
    void onGranted();

    /**
     * 拒绝的权限
     * @param context
     * @param permissions
     */
    void onDenied(Context context, List<String> permissions);

//    /**
//     * 勾选了不在提示的权限，需要向用户解释为什么需要这个权限
//     * @param deniedPermission
//     */
//    void onShouldShowRationale(List<String> deniedPermission);
}

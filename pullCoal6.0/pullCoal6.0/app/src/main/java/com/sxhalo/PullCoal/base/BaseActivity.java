package com.sxhalo.PullCoal.base;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyApplication;
import com.sxhalo.PullCoal.ui.payment.PaymentInformationActivity;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.StatusBarUtil;
import com.sxhalo.PullCoal.utils.ToastUtils;
import com.sxhalo.PullCoal.utils.UIHelper;
import com.sxhalo.PullCoal.weight.dialog.RLAlertDialog;
import com.sxhalo.PullCoal.weight.popwidow.SelectSharePopupWindow;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 所有Activity的基类
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public abstract class BaseActivity<T1 extends BaseContract.BasePresenter> extends SupportActivity implements IBaseView, BaseContract.BaseView {


    @Inject
    protected T1 mPresenter;

    private View mRootView;
    private Unbinder unbinder;
    private ToastUtils toastUtils; //自定义Toast

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = createView(null, null, savedInstanceState);
        setContentView(mRootView);
        //设置状态栏颜色
        setStatusBar();
        initInjector(MyApplication.getInstance().getApplicationComponent());
        attachView();
        bindView(mRootView, savedInstanceState);
//        initStateView();
        initData();
    }


    /**
     * 设置状态栏颜色
     * 默认:沉浸式,状态栏字体黑色
     */
    protected void setStatusBar() {
        setStatusBar(this, Color.TRANSPARENT, Color.parseColor("#33000000"),
                true, true);
    }

    /**
     * 6.0以上机型适用某些或者界面不适用沉浸
     *
     * @param activity
     * @param color         状态栏颜色
     * @param isTransparent 是否是沉浸式
     * @param isBlack       状态栏字体是否为黑色
     */
    protected void setStatusBar(Activity activity, @ColorInt int color, boolean isTransparent,
                                boolean isBlack) {
        StatusBarUtil.setUseStatusBarColor(activity, color);
        StatusBarUtil.setSystemStatus(activity, isTransparent, isBlack);
    }


    /**
     * 重写该方法,设置状态栏颜色
     *
     * @param activity
     * @param color         状态栏颜色
     * @param surfaceColor  兼容5.0到6.0之间的状态栏颜色字体只能是白色，如果沉浸的颜色与状态栏颜色冲突,
     *                      设置一层浅色对比能显示出状态栏字体。
     * @param isTransparent 是否是沉浸式
     * @param isBlack       状态栏字体是否为黑色
     */
    protected void setStatusBar(Activity activity, @ColorInt int color, int surfaceColor,
                                boolean isTransparent, boolean isBlack) {

        StatusBarUtil.setUseStatusBarColor(activity, color, surfaceColor);
        StatusBarUtil.setSystemStatus(activity, isTransparent, isBlack);
    }


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(getContentLayout(), container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public View getView() {
        return mRootView;
    }


    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    private void initStateView() {
    }


    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }


    /**
     * 界面Toast
     * @param message
     */
    public void displayToast(String message){
        if (toastUtils == null){
            toastUtils = new ToastUtils();
        }
        toastUtils.displayToast(message,this);
    }

    /**
     * 弹出支付框
     * @param entity  跳转传递的实体类
     * @param type  区分煤炭和货运的参数 0-煤炭 1-货运
     */
    public void showPayDialog(final Serializable entity, final String type) {
        new RLAlertDialog(this, "系统提示", "这是一个付费信息，支付后可查看更多详细信息！", "取消",
                "去支付", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                Map<String,Serializable> map = new HashMap<>();
                map.put("type", type);
                map.put("Entity", entity);
                UIHelper.jumpAct(getParent(), PaymentInformationActivity.class,map,false);
                Intent intent = new Intent(getParent(), PaymentInformationActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("Entity", entity);
                startActivity(intent);
            }
        }).show();
    }


    /**
     * 分享弹框
     * @param title
     * @param targetUrl
     * @param summary
     */
    public void shareDailog(final String title, final String targetUrl, final String summary){
        LogUtil.e(title + "分享路径",targetUrl);
        final SelectSharePopupWindow selectSharePopupWindow = new SelectSharePopupWindow(this);
        selectSharePopupWindow.setSelectSharePopupWindow(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.wechat:
//                        if (MyApplication.getIWXAPI().isWXAppInstalled()) {
//                            if (wxSceneUtils == null){
//                                wxSceneUtils = new WXSceneUtils(mContext);
//                            }
//                            wxSceneUtils.setWXSceneSession(title, targetUrl, summary,false);
//                        }else{
//                            displayToast(getString(R.string.uninstalled_wechat));
//                        }
                        break;
                    case R.id.moments:
//                        if (MyApplication.getIWXAPI().isWXAppInstalled()) {
//                            if (wxSceneUtils == null) {
//                                wxSceneUtils = new WXSceneUtils(mContext);
//                            }
//                            wxSceneUtils.setWXSceneSession(title, targetUrl, summary, true);
//                        }else{
//                            displayToast(getString(R.string.uninstalled_wechat));
//                        }
                        break;
                    case R.id.qq:
//                        if (isQQAvilible()) {
//                            if (qqShareUtils == null) {
//                                qqShareUtils = new QQShareUtils(mContext);
//                            }
//                            qqShareUtils.setQShareCommit(title, targetUrl, summary);
//                        }
                        break;
                    case R.id.qzone:
//                        if (isQQAvilible()) {
//                            if (qqShareUtils == null) {
//                                qqShareUtils = new QQShareUtils(mContext);
//                            }
//                            qqShareUtils.shareToQzone(title, targetUrl, summary);
//                        }
                        break;
                }
                selectSharePopupWindow.dismiss();
            }
        });
//        selectSharePopupWindow.showAtLocation(this.findViewById(R.id.root_view), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
//        selectSharePopupWindow.showAsDropDown(); //设置layout在PopupWindow中显示的位置
    }

    /**
     * 判断是否安装QQ
     * @return
     */
    public boolean isQQAvilible () {
        PackageManager packageManager = getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        displayToast(getString(R.string.uninstalled_qq));
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (unbinder != null) {
            unbinder.unbind();
        }

        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}

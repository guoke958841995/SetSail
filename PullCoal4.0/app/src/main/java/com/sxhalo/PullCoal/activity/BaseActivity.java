package com.sxhalo.PullCoal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.retrofithttp.progress.ProgressDialogHandler;
import com.sxhalo.PullCoal.tools.CallBack;
import com.sxhalo.PullCoal.tools.image.ImageManager;
import com.sxhalo.PullCoal.ui.ColaProgress;
import com.sxhalo.PullCoal.ui.StatusBarUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.LoadProgressDialog;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.popwin.SelectSharePopupWindow;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.wxapi.QQShareUtils;
import com.sxhalo.PullCoal.wxapi.WXSceneUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 所有activity的基类
 * Created by liz on 2017/4/5.
 */
public abstract class BaseActivity extends AppCompatActivity {

    View statusView;
    protected Activity mContext;
    private  ImageManager mImageManager;  //图片加载空件工具创建
    private  ColaProgress mColaProgress;  //点击不可取消的等待条
    private  LoadProgressDialog dialog;    //点击可取消的等待条

    private QQShareUtils qqShareUtils;  //qq 分享工具
    private WXSceneUtils wxSceneUtils;

    public static final String ACCOUNT_CONFLICT_MESSAGE = "com.sxhalo.account_conflict_message";


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mContext = this;
            upDataAppException();
            //竖屏锁定
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // 添加Activity到堆栈
            AppManager.getAppManager().addActivity(this);
            createMainView(savedInstanceState);
            initTitle();
            getData();
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),true);
            GHLog.e("界面初始化",e.toString());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 进行数据处理
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存数据
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        try {
            //竖屏锁定
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            View  mBaseBinding = LayoutInflater.from(this).inflate(R.layout.activity_base, null, false);
            statusView = mBaseBinding.findViewById(R.id.status_view);
            View bindingView = getLayoutInflater().inflate(layoutResID,null, false);
            // content
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bindingView.setLayoutParams(params);
            RelativeLayout mContainer = (RelativeLayout) mBaseBinding.findViewById(R.id.container);
            mContainer.addView(bindingView);
            getWindow().setContentView(mBaseBinding);

            ButterKnife.bind(this);

            //设置全屏的沉侵式状态栏
            setStatusBar(-1,-1);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),true);
            GHLog.e("界面初始化",e.toString());
        }
    }

    /**
     *
     * @param barHeight  设置系统栏高度
     * @param mColor     设置
     */
    public void setStatusBar(int barHeight, int mColor){
        if (barHeight == -1){
            barHeight = StatusBarUtils.getStatusBarHeight(mContext);
        }

        if (mColor == -1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                mColor = R.color.transparent;
            }else{
                mColor = R.color.transparent_half;
            }
        }
        if (statusView != null){
            // 设置固定大小的占位符
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) statusView.getLayoutParams(); //取控件View当前的布局参数
            linearParams.height = barHeight;// 控件的高强制设成当前手机状态栏高度
            statusView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件</pre>
            statusView.setBackgroundColor(getResources().getColor(mColor));
        }else{
            GHLog.e("statusView为空","请检查代码");
        }
        if (barHeight == 0){
            //设置全屏的沉侵式状态栏
            StatusBarUtils.with(mContext).setIsActionBar(false).init(false);
        }else{
            //设置全屏的沉侵式状态栏
            StatusBarUtils.with(mContext).init(true);
        }
    }

    /**
     * 设置界面视图
     */
    protected abstract void createMainView(Bundle savedInstanceState);

    /**
     * 设置标题
     */
    protected abstract void initTitle();

    /**
     * 初始化数据
     */
    protected abstract void getData();

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            // 结束Activity从堆栈中移除
            AppManager.getAppManager().finishActivity(this);
        } catch (Exception e) {
            GHLog.e("onDestroy",e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 图片加载控件
     * @return
     */
    public ImageManager getImageManager() {
        if (mImageManager == null){
            mImageManager = new ImageManager(this);
        }
        return mImageManager;
    }

    /**
     * Toast提示
     *
     * @param text
     */
    public void displayToast(String text) {
        if (text == null)
            return;
        View view = getLayoutInflater().inflate(R.layout.myself_toast, null);
        TextView message = (TextView) view.findViewById(R.id.chapterName);
        message.setText(text);
        Toast start = new Toast(this);
        int get_height = BaseUtils.getWindowsHight(getApplication()) / 6;
        start.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER, 0,
                get_height);
        start.setDuration(Toast.LENGTH_SHORT);
        start.setView(view);
        start.show();
    }

    /**
     * 加载进度条
     *
     * @param message
     */
    public void showProgressDialog(String message) {
        if (dialog == null){
            dialog = new LoadProgressDialog(this, message);
        }
        dialog.show();
    }

    /**
     * 列表无数据时候显示空数据页面
     * @param count 列表数据个数
     * @param emptyView 空数据视图
     * @param listView 列表
     */
    public void showEmptyView (int count, ViewGroup emptyView, ViewGroup listView) {
        if (count == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 关闭进度条
     */
    public void dismisProgressDialog() {
        if (dialog == null) {
            return;
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }

    /**
     * 创建点击反回按钮无效的等待条
     * @param mActivity
     * @param showText
     */
    public void showDelog(Activity mActivity, String showText) {
        mColaProgress = ColaProgress.show(mActivity, showText, false, false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
//						Utils.showText(activity, 0, "联网中请稍后");
                        // dismissDelog();
                    }
                });
    }

    public void dismissDelog() {
        if (mColaProgress != null) {
            mColaProgress.dismiss();
        }
    }



    private void upDataAppException(){
        new CallBack().setAppException(new CallBack.AppException() {
            @Override
            public void onBack(final Context cont, String crashReport) {
                try {
//                    RefreshTool  data = (RefreshTool)getJacksonTools().readJsonToEntity(crashReport, RefreshTool.class);
//                    if (data.getSuccess().equals("0")){
//                        //退出
//                        AppManager.getAppManager().AppExit(cont);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 弹出支付框
     * @param entity  跳转传递的实体类
     * @param type  区分煤炭和货运的参数 0-煤炭 1-货运
     */
    public void showPayDialog(final Serializable entity, final String type) {
        LayoutInflater inflater1 = mContext.getLayoutInflater();
        View layout = inflater1.inflate(R.layout.dialog_updata, null);
        ((TextView) layout.findViewById(R.id.updata_message)).setText("这是一个付费信息，支付后可查看更多详细信息！");
        new RLAlertDialog(mContext, "系统提示", layout, "取消",
                "去支付", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(mContext, PaymentInformationActivity.class);
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
        GHLog.e(title + "分享路径",targetUrl);
        final SelectSharePopupWindow selectSharePopupWindow = new SelectSharePopupWindow(this);
        selectSharePopupWindow.setSelectSharePopupWindow(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.wechat:
                        if (MyAppLication.getIWXAPI().isWXAppInstalled()) {
                            if (wxSceneUtils == null){
                                wxSceneUtils = new WXSceneUtils(mContext);
                            }
                            wxSceneUtils.setWXSceneSession(title, targetUrl, summary,false);
                        }else{
                            displayToast(getString(R.string.uninstalled_wechat));
                        }
                        break;
                    case R.id.moments:
                        if (MyAppLication.getIWXAPI().isWXAppInstalled()) {
                            if (wxSceneUtils == null) {
                                wxSceneUtils = new WXSceneUtils(mContext);
                            }
                            wxSceneUtils.setWXSceneSession(title, targetUrl, summary, true);
                        }else{
                            displayToast(getString(R.string.uninstalled_wechat));
                        }
                        break;
                    case R.id.qq:
                        if (isQQAvilible()) {
                            if (qqShareUtils == null) {
                                qqShareUtils = new QQShareUtils(mContext);
                            }
                            qqShareUtils.setQShareCommit(title, targetUrl, summary);
                        }
                        break;
                    case R.id.qzone:
                        if (isQQAvilible()) {
                            if (qqShareUtils == null) {
                                qqShareUtils = new QQShareUtils(mContext);
                            }
                            qqShareUtils.shareToQzone(title, targetUrl, summary);
                        }
                        break;
                }
                selectSharePopupWindow.dismiss();
            }
        });
        selectSharePopupWindow.showAtLocation(this.findViewById(R.id.root_view), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
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

}

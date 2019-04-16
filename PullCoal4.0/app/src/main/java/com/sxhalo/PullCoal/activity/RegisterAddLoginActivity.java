package com.sxhalo.PullCoal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

//import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.FragmentAdapter;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.fragment.HomePagerFragment;
import com.sxhalo.PullCoal.fragment.LoginFragment;
import com.sxhalo.PullCoal.fragment.RegisterFragment;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.wxapi.BaseUiListener;
import com.tencent.connect.common.Constants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 登陆和注册
 * Created by amoldZhang on 2017/1/7.
 */
public class RegisterAddLoginActivity extends BaseActivity {


    @Bind(R.id.login_submit)
    TextView login_submit;
    @Bind(R.id.register_submit)
    TextView register_submit;

    @Bind(R.id.mViewPager)
    ViewPager mViewPager;


    private  final String TAG_NAME = "注册登录界面";
    public static final int REGISTER_home = 10;
    public static final int REGISTER = 11;
    public static final int FOUND_PWD = 12;
    public static final int WX = 13;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex = 0;
    /**界面切换数组*/
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    /**界面切换渲染器*/
    private FragmentAdapter mFragmentAdapter;
    /**
     * 界面fragment
     */
    private LoginFragment loginfragment;
    private RegisterFragment registerfragment;


//    public  SsoHandler mSsoHandler;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_add_login);
    }

    @Override
    protected void initTitle() {
        init();
    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.back, R.id.login_submit, R.id.register_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.register_submit:
                if (currentIndex == 0) {
                    currentIndex = 1;
                    register_submit.setText("登录");
                } else {
                    currentIndex = 0;
                    register_submit.setText("注册");
                }
                mViewPager.setCurrentItem(currentIndex);
                break;
        }
    }

    private void init() {
        try {

            loginfragment = new LoginFragment();
            //接受activity中fragment中返回回掉
            loginfragment.onCallBack(new LoginFragment.Callback() {
                @Override
                public void onCallBack() {
                    Intent intent = new Intent(HomePagerFragment.RECEIVED_ACTION);
                    sendBroadcast(intent);
                    finish();
                }
            });
            registerfragment = new RegisterFragment();
            mFragmentList.add(loginfragment);
            mFragmentList.add(registerfragment);

            mFragmentAdapter = new FragmentAdapter(
                    this.getSupportFragmentManager(), mFragmentList);
            mViewPager.setAdapter(mFragmentAdapter);
            mViewPager.setCurrentItem(currentIndex);

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


                /**
                 * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
                 */
                @Override
                public void onPageScrollStateChanged(int state) {

                }

                /**
                 * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
                 * offsetPixels:当前页面偏移的像素位置
                 */
                @Override
                public void onPageScrolled(int position, float offset,
                                           int offsetPixels) {

                }

                @SuppressLint("ResourceAsColor") @Override
                public void onPageSelected(int position) {
                    register_submit.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    login_submit.setTextColor(getResources().getColor(R.color.app_title_text_color_normal));
                    switch (position) {
                        case 0:
                            login_submit.setText("登录");
                            register_submit.setText("注册");
                            break;
                        case 1:
                            login_submit.setText("注册");
                            register_submit.setText("登录");
                            break;
                    }
                    currentIndex = position;
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        System.out.println("第三方登陆数据返回码" + requestCode);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_LOGIN:
                MyAppLication.getTencent().onActivityResultData(requestCode, resultCode, data,new BaseUiListener(this,MyAppLication.getTencent()));
                MyAppLication.dismissDelog();
                break;
            case 32973://SsoHandler.REQUEST_CODE_SSO_AUTH
                // SSO 授权回调
                // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
//                if (mSsoHandler != null) {
//                    Log.i("新浪微博登陆返回","返回");
//                    //不能少
//                    mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//                    MyAppLication.dismissDelog();
//                }
                break;
            case REGISTER:
                String collBack1 = SharedTools.getStringValue(this,"back","不返回");
                if (collBack1.equals("返回")){
                    finish();
                }
                break;
            case FOUND_PWD:
                    finish();
                break;
            default:
                break;
        }
    }
}

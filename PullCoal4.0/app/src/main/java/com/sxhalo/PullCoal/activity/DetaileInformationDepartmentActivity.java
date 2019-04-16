package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.fragment.ListCoalFragment;
import com.sxhalo.PullCoal.fragment.ListTranporstFragment;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.recyclerviewmode.SimpleViewPagerIndicator;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderInformationDepartmentEssential;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 信息部详情
 * Created by amoldZhang on 2016/12/12.
 */
public class DetaileInformationDepartmentActivity extends BaseActivity{

    private static final String TAG = "DetaileInformationDepartmentActivity";

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_bar_right_imageview)
    ImageView rightImage;

    @Bind(R.id.id_stickynavlayout_indicator)
    SimpleViewPagerIndicator mIndicator;
    @Bind(R.id.id_stickynavlayout_topview)
    RelativeLayout headView;
    @Bind(R.id.id_stickynavlayout_viewpager)
    ViewPager mViewPager;


    private String[] mTitles = new String[] { "煤炭信息", "货运信息" };
    private InformationDepartment dataInfor;
    private HeaderInformationDepartmentEssential inforDepartmentEsl;

    private String InfoDepartId;
    private String TYPE;
//    private FragmentManager fragmentManager;
//    private int defaultHeight;  // 当子布局没有数据的默认布局高度


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information_department_details);
        // TYPE == 0  跳转  TYPE != 0 直接结束
        TYPE = getIntent().getStringExtra("TYPE") == null ? "0" : getIntent().getStringExtra("TYPE");
    }

    @Override
    protected void initTitle() {
        title.setText("信息部详情");
        rightImage.setVisibility(View.VISIBLE);
        rightImage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_share));
    }

    @Override
    protected void getData() {
        try {
            InfoDepartId = getIntent().getStringExtra("InfoDepartId");
            GHLog.i("信息部详情", "InfoDepartId" + InfoDepartId);
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("coalSalesId", InfoDepartId);
            params.put("regionCode", SharedTools.getStringValue(this, "adCode", ""));
            params.put("longitude", SharedTools.getStringValue(this, "longitude", ""));
            params.put("latitude", SharedTools.getStringValue(this, "latitude", ""));
            new DataUtils(this, params).getCoalSalesInfo(new DataUtils.DataBack<InformationDepartment>() {
                @Override
                public void getData(InformationDepartment dataInfor) {
                    try {
                        if (dataInfor == null) {
                            return;
                        }
                        initView(dataInfor);
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    private void initView(final InformationDepartment dataInformationDepartment) {
        try {
            this.dataInfor = dataInformationDepartment;
            // 信息部基本信息
            initEssential(dataInformationDepartment,TYPE);
            initListData();
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }

    }

    /**
     * 数据信息加载
     */
    private void initListData() {
        try {
            mIndicator.setTitles(mTitles);
            mIndicator.onItemClick(new SimpleViewPagerIndicator.ItemClick() {
                @Override
                public void setItemClick(View tv) {
                    if (0 == (Integer) tv.getTag()){
                        mViewPager.setCurrentItem(0);
                    }else{
                        mViewPager.setCurrentItem(1);
                    }
                }
            });
            BaseFragmentAdapter mPagerAdapter =
                    new BaseFragmentAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    mIndicator.scroll(position, positionOffset);
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            mViewPager.setCurrentItem(0);
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }


    /**
     * 初始化信息部基本信息
     */
    private void initEssential(final InformationDepartment dataInformationDepartment,String TYPE) {
        // 信息部基本信息
        inforDepartmentEsl = new HeaderInformationDepartmentEssential(this);
        //当点击（关注按钮的逻辑判断）
        inforDepartmentEsl.setOnClickListener(new HeaderInformationDepartmentEssential.HeaderOnClickListener() {
            @Override
            public void HeaderOnClick(String follow) {
                String userId = SharedTools.getStringValue(DetaileInformationDepartmentActivity.this, "userId", "-1");
                if (!"-1".equals(userId)) {
                    // 0-未关注，1-关注
                    if ("0".equals(dataInformationDepartment.getIsFollow())) {
                        setFollow("1");
                    } else {
                        showDaiLog(DetaileInformationDepartmentActivity.this, getString(R.string.cancel_focus_tips));
                    }
                } else {
                    UIHelper.jumpActLogin(DetaileInformationDepartmentActivity.this,  false);
                }
            }
        });
        headView.addView(inforDepartmentEsl.getView(dataInformationDepartment,TYPE));
    }

    private void showDaiLog(Activity mActivity, final String message) {
//        LayoutInflater inflater1 = mActivity.getLayoutInflater();
//        View layout = inflater1.inflate(R.layout.dialog_updata, null);
//        ((TextView) layout.findViewById(R.id.updata_message)).setText(getString(R.string.cancel_focus_tips));
        new RLAlertDialog(mActivity, "系统消息", message, "取消关注",
                "容朕想想", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                setFollow("0");
            }

            @Override
            public void onRightClick() {
            }
        }).show();
    }

    private void callPhone(TransportMode transportMode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", transportMode.getPublishUserPhone());
        map.put("callType", Constant.CALE_TYPE_FREIGHT);
        map.put("targetId", transportMode.getTransportId());
        UIHelper.showCollTel(mContext, map, true);
    }

    private class BaseFragmentAdapter extends FragmentPagerAdapter {


        public BaseFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (0 == position) {
//                fragment = ListCoalFragment.getFragment(InfoDepartId,mViewPager,position,defaultHeight);
                fragment = ListCoalFragment.getFragment(InfoDepartId);
            } else if (1 == position) {
//                fragment =  ListTranporstFragment.getFragment(InfoDepartId,mViewPager,position,defaultHeight);
                fragment =  ListTranporstFragment.getFragment(InfoDepartId);
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            if (0 == position) {
                title = "煤炭信息";
            } else if (1 == position) {
                title =  "货运信息";
            }
            return title;
        }
        @Override
        public int getCount() {
            return 2;
        }

    }

    /**
     * 关注信息部
     * follow  // 0-关注，1-取消关注
     */
    private void setFollow(String follow) {
        String userId = SharedTools.getStringValue(this, "userId", "-1");
        if (userId.equals("-1")) {
            UIHelper.jumpActLogin(DetaileInformationDepartmentActivity.this,  false);
            return;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("coalSalesId", InfoDepartId);
        if (follow.equals("1")) {
            getUserCoalSalesCreate(follow, params);
        } else {
            getUserCoalSalesDelete(follow, params);
        }
    }

    /**
     * 关注信息部
     */
    private void getUserCoalSalesCreate(final String follow, LinkedHashMap<String, String> params) {
        try {
            new DataUtils(this, params).getUserCoalSalesCreate(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String message) {
                    if (StringUtils.isEmpty(message)) {
                        return;
                    }
                    dataInfor.setIsFollow(follow);
                    inforDepartmentEsl.setFollow(dataInfor);
//                    mAdapter.replace();
                    displayToast(message);
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }

    }

    /**
     * 取消关注信息部
     */
    private void getUserCoalSalesDelete(final String follow, LinkedHashMap<String, String> params) {
        try {
            new DataUtils(this, params).getUserCoalSalesDelete(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String message) {
                    if (StringUtils.isEmpty(message)) {
                        return;
                    }
                    dataInfor.setIsFollow(follow);
                    inforDepartmentEsl.setFollow(dataInfor);
//                    mAdapter.replace();
                    setResult(RESULT_OK);
                    displayToast(message);
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }


    @OnClick({R.id.title_bar_left, R.id.title_bar_right_imageview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
//                if (!TYPE.equals("0")){
//                    setResult(RESULT_OK);
//                }
                finish();
                break;
            case R.id.title_bar_right_imageview:
                if (!StringUtils.isEmpty(dataInfor.getCompanyName())){
                    String title = dataInfor.getCompanyName();
                    String targetUrl = new Config().getSHARE_RELEASE_SALES() + "?coalSalesId=" + InfoDepartId;
                    GHLog.e("分享路径",targetUrl);
                    //信息部名称加信息部地址
                    String summary = dataInfor.getCompanyName() + "  地址：" + dataInfor.getAddress();
                    if(BaseUtils.isNetworkConnected(getApplicationContext())){
                        shareDailog(title,targetUrl,summary);
                    }else{
                        displayToast(getString(R.string.unable_net_work));
                    }
                }
                break;
        }
    }


}

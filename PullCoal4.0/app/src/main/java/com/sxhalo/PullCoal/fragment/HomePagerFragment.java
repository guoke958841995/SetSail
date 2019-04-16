package com.sxhalo.PullCoal.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amoldzhang.hightlight.GuideView;
import com.amoldzhang.hightlight.GuideViewHelper;
import com.amoldzhang.hightlight.LightType;
import com.amoldzhang.hightlight.style.CenterLeftStyle;
import com.amoldzhang.hightlight.style.CenterRightStyle;
import com.amoldzhang.hightlight.style.LeftBottomStyle;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.MainSearchActivity;
import com.sxhalo.PullCoal.activity.MessageCenterActivity;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.AdvertisementEntity;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.map.NaviRoutePlanning;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.UserDailogUtils;
import com.sxhalo.PullCoal.ui.guide.GuidePageManager;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullHeadViewLayout;
import com.sxhalo.PullCoal.ui.pullrecyclerview.uihelper.TemplateContainerImpl;
import com.sxhalo.PullCoal.ui.recyclerviewmode.AdHeaderView;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderAdViewView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.PullToRefreshListView;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 首页功能实现类
 * Created by amoldZhang on 2016/12/6.
 */
public class HomePagerFragment extends Fragment {

    @Bind(R.id.deco_view1)
    RelativeLayout decoView1;
    @Bind(R.id.deco_view2)
    RelativeLayout decoView2;
    @Bind(R.id.deco_view3)
    RelativeLayout decoView3;
    @Bind(R.id.deco_view4)
    RelativeLayout decoView4;

    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.layout_search)
    LinearLayout layoutSearch;
    @Bind(R.id.message_ll)
    RelativeLayout messageLL;
    @Bind(R.id.iv_message)
    ImageView ivMessage;
    @Bind(R.id.iv_red_point)
    ImageView ivRedPoint;
    @Bind(R.id.layout_search_text)
    TextView layoutSearchHiteText;


    @Bind(R.id.refresh_scroll_view)
    PullToRefreshListView refreshRecyclerView;

    //获取当前活动界面
    private FragmentActivity myActivity;

    //刷新时消息列表
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private MyReceiver myReceiver;
    public static final String RECEIVED_ACTION = "com.sxhalo.PullCoal.RECEIVED_ACTION";
    private String city = "榆林";
    private View view;
    private UserDailogUtils dailog;


    private TemplateContainerImpl impl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_pager, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        initViewRecyclerView();
        initData(true);
        initTitleView();
        registerMyReceiver();
    }

    private void initTitleView() {
        setNavi(false);
        tvLocation.setTextColor(getResources().getColor(R.color.black));
        tvLocation.setText(city);
        tvLocation.setVisibility(View.VISIBLE);
        layoutSearch.setVisibility(View.VISIBLE);
        messageLL.setVisibility(View.VISIBLE);
        ivMessage.setImageDrawable(getResources().getDrawable(R.mipmap.message_center_icon));
        layoutSearchHiteText.setText("让买煤更快捷");
    }


    @OnClick({R.id.tv_location, R.id.layout_search, R.id.iv_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                setNavi(true);
                break;
            case R.id.layout_search:
                // 点击后跳转搜索页面
//                if (currIndex == 0) {
                UIHelper.jumpAct(myActivity, MainSearchActivity.class, false);
//                } else {
//                    UIHelper.showSearch(this, Constant.Search_Freight);
//                }
                break;
            case R.id.iv_message:
                // 点击后跳入消息中心
                UIHelper.jumpAct(myActivity, MessageCenterActivity.class, false);
                SharedTools.putBooleanValue(myActivity,"ivRedPoint",false);
                setIvRedPoint();
                break;
        }
    }

    private void setNavi(final boolean flage) {
        new PermissionUtil().requestPermissions(myActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                if (flage){
                    ((BaseActivity)myActivity).showProgressDialog("定位中，请稍后...");
                }
                //定位
                NaviRoutePlanning navi = new NaviRoutePlanning(myActivity);
                navi.startLocation();
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }

    /**
     * 设置定位信息
     * @param intent
     */
    public void setLocationText(Intent intent){
        if (tvLocation != null){
            city = intent.getStringExtra("city").replace("市", "");
            tvLocation.setText(city);
            ((BaseActivity)myActivity).dismisProgressDialog();
        }
    }

    /**
     * 设置红点显示信息
     */
    public void setIvRedPoint(){
        if (ivRedPoint != null ){
            if (SharedTools.getBooleanValue(myActivity,"ivRedPoint",false)){
                ivRedPoint.setVisibility(View.VISIBLE);
            }else{
                ivRedPoint.setVisibility(View.GONE);
            }

        }
    }

    public void registerMyReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(RECEIVED_ACTION);
        myActivity.registerReceiver(myReceiver, filter);
    }

    private void initData(boolean flage) {
        try{
            new DataUtils(myActivity,new LinkedHashMap<String, String>()).getHome(new DataUtils.DataBack<HomeData>() {
                @Override
                public void getData(HomeData data) {
                    try {
                        if (data == null) {
                            return;
                        }
                        setData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    refreshRecyclerView.onRefreshComplete();  //数据加载完成后，关闭header,footer
                }
            },flage);
        }catch (Exception e){
            GHLog.e("联网错误", e.toString());
        }
    }

    /**
     * 将联网获取的数据进行赋值，并刷新界面
     */
    private void setData(HomeData data) {
        try {
            impl.startConstruct(data);
            refreshRecyclerView.onRefreshComplete();  //数据加载完成后，关闭header,footer
        } catch (Exception e) {
            GHLog.e("数据刷新", e.toString());
        }
    }

    /**
     * RecyclerView 初始化
     */
    private void initViewRecyclerView() {
        impl = new TemplateContainerImpl(myActivity);

        //设置刷新模式 ，both代表支持上拉和下拉，pull_from_end代表上拉，pull_from_start代表下拉
        refreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        refreshRecyclerView.setHeaderLayout(new PullHeadViewLayout(myActivity));

        //3.设置监听事件
        refreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData(false);//请求网络数据，并更新listview组件
                    }
                }, 1500);
            }
        });
        impl.setListView(refreshRecyclerView);

        view.post(new Runnable() {
            @Override
            public void run() {
                showGuideViews();
            }
        });
    }

    private void showGuideViews() {
        // 新手功能引导页
        if(GuidePageManager.hasNotShowed(myActivity, HomePagerFragment.class.getSimpleName())){
            showClickDecoToNext(HomePagerFragment.class.getSimpleName());
        }else{
            getAdData();
        }
    }


    /**
     * 获取广告数据
     */
    private void getAdData() {
        try {
            LinkedHashMap<String, String> myParams = new LinkedHashMap<>();
            myParams.put("adCotegoryCode","pop-up-ad");
            new DataUtils(myActivity,myParams).getAdvertisement(new DataUtils.DataBack<APPData<AdvertisementEntity>>() {
                @Override
                public void getData(APPData<AdvertisementEntity> advertisement) {
                    try {
                        if (advertisement == null || advertisement.getList().size() == 0 || advertisement.getList().get(0) == null) {
                            //没有广告
                        } else {
                            //有广告
                            showADDialog(advertisement.getList());
                        }
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出对话框问询
     */
    private void showADDialog(final List<AdvertisementEntity> advertisementEntityList)throws Exception{
        AdHeaderView adHeaderView = new AdHeaderView(myActivity,null,advertisementEntityList);
        adHeaderView.setADItemOnClickListener(new HeaderAdViewView.ADOnClickListener() {
            @Override
            public void adItemOnClickListener(View view, int position) {
                try {
                    AdvertisementEntity mEntity = advertisementEntityList.get(position);
                    System.out.println("跳转连接++++"+mEntity.getAdUrl());
                    if(mEntity.getAdUrl().equals("")||mEntity.getAdUrl().equals("null")){
                    }else{
                        GHLog.i("头条点击", position+"");
                        Intent intent = new Intent(myActivity, WebViewActivity.class);
                        intent.putExtra("URL",mEntity.getAdUrl());
                        myActivity.startActivity(intent);
                    }
                    dailog.dismiss();
                } catch (Exception e) {
                    GHLog.e("头条点击", e.toString());
                }
            }
        });

        LayoutInflater inflater1 = getLayoutInflater();
        View layout = inflater1.inflate(R.layout.dialog_updata_advertisement, null,false);
        RelativeLayout viewGroup = (RelativeLayout)layout.findViewById(R.id.count_view_group);
        adHeaderView.getView().setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        viewGroup.addView(adHeaderView.getView());
        dailog = new UserDailogUtils(myActivity, layout, new UserDailogUtils.Listener() {

            @Override
            public void onDissmiss() {
            }

            @Override
            public void onClick() {

            }
        });

        dailog.setCancelable(false);
        dailog.showDialog();
    }



    private GuideViewHelper helper;
    /**
     *自己控制显示下一个高亮
     */
    private void showClickDecoToNext(final String pageTag){
        View deco_view1 = LayoutInflater.from(myActivity).inflate(R.layout.view_home_page_001, (ViewGroup) myActivity.getWindow().getDecorView(), false);
        View deco_view2 = LayoutInflater.from(myActivity).inflate(R.layout.view_home_page_002, (ViewGroup) myActivity.getWindow().getDecorView(),false);
        View deco_view3 = LayoutInflater.from(myActivity).inflate(R.layout.view_home_page_003, (ViewGroup) myActivity.getWindow().getDecorView(), false);
        View deco_view4 = LayoutInflater.from(myActivity).inflate(R.layout.view_home_page_004, (ViewGroup) myActivity.getWindow().getDecorView(), false);


        deco_view1.findViewById(R.id.btn_home_page_next_001).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.nextLight();
            }
        });
        deco_view2.findViewById(R.id.btn_home_page_next_002).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.nextLight();
            }
        });
        deco_view3.findViewById(R.id.btn_home_page_next_003).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.nextLight();
            }
        });
        deco_view4.findViewById(R.id.btn_home_page_next_004).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.nextLight();
            }
        });

        ((TextView)deco_view1.findViewById(R.id.highest_001)).setText("");
        ((TextView)deco_view2.findViewById(R.id.highest_002)).setText("");
        ((TextView)deco_view3.findViewById(R.id.highest_003)).setText("");
        ((TextView)deco_view4.findViewById(R.id.highest_004)).setText("");

        //注意这里要是addView第一个参数传的是View
        // 一定注意LayoutInflater.from(this).inflate中第二个一定要传入个ViewGroup
        //为了保存XML中的LayoutParams
        helper = new GuideViewHelper(myActivity)
                .addView(decoView1, new CenterRightStyle(deco_view1))
                .addView(decoView2, new LeftBottomStyle(deco_view2))
                .addView(decoView3, new CenterLeftStyle(deco_view3))
                .addView(decoView4, new CenterLeftStyle(deco_view4))
                .type(LightType.Circle)
                .onDismiss(new GuideView.OnDismissListener() {
                    @Override
                    public void dismiss() {
                        GuidePageManager.setHasShowedGuidePage(myActivity, pageTag, true);
                        getAdData();
                    }
                });
        helper.show();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 添加好友或者删除好友时收到通知 需要刷新界面
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RECEIVED_ACTION.equals(intent.getAction())) {
                initData(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myActivity.unregisterReceiver(myReceiver);
    }
}

package com.sxhalo.PullCoal.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.OfflineDownloadedAdapter;
import com.sxhalo.PullCoal.adapter.OfflineListAdapter;
import com.sxhalo.PullCoal.adapter.OfflinePagerAdapter;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2017/1/13.
 */
public class OfflineMapActivity extends BaseActivity implements OfflineMapManager.OfflineMapDownloadListener,ViewPager.OnPageChangeListener, OfflineMapManager.OfflineLoadedListener {

    @Bind(R.id.all_list_text)
    TextView allListText;
    @Bind(R.id.download_list_text)
    TextView downloadListText;
    @Bind(R.id.content_viewpage)
    ViewPager mContentViewPage;
    @Bind(R.id.but_all_updata)
    Button butAllUpdata;
    @Bind(R.id.but_all_continue)
    Button butAllContinue;
    @Bind(R.id.but_all_pause)
    Button butAllPause;
    @Bind(R.id.but_all_cancel)
    Button butAllCancel;
    @Bind(R.id.bottom_button)
    LinearLayout bottomButton;

    /**
     * 更新所有列表
     */
    private final static int UPDATE_LIST = 0;
    /**
     * 显示toast log
     */
    private final static int SHOW_MSG = 1;

    private final static int DISMISS_INIT_DIALOG = 2;
    private final static int SHOW_INIT_DIALOG = 3;

    private OfflineMapManager amapManager = null;// 离线地图下载控制器
    private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
    // 刚进入该页面时初始化弹出的dialog
    private ProgressDialog initDialog;

    // view pager 两个list以及他们的adapter
    private ExpandableListView mAllOfflineMapList;
    private ListView mDownLoadedList;

    private OfflineListAdapter adapter;
    private OfflineDownloadedAdapter mDownloadedAdapter;
    private PagerAdapter mPageAdapter;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case UPDATE_LIST:
                        if (mContentViewPage.getCurrentItem() == 0) {
                            ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                        } else {
                            mDownloadedAdapter.notifyDataChange();
                        }
                        break;
                    case SHOW_MSG:
                        displayToast((String)msg.obj);
                        break;

                    case DISMISS_INIT_DIALOG:
                        initDialog.dismiss();
                        handler.sendEmptyMessage(UPDATE_LIST);
                        break;
                    case SHOW_INIT_DIALOG:
                        if (initDialog != null) {
                            initDialog.show();
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                GHLog.e("",e.toString());
            }
        }

    };

    @Override
    public void onDownload(int status, int completeCode, String downName) {
        switch (status) {
            case OfflineMapStatus.SUCCESS:
                // changeOfflineMapTitle(OfflineMapStatus.SUCCESS, downName);
                break;
            case OfflineMapStatus.LOADING:
                GHLog.d("amap-download", "download: " + completeCode + "%" + ","
                        + downName);
                // changeOfflineMapTitle(OfflineMapStatus.LOADING, downName);
                break;
            case OfflineMapStatus.UNZIP:
                GHLog.d("amap-unzip", "unzip: " + completeCode + "%" + "," + downName);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP, downName);
                break;
            case OfflineMapStatus.WAITING:
                GHLog.d("amap-waiting", "WAITING: " + completeCode + "%" + ","
                        + downName);
                break;
            case OfflineMapStatus.PAUSE:
                GHLog.d("amap-pause", "pause: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.STOP:
                break;
            case OfflineMapStatus.ERROR:
                GHLog.e("amap-download", "download: " + " ERROR " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_AMAP:
                GHLog.e("amap-download", "download: " + " EXCEPTION_AMAP " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                GHLog.e("amap-download", "download: " + " EXCEPTION_NETWORK_LOADING "
                        + downName);
                Toast.makeText(OfflineMapActivity.this, "网络异常", Toast.LENGTH_SHORT)
                        .show();
                amapManager.pause();
                break;
            case OfflineMapStatus.EXCEPTION_SDCARD:
                GHLog.e("amap-download", "download: " + " EXCEPTION_SDCARD "
                        + downName);
                break;
            default:
                break;
        }

        // changeOfflineMapTitle(status, downName);
        handler.sendEmptyMessage(UPDATE_LIST);
    }

    @Override
    public void onCheckUpdate(boolean hasNew, String name) {
        GHLog.i("amap-demo", "onCheckUpdate " + name + " : " + hasNew);
        Message message = new Message();
        message.what = SHOW_MSG;
//        message.obj = "CheckUpdate " + name + " : " + hasNew;
        if (hasNew) {
            message.obj = name  + "有更新了";
        } else {
            message.obj = name  + "暂无更新";
        }
        handler.sendMessage(message);
    }

    @Override
    public void onRemove(boolean success, String name, String describe) {
        GHLog.i("amap-demo", "onRemove " + name + " : " + success + " , "
                + describe);
        handler.sendEmptyMessage(UPDATE_LIST);

        Message message = new Message();
        message.what = SHOW_MSG;
//        message.obj = "onRemove " + name + " : " + success + " , " + describe;
        if (success) {
            message.obj = getString(R.string.delete_map_success);
        }else {
            message.obj = getString(R.string.delete_map_failed);
        }
        handler.sendMessage(message);

    }

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_offline_map1);
    }

    @Override
    protected void initTitle() {
        //指定离线地图下载路径
        MapsInitializer.sdcardDir = mContext.getExternalCacheDir() + "";
    }

    @Override
    protected void getData() {
        //构造离线地图类
        amapManager = new OfflineMapManager(this, this);
        //离线地图初始化完成监听
        amapManager.setOnOfflineLoadedListener(this);
        initDialog();
    }

    @OnClick({R.id.back_image_view, R.id.all_list_text, R.id.download_list_text, R.id.content_viewpage, R.id.but_all_updata, R.id.but_all_continue, R.id.but_all_pause, R.id.but_all_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_image_view:
                finish();
                break;
            case R.id.all_list_text:
                int paddingHorizontal = downloadListText.getPaddingLeft();
                int paddingVertical = downloadListText.getPaddingTop();
                mContentViewPage.setCurrentItem(0);
                allListText.setTextColor(getResources().getColor(R.color.white));
                downloadListText.setTextColor(getResources().getColor(R.color.app_title_text_color));

                downloadListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab2_normal);
                allListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab1_pressed);
                allListText.setPadding(paddingHorizontal, paddingVertical,
                        paddingHorizontal, paddingVertical);
                downloadListText.setPadding(paddingHorizontal, paddingVertical,
                        paddingHorizontal, paddingVertical);

                mDownloadedAdapter.notifyDataChange();
                break;
            case R.id.download_list_text:
                int paddingHorizontal1 = downloadListText.getPaddingLeft();
                int paddingVertical1 = downloadListText.getPaddingTop();
                mContentViewPage.setCurrentItem(1);
                downloadListText.setTextColor(getResources().getColor(R.color.white));
                allListText.setTextColor(getResources().getColor(R.color.app_title_text_color));

                downloadListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab2_pressed);

                allListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab1_normal);

                allListText.setPadding(paddingHorizontal1, paddingVertical1,
                        paddingHorizontal1, paddingVertical1);

                downloadListText.setPadding(paddingHorizontal1, paddingVertical1,
                        paddingHorizontal1, paddingVertical1);

                mDownloadedAdapter.notifyDataChange();
                break;
            case R.id.content_viewpage:
                break;
            case R.id.but_all_updata:
                break;
            case R.id.but_all_continue:
                break;
            case R.id.but_all_pause:
                break;
            case R.id.but_all_cancel:
                break;
        }
    }

    /**
     * 初始化如果已下载的城市多的话，会比较耗时
     */
    private void initDialog() {
        if (initDialog == null)
            initDialog = new ProgressDialog(this);
        initDialog.setMessage(getString(R.string.get_offline_map_list_tips));
        initDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        initDialog.setIndeterminate(false);
        initDialog.setCancelable(false);
        initDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissDialog() {
        if (initDialog != null) {
            initDialog.dismiss();
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int paddingHorizontal = downloadListText.getPaddingLeft();
        int paddingVertical = downloadListText.getPaddingTop();

        switch (position) {
            case 0:
                allListText.setTextColor(getResources().getColor(R.color.white));
                downloadListText.setTextColor(getResources().getColor(R.color.app_title_text_color));
                allListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab1_pressed);
                downloadListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab2_normal);
                // mPageAdapter.notifyDataSetChanged();
                break;
            case 1:
                allListText.setTextColor(getResources().getColor(R.color.app_title_text_color));
                downloadListText.setTextColor(getResources().getColor(R.color.white));
                allListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab1_normal);

                downloadListText
                        .setBackgroundResource(R.drawable.offlinearrow_tab2_pressed);
                // mDownloadedAdapter.notifyDataChange();
                break;
        }
        handler.sendEmptyMessage(UPDATE_LIST);
        downloadListText.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
        allListText.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onVerifyComplete() {
        initAllCityList();
        initDownloadedList();
        initViewpage();
        dissmissDialog();
    }

    /**
     * 初始化所有城市列表
     */
    public void initAllCityList() {
        // 扩展列表
        View provinceContainer = LayoutInflater.from(OfflineMapActivity.this)
                .inflate(R.layout.offline_province_listview, null);
        mAllOfflineMapList = (ExpandableListView) provinceContainer
                .findViewById(R.id.province_download_list);
        initProvinceListAndCityMap();
        // adapter = new OfflineListAdapter(provinceList, cityMap, amapManager,
        // OfflineMapActivity.this);
        adapter = new OfflineListAdapter(provinceList, amapManager,OfflineMapActivity.this);
        // 为列表绑定数据源
        mAllOfflineMapList.setAdapter(adapter);
        // adapter实现了扩展列表的展开与合并监听
        mAllOfflineMapList.setOnGroupCollapseListener(adapter);
        mAllOfflineMapList.setOnGroupExpandListener(adapter);
        mAllOfflineMapList.setGroupIndicator(null);
    }

    /**
     * sdk内部存放形式为<br>
     * 省份 - 各自子城市<br>
     * 北京-北京<br>
     * ...<br>
     * 澳门-澳门<br>
     * 概要图-概要图<br>
     * <br>
     * 修改一下存放结构:<br>
     * 概要图-概要图<br>
     * 直辖市-四个直辖市<br>
     * 港澳-澳门香港<br>
     * 省份-各自子城市<br>
     */
    private void initProvinceListAndCityMap() {

        List<OfflineMapProvince> lists = amapManager
                .getOfflineMapProvinceList();

        provinceList.add(null);
        provinceList.add(null);
        provinceList.add(null);
        // 添加3个null 以防后面添加出现 index out of bounds

        ArrayList<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
        ArrayList<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
        ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图

        for (int i = 0; i < lists.size(); i++) {
            OfflineMapProvince province = lists.get(i);
            if (province.getCityList().size() != 1) {
                // 普通省份
                provinceList.add(i + 3, province);
                // cityMap.put(i + 3, cities);
            } else {
                String name = province.getProvinceName();
                if (name.contains("香港")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("澳门")) {
                    gangaoList.addAll(province.getCityList());
                } else if (name.contains("全国概要图")) {
                    gaiyaotuList.addAll(province.getCityList());
                } else {
                    // 直辖市
                    cityList.addAll(province.getCityList());
                }
            }
        }

        // 添加，概要图，直辖市，港口
        OfflineMapProvince gaiyaotu = new OfflineMapProvince();
        gaiyaotu.setProvinceName("概要图");
        gaiyaotu.setCityList(gaiyaotuList);
        provinceList.set(0, gaiyaotu);// 使用set替换掉刚开始的null

        OfflineMapProvince zhixiashi = new OfflineMapProvince();
        zhixiashi.setProvinceName("直辖市");
        zhixiashi.setCityList(cityList);
        provinceList.set(1, zhixiashi);

        OfflineMapProvince gaogao = new OfflineMapProvince();
        gaogao.setProvinceName("港澳");
        gaogao.setCityList(gangaoList);
        provinceList.set(2, gaogao);

        // cityMap.put(0, gaiyaotuList);// 在HashMap中第0位置添加全国概要图
        // cityMap.put(1, cityList);// 在HashMap中第1位置添加直辖市
        // cityMap.put(2, gangaoList);// 在HashMap中第2位置添加港澳

    }

    /**
     * 初始化已下载列表
     */
    public void initDownloadedList() {
        mDownLoadedList = (ListView) LayoutInflater.from(
                OfflineMapActivity.this).inflate(
                R.layout.offline_downloaded_list, null);
        android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
                android.widget.AbsListView.LayoutParams.MATCH_PARENT,
                android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
        mDownLoadedList.setLayoutParams(params);
        mDownloadedAdapter = new OfflineDownloadedAdapter(this, amapManager);
        mDownLoadedList.setAdapter(mDownloadedAdapter);
    }

    private void initViewpage() {
        mPageAdapter = new OfflinePagerAdapter(mContentViewPage,
                mAllOfflineMapList, mDownLoadedList);

        mContentViewPage.setAdapter(mPageAdapter);
        mContentViewPage.setCurrentItem(0);
        mContentViewPage.setOnPageChangeListener(this);
    }
}

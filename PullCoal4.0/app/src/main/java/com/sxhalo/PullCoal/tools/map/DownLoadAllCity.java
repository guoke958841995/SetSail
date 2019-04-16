//package com.sxhalo.PullCoal.tools.map;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ExpandableListView;
//
//import com.amap.api.maps.offlinemap.OfflineMapCity;
//import com.amap.api.maps.offlinemap.OfflineMapManager;
//import com.amap.api.maps.offlinemap.OfflineMapProvince;
//import com.sxhalo.PullCoal.R;
//import com.sxhalo.PullCoal.adapter.OfflineListAdapter;
//import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderOfflineMapView;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by amoldZhang on 2016/10/23.
// */
//public class DownLoadAllCity {
//
//    private Activity mActivity;
//    private OfflineMapManager amapManager;
//    private List<OfflineMapProvince> downloadAllCityList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
//    private List<OfflineMapCity> citys;
//    private int what;
//
//    private ExpandableListView mDownloaded_ELV;
//    private HeaderOfflineMapView headerView;
//    private OfflineListAdapter mAllCityMapAdapter;
//
//    public DownLoadAllCity(Activity mActivity, OfflineMapManager amapManager, int what) {
//        this.mActivity = mActivity;
//        this.amapManager = amapManager;
//        this.what = what;
//        setData();
//    }
//
//    private void setData() {
//        ReorganizationData data = new ReorganizationData(amapManager);
//        downloadAllCityList = data.getProvinceList();
//        citys = data.getCitys();
//    }
//
//
//    public View onCreateView(){
//        View downloadeView = LayoutInflater.from(mActivity)
//                .inflate(R.layout.download_view, null);
//        mDownloaded_ELV = (ExpandableListView)downloadeView.findViewById(R.id.offline_downloaded_list);
//
//
//        // 设置自定义视图
//        headerView = new HeaderOfflineMapView(mActivity,amapManager,what);
//        headerView.fillView(citys, mDownloaded_ELV);
//
//
//        mAllCityMapAdapter = new OfflineListAdapter(downloadAllCityList, amapManager,mActivity,what);
//        // 为列表绑定数据源
//        mDownloaded_ELV.setAdapter(mAllCityMapAdapter);
//        // adapter实现了扩展列表的展开与合并监听
//        mDownloaded_ELV.setOnGroupCollapseListener(mAllCityMapAdapter);
//        mDownloaded_ELV.setOnGroupExpandListener(mAllCityMapAdapter);
//        mDownloaded_ELV.setGroupIndicator(null);
//
//        return downloadeView;
//    }
//
//    public void setRefresh(){
//        headerView.dealWithTheView();
//        mAllCityMapAdapter.notifyDataSetChanged();
//    }
//
//}

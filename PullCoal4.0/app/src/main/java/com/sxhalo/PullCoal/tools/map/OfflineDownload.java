//package com.sxhalo.PullCoal.tools.map;
//
//import android.app.Activity;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ExpandableListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.amap.api.maps.offlinemap.OfflineMapCity;
//import com.amap.api.maps.offlinemap.OfflineMapManager;
//import com.amap.api.maps.offlinemap.OfflineMapProvince;
//import com.sxhalo.PullCoal.R;
//import com.sxhalo.PullCoal.adapter.OfflineDownloadedAdapter;
//import com.sxhalo.PullCoal.adapter.OfflineListAdapter;
//import com.sxhalo.PullCoal.ui.CustomListView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class OfflineDownload {
//
//	private Activity mActivity;
//	private OfflineMapManager amapManager;
//	private ExpandableListView mDownloaded_ELV;
//	private List<OfflineMapProvince> downloadProvinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
//	private List<OfflineMapCity> citys;
//	private int what;
////	private HeaderDoweloadOfflineMapView headerView;
//	private OfflineListAdapter mMapAdapter;
//
//
//	private RelativeLayout wifi_rl,downloading_rl_text,downloaded_rl_text;
//	private TextView upText,downText;
//	private CustomListView downlondingLv;
//	private OfflineDownloadedAdapter mDownloadedAdapter;
//
//	public OfflineDownload(Activity mActivity,OfflineMapManager amapManager,int what) {
//		this.mActivity = mActivity;
//		this.amapManager = amapManager;
//		this.what = what;
//		setDate();
//	}
//
//	private void setDate(){
//		ReorganizationData data = new ReorganizationData(amapManager);
//		downloadProvinceList = data.getdownloadProvinceList();
//		citys = amapManager.getDownloadingCityList();
//	}
//
//
//	public  View  onCreateView(){
//
//		View downloadeView = LayoutInflater.from(mActivity).inflate(R.layout.download_view, null);
//
//		mDownloaded_ELV = (ExpandableListView)downloadeView.findViewById(R.id.offline_downloaded_list);
//
////		// 设置自定义视图
////		headerView = new HeaderDoweloadOfflineMapView(mActivity,amapManager,what);
////		headerView.fillView(citys, mDownloaded_ELV);
//
//
//		View view = LayoutInflater.from(mActivity).inflate(R.layout.offline_header_layout, mDownloaded_ELV, false);
//		wifi_rl = (RelativeLayout) view.findViewById(R.id.wifi_rl);
//		downloading_rl_text = (RelativeLayout) view.findViewById(R.id.downloading_rl_text);
//		upText = (TextView)view.findViewById(R.id.upText);
//		upText.setText("正在下载");
//		downlondingLv = (CustomListView)view.findViewById(R.id.offline_downloading_list);
//		downloaded_rl_text = (RelativeLayout) view.findViewById(R.id.downloaded_rl_text);
//		downText = (TextView)view.findViewById(R.id.downText);
//		downText.setText("下载完成");
//		mDownloadedAdapter = new OfflineDownloadedAdapter(mActivity, amapManager,citys,what);
//		downlondingLv.setAdapter(mDownloadedAdapter);
//		mDownloaded_ELV.addHeaderView(view);
//
//
//		mMapAdapter = new OfflineListAdapter(downloadProvinceList, amapManager,mActivity,what);
//		// 为列表绑定数据源
//		mDownloaded_ELV.setAdapter(mMapAdapter);
//		// adapter实现了扩展列表的展开与合并监听
//		mDownloaded_ELV.setOnGroupCollapseListener(mMapAdapter);
//		mDownloaded_ELV.setOnGroupExpandListener(mMapAdapter);
//		mDownloaded_ELV.setGroupIndicator(null);
//
//		return downloadeView;
//	}
//
//
//	public void setRefresh(){
//		setDate();
//		if (citys.size() != 0){
//			downloading_rl_text.setVisibility(View.VISIBLE);
//		}else{
//			downloading_rl_text.setVisibility(View.GONE);
//		}
//		if (downloadProvinceList.size() != 0){
//			downloaded_rl_text.setVisibility(View.VISIBLE);
//		}else{
//			downloaded_rl_text.setVisibility(View.GONE);
//		}
//		System.out.print("");
//		Log.i("downloadProvinceList", "setRefresh: "+downloadProvinceList.size());
//		Log.i("citys", "setRefresh: "+citys.size());
////		headerView.dealWithTheView(citys,downloadProvinceList);
//		mDownloadedAdapter.notifyDataChange();
//		mMapAdapter.onRefresh(downloadProvinceList);
//	}
//
//
//
//}

package com.sxhalo.PullCoal.tools.map;

import android.util.Log;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;

import java.util.ArrayList;
import java.util.List;

public class ReorganizationData {

	private OfflineMapManager amapManager;

	public ReorganizationData(OfflineMapManager amapManager) {
		this.amapManager = amapManager;
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
	public List<OfflineMapProvince> getProvinceList() {
		List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();
		List<OfflineMapProvince> lists = amapManager.getOfflineMapProvinceList();

		provinceList.add(null);
		provinceList.add(null);
//		provinceList.add(null);
		// 添加3个null 以防后面添加出现 index out of bounds

//		ArrayList<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
		ArrayList<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
		ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图

		for (int i = 0; i < lists.size(); i++) {
			OfflineMapProvince province = lists.get(i);
			if (province.getCityList().size() != 1) {
				// 普通省份
//				provinceList.add(i + 3, province);
				provinceList.add(i + 2, province);
			} else {
				String name = province.getProvinceName();
				if (name.contains("香港")) {
					gangaoList.addAll(0,province.getCityList());
				} else if (name.contains("澳门")) {
					gangaoList.addAll(1,province.getCityList());
				} else if (name.contains("全国概要图")) {
					gaiyaotuList.addAll(province.getCityList());
				} else {
					// 直辖市
//					cityList.addAll(province.getCityList());
					gaiyaotuList.addAll(province.getCityList());
				}
			}
		}



		// 添加，概要图，直辖市，港口
		OfflineMapProvince gaiyaotu = new OfflineMapProvince();
		gaiyaotu.setProvinceName("全国概要图+直辖市");
		gaiyaotu.setCityList(gaiyaotuList);
		provinceList.set(0, gaiyaotu);// 使用set替换掉刚开始的null

//		OfflineMapProvince zhixiashi = new OfflineMapProvince();
//		zhixiashi.setProvinceName("直辖市");
//		zhixiashi.setCityList(cityList);
//		provinceList.set(1, zhixiashi);

		OfflineMapProvince gaogao = new OfflineMapProvince();
		gaogao.setProvinceName("港澳地区");
		gaogao.setCityList(gangaoList);
		provinceList.set(1, gaogao);
		return provinceList;
	}



	/**
	 * 获取已经下载的省列表城市
	 * @return
	 */
	public List<OfflineMapProvince> getdownloadProvinceList() {
		try{
			List<OfflineMapProvince> downloadProvinceList = new ArrayList<OfflineMapProvince>();
//		List<OfflineMapProvince> lists = amapManager.getDownloadOfflineMapProvinceList() ;
//			List<OfflineMapProvince> lists = setDownloadProvince() ;
			List<OfflineMapProvince> lists = amapManager.getDownloadingProvinceList() ;

			if(lists.size() != 0){
				downloadProvinceList.add(null);
				downloadProvinceList.add(null);

				ArrayList<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
				ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图

				for (int i = 0; i < lists.size(); i++) {
					OfflineMapProvince province = lists.get(i);
					if (province.getCityList().size() != 1) {
						// 普通省份
						downloadProvinceList.add(i + 2, province);
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
							gaiyaotuList.addAll(province.getCityList());
						}
					}
				}

				// 添加，概要图，直辖市
				OfflineMapProvince gaiyaotu = new OfflineMapProvince();
				gaiyaotu.setProvinceName("全国概要图+直辖市");
				gaiyaotu.setCityList(gaiyaotuList);
				downloadProvinceList.set(0, gaiyaotu);// 使用set替换掉刚开始的null


				OfflineMapProvince gaogao = new OfflineMapProvince();
				gaogao.setProvinceName("港澳地区");
				gaogao.setCityList(gangaoList);
				downloadProvinceList.set(1, gaogao);


//			if(downloadProvinceList.get(0).getCityList().size() == 0){
//				if(downloadProvinceList.get(1).getCityList().size() == 0){
//					downloadProvinceList.remove(1);
//					downloadProvinceList.remove(0);
//				}else{
//					downloadProvinceList.remove(0);
//				}
//
//			}else{
//				if(downloadProvinceList.get(1).getCityList().size() == 0){
//					downloadProvinceList.remove(1);
//				}
//			}

			}
			return downloadProvinceList;
		}catch (Exception e){
			Log.e("以下载的省列表数据处理异常",e.toString());
			return amapManager.getDownloadingProvinceList();
		}
	}




	public List<OfflineMapCity> getCitys() {
		List<OfflineMapCity> citys = new ArrayList<OfflineMapCity>();
		List<OfflineMapProvince> lists = amapManager.getOfflineMapProvinceList();

//		List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();

		for (int i = 0; i < lists.size(); i++) {
			OfflineMapProvince province = lists.get(i);
			if (province.getCityList().size() != 1) {
				for(int k=0;k<province.getCityList().size();k++){
					String name = province.getCityList().get(k).getCity();
					if(name.contains("西安")){
						citys.add(province.getCityList().get(k));
					}
				}
//				provinceList.add(province);
			} else {
				String name = province.getProvinceName();
				if (name.contains("香港")) {
//					gangaoList.addAll(province.getCityList());
				} else if (name.contains("澳门")) {
//					gangaoList.addAll(province.getCityList());
				} else if (name.contains("全国概要图")) {
					citys.addAll(province.getCityList());
				} else {
					// 直辖市
					if(name.contains("上海")){
						citys.addAll(province.getCityList());
					}
					if(name.contains("北京")){
						citys.addAll(province.getCityList());
					}
				}
			}
		}
		return citys;
	}


	/**
	 * 将以下载的城市处理成以省格式返回
	 * @return
	 */
	public ArrayList<OfflineMapProvince> setDownloadProvince() {
		try{
			//获取所有存在有离线地图的省的列表。
			ArrayList<OfflineMapProvince> offProvince = amapManager.getOfflineMapProvinceList();
			//获取所有已下载的城市
			ArrayList<OfflineMapCity>  offCity = amapManager.getDownloadOfflineMapCityList();

			ArrayList<OfflineMapProvince> offNewProvince = new ArrayList<OfflineMapProvince>();
			offNewProvince = offProvince;

			if (offCity.size() != 0){
				for (int i=0;i<offProvince.size();i++){
					ArrayList<OfflineMapCity> cityList = offProvince.get(i).getCityList();
					ArrayList<OfflineMapCity> list = new ArrayList<OfflineMapCity>();
					if (cityList.size() != 0) {
						for (int j = 0; j < cityList.size(); j++) {
							String cityCode = cityList.get(j).getCode();
							for (int k = 0; k < offCity.size(); k++) {
								if (cityCode.equals(offCity.get(k).getCode())) {
									list.add(cityList.get(j));
								}
							}
						}
					}
					offNewProvince.get(i).setCityList(list);
				}
			}

			for (int i=0;i<offNewProvince.size();i++) {
				if (offNewProvince.get(i).getCityList().size() == 0){
					offNewProvince.remove(offNewProvince.get(i));
				}
			}
			return offNewProvince;
		}catch (Exception e){
			Log.e("省列表数据处理",e.toString());
			return amapManager.getDownloadingProvinceList();
		}
	}

}

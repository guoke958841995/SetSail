package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.AreaModel;
import com.sxhalo.PullCoal.model.CityModel;
import com.sxhalo.PullCoal.model.ProvinceModel;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.FileUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 地区选择页面
 * Created by liz on 2017/8/23.
 */
public class SelectAreaActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_select_area)
    TextView tvSelectArea;
    @Bind(R.id.gridview)
    GridView gridview;
    @Bind(R.id.tv_back)
    TextView tvBack;

    private int tag = 0;//用来标记gridview是一级列表还是二级列表
    private String strProvince;//用来记录当前点击了哪个省
    private String strCity;//用来记录当前点击了哪个市
    private String strArea;//
    private List<ProvinceModel> provinceModels;//省菜单数据
    private List<CityModel> cityModels;//市菜单数据
    private List<AreaModel> areaModels;//区菜单数据
    private MyAdapter adapter;
//    private int level = 0;//默认显示选择全部
    private String cityCode;//二级城市地区code

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_area);
    }

    @Override
    protected void initTitle() {
        title.setText("地区选择");
//        level = getIntent().getIntExtra("level", 0);
        initData();
    }

    private void initData() {
        try {
            String resString = FileUtils.loadDataFromFile(this, "region_code");
            Gson gson = new Gson();
            Type type = new TypeToken<List<ProvinceModel>>(){}.getType();
            provinceModels = gson.fromJson(resString,type);
        } catch (Exception e) {
            getRegionCode();
            GHLog.e("没找到地址库","地址库找不到了");
            MyException.uploadExceptionToServer(this,e.fillInStackTrace());
        }
    }


    /**
     * 获取行政区数据
     * **/
    private void getRegionCode() {
        try {
            new DataUtils(mContext).getRegionCode(new DataUtils.DataBack<List<ProvinceModel>>() {
                @Override
                public void getData(List<ProvinceModel> dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
                        //存入本地文件
                        String JsonString = new Gson().toJson(dataList);
                        FileUtils.saveDataToFile(mContext, JsonString, "region_code");

                        initData();
                    } catch (Exception e) {
                        GHLog.e("地址存入异常", e.toString());
                        MyException.uploadExceptionToServer(mContext, e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    GHLog.e("联网失败", e.toString());
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    protected void getData() {
        try {
            adapter = new MyAdapter();
            adapter.setData(provinceModels);
            gridview.setAdapter(adapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setDisplayData(tag, position);
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    private void setDisplayData(int type, int position) {
        switch (type) {
            case 0:
                //省列表
                ProvinceModel provinceModel = ((ProvinceModel) adapter.getItem(position));
                strProvince = provinceModel.getProvinceName();
                if (provinceModel.getCitys().size() > 0) {
                    tag = 1;
                    tvBack.setVisibility(View.VISIBLE);
                    tvSelectArea.setText("当前地区：" + strProvince);
                    cityModels = new ArrayList<CityModel>();
                    cityModels.addAll(provinceModels.get(position).getCitys());
                    adapter.refreshData(cityModels);
                } else {
                    tvBack.setVisibility(View.GONE);
                    Intent intent = new Intent();
                    intent.putExtra("code", provinceModel.getProvinceId());
                    intent.putExtra("name", strProvince);
                    intent.putExtra("strCity", strCity);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case 1:
                //市列表
                CityModel cityModel = ((CityModel) adapter.getItem(position));
                strCity = cityModel.getCityName();
                cityCode = cityModel.getCityId();
                if (cityModel.getAreas().size() > 0) {
                    tag = 2;
                    tvBack.setVisibility(View.VISIBLE);
                    areaModels = new ArrayList<AreaModel>();
                    tvSelectArea.setText("当前地区：" + strProvince + strCity);
                    areaModels.addAll(cityModel.getAreas());
                    adapter.refreshData(areaModels);
                } else {
                    tvBack.setVisibility(View.GONE);
                    Intent intent = new Intent();
                    intent.putExtra("code", cityCode);
                    String string = strProvince + strCity;
                    string = StringUtils.setString(string, "全", 0);
                    intent.putExtra("name", string);
                    intent.putExtra("strCity", strCity);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case 2:
                //区列表
                AreaModel areaModel = (AreaModel) adapter.getItem(position);
                strArea = areaModel.getAreaName();
                Intent intent = new Intent();
//                switch (level) {
//                    case 0:
                        String string = strProvince + strCity + strArea;
                        string = StringUtils.setString(string, "全", 0);
                        intent.putExtra("code", areaModel.getAreaId());
                        intent.putExtra("name", string);
                        intent.putExtra("strCity", strCity);
//                        break;
//                    case 2:
//                        String string1 = strProvince + strCity;
//                        string1 = StringUtils.setString(string1, "全", 0);
//                        intent.putExtra("code", cityCode);
//                        intent.putExtra("name", string1);
//                        break;
//                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @OnClick({R.id.title_bar_left, R.id.tv_back, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.tv_back:
                if (tag == 2) {
                    tag = 1;
                    tvSelectArea.setText("当前地区：" + strProvince);
                    adapter.refreshData(cityModels);
                }else if (tag == 1) {
                    tag = 0;
                    tvSelectArea.setText("请选择目的地");
                    tvBack.setVisibility(View.GONE);
                    adapter.refreshData(provinceModels);
                }
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }

    class MyAdapter<T> extends BaseAdapter {

        public List<T> getData() {
            return data;
        }

        public void setData(List<T> data) {
            this.data.addAll(data);
        }

        private List<T> data = new ArrayList<T>();

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.area_grid_item, null, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            switch (tag) {
                case 0:
                    textView.setText(((ProvinceModel) data.get(position)).getProvinceName());
                    break;
                case 1:
                    textView.setText(((CityModel) data.get(position)).getCityName());
                    break;
                case 2:
                    textView.setText(((AreaModel) data.get(position)).getAreaName());
                    break;
            }
            return convertView;
        }

        public void refreshData(List<T> list) {
            data.clear();
            data.addAll(list);
            notifyDataSetChanged();
        }
    }
}

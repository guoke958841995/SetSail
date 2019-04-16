package com.sxhalo.PullCoal.ui;

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
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.bean.APPData;
import com.sxhalo.PullCoal.bean.AccessKeyBean;
import com.sxhalo.PullCoal.bean.AdvertisementBean;
import com.sxhalo.PullCoal.bean.AreaModel;
import com.sxhalo.PullCoal.bean.CityModel;
import com.sxhalo.PullCoal.bean.Dictionary;
import com.sxhalo.PullCoal.bean.ProvinceModel;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.utils.FileUtils;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 地区选择
 * Created by amoldZhang on 2019/4/12
 */
public class SelectAreaActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_select_area)
    TextView tvSelectArea;
    @BindView(R.id.gridview)
    GridView gridview;
    @BindView(R.id.tv_back)
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
    public int getContentLayout() {
        return R.layout.activity_select_area;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {

        title.setText("地区选择");
//        level = getIntent().getIntExtra("level", 0);
        try {
            String resString = FileUtils.loadDataFromFile(this, AppConstant.REGION_CODE_FILE_NAME);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ProvinceModel>>() {
            }.getType();
            provinceModels = gson.fromJson(resString, type);
        } catch (Exception e) {
            initData();
            LogUtil.e("没找到地址库", "地址库找不到了");
        }

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
            LogUtil.e(getClass().getName(), e.toString());
        }
    }

    @Override
    public void initData() {
        getRegionCode();
    }

    private void getRegionCode() {
        //获取数据
        mPresenter.getRegionCode(this);
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

    /**
     * 获取地址数据 联网返回
     *
     * @param provinceModelList
     * @param e
     */
    @Override
    public void saveRegionCode(List<ProvinceModel> provinceModelList, Throwable e) {
        if (provinceModelList == null) {
            return;
        }
        //存入本地文件
        String JsonString = new Gson().toJson(provinceModelList);
        FileUtils.saveDataToFile(this, JsonString, AppConstant.REGION_CODE_FILE_NAME);
        initData();
    }

    /**
     * 空实现 不用管
     *
     * @param accessKeyBean
     * @param e
     */
    @Override
    public void savePublicKey(AccessKeyBean accessKeyBean, Throwable e) {
    }

    /**
     * 空实现 不用管
     *
     * @param advertisementBeanAPPData
     * @param e
     */
    @Override
    public void loadAdverData(APPData<AdvertisementBean> advertisementBeanAPPData, Throwable e) {
    }

    /**
     * 空实现 不用管
     *
     * @param dictionaryList
     * @param e
     */
    @Override
    public void updateDictionary(List<Dictionary> dictionaryList, Throwable e) {
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

package com.sxhalo.PullCoal.ui.popwin;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.model.AreaModel;
import com.sxhalo.PullCoal.model.CityModel;
import com.sxhalo.PullCoal.model.ProvinceModel;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.FileUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 地区选择popupwindow
 */
public class SelectAreaPopupWindow extends PopupWindow implements View.OnClickListener {

    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地

    private int type;//用来区别是哪个界面的地区选择  0：货运界面 1：订阅路线界面

    private BaseActivity myActivity;
    private View view;
    private TextView tvSelectArea, tvBack, tvCancel, textView;
    private GridView gridview;
    private int tag = 0;//用来标记gridview是一级列表还是二级列表
    private int currentType = 0;//用来判断点击的是出发地还是目的地
    private String strProvince;//用来记录当前点击了哪个省
    private String strCity;//用来记录当前点击了哪个市
    private List<ProvinceModel> provinceModels;//省菜单数据
    private List<CityModel> cityModels;//市菜单数据
    private List<AreaModel> areaModels;//区菜单数据
    private MyAdapter adapter;

    public String getStartCode() {
        return startCode;
    }

    public String getEndCode() {
        return endCode;
    }

    private String startCode;//出发地code
    private String endCode;//目的地code

    public SelectAreaPopupWindow(BaseActivity activity, int type) {
        this.myActivity = activity;
        this.type = type;
        initData();
    }

    private void initData() {
        try {
            String resString = FileUtils.loadDataFromFile(myActivity, "region_code");
            Gson gson = new Gson();
            Type type = new TypeToken<List<ProvinceModel>>(){}.getType();
            provinceModels =  gson.fromJson(resString,type);
            initView();
            initListener();
        } catch (Exception e) {
            getRegionCode();
            GHLog.d("没找到地址库","地址库找不到了");
            MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
        }
    }

    /**
     * 获取行政区数据
     * **/
    private void getRegionCode() {
        try {
            new DataUtils(myActivity).getRegionCode(new DataUtils.DataBack<List<ProvinceModel>>() {
                @Override
                public void getData(List<ProvinceModel> dataList) {
                    try {
                        if (dataList == null) {
                            return;
                        }
                        //存入本地文件
                        String JsonString = new Gson().toJson(dataList);
                        FileUtils.saveDataToFile(myActivity, JsonString, "region_code");

                        initData();
                    } catch (Exception e) {
                        GHLog.e("地址存入异常", e.toString());
                        MyException.uploadExceptionToServer(myActivity, e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    GHLog.e("联网失败", e.toString());
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(myActivity,e.fillInStackTrace());
            GHLog.e("联网错误", e.toString());
        }
    }
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.area_popview, null);
        setContentView(view);
        tvSelectArea = (TextView) view.findViewById(R.id.tv_select_area);
        tvBack = (TextView) view.findViewById(R.id.tv_back);
        gridview = (GridView) view.findViewById(R.id.gridview);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(00000000);
        setBackgroundDrawable(dw);
        setFocusable(true);
        setOutsideTouchable(false);
        this.update();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return true;
            }
        });
        adapter = new MyAdapter();
        adapter.setData(provinceModels);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setDisplayData(tag, position);
            }
        });
    }

    private void setDisplayData(int type, int position) {
        switch (type) {
            case 0:
                //市列表
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
                    tag = 0;
                    tvBack.setVisibility(View.GONE);
                    textView.setText(strProvince);
                    setCurrentCode(provinceModel.getProvinceId());
                    adapter.refreshData(provinceModels);
                    dismiss();
                }
                break;
            case 1:
                //区列表
                CityModel cityModel = ((CityModel) adapter.getItem(position));
                strCity = cityModel.getCityName();
                if (cityModel.getAreas().size() > 0) {
                    tag = 2;
                    tvBack.setVisibility(View.VISIBLE);
                    areaModels = new ArrayList<AreaModel>();
                    tvSelectArea.setText("当前地区：" + strProvince + strCity);
                    areaModels.addAll(cityModel.getAreas());
                    adapter.refreshData(areaModels);
                } else {
                    tag = 0;
                    tvBack.setVisibility(View.GONE);
                    if (this.type == 0) {
                        //货运界面  只显示县
                        textView.setText(strCity);
                    } else {
                        //订阅路线  显示省+市+县
                        textView.setText(strProvince + "-" + strCity);
                    }
                    setCurrentCode(cityModel.getCityId());
                    adapter.refreshData(provinceModels);
                    dismiss();
                }
                break;
            case 2:
                //省列表0
                tag = 0;
                tvBack.setVisibility(View.GONE);
                AreaModel areaModel = (AreaModel) adapter.getItem(position);
                if (this.type == 0) {
                     //货运界面  只显示县
                    textView.setText(areaModel.getAreaName());
                } else {
                    //订阅路线  显示省+市+县
                    textView.setText(strProvince + "-" + strCity + "-" + areaModel.getAreaName());
                }
                setCurrentCode(areaModel.getAreaId());
                adapter.refreshData(provinceModels);
                dismiss();
                break;
        }
    }

    private void setCurrentCode(String code) {
        switch (currentType) {
            case TYPE_START:
                startCode = code;
                break;
            case TYPE_END:
                endCode = code;
                break;
        }
    }

    private void initListener() {
        tvBack.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                dismiss();
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
                convertView = myActivity.getLayoutInflater().inflate(R.layout.area_grid_item, null, false);
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

    public void showPopupWindow(TextView textView, int currentType, View location) {
        this.currentType = currentType;
        this.textView = textView;
        tvSelectArea.setText("请选择地区");
        if (Build.VERSION.SDK_INT < 24) {
            if (!this.isShowing()) {
                this.showAsDropDown(location);
            } else {
                this.dismiss();
            }
        } else {
            if (!this.isShowing()) {
                int[] a = new int[2];
                location.getLocationInWindow(a);
                this.showAtLocation((myActivity).getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, location.getHeight()+a[1]);
                this.update();
            } else {
                this.dismiss();
            }
        }
    }
}

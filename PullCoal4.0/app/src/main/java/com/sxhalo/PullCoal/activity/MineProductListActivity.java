package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.popwin.SelectAreaPopupWindow;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 矿口筛选列表
 * Created by amoldZhang on 2017/8/4.
 */
public class MineProductListActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<MineMouth>{

    @Bind(R.id.coal_list_type)
    SmoothListView listView;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.tv_area)
    TextView tvArea;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;
    @Bind(R.id.layout1)
    RelativeLayout layout1;
    @Bind(R.id.layout_top)
    LinearLayout layout2;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.tv_order)
    TextView tvOrder;

    private BaseAdapterUtils baseAdapterUtils;
    private List<MineMouth> mineProductList = new ArrayList<MineMouth>();
    private int currentPage = 1;
    private String regionCode;//地区code 默认全国是0
    private int orderType = 0;//排序条件 0综合排序 1煤炭数排序 2货运数排序 3营业状态排序
    private FilterPopupWindow filterPopupWindow;
    private SelectAreaPopupWindow areaPopupWindow;
    private PopupWindow mPopupWindow;
    private boolean needRefresh = false;
    private String[] strings = new String[]{"综合排序", "煤炭数排序", "货运数排序", "营业状态排序"};
    private List<Map<String, String>> stringList = new ArrayList<Map<String, String>>();
    private Map<String, FilterEntity> filterMap = new HashMap<String, FilterEntity>();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_product_list);
    }

    @Override
    protected void initTitle() {
        initListener();
        initData();
//        createPopupWindow();
    }


    private void initListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                baseAdapterUtils.onRefresh();
            }
        });


//        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                    //根据关键刷新数据
//                    baseAdapterUtils.onRefresh();
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    @Override
    protected void getData() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        //搜索条件
        if (!StringUtils.isEmpty(etSearch.getText().toString().trim())) {
            params.put("searchValue", etSearch.getText().toString().trim());
        }
        //地区
        if (!StringUtils.isEmpty(regionCode)) {
            params.put("regionCode", regionCode);
        }
        if (filterMap.size() > 0) {
            for (Map.Entry<String, FilterEntity> entry : filterMap.entrySet()) {
                if (entry.getKey().equals("分类") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                    //信息部id
                    params.put("typeId", entry.getValue().dictCode);
                }
                if (entry.getKey().equals("状态") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                    //营业状态
                    params.put("operatingStatus", entry.getValue().dictCode);
                }
            }
        }
        params.put("orderType", orderType + "");
        params.put("currentPage", currentPage + "");
        params.put("pageSize", "10");
        new DataUtils(this,params).getCompaniesList(new DataUtils.DataBack<APPDataList<MineMouth>>() {
            @Override
            public void getData(APPDataList<MineMouth> dataList) {
                try {
                    if (dataList.getList().size() != 0) {
                        baseAdapterUtils.refreshData(dataList.getList());
                        mineProductList = baseAdapterUtils.getListData();
                    }
                    showEmptyView(baseAdapterUtils.getCount(), relativeLayout, listView);
                } catch (Exception e) {
                    GHLog.e(getClass().getName(),e.toString());
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                }
            }
        });
    }

    private void initData() {
        baseAdapterUtils = new BaseAdapterUtils(mContext, listView);
        baseAdapterUtils.settingList(true, false);
        baseAdapterUtils.setViewItemData(R.layout.home_recommend_item, mineProductList);
        baseAdapterUtils.setBaseAdapterBack(this);
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, MineMouth mineProduct, int pos) {  //
        try {
            RelativeLayout relativeLayout = (RelativeLayout) helper.getView().findViewById(R.id.recommend_mine_rl);
            relativeLayout.setVisibility(View.VISIBLE);
            ImageView iv = (ImageView)helper.getView().findViewById(R.id.recommend_mine_IV);
            getImageManager().loadMinebgUrlImage(mineProduct.getMineMouthPic(),iv);
            helper.setText(R.id.recommend_mine_name, mineProduct.getMineMouthName());
            helper.setText(R.id.recommend_mine_postal_address_v, mineProduct.getAddress());
            helper.setText(R.id.mine_info_num, mineProduct.getCoalSalesNum() + "个");

            helper.setText(R.id.mine_coal_num, mineProduct.getGoodsTotal() + "个");
            helper.setText(R.id.mine_freight_num, mineProduct.getTransTotal() + "个");

            String typeId = mineProduct.getTypeId();
            Dictionary sys100004 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100004"}).get(0);
            String typeValue = "未知";
            for (FilterEntity filterEntity : sys100004.list){
                if (filterEntity.dictCode.equals(typeId)){
                    typeValue = filterEntity.dictValue;
                    break;
                }
            }
            helper.setText(R.id.mine_type, typeValue);

            ImageView mineBusinessType = (ImageView)helper.getView().findViewById(R.id.mine_business_type);
            TextView doBusinessTime = (TextView)helper.getView().findViewById(R.id.do_business_time);
            //1（营业）、2（停业）、3（关闭）、4（筹建）、5（其他）',
            if (mineProduct.getOperatingStatus().equals("1")){
                mineBusinessType.setImageResource(R.mipmap.do_business_status);
                mineBusinessType.setVisibility(View.VISIBLE);
                doBusinessTime.setVisibility(View.GONE);
            }else if (mineProduct.getOperatingStatus().equals("2")){
                mineBusinessType.setImageResource(R.mipmap.out_of_business_status);
                mineBusinessType.setVisibility(View.VISIBLE);
                doBusinessTime.setVisibility(View.VISIBLE);
                String time = StringUtils.isEmpty(mineProduct.getReportEndDate())? "" : DateUtil.strToStrType(mineProduct.getReportEndDate(),"MM-dd").replace("-",".");
                doBusinessTime.setText("预计"+time+"营业");
            } else {
                doBusinessTime.setVisibility(View.GONE);
                mineBusinessType.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            GHLog.e("矿口赋值列表",e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<MineMouth> mAdapter) {
        Intent intent = new Intent();
        GHLog.i("矿口展示", position + "被点击");
        intent.setClass(MineProductListActivity.this, DetaileMineActivity.class);
        String inforDepartId = mAdapter.getItem(position - 1).getMineMouthId();
        intent.putExtra("InfoDepartId", inforDepartId);
        startActivity(intent);
    }

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        getData();
    }

    @Override
    public void getOnLoadMore(int page) {
        this.currentPage = page;
        getData();
    }

    @OnClick({R.id.title_bar_left, R.id.map_type, R.id.layout_order, R.id.layout_filter, R.id.layout_area})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                UIHelper.showFindPunctuation(this, SearchPunctuationActivity.class, "查找矿口", "");
                break;
            case R.id.layout_area:
                if (areaPopupWindow == null) {
                    createAreaWindow();
                }
                areaPopupWindow.showPopupWindow(tvArea,0,this.view);
                break;
            case R.id.layout_order:
                if (mPopupWindow == null) {
                    createPopupWindow();
                }
                showPopupWindow();
                break;
            case R.id.layout_filter:
                if (filterPopupWindow == null) {
                    createFilterWindow();
                }
                if (!filterPopupWindow.isShowing()) {
                    filterPopupWindow.showAsDropDown(this.view);
                    tvFilter.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                } else {
                    filterPopupWindow.dismiss();
                    tvFilter.setTextColor(getResources().getColor(R.color.black));
                }
//                filterPopupWindow.showFilterPopup(this.view);
                break;
        }
    }

    /**
     * 显示排序弹出框
     */
    private void showPopupWindow() {
        if (mPopupWindow.isShowing()) {
            darkenBackground(1f);
            mPopupWindow.dismiss();
        } else {
            darkenBackground(0.7f);
            mPopupWindow.showAsDropDown(this.view);
        }
    }

    /**
     * 改变背景颜色
     */
    private void darkenBackground(Float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    /**
     * 创建排序弹出框
     */
    private void createPopupWindow() {
        for (int i = 0; i < strings.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", strings[i]);
            if (i == 0) {
                map.put("show", "true");
            } else {
                map.put("show", "false");
            }
            stringList.add(map);
        }
        // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null);
        // 创建PopupWindow实例,宽度和高度充满
        mPopupWindow = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 使其聚集
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        mPopupWindow.setAnimationStyle(R.style.AnimationFade);

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1f);
                if (needRefresh) {
                    needRefresh = false;
                    baseAdapterUtils.onRefresh();
                }
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        ListView lv = (ListView) customView.findViewById(R.id.popview_listview);
        final QuickAdapter<Map<String, String>> adapter = new QuickAdapter<Map<String, String>>(this, R.layout.popupwindow_list_item, stringList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> item, int position) {
                helper.setText(R.id.tv_list_item, item.get("title"));
                if (item.get("show").equals("true")) {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (orderType == i) {
                    mPopupWindow.dismiss();
                    return;
                }
                needRefresh = true;
                orderType = i;
                for (int k = 0; k < stringList.size(); k++) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (k == i) {
                        map.put("show", "true");
                    } else {
                        map.put("show", "false");
                    }
                    map.put("title", stringList.get(k).get("title"));
                    stringList.set(k, map);
                }
                tvOrder.setText(strings[i]);
                adapter.replaceAll(stringList);
                mPopupWindow.dismiss();
            }
        });
    }

    private void createFilterWindow() {
        List<Dictionary> data = new ArrayList<Dictionary>();
        Dictionary sys100004 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100004"}).get(0);
        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.setChecked(true);
        filterEntity1.dictCode = "";
        sys100004.title = "分类";
        filterEntity1.dictValue = "不限";
        sys100004.list.add(0, filterEntity1);
        Dictionary sys100005 = new Dictionary();
        sys100005.list = new ArrayList<FilterEntity>();
        sys100005.title = "状态";
        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.setChecked(true);
        filterEntity2.dictCode = "";
        filterEntity2.dictValue = "不限";
        FilterEntity filterEntity3 = new FilterEntity();
        filterEntity3.dictCode = "1";
        filterEntity3.dictValue = "营业";
        FilterEntity filterEntity4 = new FilterEntity();
        filterEntity4.dictCode = "2";
        filterEntity4.dictValue = "停业";
        sys100005.list.add(0, filterEntity2);
        sys100005.list.add(1, filterEntity3);
        sys100005.list.add(2, filterEntity4);
        data.add(sys100004);
        data.add(sys100005);
        filterPopupWindow = new FilterPopupWindow(this, data, false, false,false, listView.getHeight());
        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                filterMap = filterPopupWindow.getMap();
                baseAdapterUtils.onRefresh();
            }
        });
    }

    private void createAreaWindow() {
        areaPopupWindow = new SelectAreaPopupWindow(this, 0);
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (StringUtils.isEmpty(areaPopupWindow.getStartCode())) {
                    //弹出地区选择后没有选择任何地区 拦截联网
                    return;
                }
                if (!StringUtils.isEmpty(regionCode) && regionCode.equals(areaPopupWindow.getStartCode())) {
                    //之前选择过地区后 第二次选择同样的地区 拦截联网
                    return;
                }
                if (StringUtils.isEmpty(regionCode) && "0".equals(areaPopupWindow.getStartCode())) {
                    //默认是全国 并且选择全国的时候  拦截联网
                    return;
                }
                if (areaPopupWindow.getStartCode().equals(regionCode)) {
                    //当两次选择相同的时候 拦截联网
                    return;
                }
                if ("0".equals(areaPopupWindow.getStartCode())) {
                    regionCode ="";
                } else {
                    regionCode = areaPopupWindow.getStartCode();
                }
                baseAdapterUtils.onRefresh();
            }
        });
    }
}

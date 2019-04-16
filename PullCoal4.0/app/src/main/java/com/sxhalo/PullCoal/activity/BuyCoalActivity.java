package com.sxhalo.PullCoal.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.AdvertisementEntity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.ListCoalEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.addrightview.LayoutContrastView;
import com.sxhalo.PullCoal.ui.popwin.CommonPopupWindow;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.recyclerviewmode.AdHeaderView;
import com.sxhalo.PullCoal.ui.recyclerviewmode.GridHeaderView;
import com.sxhalo.PullCoal.ui.recyclerviewmode.PreferenceHeaderView;
import com.sxhalo.PullCoal.ui.recyclerviewmode.StickySuspensionGroupView;
import com.sxhalo.PullCoal.ui.recyclerviewmode.StickySuspensionView;
import com.sxhalo.PullCoal.ui.recyclerviewmode.TestLoadFooter;
import com.sxhalo.PullCoal.ui.recyclerviewmode.TestRefreshHeader;
import com.sxhalo.PullCoal.ui.smoothlistview.header.HeaderAdViewView;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.srecyclerview.SRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.AREA_CODE;
import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

/**
 * 煤炭列表
 * Created by amoldZhang on 2018/7/23.
 */
public class BuyCoalActivity extends BaseActivity {

    @Bind(R.id.mRecyclerView)
    SRecyclerView mRecyclerView;
    @Bind(R.id.sticky_header_view)
    LinearLayout stickHeaderView;

    @Bind(R.id.tv_order)
    TextView tvOrder;
    @Bind(R.id.tv_msg_type)
    TextView tvMsgType;

    @Bind(R.id.et_search)
    EditText etSearch;

    @Bind(R.id.mode_type_layout)
    LinearLayout modeTypeLayout;
    @Bind(R.id.mode_type_anchor)
    View modeTypeAnchor;
    @Bind(R.id.tv_map_type)
    TextView tvMapType;
    @Bind(R.id.arrow)
    ImageView arrowImageView;
    @Bind(R.id.iv_map_type)
    ImageView ivMapType;

    @Bind(R.id.ll_contrast)
    LayoutContrastView llContrast;


    //视图类型  0:信息部煤炭 1:煤炭列表 2:矿口煤炭 3:图片
    private int MODE_TYPE = 0 ;
    TextView TV1, TV2;

    CommonAdapter<ListCoalEntity> mAdapter;
    private List<ListCoalEntity> myListData = new ArrayList<ListCoalEntity>();

    // 0全部信息 1免费信息 2收费信息
    private String freeInfor = "0";
    //价格优先 0:否1:是
    private String firstPrice = "0";
    private String orderType = "0";//0：发布时间排序 1：综合排序 2：发热量排序
    private String regionCode = "0";  //地区筛选

    private EmptyWrapper mEmptyWrapper;

    private List<AdvertisementEntity> adList = new ArrayList<>();  //广告视图数据
    private List<Coal> gridList = new ArrayList<>(); //精品煤炭数据

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition = 1;
    //筛选设置
    private StickySuspensionGroupView stickySuspensionGroupView;
    private String type = "0";  //当前哪一个筛选条目
    private List<FilterEntity> filterEntityList = new ArrayList<>(); //煤种筛选数据
    private String categoryId = "-1";  //当前煤种  默认全部
    private String calorificValue = "-1";  //当前发热量  默认不限
    private int age1 = 0 ,age2 = 0;  //选择的哪一个
    private int currentPage = 1; //当前第几页
    private Map<String, FilterEntity> map = new HashMap<String, FilterEntity>();
    private View footer;
    private AdHeaderView adHeaderView; // 广告数据布局
    private GridHeaderView<Coal> gridHeaderView; //煤炭推荐布局
    private StickySuspensionView stickySuspensionView; //筛选条件布局
    //当前条目索引
    private int stickPosition = 4; //当前悬浮框显示位置 默认为第四个
    private int itemHeight;  //获取当前显示条目的高度
    private MyReceiver myReceiver;
    private PreferenceHeaderView<ListCoalEntity> preferenceHeaderView;
    private List<ListCoalEntity> browseDataList = new ArrayList<>();   //煤炭记录

    //待支付的煤炭
    private ListCoalEntity coalEntity = new ListCoalEntity();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_buy_coal);
    }

    @Override
    protected void initTitle() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                setViewUp(stickPosition);
                getDataType(true);
            }
        });
        setModelView();
    }

    @Override
    protected void getData() {
        showProgressDialog("数据加载中，请稍后...");
        registerMyReceiver();
        getInItData(false);
    }

    private void getInItData(final boolean flage) {
        new DataUtils(this).getCoalGoodsTop(new DataUtils.DataBack<ListCoalEntity>() {

            @Override
            public void getData(ListCoalEntity listCoal) {

                adList = listCoal.getAdvertisementEntityList();
                gridList = listCoal.getCoal070004();
                browseDataList = listCoal.getBrowseDataList();
                if (mAdapter == null){
                    initView();
                    getDataType(false);
                }else{
                    initRecyclerViewHead();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void getError(Throwable e) {

            }
        },flage);
    }

    private void getDataType(final boolean flage) {
        try {
            LinkedHashMap<String, String> params = getParams();

            params.put("currentPage", currentPage + "");
            if (1 == MODE_TYPE || 3 == MODE_TYPE){
                params.put("viewType",  0+ "");
            }else if(0 == MODE_TYPE){
                params.put("viewType",  1+ "");
            }else{
                params.put("viewType", MODE_TYPE + "");
            }

            if (1 == MODE_TYPE){
                params.put("pageSize", "20");
            }else if (3 == MODE_TYPE){
                params.put("pageSize", "10");
            }else{
                params.put("pageSize", "5");
            }
            new DataUtils(this, params).getCoalGoodsList(new DataUtils.DataBack<ListCoalEntity>() {

                @Override
                public void getData(ListCoalEntity coalEntity) {
                    try {
                        if (mAdapter == null){
                            setRecyclerViewAdapter();
                        }
                        if (currentPage == 1){
                            refreshData(coalEntity);
                        }else{
                            loadData(coalEntity);
                        }

                        if (coalEntity.getCount() <= myListData.size()){
                            mRecyclerView.loadNoMoreData();
                        }else{
                            mRecyclerView.loadingComplete();
                        }

                        if (footer != null){
                            mRecyclerView.removeFooter(footer);
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    // 刷新操作
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        int placeholders = 5;
                        switch (MODE_TYPE){
                            case 0:
                                placeholders = 4;
                                break;
                            case 1:
                                placeholders = 5;
                                break;
                            case 2:
                                placeholders = 4;
                                break;
                            case 3:
                                placeholders = 4;
                                break;

                        }
                        footer = LayoutInflater.from(mContext).inflate(R.layout.layout_no_data, mRecyclerView, false);
                        if (myListData.size() == 0){
                            footer.findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
                            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                            footer.setLayoutParams(layoutParams);
                        }else if (myListData.size() <= placeholders){
                            footer.findViewById(R.id.layout_no_data).setVisibility(View.GONE);
                            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                            layoutParams.height = BaseUtils.dip2px(mContext,(itemHeight * (placeholders - myListData.size())) - itemHeight * 1/2);
                            if (layoutParams.height <= 0){
                                layoutParams.height = 0;
                            }
                            footer.setLayoutParams(layoutParams);
                        }else{
                            footer.findViewById(R.id.layout_no_data).setVisibility(View.GONE);
                            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                            layoutParams.height = 0;
                            footer.setLayoutParams(layoutParams);
                        }
                        mRecyclerView.addFooter(footer);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新操作
                                mAdapter.notifyDataSetChanged();
                            }
                        });

                        dismisProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },flage);
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }


    private void setModelView(){
        String userId = SharedTools.getStringValue(this,"userId","-1");
        if (!"-1".equals(userId)){
            MODE_TYPE = SharedTools.getIntValue(this,userId + "MODE_TYPE",MODE_TYPE);
        }
        int imageModel = 0;
        String textModel = "信息部";
        if (0 == MODE_TYPE){
            imageModel = R.mipmap.icon_buy_coal_model;
            textModel = "信息部";
        }else if (1 == MODE_TYPE){
            imageModel = R.mipmap.icon_buy_coal_list;
            textModel = "列表";
        }else if(2 == MODE_TYPE){
            imageModel = R.mipmap.icon_buy_coal_model;
            textModel = "矿口";
        }else if (3 == MODE_TYPE){
            imageModel = R.mipmap.icon_buy_coal_image;
            textModel = "图片";
        }
        tvMapType.setText(textModel);
        ivMapType.setImageResource(imageModel);
    }

    /**
     * 获取公共的筛选条件
     * @return
     */
    private LinkedHashMap<String, String> getParams(){
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        //搜索条件
        if (!StringUtils.isEmpty(etSearch.getText().toString().trim())) {
            currentPage = 1;
            params.put("searchValue", etSearch.getText().toString().trim());
        }

        if (map.size() > 0) {
            for (Map.Entry<String, FilterEntity> entry : map.entrySet()) {
                if (entry.getKey().equals("全硫分") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                    //全硫分
                    params.put("totalSulfur", entry.getValue().dictCode);
                }
                if (entry.getKey().equals("灰分") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                    //灰分
                    params.put("ashValue", entry.getValue().dictCode);
                }
                if (entry.getKey().equals("工艺") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                    //加工工艺
                    params.put("technology", entry.getValue().dictCode);
                }
            }
        }
        if (!regionCode.equals("0")) {
            params.put("regionCode", regionCode);
        }
        //是否价格优先
        params.put("firstPrice", firstPrice);
        //是否免费
        params.put("freeInfor", freeInfor);
        //排序方式
        params.put("orderType", orderType);

        //煤种
        if (!"-1".equals(categoryId) && filterEntityList.size() != 0){
            params.put("categoryId", categoryId);
        }
        //发热量
        if (!"-1".equals(calorificValue) && filterEntityList.size() != 0) {
            //发热量
            params.put("calorificValue", calorificValue);
        }

        return params;
    }

    /**
     * 布局初始化
     */
    private void initView() {
        //初始化煤种筛选条件
        crectPOPwin();
        //初始化排序筛选条件
        createOrderPopupWindow();
        //初始化RecyclerView
        initRecyclerView();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        final RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(recyclerView, mToPosition);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //在此做处理
                try {
                    if (null != manager) {
                        //根据索引来获取对应的itemView
                        View firstVisiableChildView = manager.findViewByPosition(stickPosition);
                        if (firstVisiableChildView != null){
                            //获取当前显示条目的高度
                            itemHeight = firstVisiableChildView.getHeight();
                            //获取当前Recyclerview 偏移量
                            int flag = (stickPosition) * itemHeight - firstVisiableChildView.getTop();
                            if (flag >= stickPosition * itemHeight) {
                                //做显示布局操作
                                stickHeaderView.setVisibility(View.VISIBLE);
                            } else {
                                //做隐藏布局操作
                                stickHeaderView.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //如果设置了加载监听，就是需要刷新加载功能，如果没有设置加载监听，那么就没有下拉与底部加载
        mRecyclerView.setLoadListener(new SRecyclerView.LoadListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage = 1;
                        getDataType(false);
                    }
                }, 1500);
            }

            @Override
            public void loading() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage ++ ;
                        getDataType(false);
                    }
                }, 1500);
            }
        });

        //可以手动设置一个刷新头部，应该在setAdapter方法之前调用，适用于某个列表需要特殊刷新头的场景
        //SRecyclerView的刷新头部和加载尾部的设置有两种种方法：
        // 代码设置，全局配置。如果两种方法都没有设置，则使用自带的默认刷新头和加载尾
        mRecyclerView.setRefreshHeader(new TestRefreshHeader(this));
        mRecyclerView.setLoadingFooter(new TestLoadFooter(this));

        //这里的适配器使用的一个简易的SRV适配器：BaseSRVAdapter，可以很大程度上减少适配器的代码量，
        // 这个适配器同样也可以用于普通的RecyclerView，当然这里也可以用原生的适配器
        setRecyclerViewAdapter();

        //SRV的代码刷新，应该在setAdapter方法之后调用，true表示有刷新动画，false无动画
//        mRecyclerView.startRefresh(true);

        mRecyclerView.setItemClickListener(new SRecyclerView.ItemClickListener() {
            @Override
            public void click(View v, int position) {
                try {
                    if (1 == MODE_TYPE || 3 == MODE_TYPE){
                        Coal coal = mAdapter.getDatas().get(position).getCoal();
                        coalEntity = new ListCoalEntity();
                        coalEntity.setCoal(coal);
                        coalEntity.setPos(position);
                        onClockItem();
                    }
                } catch (Exception e) {
                    Log.i("煤炭列表点击", e.toString());
                }
            }
        });

        llContrast.initView(new LayoutContrastView.CoalBack(){
            @Override
            public void setLayout(boolean flage){
                if (flage) {
                    llContrast.setVisibility(View.VISIBLE);
                }else {
                    llContrast.setVisibility(View.GONE);
                }
                initRecyclerViewHead();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setRecyclerViewAdapter(){
        mAdapter = new CommonAdapter<ListCoalEntity>(this, R.layout.layout_item_coal_buy, myListData){
            @Override
            protected void convert(ViewHolder holder,final ListCoalEntity listCoalEntity,final int position){
                if (MODE_TYPE == 1){
                    holder.getView(R.id.layout_mode_3).setVisibility(View.GONE);
                    holder.getView(R.id.layout_mode_2).setVisibility(View.VISIBLE);
                    holder.getView(R.id.layout_mode_1).setVisibility(View.GONE);
                    setModeCoalListInitItem(holder,listCoalEntity.getCoal(),position);
                }else if (MODE_TYPE == 3){
                    holder.getView(R.id.layout_mode_3).setVisibility(View.VISIBLE);
                    holder.getView(R.id.layout_mode_2).setVisibility(View.GONE);
                    holder.getView(R.id.layout_mode_1).setVisibility(View.GONE);
                    setModeCoalImageInitItem(holder,listCoalEntity.getCoal(),position);
                }else{
                    holder.getView(R.id.layout_mode_3).setVisibility(View.GONE);
                    holder.getView(R.id.layout_mode_2).setVisibility(View.GONE);
                    holder.getView(R.id.layout_mode_1).setVisibility(View.VISIBLE);
                    setModeInitItem(holder,listCoalEntity,position);
                    if (position-stickPosition-1 <= 0 || myListData.size()-1 == position + stickPosition){
                        holder.getView(R.id.item_line).setVisibility(View.GONE);
                    }else{
                        holder.getView(R.id.item_line).setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        //可以设置一个EmptyView
        //mRecyclerView.setEmptyView(new View(this));
        initEmptyView();

        //初始化RecyclerView的头部 ,可以添加一个或多个头部
        initRecyclerViewHead();

        //可以添加一个或多个尾部
//        if (footer == null){
//            footer = LayoutInflater.from(this).inflate(R.layout.layout_no_data, mRecyclerView, false);
//            mRecyclerView.addFooter(footer);
//        }
        mRecyclerView.setAdapter(mAdapter);
    }


    /**
     * 初始化煤炭列表模式
     * @param holder
     * @param coals
     * @param position
     */
    private void setModeCoalListInitItem(ViewHolder holder,final Coal coals,final int position) {
        if (position == stickPosition + 1){
            holder.getView(R.id.layout_title).setVisibility(View.VISIBLE);
            holder.getView(R.id.layout_title).setOnClickListener(null);
        }else {
            holder.getView(R.id.layout_title).setVisibility(View.GONE);
        }

        if (0 == position%2){
            holder.getView(R.id.layout_coal_list_bg).setBackgroundResource(R.color.coal_list_bg_0);
        }else{
            holder.getView(R.id.layout_coal_list_bg).setBackgroundResource(R.color.coal_list_bg_1);
        }
        holder.setText(R.id.coal_item_name_layout2, coals.getCoalName());
        holder.setText(R.id.tv_com_calorificvalue_layout2, coals.getCalorificValue());
        holder.setText(R.id.tv_com_sulfur_layout2, coals.getTotalSulfur());
        holder.setText(R.id.mine_mouth_name_layout2, coals.getMineMouthName());
        holder.setText(R.id.coal_time_layoyt2, coals.getDifferMinute());

        String payName = "";
        //当资讯信息是免费的
        if (coals.getConsultingFee().equals("0")) {
            holder.setText(R.id.coal_price_layout2, "¥"+ coals.getOneQuote());
            holder.getView(R.id.coal_price_layout2).setVisibility(View.VISIBLE);
            holder.getView(R.id.coal_price_layout2_1).setVisibility(View.GONE);
            holder.getView(R.id.if_fell_layout2).setVisibility(View.INVISIBLE);
        } else {
            //当前登录用户和发布人同属于一个信息部
            if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())){
                payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100);
                holder.setText(R.id.coal_price_layout2, "¥"+coals.getOneQuote());
                holder.getView(R.id.coal_price_layout2).setVisibility(View.VISIBLE);
                holder.getView(R.id.coal_price_layout2_1).setVisibility(View.GONE);
                holder.getView(R.id.if_fell_layout2).setVisibility(View.VISIBLE);
            }else if (coals.getIsPay().equals("1") && coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {  //0未支付，1已支付
                //当资讯信息已经付过费
//                payName = "(已支付)收费信息";
                payName = " ¥" + (Double.valueOf(coals.getConsultingFee()) / 100);
                holder.getView(R.id.if_fell_layout2).setVisibility(View.VISIBLE);
                holder.setText(R.id.coal_price_layout2, "¥"+coals.getOneQuote());
                holder.getView(R.id.coal_price_layout2).setVisibility(View.VISIBLE);
                holder.getView(R.id.coal_price_layout2_1).setVisibility(View.GONE);
            } else {
                //当资讯信息尚未付费
                payName = "" + "¥" + (Double.valueOf(coals.getConsultingFee()) / 100);
                holder.getView(R.id.if_fell_layout2).setVisibility(View.VISIBLE);
                holder.setText(R.id.coal_price_layout2_1, "付费可见");
                holder.getView(R.id.coal_price_layout2).setVisibility(View.GONE);
                holder.getView(R.id.coal_price_layout2_1).setVisibility(View.VISIBLE);
            }
        }
        holder.setText(R.id.if_fell_layout2_text, payName.replace(".0",""));

    }


    /**
     * 初始化煤炭图片列表模式
     * @param helper
     * @param coals
     * @param position
     */
    private void setModeCoalImageInitItem(ViewHolder helper,final Coal coals,final int position) {
        try {
            if (Utils.isImagesTrue(coals.getImageUrl()) == 200 ){
                getImageManager().loadCoalTypeUrlImage(coals.getImageUrl(),coals.getCategoryImage(),(ImageView)helper.getView(R.id.iv_com_head));
            }else{
                getImageManager().loadCoalTypeUrlImage(coals.getCategoryImage(),(ImageView)helper.getView(R.id.iv_com_head));
            }
            helper.setText(R.id.coal_item_name, coals.getCoalName());
            helper.setText(R.id.coal_price, coals.getOneQuote());
            helper.setText(R.id.tv_com_calorificvalue, coals.getCalorificValue() + "kCal/kg");
            helper.setText(R.id.tv_com_storagerate, coals.getMineMouthName());
            helper.setText(R.id.information_name, coals.getCompanyName());
            if (StringUtils.isEmpty(coals.getCoalReportPicUrl())) {
                helper.getView(R.id.iv_test).setVisibility(View.GONE);
            } else {
                helper.getView(R.id.iv_test).setVisibility(View.VISIBLE);
            }
            helper.setText(R.id.push_time, coals.getDifferMinute());
            String payName;
            int payColor;
            //当资讯信息是免费的
            if (coals.getConsultingFee().equals("0")) {
                payName = "免费信息";
                payColor = R.color.blue;
                helper.getView(R.id.coal_price).setVisibility(View.VISIBLE);
                helper.getView(R.id.coal_price_image).setVisibility(View.VISIBLE);
                helper.getView(R.id.free_type).setVisibility(View.GONE);
                helper.getView(R.id.free_type_image).setVisibility(View.GONE);
            } else {
                ImageView typeImage = (ImageView)helper.getView(R.id.free_type_image);
                TextView typeText = (TextView)helper.getView(R.id.free_type);
                typeImage.setVisibility(View.VISIBLE);
                typeText.setVisibility(View.VISIBLE);

                if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())){
                    payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    helper.getView(R.id.coal_price).setVisibility(View.GONE);
                    helper.getView(R.id.coal_price_image).setVisibility(View.GONE);
                    typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                    typeText.setText("发布人："+ coals.getPublishUser());
                }else
                    //0未支付，1已支付
                    if (coals.getIsPay().equals("1") && coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {
                        //当资讯信息已经付过费
                        payName = "(已支付)收费信息";
                        payColor = R.color.actionsheet_red;
                        helper.getView(R.id.coal_price).setVisibility(View.VISIBLE);
                        helper.getView(R.id.coal_price_image).setVisibility(View.VISIBLE);
                        typeImage.setImageResource(R.mipmap.icon_buyer_app);
                        typeText.setText(coals.getLicenseMinute());
                    } else {
                        //当资讯信息尚未付费
                        payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + "收费信息";
                        payColor = R.color.actionsheet_red;
                        helper.getView(R.id.coal_price).setVisibility(View.GONE);
                        helper.getView(R.id.coal_price_image).setVisibility(View.GONE);
                        typeImage.setImageResource(R.mipmap.icon_about_app);

                        if (coals.getLicenseMinute().contains("已失效")){
                            typeText.setText("支付后可查看更多");
                        }else{
                            typeText.setText(coals.getLicenseMinute().contains("已失效")?"支付后可查看更多":coals.getLicenseMinute());
                        }
                    }
            }
            helper.setText(R.id.information_free, payName.replace(".0",""));
            ((TextView) helper.getView(R.id.information_free)).setTextColor(getResources().getColor(payColor));
            helper.setText(R.id.coal_price, coals.getOneQuote());


            TextView contrastText = (TextView)helper.getView(R.id.contrast_text);
            contrastText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                    //先判断是否登录
                    if (userId.equals("-1")) {
                        //未登录 先去登录
                        UIHelper.jumpActLogin(mContext,false);
                    } else {
                        if (coals.getConsultingFee().equals("0") ||
                                SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())) {
                            //免费信息或者已支付 直接对比
                            llContrast.setAddContrastView(coals);
                        } else {
                            //已登录 判断是否支付
                            if (coals.getIsPay().equals("1") && coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {
                                //已支付 直接查看
                                //免费信息或者已支付 直接对比
                                llContrast.setAddContrastView(coals);
                            } else {
                                //未支付 弹框提示
                                showPayDialog(coals, "0");

                                myReceiver = new MyReceiver();
                                IntentFilter filter = new IntentFilter();
                                filter.addAction(RECEIVED_ACTION);
                                registerReceiver(myReceiver, filter);
                            }
                        }
                    }
                }
            });

            if (llContrast.getifContrast(coals)){
                contrastText.setBackgroundResource(R.drawable.button_shape_pressed);
                contrastText.setTextColor(getResources().getColor(R.color.white));
            }else{
                contrastText.setBackgroundResource(R.drawable.bull_send_car);
                contrastText.setTextColor(getResources().getColor(R.color.actionsheet_blue));
            }
        } catch (Exception e) {
            GHLog.e("煤炭赋值", e.toString());
        }
    }

    /**
     * 初始化 版块分类煤炭列表模式
     * @param holder
     * @param listCoalEntity
     * @param position
     */
    private void setModeInitItem(final ViewHolder holder, final ListCoalEntity listCoalEntity, final int position) {
        if (MODE_TYPE == 0){
            holder.setText(R.id.mode_name, listCoalEntity.getCompanyName());
            holder.setText(R.id.mode_count, "(" + (StringUtils.isEmpty(listCoalEntity.getCompaniesNum())?"0" : listCoalEntity.getCompaniesNum().replace(".0",""))  + "个代销矿口)");
        }else{
            holder.setText(R.id.mode_name, listCoalEntity.getMineMouthName());
            holder.setText(R.id.mode_count, "(" + (StringUtils.isEmpty(listCoalEntity.getSalesTotal())?"0" : listCoalEntity.getSalesTotal().replace(".0","")) + "家代理信息部)");
        }

        holder.getView(R.id.more_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (MODE_TYPE == 0){
                    intent.setClass(mContext, DetaileInformationDepartmentActivity.class);
                    intent.putExtra("InfoDepartId", listCoalEntity.getCoalSalesId());
                }else{
                    intent.setClass(mContext, DetaileMineActivity.class);
                    intent.putExtra("InfoDepartId", listCoalEntity.getMineMouthId());
                }
                startActivity(intent);
            }
        });
        holder.setText(R.id.coal_person, "联系人："+ listCoalEntity.getContactPerson());
        holder.setText(R.id.coal_tel, listCoalEntity.getContactPhone());
        holder.getView(R.id.coal_tel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tel", listCoalEntity.getContactPhone());
                map.put("callType", Constant.CALE_TYPE_COAL);
                if (MODE_TYPE == 0){
                    map.put("targetId", listCoalEntity.getCoalSalesId());
                }else{
                    map.put("targetId", listCoalEntity.getMineMouthId());
                }
                UIHelper.showCollTel(mContext, map, true);
            }
        });
        final CustomListView listView = (CustomListView)holder.getView(R.id.mode_list);
        setChardView(holder,listView,listCoalEntity);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                coalEntity = new ListCoalEntity();
                coalEntity.setPos(position);
                coalEntity.setItemPos(pos);
                coalEntity.setCoal(listCoalEntity.getCoal070001().get(pos));
                coalEntity.setCoal070001(listCoalEntity.getCoal070001());
                onClockItem();
            }
        });

        // 展开操作布局
        holder.getView(R.id.layout_unfold).setOnClickListener(new View.OnClickListener() {
            int page = 1;
            @Override
            public void onClick(View v) {
                if (listCoalEntity.getCoal070001().size() <= 5){
                    page = 1;
                }
                getModelData(holder,listView,position,listCoalEntity,page,listCoalEntity.getCoal070001().size());
                page ++;
            }
        });

        // 收起操作布局
        holder.getView(R.id.layout_hidden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listCoalEntity.getCoal070001().size() >= 6) {//判断list长度
                    List<Coal> newList = listCoalEntity.getCoal070001().subList(0, 5);//取前五条数据
                    listCoalEntity.setCoal070001(newList);
                    setChardView(holder,listView,listCoalEntity);
                    mAdapter.notifyItemChanged(position,listCoalEntity);
                    smoothMoveToPosition(mRecyclerView, position);
                }
            }
        });
    }

    private void setChardView(ViewHolder holder,CustomListView listView,ListCoalEntity listCoalEntity){
        final QuickAdapter<Coal> adapter = new QuickAdapter<Coal>(this,R.layout.layout_item_coal_buy,listCoalEntity.getCoal070001()) {
            @Override
            protected void convert(BaseAdapterHelper holder, Coal coals, int position) {
                holder.getView(R.id.layout_mode_1).setVisibility(View.GONE);
                holder.getView(R.id.layout_mode_3).setVisibility(View.GONE);
                holder.getView(R.id.layout_mode_2).setVisibility(View.VISIBLE);

                if (position == 0){
                    holder.getView(R.id.layout_title).setVisibility(View.VISIBLE);
                    holder.getView(R.id.layout_title).setOnClickListener(null);
                }else {
                    holder.getView(R.id.layout_title).setVisibility(View.GONE);
                }

                if (0 == position%2){
                    holder.getView(R.id.layout_coal_list_bg).setBackgroundResource(R.color.coal_list_bg_0);
                }else{
                    holder.getView(R.id.layout_coal_list_bg).setBackgroundResource(R.color.coal_list_bg_1);
                }
                holder.setText(R.id.coal_item_name_layout2, coals.getCoalName());
                holder.setText(R.id.tv_com_calorificvalue_layout2, coals.getCalorificValue());
                holder.setText(R.id.tv_com_sulfur_layout2, coals.getTotalSulfur());
                holder.setText(R.id.mine_mouth_name_layout2, coals.getMineMouthName());
                holder.setText(R.id.coal_time_layoyt2, coals.getDifferMinute());

                String payName = "";
                //当资讯信息是免费的
                if (coals.getConsultingFee().equals("0")) {
                    holder.setText(R.id.coal_price_layout2, "¥"+ coals.getOneQuote());
                    holder.getView(R.id.if_fell_layout2).setVisibility(View.INVISIBLE);

                    holder.getView(R.id.coal_price_layout2).setVisibility(View.VISIBLE);
                    holder.getView(R.id.coal_price_layout2_1).setVisibility(View.GONE);
                } else {
                    //当前登录用户和发布人同属于一个信息部
                    if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())){
                        payName = "" + " ¥" + (Double.valueOf(coals.getConsultingFee()) / 100);
                        holder.setText(R.id.coal_price_layout2, "¥"+coals.getOneQuote());
                        holder.getView(R.id.if_fell_layout2).setVisibility(View.VISIBLE);

                        holder.getView(R.id.coal_price_layout2).setVisibility(View.VISIBLE);
                        holder.getView(R.id.coal_price_layout2_1).setVisibility(View.GONE);
                    }else if (coals.getIsPay().equals("1") && coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {  //0未支付，1已支付
                        //当资讯信息已经付过费
//                        payName = "(已支付)收费信息";
                        payName = " ¥" + (Double.valueOf(coals.getConsultingFee()) / 100);
                        holder.getView(R.id.if_fell_layout2).setVisibility(View.VISIBLE);
                        holder.setText(R.id.coal_price_layout2, "¥"+coals.getOneQuote());

                        holder.getView(R.id.coal_price_layout2).setVisibility(View.VISIBLE);
                        holder.getView(R.id.coal_price_layout2_1).setVisibility(View.GONE);
                    } else {
                        //当资讯信息尚未付费
                        payName = "" + " ¥" + (Double.valueOf(coals.getConsultingFee()) / 100);
                        holder.getView(R.id.if_fell_layout2).setVisibility(View.VISIBLE);
                        holder.setText(R.id.coal_price_layout2_1, "付费可见");

                        holder.getView(R.id.coal_price_layout2).setVisibility(View.GONE);
                        holder.getView(R.id.coal_price_layout2_1).setVisibility(View.VISIBLE);
                    }
                }
                holder.setText(R.id.if_fell_layout2_text, payName.replace(".0",""));
                if (MODE_TYPE == 0){
                    holder.setText(R.id.mine_mouth_name_layout2, coals.getMineMouthName());
                }else{
                    holder.setText(R.id.mine_mouth_name_layout2, coals.getCompanyName());
                }
            }
        };
        listView.setAdapter(adapter);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 刷新操作
                adapter.notifyDataSetChanged();
            }
        });

//        int coalCount;
//        if (MODE_TYPE == 0){
//            coalCount = Integer.valueOf(StringUtils.isEmpty(listCoalEntity.getCoalGoodsNum())?"0":listCoalEntity.getCoalGoodsNum().replace(".0",""));
//        }else{
//            coalCount = Integer.valueOf(StringUtils.isEmpty(listCoalEntity.getGoodsTotal())?"0":listCoalEntity.getGoodsTotal().replace(".0",""));
//        }

//        //当服务器总条数等于当前界面展示总条数或当前界面数据小于5条时，展开按钮隐藏
//        if (coalCount == listCoalEntity.getCoal070001().size() || listCoalEntity.getCoal070001().size() < 5){
//            holder.getView(R.id.layout_unfold).setVisibility(View.INVISIBLE);
//        }else{ //
//            holder.getView(R.id.layout_unfold).setVisibility(View.VISIBLE);
//        }

//        //当服务器总条数大于等于当前界面总条数并且当前界面总条数不等于5条 或者当前界面显示条数大于5（5是默认显示条数）条时
//        if ((coalCount >= listCoalEntity.getCoal070001().size() && listCoalEntity.getCoal070001().size() != 5) || listCoalEntity.getCoal070001().size() > 5){
//            holder.getView(R.id.layout_hidden).setVisibility(View.VISIBLE);
//        }else{
//            holder.getView(R.id.layout_hidden).setVisibility(View.INVISIBLE);
//        }

//        // 当服务器的总条数小于等于5 或者当前界面展示小于5时，两个按钮都隐藏
//        if (coalCount <= 5 || listCoalEntity.getCoal070001().size()< 5){
//            holder.getView(R.id.layout_hidden).setVisibility(View.INVISIBLE);
//            holder.getView(R.id.layout_unfold).setVisibility(View.INVISIBLE);
//        }
        int coalCount = listCoalEntity.getCoal070001().size();
        if (coalCount < 5){
            holder.getView(R.id.layout_hidden).setVisibility(View.GONE);
            holder.getView(R.id.layout_unfold).setVisibility(View.GONE);
        }else{
            holder.getView(R.id.layout_hidden).setVisibility(View.GONE);
            holder.getView(R.id.layout_unfold).setVisibility(View.VISIBLE);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 刷新操作
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     *  点击展开时，加载更多数据
     * @param mListPosition 当前主布局的条目
     * @param listCoalEntity 当前主布局的数据
     * @param currentPage    需要取的第几页
     */
    private void getModelData(final ViewHolder holder, final CustomListView listView, final int mListPosition, final ListCoalEntity listCoalEntity, final int currentPage ,final int count){
        try {
            LinkedHashMap<String, String> params = getParams();

            params.put("currentPage", currentPage + "");
            if(0 == MODE_TYPE){
                params.put("viewType",  1 + "");
                params.put("coalSalesId", listCoalEntity.getCoalSalesId());
            }else if(2 == MODE_TYPE){
                params.put("viewType", 2 + "");
                params.put("mineMouthId", listCoalEntity.getMineMouthId());
            }
            params.put("pageSize", "10");
            new DataUtils(this, params).getCoalGoodsModelList(new DataUtils.DataBack<ListCoalEntity>() {

                @Override
                public void getData(ListCoalEntity coalEntity) {
                    try {
                        List<Coal> entityList = coalEntity.getCoal070001();
                        if (entityList.size() != 0){
                            listCoalEntity.getCoal070001().addAll(entityList);

                            setChardView(holder,listView,listCoalEntity);
                            mAdapter.notifyItemChanged(mListPosition,listCoalEntity);
                        }else{
                            if (count != 5){
                                holder.getView(R.id.layout_hidden).setVisibility(View.VISIBLE);
                            }
                            holder.getView(R.id.layout_unfold).setVisibility(View.GONE);
                            if (0 == MODE_TYPE){
                                displayToast("当前信息部下煤炭已全部展示");
                            }else{
                                displayToast("当前矿口下煤炭已全部展示");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },true);
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

//    private void showPayDialog(final Coal coals, String s, final String type) {
//        LayoutInflater inflater1 = mContext.getLayoutInflater();
//        View layout = inflater1.inflate(R.layout.dialog_updata, null);
//        ((TextView) layout.findViewById(R.id.updata_message)).setText(s);
//        new RLAlertDialog(mContext, "系统提示", layout, "取消",
//                "去支付", new RLAlertDialog.Listener() {
//            @Override
//            public void onLeftClick() {
//            }
//
//            @Override
//            public void onRightClick() {
//                Intent intent = new Intent(mContext, PaymentInformationActivity.class);
//                intent.putExtra("type", type);
//                intent.putExtra("Entity",coals);
//                startActivity(intent);
//
//                myReceiver = new MyReceiver();
//                IntentFilter filter = new IntentFilter();
//                filter.addAction(RECEIVED_ACTION);
//                registerReceiver(myReceiver, filter);
//            }
//        }).show();
//    }

    /**
     * 初始化 RecyclerView 的头部
     */
    private void initRecyclerViewHead() {
        try {
            if (adHeaderView != null){
                mRecyclerView.removeHeader(adHeaderView.getView());
                mAdapter.notifyDataSetChanged();
                adHeaderView = null;
            }

            if (adList.size() == 0){
                stickPosition = 3;
            }else{
                stickPosition = 4;
                // 设置广告数据
                View adView = initAdHeaderView();
                mRecyclerView.addHeader(adView);
            }


            if (gridHeaderView != null){
                mRecyclerView.removeHeader(gridHeaderView.getView());
                mAdapter.notifyDataSetChanged();
                gridHeaderView.setViewLine(false);
                gridHeaderView = null;
            }

            if (gridList.size() != 0){
                //设置网格布局
                View gridView =  initGridHeaderView();
                mRecyclerView.addHeader(gridView);
                if (stickPosition == 3){
                    gridHeaderView.setViewLine(false);
                }else{
                    gridHeaderView.setViewLine(true);
                }
            }else {
                stickPosition =  stickPosition - 1;
                if (gridHeaderView != null){
                    mRecyclerView.removeHeader(gridHeaderView.getView());
                    mAdapter.notifyDataSetChanged();
                }
            }

            if (preferenceHeaderView != null){
                mRecyclerView.removeHeader(preferenceHeaderView.getView());
                mAdapter.notifyDataSetChanged();
                preferenceHeaderView = null;
            }

            if (browseDataList.size() != 0){
                View listView =  initPreferenceHeaderView(browseDataList);
                mRecyclerView.addHeader(listView);
            }else{
                stickPosition =  stickPosition - 1;
                if (preferenceHeaderView != null){
                    mRecyclerView.removeHeader(preferenceHeaderView.getView());
                    mAdapter.notifyDataSetChanged();
                }
            }

            if (stickySuspensionView != null){
                mRecyclerView.removeHeader(stickySuspensionView.getView());
                mAdapter.notifyDataSetChanged();
            }

            //设置悬浮布局
            View stickySuspension =  initStickySuspensionView();
            if (filterEntityList.size() == 0 ){
                stickySuspensionView.setView("煤炭类型",strings[filterStatus]);
                tvOrder.setText("煤炭类型");
            }else{
                stickySuspensionView.setView(filterEntityList.get(age1).dictValue,strings[filterStatus]);
                tvOrder.setText(filterEntityList.get(age1).dictValue);
            }
            tvMsgType.setText(strings[filterStatus]);
            mRecyclerView.addHeader(stickySuspension);
        } catch (Exception e) {
            Log.i("初始化 RecyclerView 的头部", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 浏览记录布局
     * @param browseDataList
     * @return
     */
    private View initPreferenceHeaderView(final List<ListCoalEntity> browseDataList) {
        preferenceHeaderView = new PreferenceHeaderView<ListCoalEntity>(mContext,mRecyclerView,3,browseDataList,1,false);
        preferenceHeaderView.setOnClickListener(new PreferenceHeaderView.OnClickListener<ListCoalEntity>(){

            @Override
            public void setItemLayout(final ViewHolder holder, final ListCoalEntity listCoalEntity, int position) {
                if (listCoalEntity.getDataTape().equals("coal070007")){
                    holder.setText(R.id.coal_item_title, "浏览");
                }else if (listCoalEntity.getDataTape().equals("coal070005")){
                    holder.setText(R.id.coal_item_title, "收藏");
                }else if (listCoalEntity.getDataTape().equals("coal070006")){
                    holder.setText(R.id.coal_item_title, "预订");
                }

                holder.getView(R.id.right_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BuyCoalActivity.this,BrowseRecordedDataActivity.class);
                        String group;
                        if (((TextView)holder.getView(R.id.coal_item_title)).getText().toString().contains("浏览")){
                            group = "浏览";
                        }else if (((TextView)holder.getView(R.id.coal_item_title)).getText().toString().contains("收藏")){
                            group = "收藏";
                        }else {
                            group = "预订";
                        }
                        intent.putExtra("groupType",group);
                        startActivity(intent);

                    }
                });
                Coal coal = listCoalEntity.getCoal();

                if (Utils.isImagesTrue(coal.getImageUrl()) == 200 ){
                    getImageManager().loadCoalTypeUrlImage(coal.getImageUrl(),coal.getCategoryImage(),(ImageView)holder.getView(R.id.iv_com_head));
                }else{
                    getImageManager().loadCoalTypeUrlImage(coal.getCategoryImage(),(ImageView)holder.getView(R.id.iv_com_head));
                }

                if (StringUtils.isEmpty(coal.getCoalReportPicUrl())) {
                    holder.getView(R.id.iv_test).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.iv_test).setVisibility(View.VISIBLE);
                }
                holder.setText(R.id.tv_name, coal.getCoalName());
                holder.setText(R.id.tv_price, coal.getOneQuote());
                holder.setText(R.id.tv_com_calorificvalue, coal.getCalorificValue() + "kCal/kg");
                holder.setText(R.id.tv_com_storagerate, coal.getMineMouthName());
                holder.setText(R.id.tv_information_name,coal.getCompanyName());
                holder.setText(R.id.tv_updata_time,coal.getDifferMinute());
                String payName = "";
                //当资讯信息是免费的
                if (coal.getConsultingFee().equals("0")) {
                    holder.getView(R.id.tv_price).setVisibility(View.VISIBLE);
                    holder.getView(R.id.tv_price_img).setVisibility(View.VISIBLE);
                    holder.getView(R.id.tv_free_type).setVisibility(View.GONE);
                    holder.getView(R.id.tv_price_type).setVisibility(View.GONE);
                } else {
                    //当前登录用户和发布人同属于一个信息部
                    if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coal.getCoalSalesId())){
                        payName = "付费信息" + "¥" + (Double.valueOf(coal.getConsultingFee()) / 100);
                        holder.getView(R.id.tv_price).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_price_img).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_free_type).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_price_type).setVisibility(View.GONE);
                    }else
                        //0未支付，1已支付
                        if (coal.getIsPay().equals("1") && coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                            //当资讯信息已经付过费
                            payName = "付费信息" + "¥" + (Double.valueOf(coal.getConsultingFee()) / 100) + "，已支付";
                            holder.getView(R.id.tv_price).setVisibility(View.VISIBLE);
                            holder.getView(R.id.tv_price_img).setVisibility(View.VISIBLE);
                            holder.getView(R.id.tv_free_type).setVisibility(View.VISIBLE);
                            holder.getView(R.id.tv_price_type).setVisibility(View.GONE);
                        } else {
                            //当资讯信息尚未付费
                            payName = "付费信息" + "¥" + (Double.valueOf(coal.getConsultingFee()) / 100);
                            holder.getView(R.id.tv_price).setVisibility(View.GONE);
                            holder.getView(R.id.tv_price_img).setVisibility(View.GONE);
                            holder.getView(R.id.tv_free_type).setVisibility(View.VISIBLE);
                            holder.setText(R.id.tv_price_type, "付费可见");
                        }
                }
                holder.setText(R.id.tv_free_type, payName);
            }

            @Override
            public void onListItemClick(View v, int position) {
                try {
                    Coal coal = browseDataList.get(position).getCoal();
                    Intent intent = new Intent();
                    intent.setClass(mContext, DetailsWebActivity.class);
                    intent.putExtra("Coal", coal);
                    intent.putExtra("inforDepartId", "煤炭详情");
                    String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                    //先判断是否登录
                    if (userId.equals("-1")) {
                        //未登录 先去登录
                        UIHelper.jumpActLogin(mContext,false);
                    } else {
                        if (coal.getConsultingFee().equals("0") ||
                                SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coal.getCoalSalesId())) {
                            //免费信息
                            startActivityForResult(intent,Constant.AREA_CODE_COAL_DETAILS);
                        } else {
                            //已登录 判断是否支付
                            if (coal.getIsPay().equals("1") && coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                                //已支付 直接查看
                                startActivityForResult(intent,Constant.AREA_CODE_COAL_DETAILS);
                            } else {
                                //未支付 弹框提示
                                showPayDialog(coal, "0");
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.i("煤炭列表点击", e.toString());
                }
            }
        });
        return preferenceHeaderView.getView();
    }

    /**
     * 设置悬浮布局
     * @return
     */
    private View initStickySuspensionView() {
        stickySuspensionView = new StickySuspensionView(this,mRecyclerView);
        stickySuspensionView.setOnClickListener(new StickySuspensionView.OnClickListener() {
            @Override
            public void onItemClick(TextView TV, int pos) {
                super.onItemClick(TV, pos);
                switch (pos){
                    case 0:
                        setViewUp(stickPosition);
                        showStickySuspensionGroupView();
                        TV1 = TV;
                        break;
                    case 1:
                        setViewUp(stickPosition);
                        showPopwindow();
                        TV2 = TV;
                        break;
                    case 2:
                        setViewUp(stickPosition);
                        showFilterPopupWindow();
                        break;
                }
            }
        });
        return stickySuspensionView.getView();
    }

    /**
     * 初始化网格布局
     * @return
     */
    private View initGridHeaderView() {
        gridHeaderView = new GridHeaderView<Coal>(mContext,mRecyclerView,2,gridList,2,false);
        gridHeaderView.setTitleText("精品煤炭");
        gridHeaderView.setOnClickListener(new GridHeaderView.OnClickListener<Coal>(){

            @Override
            public void setItemLayout(ViewHolder holder,final Coal coals, int position) {

                if (Utils.isImagesTrue(coals.getImageUrl()) == 200 ){
                    getImageManager().loadCoalTypeUrlImage(coals.getImageUrl(),coals.getCategoryImage(),(ImageView) holder.getView(R.id.iv_com_head));
                }else{
                    getImageManager().loadCoalTypeUrlImage(coals.getCategoryImage(),(ImageView)holder.getView(R.id.iv_com_head));
                }

                holder.setText(R.id.coal_item_name, coals.getCoalName());
                holder.setText(R.id.coal_price, coals.getOneQuote());
                holder.setText(R.id.tv_com_calorificvalue, coals.getCalorificValue() + "kCal/kg");

                TextView contrastText = (TextView)holder.getView(R.id.contrast_text);
                contrastText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                        //先判断是否登录
                        if (userId.equals("-1")) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(BuyCoalActivity.this,false);
                        } else {
                            if (coals.getConsultingFee().equals("0") ||
                                    SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())) {
                                //免费信息或者已支付 直接对比
                                llContrast.setAddContrastView(coals);
                            } else {
                                //已登录 判断是否支付
                                if (coals.getIsPay().equals("1") && coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {
                                    //已支付 直接查看
                                    //免费信息或者已支付 直接对比
                                    llContrast.setAddContrastView(coals);
                                    initRecyclerViewHead();
                                } else {
                                    //未支付 弹框提示
                                    showPayDialog(coals, "0");
                                }
                            }
                        }
                        myReceiver = new MyReceiver();
                        IntentFilter filter = new IntentFilter();
                        filter.addAction(RECEIVED_ACTION);
                        registerReceiver(myReceiver, filter);
                    }
                });

                if (llContrast.getifContrast(coals)){
                    contrastText.setBackgroundResource(R.drawable.button_shape_pressed);
                    contrastText.setTextColor(getResources().getColor(R.color.white));
                }else{
                    contrastText.setBackgroundResource(R.drawable.bull_send_car);
                    contrastText.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                }
            }

            @Override
            public void onGridTitleTextClick() {
                Intent intent = new Intent(mContext, CoalRecommendActivity.class);
                intent.putExtra("InfoDepartId",gridList.get(0).getCoalSalesId());
                startActivity(intent);
            }

            @Override
            public void onGridItemClick(View v, int position) {
                try {
                    Coal coal = gridList.get(position);
                    Intent intent = new Intent();
                    intent.setClass(mContext, DetailsWebActivity.class);
                    intent.putExtra("Coal", coal);
                    intent.putExtra("inforDepartId", "煤炭详情");
                    String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                    //先判断是否登录
                    if (userId.equals("-1")) {
                        //未登录 先去登录
                        UIHelper.jumpActLogin(mContext,false);
                    } else {
                        if (coal.getConsultingFee().equals("0") ||
                                SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coal.getCoalSalesId())) {
                            //免费信息
                            startActivityForResult(intent,Constant.AREA_CODE_COAL_DETAILS);
                        } else {
                            //已登录 判断是否支付
                            if (coal.getIsPay().equals("1") && coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                                //已支付 直接查看
                                startActivityForResult(intent,Constant.AREA_CODE_COAL_DETAILS);
                            } else {
                                //未支付 弹框提示
                                showPayDialog(coal, "0");
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.i("煤炭列表点击", e.toString());
                }

            }
        });
        return gridHeaderView.getView();
    }

    /**
     * 初始化 广告布局
     * @return
     */
    private View initAdHeaderView() {
        adHeaderView = new AdHeaderView(mContext,mRecyclerView,adList);
        adHeaderView.setADItemOnClickListener(new HeaderAdViewView.ADOnClickListener() {
            @Override
            public void adItemOnClickListener(View view, int position) {
                try {
                    AdvertisementEntity mEntity = adList.get(position);
                    System.out.println("跳转连接++++"+mEntity.getAdUrl());
                    if(mEntity.getAdUrl().equals("")||mEntity.getAdUrl().equals("null")){
                    }else{
                        GHLog.i("头条点击", position+"");
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra("URL",mEntity.getAdUrl());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    GHLog.e("头条点击", e.toString());
                }
            }
        });
        return adHeaderView.getView();
    }

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * 界面数据刷新
     * @param coalEntity
     */
    private void refreshData(ListCoalEntity coalEntity) {
        mRecyclerView.refreshComplete();
        myListData.clear();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 刷新操作
                mAdapter.notifyDataSetChanged();
            }
        });

        List<ListCoalEntity> entityList = new ArrayList<>();
        if (1 == MODE_TYPE || 3 == MODE_TYPE){
            entityList = coalEntity.getCoalList();
        }else if (0 == MODE_TYPE){
            entityList = coalEntity.getCoal070003();
        }else if (2 == MODE_TYPE){
            entityList = coalEntity.getCoal010001();
        }
        myListData.addAll(entityList);
        mAdapter.notifyDataSetChanged();
    }

    private void loadData(ListCoalEntity coalEntity) {
        try {
            mRecyclerView.loadingComplete();

            List<ListCoalEntity> entityList = new ArrayList<>();
            if (1 == MODE_TYPE || 3 == MODE_TYPE){
                entityList = coalEntity.getCoalList();
            }else if (0 == MODE_TYPE){
                entityList = coalEntity.getCoal070003();
            }else if (2 == MODE_TYPE){
                entityList = coalEntity.getCoal010001();
            }

            if (entityList.size() != 0){
                int index = myListData.size() + 1;
                myListData.addAll(entityList);
                mAdapter.notifyItemInserted(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initEmptyView(){
        if (mEmptyWrapper == null){
            mEmptyWrapper = new EmptyWrapper(mAdapter);
            mEmptyWrapper.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.layout_no_data, mRecyclerView, false));
        }
    }


    /**
     * 煤炭条目点击
     */
    private void onClockItem(){
        if (coalEntity == null)return;
        Coal coal = coalEntity.getCoal();
        Intent intent = new Intent();
        intent.setClass(mContext, DetailsWebActivity.class);
        intent.putExtra("Coal", coal);
        intent.putExtra("inforDepartId", "煤炭详情");
        String userId = SharedTools.getStringValue(mContext, "userId", "-1");
        //先判断是否登录
        if (userId.equals("-1")) {
            //未登录 先去登录
            UIHelper.jumpActLogin(mContext,false);
            coalEntity = null;
        } else {
            if (coal.getConsultingFee().equals("0") ||
                    SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coal.getCoalSalesId())) {
                //免费信息
                startActivityForResult(intent,Constant.AREA_CODE_COAL_DETAILS);
            } else {
                //已登录 判断是否支付
                if (coal.getIsPay().equals("1") && coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                    //已支付 直接查看
                    startActivityForResult(intent,Constant.AREA_CODE_COAL_DETAILS);
                } else {
                    //未支付 弹框提示
                    showPayDialog(coal, "0");
                }
            }
        }
    }

    /**
     * 让指定条目滚动到顶端
     * @param position
     */
    private void setViewUp(int position){
        if (position != -1) {
            smoothMoveToPosition(mRecyclerView,position);
        }else {
            smoothMoveToPosition(mRecyclerView,position+1);
        }
    }

    @OnClick({R.id.title_bar_left,R.id.mode_type_layout,R.id.rl_order, R.id.rl_msg_type, R.id.layout_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.mode_type_layout:
                arrowImageView.animate().setDuration(300).rotation(0).start();
                initPopWindow(modeTypeAnchor);
                break;
            case R.id.rl_order:
                showStickySuspensionGroupView();
                break;
            case R.id.rl_msg_type:
                type = "1";
                showPopwindow();
                break;
            case R.id.layout_filter:
                showFilterPopupWindow();
                break;
        }
    }

    private PopupWindow popupWindow;
    /**
     * 初始化弹框
     * @param showWiew
     */
    private void initPopWindow(final View showWiew){
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
                //设置PopupWindow布局
                .setView(R.layout.purchase_release_popwin_view)
                //设置宽高
                .setWidthAndHeight(showWiew.getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT)
//                //设置动画
//                .setAnimationStyle(R.style.AnimDown)
                //设置背景颜色，取值范围0.0f-1.0f 值越小越暗 1.0f为透明
                .setBackGroundLevel(0.5f)
                //设置PopupWindow里的子View及点击事件
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        setChildView(showWiew,view,getModeData());
                    }
                })
                //设置外部是否可点击 默认是true
                .setOutsideTouchable(true)
                //开始构建
                .create();
//        int height = getModeData().size()* (popupWindow.getHeight() + showWiew.getTop());
        int height = showWiew.getTop();
        int width = showWiew.getWidth();
        //弹出PopupWindow
        popupWindow.showAsDropDown(showWiew,0,height);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                arrowImageView.animate().setDuration(300).rotation(90).start();
                int select = 0;
                for (int i = 0;i<getModeData().size();i ++){
                    if (getModeData().get(i).equals(tvMapType.getText().toString())){
                        select = i;
                        break;
                    }
                }
                if (MODE_TYPE == select){
                    return;
                }else{
                    myListData.clear();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter != null){
                                // 刷新操作
                                mAdapter.notifyDataSetChanged();
                                mAdapter = null;
                            }
                        }
                    });
                    MODE_TYPE = select;

                    // TODO 改变布局列表样式
                    currentPage = 1;
                    getDataType(true);
                    String userId = SharedTools.getStringValue(mContext,"userId","-1");
                    if (!"-1".equals(userId)){
                        SharedTools.putIntValue(mContext,userId + "MODE_TYPE",MODE_TYPE);
                    }
                    setModelView();
                }
            }
        });
    }

    /**
     * 弹框数据展示布局
     * @param showWiew
     * @param view
     * @param data
     */
    private void setChildView(final View showWiew,View view,final ArrayList<String> data) {
        //获得PopupWindow布局里的View
        ListView listView = (ListView)view.findViewById(R.id.listview);
        QuickAdapter mAdapter = new QuickAdapter<String>(this, R.layout.purchase_release_popwin_view_item,data) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item, int pos) {
                try{
                    helper.setText(R.id.text_view,item);
                }catch (Exception e){
                    GHLog.e(getClass().getName(),e.toString());
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                }
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (popupWindow != null){
                    tvMapType.setText(data.get(position) + "");
                    popupWindow.dismiss();
                }
            }
        });
    }


    /**
     * 获取布局模式列表
     * @return
     */
    private ArrayList<String> getModeData(){
        //筛选数据
        ArrayList<String> modeList = new ArrayList<String>();
        modeList.add("信息部");
        modeList.add("列表");
        modeList.add("矿口");
        modeList.add("图片");
        return modeList;
    }


    private FilterPopupWindow filterPopupWindow;
    private void showFilterPopupWindow(){
        if (filterPopupWindow == null) {
            createPopupWindow();
        }
        if (!filterPopupWindow.isShowing()) {
            filterPopupWindow.showAsDropDown(stickHeaderView,0,0);
        } else {
            filterPopupWindow.dismiss();
        }
    }

    private void showStickySuspensionGroupView(){
        try {
            type = "0";
            int pos = 0;
            if (filterEntityList.size() == 0){
                filterEntityList .addAll(getFilterPopupWindowData());
            }else{
                for (int i = 0 ;i<filterEntityList.size(); i++) {
                    if (filterEntityList.get(i).isChecked()){
                        pos = i;
                        break;
                    }
                }
            }
            stickySuspensionGroupView.showPopWindow(stickHeaderView,filterEntityList,pos,type);
            darkenBackground(0.7f);
        } catch (Exception e) {
            GHLog.e("popwindow创建与显示", e.toString());
        }
    }

    /**
     * 煤种筛选
     */
    private void crectPOPwin(){
        stickySuspensionGroupView = new StickySuspensionGroupView(
                mContext,stickHeaderView,(mRecyclerView.getHeight() - BaseUtils.dip2px(this, 46))/2);
        stickySuspensionGroupView.setDismissListener(new StickySuspensionGroupView.DismissListener() {
            @Override
            public void onDismissListener(int aog1,int aog2, String type) {
                super.onDismissListener(aog1,aog2, type);
                darkenBackground(1f);
                if ("0".equals(type)){
                    age1 = aog1;
                    age2 = aog2;
                    if (filterEntityList.size() != 0){
                        categoryId = filterEntityList.get(age1).dictCode;
                        calorificValue = filterEntityList.get(age1).list.get(age2).dictCode;
                        tvOrder.setText(filterEntityList.get(age1).dictValue);
                        if (TV1 != null){
                            TV1.setText(filterEntityList.get(age1).dictValue);
                        }
                    }
                }else{
                }
                currentPage = 1;
                getDataType(true);
            }
        });
    }

    private PopupWindow popupOrderWindow;
    private List<Map<String, String>> stringList = new ArrayList<Map<String, String>>();
    private int filterStatus = 0;//发布时间排序 1综合排序 2发热量排序
    private String[] strings = new String[]{"最新发布", "综合排序", "发热量排序"};
    private boolean needRefresh = false;
    /**
     * 创建排序popupwindow 排序
     */
    private void createOrderPopupWindow() {
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

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null, false);
        // 创建PopupWindow实例,宽度和高度充满
        popupOrderWindow = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupOrderWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupOrderWindow.setOutsideTouchable(true);
        //刷新状态（必须刷新否则无效）
        popupOrderWindow.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupOrderWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
//        popupWindow.setAnimationStyle(R.style.AnimationFade);
        popupOrderWindow.setAnimationStyle(R.style.AnimationTop);

        popupOrderWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setOrderType(filterStatus + "");
                tvMsgType.setText(stringList.get(filterStatus).get("title"));
                if (TV2 != null){
                    TV2.setText(stringList.get(filterStatus).get("title"));
                }
                darkenBackground(1f);
                if (!needRefresh) {
                    needRefresh = true;
                }else{
                    needRefresh = false;
                    currentPage = 1;
                    getDataType(true);
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
                    helper.getView(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (filterStatus == i) {
                    needRefresh = false;
                    popupOrderWindow.dismiss();
                    return;
                }
                filterStatus = i;
                needRefresh = true;
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
                tvMsgType.setText(strings[i]);
                adapter.replaceAll(stringList);
                popupOrderWindow.dismiss();
            }
        });
    }

    private void showPopwindow(){
        try {
            if (popupOrderWindow != null && popupOrderWindow.isShowing()) {
                popupOrderWindow.dismiss();
            } else {
                popupOrderWindow.showAsDropDown(stickHeaderView);
                darkenBackground(0.7f);
            }
        } catch (Exception e) {
            GHLog.e("popwindow创建与显示", e.toString());
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
     * 创建煤种筛选条件
     * @return
     */
    private List<FilterEntity> getFilterPopupWindowData() {
        List<FilterEntity> mLeftRvData = new ArrayList<>();
        Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100002"}).get(0);

        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setChecked(true);
        filterEntity.dictCode = "-1";
        filterEntity.dictValue = "全部";
        coalTypes.list.add(0,filterEntity);

        mLeftRvData.addAll(coalTypes.list);

        for (FilterEntity filter : mLeftRvData){
            Dictionary sys100007 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100007"}).get(0);
            FilterEntity filterEntity1 = new FilterEntity();
            filterEntity1.setChecked(true);
            filterEntity1.dictCode = "-1";
            filterEntity1.dictValue = "发热量不限";
            sys100007.list.add(0, filterEntity1);
            filter.list = new ArrayList<>();
            filter.list.addAll(sys100007.list);
        }
        return mLeftRvData;
    }

    /**
     * 创建筛选条件
     */
    private void createPopupWindow() {
        List<Dictionary> data = new ArrayList<>();

        //全硫分
        Dictionary sys100010 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100010"}).get(0);
        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.setChecked(true);
        filterEntity1.dictCode = "";
        filterEntity1.dictValue = "不限";
        sys100010.list.add(0, filterEntity1);

        //灰分
        Dictionary sys100011 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100011"}).get(0);
        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.setChecked(true);
        filterEntity2.dictCode = "";
        filterEntity2.dictValue = "不限";
        sys100011.list.add(0, filterEntity2);

        //工艺
        Dictionary sys100008 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100008"}).get(0);
        FilterEntity filterEntity3 = new FilterEntity();
        filterEntity3.setChecked(true);
        filterEntity3.dictCode = "";
        filterEntity3.dictValue = "不限";
        sys100008.title = "工艺";
        sys100008.list.add(0, filterEntity3);


        data.add(sys100010);
        data.add(sys100011);
        data.add(sys100008);
        filterPopupWindow = new FilterPopupWindow(mContext, data, true, true,true, (mRecyclerView.getHeight() - BaseUtils.dip2px(this, 46)));
        filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setFirstPrice(filterPopupWindow.isOpened() == true ? "1" : "0");
                setFreeInfor(filterPopupWindow.isInforOpened() == true ? "2" : "0");
                setMap(filterPopupWindow.getMap());
                currentPage = 1;
                getDataType(true);
            }
        });

        filterPopupWindow.setOnClickListener(new FilterPopupWindow.ResetRegionCode() {
            @Override
            public void reset(String follow) {

            }
        });
        filterPopupWindow.setTitle("价格优先");
    }

    private void setMap(Map<String, FilterEntity> map) {
        this.map = map;
    }

    /**
     * 设置价格优先 0:否1:是
     *
     * @param firstPrice
     */
    public void setFirstPrice(String firstPrice) {
        this.firstPrice = firstPrice;
    }
    /**
     * 全部和收费信息:0全部2:收费
     *
     * @param freeInfor
     */
    public void setFreeInfor(String freeInfor) {
        this.freeInfor = freeInfor;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AREA_CODE:
                    if (data != null){
                        String code = data.getStringExtra("code");
                        String name = data.getStringExtra("name");
                        setRegionCode(code);
                        filterPopupWindow.setAreaText(name);
                    }
                    break;
                case Constant.AREA_CODE_COAL_DETAILS:
                    if (llContrast != null){
                        llContrast.setContrastLayout();
                    }
                    if (data != null && "1".equals(data.getStringExtra("upData"))){
                        getInItData(true);
                    }
                    break;
            }
        }
    }

    public void registerMyReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_ACTION);
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null){
            unregisterReceiver(myReceiver);
        }
    }

    /**
     *
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RECEIVED_ACTION.equals(intent.getAction()) && intent != null) {
                try {
                    GHLog.e("MyReceiver", "在线买煤数据刷新");
                    getInItData(false);
                    if (coalEntity == null){
//                        getDataType(true);
                    }else{
                        if (MODE_TYPE == 0 || MODE_TYPE == 2){
                            Coal coal = coalEntity.getCoal070001().get(coalEntity.getItemPos());
                            coal.setIsPay("1");
                            coal.setLicenseMinute("7天后失效");
//                            mAdapter.getDatas().get(coalEntity.getPos()).getCoal070001().set(coalEntity.getItemPos(),coalEntity.getCoal());
                            mAdapter.notifyItemChanged(coalEntity.getPos(),mAdapter.getDatas().get(coalEntity.getPos()));
                        }else if (MODE_TYPE == 1 || MODE_TYPE == 3) {
                            coalEntity.getCoal().setIsPay("1");
                            coalEntity.getCoal().setLicenseMinute("7天后失效");
                            mAdapter.notifyItemChanged(coalEntity.getPos(),coalEntity.getCoal());
                        }
                    }
                } catch (Exception e) {
                    GHLog.e("MyReceiver", e.toString());
                }
            }
        }
    }

}

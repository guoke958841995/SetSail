package com.sxhalo.PullCoal.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.Discover;
import com.sxhalo.PullCoal.model.SearchEntity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserDemand;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.addrightview.LayoutContrastView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

/**
 * 首页跳转后的搜索界面
 */
public class SearchResultActivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<SearchEntity> {


    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.search_list_type)
    SmoothListView searchListType;


    @Bind(R.id.ll_contrast)
    LayoutContrastView llContrast;


    @Bind(R.id.listview_no_net)
    LinearLayout listviewNoNet;
    @Bind(R.id.listview_ementy)
    LinearLayout listviewEment;
    @Bind(R.id.text_ementy)
    TextView textEment;

    private String keyWord;
    private BaseAdapterUtils<SearchEntity> baseAdapter;
    private List<SearchEntity> searchEntityList = new ArrayList<SearchEntity>();
    private Map<String, String> coalTypeMap = new HashMap<String, String>();
    private Serializable serializable;

    private int type;  //标记当前点击的类型
    private MyReceiver myReceiver;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_result);
    }

    @Override
    protected void initTitle() {
        keyWord = getIntent().getStringExtra("keyword");
        etSearch.setText(keyWord);
        etSearch.setSelection(keyWord.length());
        registerMyBroadcast();
        initListener();
        queryCoalType();

    }

    private void registerMyBroadcast() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_ACTION);
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void getData() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("searchValue", keyWord);
        new DataUtils(this, params).getHomeSearch(new DataUtils.DataBack<List<APPData<Map<String, Object>>>>() {
            @Override
            public void getData(List<APPData<Map<String, Object>>> data) {
                if (data == null) {
                    return;
                }
                SearchEntity entity;
                if (data.size() == 0) {
                    entity = new SearchEntity();
                } else {
                    entity = new SearchEntity().getSearchEntity(data);
                }
                if (entity.getCoalProductList().size() == 0 && entity.getInfoDepartmentList().size() == 0 && entity.getMineMouthList().size() == 0 && entity.getTransportModeList().size() == 0 && entity.getDriverList().size() == 0 && entity.getUserDemandEntityList().size() == 0) {
                    displayToast(getString(R.string.no_result_tips));
                }
                setData(entity);
                baseAdapter.refreshData(searchEntityList);

                if (baseAdapter.getCount() == 0) {
                    searchListType.setVisibility(View.GONE);
                    listviewNoNet.setVisibility(View.GONE);
                    listviewEment.setVisibility(View.VISIBLE);
                    textEment.setText("未查找到您搜索的相关内容，请重新搜索");
                } else {
                    searchListType.setVisibility(View.VISIBLE);
                    listviewNoNet.setVisibility(View.GONE);
                    listviewEment.setVisibility(View.GONE);
                }
            }

            @Override
            public void getError(Throwable e) {
                listviewNoNet.setVisibility(View.VISIBLE);
                searchListType.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchEntityList != null && searchEntityList.size() != 0) {
            llContrast.setContrastLayout();
        }else{
            llContrast.setVisibility(View.GONE);
        }
    }

    private void queryCoalType() {
        Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100002"}).get(0);
        for (int i = 0; i < coalTypes.list.size(); i++) {
            coalTypeMap.put(coalTypes.list.get(i).dictCode, coalTypes.list.get(i).dictValue);
        }
    }

    /**
     * 对接口反回的数据进行处理
     *
     * @param entity
     */
    public void setData(SearchEntity entity) {
        searchEntityList.clear();
        if (entity.getCoalProductList() != null && entity.getCoalProductList().size() != 0) {
            SearchEntity listCoal = new SearchEntity();
            listCoal.setCoalProduct(entity.getCoalProductList());
            listCoal.setTitle("煤炭");
            searchEntityList.add(listCoal);

            if (llContrast.getContrastList().size() != 0){
                llContrast.setVisibility(View.VISIBLE);
            }
        }else{
            llContrast.setVisibility(View.GONE);
        }
        if (entity.getInfoDepartmentList() != null && entity.getInfoDepartmentList().size() != 0) {
            SearchEntity listDiscover = new SearchEntity();
            listDiscover.setInfoDepartmentList(entity.getInfoDepartmentList());
            listDiscover.setTitle("信息部");
            searchEntityList.add(listDiscover);
        }
        if (entity.getMineMouthList() != null && entity.getMineMouthList().size() != 0) {
            SearchEntity listMine = new SearchEntity();
            listMine.setMineMouthList(entity.getMineMouthList());
            listMine.setTitle("矿口");
            searchEntityList.add(listMine);
        }
        if (entity.getTransportModeList() != null && entity.getTransportModeList().size() != 0) {
            SearchEntity transportModeList = new SearchEntity();
            transportModeList.setTransportModeList(entity.getTransportModeList());
            transportModeList.setTitle("货运");
            searchEntityList.add(transportModeList);
        }
        if (entity.getDriverList() != null && entity.getDriverList().size() != 0) {
            SearchEntity userEntityList = new SearchEntity();
            userEntityList.setDriverList(entity.getDriverList());
            userEntityList.setTitle("司机");
            searchEntityList.add(userEntityList);
        }
        if (entity.getUserDemandEntityList() != null && entity.getUserDemandEntityList().size() != 0) {
            SearchEntity userDemandEntity = new SearchEntity();
            userDemandEntity.setUserDemandEntityList(entity.getUserDemandEntityList());
            userDemandEntity.setTitle("买煤求购");
            searchEntityList.add(userDemandEntity);
        }
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        finish();
    }

    private void initListener() {
        baseAdapter = new BaseAdapterUtils<SearchEntity>(this, searchListType);
        baseAdapter.settingList(false,false);
        baseAdapter.setViewItemData(R.layout.search_layout_all_view, searchEntityList);
        baseAdapter.setBaseAdapterBack(this);
        baseAdapter.settingList(false, false);

        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    //                    adapter.getFilter().filter(s);
                    //                    listView.setVisibility(View.VISIBLE);
                } else {
//                        entity = null;
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyWord = etSearch.getText().toString().trim();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    searchEntityList.clear();
                    baseAdapter.refreshData(searchEntityList);
                    getData();
                    return true;
                }
                return false;
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
                if (baseAdapter != null){
                    baseAdapter.refreshData();
                }
            }
        });
    }

    /**
     * what 0 条目点击 1 查看更多 2 条目赋值
     * 创建矿口列表展示界面
     */
    private void createMineMouthList(int what, BaseAdapterHelper helper, final Discover discover, int pos) {
        switch (what) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(mContext, DetaileMineActivity.class);
                intent.putExtra("InfoDepartId", discover.getItemId());
                startActivity(intent);
                break;
            case 1:
                //跳转查找矿口地图
                UIHelper.showFindPunctuation(mContext, SearchPunctuationActivity.class, "查找矿口", keyWord);
                break;
            case 2:
                helper.getView().findViewById(R.id.search_mine_layout).setVisibility(View.VISIBLE);

                helper.setText(R.id.search_name, StringUtils.setHighLightColor(mContext, discover.getItemName(), keyWord));
                helper.getView().findViewById(R.id.search_dis).setVisibility(View.GONE);
                helper.setText(R.id.search_address, StringUtils.setHighLightColor(mContext, discover.getAddress(), keyWord));
                helper.getView().findViewById(R.id.find_search_goto).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UIHelper.showRoutNavi(mContext, discover.getLatitude(), discover.getLongitude(), discover.getAddress());
                    }
                });
                break;
        }
    }

    /**
     *  what 0 条目点击 1 查看更多 2 条目赋值
     * 创建信息部列表展示界面
     */
    private void createInformationList(int what, BaseAdapterHelper helper, final Discover discover, int pos) {
        switch (what) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(mContext, DetaileInformationDepartmentActivity.class);
                intent.putExtra("InfoDepartId", discover.getItemId());
                startActivity(intent);
                break;
            case 1:
                //跳转查找信息部地图
                UIHelper.showFindPunctuation(mContext, SearchPunctuationActivity.class, "查信息部", keyWord);
                break;
            case 2:
                helper.getView().findViewById(R.id.search_mine_layout).setVisibility(View.VISIBLE);

                helper.setText(R.id.search_name, StringUtils.setHighLightColor(mContext, discover.getItemName(), keyWord));
                helper.getView().findViewById(R.id.search_dis).setVisibility(View.GONE);
                helper.setText(R.id.search_address, StringUtils.setHighLightColor(mContext, discover.getAddress(), keyWord));
                helper.getView().findViewById(R.id.find_search_goto).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UIHelper.showRoutNavi(mContext, discover.getLatitude(), discover.getLongitude(), discover.getAddress());
                    }
                });
                break;
        }
    }

    /**
     *  what 0 条目点击 1 查看更多 2 条目赋值
     * 创建煤列表展示界面
     */
    private void createCoalList(int what, BaseAdapterHelper helper,final Coal coal, int pos) {
        switch (what) {
            case 0:
                String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录 先去登录
                    UIHelper.jumpActLogin(mContext,false);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, DetailsWebActivity.class);
                    intent.putExtra("Coal", coal);
                    intent.putExtra("inforDepartId", "煤炭详情");
                    serializable = coal;
                    if (SharedTools.getStringValue(mContext, "coalSalesId", "-1").equals(coal.getCoalSalesId())) {
                        startActivity(intent);
                    } else {
                        nextMove(coal.getConsultingFee(), coal.getIsPay(), intent, "0");
                    }
                }
                break;
            case 1:
                Intent intent1 = new Intent(mContext, SearchActivity.class);
                intent1.putExtra("keyword", keyWord);
                intent1.putExtra("TAG", Constant.Search_Coal);
                startActivity(intent1);
                break;
            case 2:
                helper.getView().findViewById(R.id.search_coal_layout).setVisibility(View.VISIBLE);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append("【");
                String stringType;
                String price = coal.getConsultingFee();
                ForegroundColorSpan colorSpan;
                if ("0".equals(price)) {
                    //免费资讯
                    stringType = "免费资讯";
                    colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.actionsheet_blue));
                } else {
                    //收费资讯
                    stringType = "收费资讯"  + "¥" + (Double.valueOf(price)/100);
                    colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.actionsheet_red));
                }
                spannableStringBuilder.append(stringType);
                spannableStringBuilder.setSpan(colorSpan, 1, stringType.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append("】");
                String rest = coal.getCoalName() + "，产自【" + coal.getMineMouthName() + "】，发热量：" + coal.getCalorificValue() + "kCal/kg，【"  + coal.getCompanyName() + "】" + coal.getDifferMinute();
                spannableStringBuilder.append(StringUtils.setHighLightColor(mContext, rest, keyWord));
                helper.setText(R.id.tv_coal_content, spannableStringBuilder);

                TextView contrastText = (TextView)helper.getView(R.id.contrast_text);
                contrastText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = SharedTools.getStringValue(mContext, "userId", "-1");
                        //加入对比时，要先判断是否登录
                        if (userId.equals("-1")) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(SearchResultActivity.this,false);
                        } else {
                            if (coal.getConsultingFee().equals("0") ||
                                    SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coal.getCoalSalesId())) {
                                //免费信息或者已支付 直接对比
                                llContrast.setAddContrastView(coal);
                            } else {
                                //判断是否支付
                                if (coal.getIsPay().equals("1")&& coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                                    //免费信息或者已支付 直接对比
                                    llContrast.setAddContrastView(coal);
                                } else {
                                    //未支付 弹框提示
                                    showPayDialog(coal, "0");
                                }
                            }
                        }
                    }
                });

                if (llContrast.getifContrast(coal)){
                    contrastText.setBackgroundResource(R.drawable.button_shape_pressed);
                    contrastText.setTextColor(getResources().getColor(R.color.white));
                }else{
                    contrastText.setBackgroundResource(R.drawable.bull_send_car);
                    contrastText.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                }
                break;
        }
    }


    /**
     * 创建货运列表展示界面
     */
    private void createFreight(int what, BaseAdapterHelper helper, TransportMode item, int pos) {
        switch (what) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(mContext, TransportDetailActivity.class);
                intent.putExtra("waybillId", item.getTransportId());
                serializable = item;
                if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(item.getCoalSalesId())){
                    startActivity(intent);
                }else {
                    nextMove(item.getConsultingFee(), item.getIsPay(), intent, "1");
                }
                break;
            case 1:
                Intent intent1 = new Intent(mContext, SearchActivity.class);
                intent1.putExtra("keyword", keyWord);
                intent1.putExtra("TAG", Constant.Search_Freight);
                startActivity(intent1);
                break;
            case 2:
                helper.getView().findViewById(R.id.search_freight_layout).setVisibility(View.VISIBLE);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append("【");
                String stringType;
                String price = item.getConsultingFee();
                ForegroundColorSpan colorSpan;
                if ("0".equals(price)) {
                    //免费资讯
                    stringType = "免费资讯";
                    colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.actionsheet_blue));
                } else {
                    //收费资讯
                    stringType = "收费资讯"  + "¥" + (Double.valueOf(price)/100);
                    colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.actionsheet_red));
                }
                spannableStringBuilder.append(stringType);
                spannableStringBuilder.setSpan(colorSpan, 1, stringType.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append("】，");
                String freightType;
                if ("0".equals(StringUtils.isEmpty(item.getTransportType()) ? "0" : item.getTransportType())) {
                    //新发货运
                    freightType = "还需" + item.getSurplusNum() + "车";
                } else {
                    //长期货运
                    freightType = "长期货运";
                }
                String rest = "由【"  + item.getFromPlace() + "】至【" + item.getToPlace() + "】，" + "运费" + item.getCost() + freightType + "，【" + item.getCompanyName() + "】" + item.getDifferMinute();
                spannableStringBuilder.append(StringUtils.setHighLightColor(mContext, rest, keyWord));
                helper.setText(R.id.tv_freight_content, spannableStringBuilder);

                break;
        }
    }

    /**
     * 创建司机列表展示界面
     */
    private void createDriver(int what, BaseAdapterHelper helper, UserEntity item, int pos) {
        switch (what) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(mContext, DetaileFriendActivity.class);
                UserEntity driver = item;
                intent.putExtra("UserEntity", driver);
                startActivity(intent);
                break;
            case 1:
                Intent intent1 = new Intent(mContext, SearchActivity.class);
                intent1.putExtra("keyword", keyWord);
                intent1.putExtra("TAG", Constant.Search_Driver);
                startActivity(intent1);
                break;
            case 2:
                helper.getView().findViewById(R.id.search_driver_layout).setVisibility(View.VISIBLE);
                String derverName = item.getRealName();
                helper.setText(R.id.drivert_name_num, StringUtils.setHighLightColor(mContext, (derverName), keyWord));

                String carLength = item.getVehicleLength();
                if (StringUtils.isEmpty(carLength)) {
                    carLength = "--  ";
                } else {
                    carLength = carLength + "米  ";
                }

                Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
                String vehicleModels = "";
                for (FilterEntity filterEntity : sys100005.list) {
                    if (filterEntity.dictCode.equals(item.getVehicleMode())) {
                        vehicleModels = filterEntity.dictValue;
                        break;
                    }
                }
                if (StringUtils.isEmpty(vehicleModels)) {
                    vehicleModels = "--  ";
                } else {
                    vehicleModels = vehicleModels + "  ";
                }

                String carLoad = item.getVehicleLoad();
                if (StringUtils.isEmpty(carLoad)) {
                    carLoad = "--";
                } else {
                    carLoad = "载重" + carLoad + "吨";
                }
                //车长 、车型、载重
                helper.setText(R.id.drivert_car_content, StringUtils.setHighLightColor(mContext, (carLength + vehicleModels + "  " + carLoad), keyWord));

                helper.setText(R.id.drivert_common_singular, StringUtils.setHighLightColor(mContext, ("累计：" + item.getCumulative() + "单"), keyWord));

                //  好友状态判断
                TextView friendIV = (TextView) helper.getView().findViewById(R.id.tv_is_friend);
                if (item.getIsFriend().equals("0")) {
                    friendIV.setVisibility(View.GONE);
                } else {
                    friendIV.setVisibility(View.VISIBLE);
                }

                //是否是专线司机
                if (item.getSpecialLine().equals("1")) {
                    helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.GONE);
                }
                break;
        }
    }


    /**
     * 点击信息后下一步操作
     * @param consultingFee
     * @param isPay
     * @param intent
     * @param type
     */
    private void nextMove(String consultingFee, String isPay, Intent intent, String type) {
        String userId = SharedTools.getStringValue(this, "userId", "-1");
        //先判断是否登录
        if (userId.equals("-1")) {
            //未登录 先去登录
            UIHelper.jumpActLogin(this,  false);
        } else {
            if (consultingFee.equals("0")) {
                //免费信息或者已支付 可以直接查看详情
                startActivity(intent);
            } else {
                //已登录 判断是否支付
                if (isPay.equals("1")) {
                    //已支付 直接查看
                    startActivity(intent);
                } else {
                    //未支付 弹框提示
                    showPayDialog(serializable, type);
                }
            }
        }
    }

    /**
     * what 0 条目点击 1 查看更多 2 条目赋值
     * 创建买煤求购列表展示界面
     */
    private void createPurchase(int what, BaseAdapterHelper helper, UserDemand userDemand, int pos) {
        switch (what) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(mContext, DetailPurchasingReservationActivity.class);
                intent.putExtra("demandId", userDemand.getDemandId());
                startActivity(intent);
                break;
            case 1:
                Intent intent1 = new Intent(mContext, SearchActivity.class);
                intent1.putExtra("keyword", keyWord);
                intent1.putExtra("TAG", Constant.Search_Purchase);
                startActivity(intent1);
                break;
            case 2:
                TextView tvStatus = (TextView) helper.getView().findViewById(R.id.tv_status);
                if (userDemand.getRealnameAuthState().equals("1")) {
                    //已认证 显示名字 隐藏中间
                    helper.setText(R.id.tv_name, StringUtils.setName(userDemand.getContactPerson()));
                    tvStatus.setText("已认证");
                    tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_green));
                } else {
                    //未认证 显示电话号码隐藏中间4位
                    helper.setText(R.id.tv_name, StringUtils.setPhoneNumber(userDemand.getContactPhone()));
                    tvStatus.setText("未认证");
                    tvStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_light_gray));
                }
                CircleImageView ivHead = (CircleImageView)helper.getView().findViewById(R.id.iv_head);
                if (!StringUtils.isEmpty(userDemand.getHeadPic())){
                    // 头像
                    ((BaseActivity)mContext).getImageManager().loadUrlImageView(userDemand.getHeadPic(), ivHead);
                }else{
                    ivHead.setImageResource(R.mipmap.main_tab_item);
                }
                String coalType = coalTypeMap.get(userDemand.getCategoryId());
                helper.setText(R.id.tv_title, StringUtils.setHighLightColor(mContext, "求购基低位发热量" + userDemand.getCalorificValue() + "Kcal/Kg" + userDemand.getCoalName(), keyWord));
                helper.setText(R.id.tv_coal_name, StringUtils.setHighLightColor(mContext, coalType, keyWord));
                helper.setText(R.id.tv_area, StringUtils.setHighLightColor(mContext, userDemand.getRegionName(), keyWord));
                helper.getView().findViewById(R.id.search_purchase_layout).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, SearchEntity searchEntity, final int pos) {
        if (pos == 0) {
            helper.getView().findViewById(R.id.search_layout_line).setVisibility(View.GONE);
        } else {
            helper.getView().findViewById(R.id.search_layout_line).setVisibility(View.VISIBLE);
        }
        TextView title = (TextView) helper.getView().findViewById(R.id.title_search);
        title.setText(searchEntity.getTitle());
        final String titleString = searchEntity.getTitle();
        CustomListView listView = (CustomListView) helper.getView().findViewById(R.id.layout_search_listview);
        final List<Object> objects = setDataItem(searchEntity);
        final QuickAdapter<Object> mAdaptetItem = new QuickAdapter<Object>(mContext, R.layout.search_layout_all_view_item, objects) {
            @Override
            protected void convert(BaseAdapterHelper helper, Object data, final int pos) {
                try {
                    if (pos > 2) {
                        //列表最多只显示三条数据
                        return;
                    }
                    if (titleString.contains("煤炭")) {
                        createCoalList(2, helper, (Coal) data, pos);
                        final Coal coalData = (Coal) data;
                        helper.getView().findViewById(R.id.search_coal_layout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createCoalList(0, null, coalData, pos);
                            }
                        });
                    } else if (titleString.contains("信息部")) {
                        createInformationList(2, helper, (Discover) data, pos);
                    } else if (titleString.contains("矿口")) {
                        createMineMouthList(2, helper, (Discover) data, pos);
                    } else if (titleString.contains("货运")) {
                        createFreight(2, helper, (TransportMode) data, pos);
                    } else if (titleString.contains("司机")) {
                        createDriver(2, helper, (UserEntity) data, pos);
                    }else if (titleString.contains("买煤求购")) {
                        createPurchase(2, helper, (UserDemand) data, pos);
                    }
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        listView.setAdapter(mAdaptetItem);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (titleString.equals("信息部")) {
                        createInformationList(0, null, (Discover) mAdaptetItem.getItem(position), position);
                    } else if (titleString.equals("矿口")) {
                        createMineMouthList(0, null, (Discover) mAdaptetItem.getItem(position), position);
                    } else if (titleString.equals("货运")) {
                        createFreight(0, null, (TransportMode) mAdaptetItem.getItem(position), position);
                    } else if (titleString.equals("司机")) {
                        createDriver(0, null, (UserEntity) mAdaptetItem.getItem(position), position);
                    }else if (titleString.equals("买煤求购")) {
                        createPurchase(0, null, (UserDemand) mAdaptetItem.getItem(position), position);
                    }
                } catch (Exception e) {
                    GHLog.e("搜索点击", e.toString());
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                }
            }
        });
        helper.getView().findViewById(R.id.layout_search_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleString.equals("煤炭")) {
                    createCoalList(1, null, null, pos);
                } else if (titleString.equals("信息部")) {
                    createInformationList(1, null, null, pos);
                } else if (titleString.equals("矿口")) {
                    createMineMouthList(1, null, null, pos);
                } else if (titleString.equals("货运")) {
                    createFreight(1, null, null, pos);
                } else if (titleString.equals("司机")) {
                    createDriver(1, null, null, pos);
                } else if (titleString.equals("买煤求购")) {
                    createPurchase(1, null, null, pos);
                }
            }
        });
    }

    /**
     * 对反回的数据进行条数处理，使数据不超过3条
     *
     * @param searchEntity
     * @return
     */
    private List<Object> setDataItem(SearchEntity searchEntity) {
        List<Object> objectNew = new ArrayList<Object>();
        if (searchEntity.getTitle().equals("煤炭")) {
            for (int i = 0; i < searchEntity.getCoalProductList().size(); i++) {
                objectNew.add(i, searchEntity.getCoalProductList().get(i));
            }
        } else if (searchEntity.getTitle().equals("信息部")) {
            for (int i = 0; i < searchEntity.getInfoDepartmentList().size(); i++) {
                objectNew.add(i, searchEntity.getInfoDepartmentList().get(i));
            }
        } else if (searchEntity.getTitle().equals("矿口")) {
            for (int i = 0; i < searchEntity.getMineMouthList().size(); i++) {
                objectNew.add(i, searchEntity.getMineMouthList().get(i));
            }
        } else if (searchEntity.getTitle().equals("货运")) {
            for (int i = 0; i < searchEntity.getTransportModeList().size(); i++) {
                objectNew.add(i, searchEntity.getTransportModeList().get(i));
            }
        } else if (searchEntity.getTitle().equals("司机")) {
            for (int i = 0; i < searchEntity.getDriverList().size(); i++) {
                objectNew.add(i, searchEntity.getDriverList().get(i));
            }
        }else if (searchEntity.getTitle().equals("买煤求购")) {
            for (int i = 0; i < searchEntity.getUserDemandEntityList().size(); i++) {
                objectNew.add(i, searchEntity.getUserDemandEntityList().get(i));
            }
        }
        return objectNew;
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<SearchEntity> mAdapter) {

    }

    @Override
    public void getOnRefresh(int page) {
    }

    @Override
    public void getOnLoadMore(int page) {
    }


    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RECEIVED_ACTION.equals(intent.getAction()) && intent != null) {
                try {
                    getData();
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("MyReceiver", e.toString());
                }
            }
        }
    }
}

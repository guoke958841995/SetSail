package com.sxhalo.PullCoal.ui.order.myreleaseFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.Dictionary;
import com.sxhalo.PullCoal.bean.Orders;
import com.sxhalo.PullCoal.bean.UserDemand;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.ui.order.contract.MyReleaseContract;
import com.sxhalo.PullCoal.ui.order.presenter.MyReleasePresenter;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;

public class MyReleaseFragment extends BaseFragment<MyReleasePresenter> implements MyReleaseContract.View {
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.iv_no_data)
    ImageView ivNoData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.my_Release_footer)
    ClassicsFooter myReleaseFooter;
    @BindView(R.id.my_Release_RefreshLayout)
    SmartRefreshLayout myReleaseRefreshLayout;
    @BindView(R.id.layout)
    RelativeLayout layout;

    private Map<String, String> coalTypeMap = new HashMap<String, String>();
    private String searchValue;//搜索关键字
    /*
     *求购单
     * */
    private Context mContext;
    private FragmentActivity myActivity;
    private MyAdapter mAdapter;

    @Override
    public int getContentLayout() {
        return R.layout.fragment_order_procurement;
    }


    @Override
    public void initInjector(ApplicationComponent appComponent) {

        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);

    }

    @SuppressLint("WrongConstant")
    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        myActivity = getActivity();
        mContext = view.getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyReleaseFragment.MyAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        queryCoalType();
        initListener();
        getMyReleaseOrderList(true, true);

    }
    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchValue = etSearch.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    getMyReleaseOrderList(true,false);
                    return true;
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    searchValue = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    public void handlerRefreshUserDemandList(APPDataList<UserDemand> userDemandlist, Throwable e) {

        if (userDemandlist != null && userDemandlist.getList() != null) {

            List<UserDemand> list = userDemandlist.getList();
            if (list.size() < 10) {
                //1,没有更多数据
                myReleaseRefreshLayout.finishLoadMore(200, true, true);
            }

            mAdapter.addData(list);
        } else {
            //加载失败
            myReleaseRefreshLayout.finishLoadMore(false);
        }
    }


    @Override
    public void handlerLoadMoreUserDemandList(APPDataList<UserDemand> userDemandlist, Throwable e) {
        if (userDemandlist != null && userDemandlist.getList() != null) {
            List<UserDemand> list = userDemandlist.getList();
            if (list.size() < 10) {
                //1,没有更多数据
                myReleaseRefreshLayout.finishLoadMore(200, true, true);
            }
            mAdapter.addData(list);
        } else {
            //加载失败
            myReleaseRefreshLayout.finishLoadMore(false);
        }
    }

    private void queryCoalType() {
        Dictionary coalTypes = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100002"}).get(0);
        for (int i = 0; i < coalTypes.list.size(); i++) {
            coalTypeMap.put(coalTypes.list.get(i).dictCode, coalTypes.list.get(i).dictValue);
        }
    }

    public void getMyReleaseOrderList(boolean isRefresh, boolean flag) {
        layout.setVisibility(View.VISIBLE);
        if ("-1".equals(PaperUtil.get(AppConstant.USER_ID_KEY, "-1"))) {
            // TODO 登录之后处理
//            return;
        }

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("searchValue", searchValue);
        params.put("timeRange", "2");
        params.put("orderState", "0");
        params.put("currentPage", "1");
        params.put("pageSize", "10");
        mPresenter.getMyReleaseOrderList(myActivity, params, isRefresh, flag);
    }

    class MyAdapter extends BaseQuickAdapter<UserDemand, BaseViewHolder> {

        public MyAdapter(@Nullable List<UserDemand> data) {
            super(R.layout.my_release_order_item, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, UserDemand entity) {

            String coalType = coalTypeMap.get(entity.getCategoryId());
            helper.setText(R.id.tv_title, "求购基低位发热量" + entity.getCalorificValue() + "Kcal/Kg" + entity.getCoalName());
            helper.setText(R.id.tv_coal_name, coalType);
            helper.setText(R.id.tv_area, entity.getRegionName());
            helper.setText(R.id.tv_num, entity.getNumber());
            helper.setText(R.id.tv_time, entity.getCreateTime());
            LinearLayout layoutBond = (LinearLayout) helper.getView(R.id.layout_bond);
            TextView tvCash = (TextView) helper.getView(R.id.tv_cash);
            TextView tvDeliver = (TextView) helper.getView(R.id.tv_deliver);
            if ("0".equals(entity.getBond())) {
                layoutBond.setVisibility(View.GONE);
                helper.setText(R.id.tv_overdue, "已发布");
                tvDeliver.setVisibility(View.GONE);
            } else {
                layoutBond.setVisibility(View.VISIBLE);
                tvCash.setText("¥" + StringUtils.setNumLenth(Float.valueOf(entity.getBond()) / 100, 2).replace(".00", ""));

                tvDeliver.setVisibility(View.VISIBLE);
                if (entity.getDeliveryTotal() == null || "0".equals(entity.getDeliveryTotal())) {
                    helper.setText(R.id.tv_deliver, "没有信息部投递意向");
                } else {
                    helper.setText(R.id.tv_deliver, "已有" + entity.getDeliveryTotal() + "家信息部投递意向");
                }
                //  01、未发布；10、已发布（待投递，确认）；20、待选定；30、磋商；40、未达成意向；41、已达成意向；50、退款；60、完成；00、取消
                String demandState = entity.getDemandState();
                if ("01".equals(demandState)) {
                    tvDeliver.setVisibility(View.GONE);
                    helper.setText(R.id.tv_overdue, "未发布");
                } else {
                    int state = Integer.valueOf(demandState);
                    switch (state) {  //状态：01、未发布；10、已发布（待投递，确认）；20、待选定；30、磋商；40、未达成意向；41、已达成意向；50、退款；60、完成；00、取消
                        case 10:
                            tvDeliver.setVisibility(View.VISIBLE);
                            String TEXT = dateDiff(entity.getDeliveryEndTime());
                            if (!"即将到期".equals(TEXT) && !"已过期".equals(TEXT)) {
                                TEXT = TEXT + "后过期";
                            }
                            helper.setText(R.id.tv_overdue, TEXT);
                            break;
                        case 20:
                            tvDeliver.setVisibility(View.VISIBLE);
                            String TEXT1 = dateDiff(entity.getDeliveryEndTime());
                            if (!"即将到期".equals(TEXT1) && !"已过期".equals(TEXT1)) {
                                TEXT1 = TEXT1 + "后过期";
                            }
                            helper.setText(R.id.tv_overdue, TEXT1);
                            break;
                        case 30:
                            tvDeliver.setVisibility(View.GONE);
                            helper.setText(R.id.tv_overdue, "磋商");
                            break;
                        case 40:
                        case 41:
                        case 50:
                            tvDeliver.setVisibility(View.GONE);
                            helper.setText(R.id.tv_overdue, "退款");
                            break;
                        case 60:
                            tvDeliver.setVisibility(View.GONE);
                            helper.setText(R.id.tv_overdue, "完成");
                            break;
                        case 00:
                            tvDeliver.setVisibility(View.GONE);
                            helper.setText(R.id.tv_overdue, "已取消");
                            break;
                    }
                }
            }


        }

    }

    private String dateDiff(String endTime) {
        String strTime = null;
        long mDay = 10;
        long mHour = 10;
        long mMin = 30;
        long mSecond = 00;// 天 ,小时,分钟,秒

        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sd.format(curDate);
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime()
                    - sd.parse(str).getTime();
            mDay = diff / nd;// 计算差多少天
            mHour = diff % nd / nh;// 计算差多少小时
            mMin = diff % nd % nh / nm;// 计算差多少分钟
            mSecond = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            if (mDay >= 1) {
                strTime = mDay + "天" + mHour + "时" + mMin + "分";
            } else {
                if (mHour >= 1) {
                    strTime = mDay + "天" + mHour + "时" + mMin + "分";
                } else {
                    if (mSecond >= 1) {
                        strTime = mDay + "天" + mHour + "时" + mMin + "分" + mSecond + "秒";
                    } else if (mSecond < 0) {
                        strTime = "已过期";
                    } else {
                        strTime = "即将到期";
                    }
                }
            }
            return strTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}

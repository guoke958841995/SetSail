package com.sxhalo.PullCoal.ui.order.coalorder;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.Orders;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.ui.order.contract.CoalOrderContract;
import com.sxhalo.PullCoal.ui.order.presenter.CoalOrderPresenter;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.PaperUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;

public class CoalOrderFragment extends BaseFragment<CoalOrderPresenter> implements CoalOrderContract.View {
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.layout_filter)
    LinearLayout layoutFilter;
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.order_coalRefreshLayout)
    SmartRefreshLayout orderCoalRefreshLayout;

    @BindView(R.id.order_coal_footer)
    ClassicsFooter order_coal_footer;

    private ArrayList<String> list;
    private Context mContext;
    private FragmentActivity mActivity;

    private String searchValue;//搜索关键字
    private int currentPage = 1;
    private MyAdapter mAdapter;


    @Override
    public int getContentLayout() {
        return R.layout.fragment_order_coal;
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
        mContext = view.getContext();
        mActivity = getActivity();
        //
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);


        orderCoalRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentPage = 1;
                getCoalOrderList(true, false);
                // LogUtil.i("xxxx","下啦刷新currentPage="+currentPage);
            }
        });
        orderCoalRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {

                currentPage = currentPage + 1;
                getCoalOrderList(false, false);
            }
        });



        mAdapter = new MyAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        initListener();
        //第一次加载数据
        getCoalOrderList(true, true);

    }


    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchValue = etSearch.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    getCoalOrderList(true,false);
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


    @OnClick({R.id.et_search, R.id.tv_filter, R.id.layout_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.tv_filter:
                break;
            case R.id.layout_filter:
                Toast.makeText(getActivity(), "筛选", Toast.LENGTH_LONG).show();

                break;
        }
    }








    /**
     * 获取煤炭订单列表
     *
     * @param isRefresh T:下来刷新，F：加载更多
     * @param flag
     */
    private void getCoalOrderList(boolean isRefresh, boolean flag) {
        //获取数据
        if ("-1".equals(PaperUtil.get(AppConstant.USER_ID_KEY, "-1"))) {
            // TODO 登录之后处理
//            return;
        }

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

//        if (map.size() > 0) {
//            for (Map.Entry<String, FilterEntity> entry : map.entrySet()) {
//                if (entry.getKey().equals("时间选择") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
//                    //时间选择
//                    params.put("timeRange", entry.getValue().dictCode);
//                }
//                if (entry.getKey().equals("订单状态") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
//                    //订单状态
//                    params.put("orderState", entry.getValue().dictCode);
//                }
//            }
//        }

        LogUtil.i("123",searchValue);
        //搜索
        params.put("searchValue", searchValue);
        //params.put("timeRange", "2");
       // params.put("orderState", "0");
        params.put("currentPage", currentPage + "");
        params.put("pageSize", "10");


        mPresenter.getCoalOrderList(mActivity, params, isRefresh, flag);


    }


    /**
     * 下拉刷新
     *
     * @param ordersAPPDataList
     * @param e
     */
    @Override
    public void handlerRefreshCoalOrderList(APPDataList<Orders> ordersAPPDataList, Throwable e) {
        orderCoalRefreshLayout.finishRefresh();//刷新成功

        if (ordersAPPDataList != null) {
            List<Orders> list = ordersAPPDataList.getList();
            if (list.size() < 10) {
                orderCoalRefreshLayout.setNoMoreData(true);
            }
            mAdapter.setNewData(list);
        }
    }

    /**
     * 加载更多
     *
     * @param ordersAPPDataList
     * @param e
     */
    @Override
    public void handlerLoadMoreCoalOrderList(APPDataList<Orders> ordersAPPDataList, Throwable e) {

        if (ordersAPPDataList != null && ordersAPPDataList.getList() != null) {

            List<Orders> list = ordersAPPDataList.getList();
            if (list.size() < 10) {
                //1,没有更多数据
                orderCoalRefreshLayout.finishLoadMore(200, true, true);
            }

            mAdapter.addData(list);
        } else {
            //加载失败
            orderCoalRefreshLayout.finishLoadMore(false);
        }
    }


    class MyAdapter extends BaseQuickAdapter<Orders, BaseViewHolder> {


        public MyAdapter(@Nullable List<Orders> data) {
            super(R.layout.view_order_coal_list_item, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Orders item) {
            TextView tv = helper.getView(R.id.tv_coal_name);
            TextView order_status = helper.getView(R.id.tv_order_status);
            TextView tv_coal = helper.getView(R.id.tv_coal);  //煤炭
            TextView tv_seller = helper.getView(R.id.tv_seller);//卖家
            TextView tv_place_of_pruduction = helper.getView(R.id.tv_place_of_pruduction);//产地
            TextView tv_time = helper.getView(R.id.tv_time);//时间
            View view = helper.getView(R.id.order_coal_item_divider);
            int position = helper.getLayoutPosition();
            tv.setText(item.getCoalName());
            String orderStatus = item.getOrderState();
            if (position == 0) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            tv_coal.setText(item.getTradingVolume().replace(".00", "") + "吨    " + item.getCalorificValue() + "kCal/kg    " + item.getCoalCategoryName());
            tv_seller.setText(item.getCoalSalesName());
            tv_place_of_pruduction.setText(item.getMineMouthName());
            tv_time.setText(item.getCreateTime());
            if (orderStatus.equals("0") || orderStatus.equals("1") || orderStatus.equals("4") || orderStatus.equals("5")) {
                //进行中 0 1 4 5
                orderStatus = "进行中";
                order_status.setTextColor(getResources().getColor(R.color.app_title_text_color));
            } else if (orderStatus.equals("2")) {
                //已拒绝 2
                orderStatus = "已拒绝";
                order_status.setTextColor(getResources().getColor(R.color.app_title_text_color));
            } else if (orderStatus.equals("3") || orderStatus.equals("6")) {
                //已超期 3 6
                orderStatus = "已超期";
                order_status.setTextColor(getResources().getColor(R.color.app_title_text_color));
            } else if (orderStatus.equals("7")) {
                //已完成 7
                orderStatus = "已完成";
                order_status.setTextColor(getResources().getColor(R.color.app_title_text_color));
            } else {
                //已取消 100
                orderStatus = "已取消";
                order_status.setTextColor(getResources().getColor(R.color.app_title_text_color));
            }
            order_status.setText(orderStatus);

        }


    }


    private void createPopupWindow() {
    }
}

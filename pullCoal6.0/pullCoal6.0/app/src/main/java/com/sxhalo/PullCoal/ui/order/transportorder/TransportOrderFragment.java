package com.sxhalo.PullCoal.ui.order.transportorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.SendCarEntity;
import com.sxhalo.PullCoal.bean.TransportMode;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.ui.order.contract.TransportOrderContract;
import com.sxhalo.PullCoal.ui.order.presenter.TransportOrderPresenter;
import com.sxhalo.PullCoal.ui.order.sendcarorder.SendCarOrderFragment;
import com.sxhalo.PullCoal.utils.PaperUtil;

import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;


/**
 * 获取货运订单
 */

public class TransportOrderFragment extends BaseFragment<TransportOrderPresenter> implements TransportOrderContract.View {
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.my_Release_footer)
    ClassicsFooter myReleaseFooter;
    @BindView(R.id.my_transport_RefreshLayout)
    SmartRefreshLayout myTransportRefreshLayout;
    private Context mContext;
    private FragmentActivity myActivity;
    private MyAdapter mAdapter;
    private String searchValue;//搜索关键字

    @Override
    public int getContentLayout() {
        return R.layout.fragment_order_transport;
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

        mAdapter = new MyAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {

        initListener();
        getTransportOrderList(true,true);

    }


    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchValue = etSearch.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    getTransportOrderList(true,false);
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
    public void handlerRefreshTransportOrderList(APPDataList<TransportMode> sendCarEntitylist, Throwable e) {
        myTransportRefreshLayout.finishRefresh();//刷新成功
        if (sendCarEntitylist != null) {
            List<TransportMode> list = sendCarEntitylist.getList();
            if (list.size() < 10) {
                myTransportRefreshLayout.setNoMoreData(true);
            }
            mAdapter.setNewData(list);
        }
    }

    @Override
    public void handlerLoadMoreTransportOrderList(APPDataList<TransportMode> sendCarEntitylist, Throwable e) {
        if (sendCarEntitylist != null && sendCarEntitylist.getList() != null) {

            List<TransportMode> list = sendCarEntitylist.getList();
            if (list.size() < 10) {
                //1,没有更多数据
                myTransportRefreshLayout.finishLoadMore(200, true, true);
            }
            mAdapter.addData(list);
        } else {
            //加载失败
            myTransportRefreshLayout.finishLoadMore(false);
        }
    }

    public void getTransportOrderList(boolean isRefresh, boolean flag) {

        if ("-1".equals(PaperUtil.get(AppConstant.USER_ID_KEY, "-1"))) {
            // TODO 登录之后处理
//            return;
        }

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("searchValue", searchValue);
        //params.put("timeRange", "2");
       // params.put("orderState", "0");
        params.put("currentPage", "1");
        params.put("pageSize", "10");
        mPresenter.getTransportOrderList(myActivity, params, isRefresh, flag);
    }

    @OnClick(R.id.btn_add)
    public void onViewClicked() {

    }

    class MyAdapter extends BaseQuickAdapter<TransportMode, BaseViewHolder> {

        public MyAdapter(@Nullable List<TransportMode> data) {
            super(R.layout.transport_order_item, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TransportMode transportMode) {
            View view_line = helper.getView(R.id.divider);
            int pos =helper.getLayoutPosition();
            if (pos == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }
            String acceptanceStatus = transportMode.getOrderState();
            String type = transportMode.getOrderType();//货运单类型：0，主动接单；1，邀请接单
            //货运单状态：0、待处理；1、同意；2、拒绝；3、司机确认拉货   4、信息部确认司机到达；100、取消
            switch (Integer.valueOf(acceptanceStatus)){
                case 0: //待处理
                    if (type.equals("0")){
                        //主动接单
                        helper.setText(R.id.tv_status, "待审核");
                    }else if (type.equals("1")){
                        //受邀
                        helper.setText(R.id.tv_status, "待接受");
                    }
                    helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                    helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));

                    helper.setTextColor(R.id.tv_status,getResources().getColor(R.color.actionsheet_blue) );
                    break;
                case 1: //同意
                    //审核通过
                    helper.setText(R.id.tv_status, "进行中");
                    helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.actionsheet_blue));

                    helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                    helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                    break;
                case 2:  //拒绝
                    if (type.equals("0")){
                        //主动接单
                        helper.setText(R.id.tv_status, "信息部拒绝");
                    }else if (type.equals("1")){
                        //受邀
                        helper.setText(R.id.tv_status, "已拒绝");
                    }
                    helper.setTextColor(R.id.tv_status,getResources().getColor( R.color.gray));

                    helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                    helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                    break;
                case 3:  //司机确认拉货
                    //已完成
                    helper.setText(R.id.tv_status, "已完成");
                    helper.setTextColor(R.id.tv_status,getResources().getColor( R.color.gray));

                    helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                    helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                    break;
                case 4: //信息部确认司机到达
                    //已完成
                    helper.setText(R.id.tv_status, "已完成");
                    helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.gray));

                    helper.setTextColor(R.id.tv_transport_order_time, getResources().getColor(R.color.gray));
                    helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                    break;
                case 100: //取消
                    //已取消
                    helper.setText(R.id.tv_status, "已取消");
                    helper.setTextColor(R.id.tv_status, getResources().getColor(R.color.gray));

                    helper.setTextColor(R.id.tv_transport_order_time,getResources().getColor(R.color.gray));
                    helper.setText(R.id.tv_transport_order_time, transportMode.getCreateTime());
                    break;
            }
            helper.setText(R.id.tv_transport_order_num, transportMode.getTransportOrderCode());
            helper.setText(R.id.tv_start, transportMode.getFromPlace());
            helper.setText(R.id.tv_end, transportMode.getToPlace());
        }
    }
}

package com.sxhalo.PullCoal.ui.order.sendcarorder;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.Orders;
import com.sxhalo.PullCoal.bean.SendCarEntity;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.ui.order.contract.SebdCarOrderContract;
import com.sxhalo.PullCoal.ui.order.presenter.SebdCarOrderPresenter;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;

public class SendCarOrderFragment extends BaseFragment<SebdCarOrderPresenter> implements SebdCarOrderContract.View {


    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.iv_no_data)
    ImageView ivNoData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.order_take_footer)
    ClassicsFooter orderTakeFooter;
    @BindView(R.id.order_take_RefreshLayout)
    SmartRefreshLayout orderTakeRefreshLayout;
    @BindView(R.id.morder_take_RecyclerView)
    RecyclerView morderTakeRecyclerView;

    private Context mContext;
    private FragmentActivity myActivity;
    private MyAdapter mAdapter;
    private String searchValue;//搜索关键字

    @Override
    public int getContentLayout() {
        return R.layout.fragment_order_take_delivery;
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
        morderTakeRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(null);
        morderTakeRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void initData() {

        getSendCarOrderList(true, true);
        initListener();
    }
    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchValue = etSearch.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    getSendCarOrderList(true,false);
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

    //上啦加载
    @Override
    public void handlerLoadMoreSendCarOrderList(APPDataList<SendCarEntity> sendCarEntitylist, Throwable e) {
        if (sendCarEntitylist != null && sendCarEntitylist.getList() != null) {

            List<SendCarEntity> list = sendCarEntitylist.getList();
            if (list.size() < 10) {
                //1,没有更多数据
                orderTakeRefreshLayout.finishLoadMore(200, true, true);
            }
            mAdapter.addData(list);
        } else {
            //加载失败
            orderTakeRefreshLayout.finishLoadMore(false);
        }
    }

    /*
     * 下啦刷新
     * */

    @Override
    public void handlerRefreshSendCarOrderList(APPDataList<SendCarEntity> sendCarEntity, Throwable e) {
        orderTakeRefreshLayout.finishRefresh();//刷新成功
        if (sendCarEntity != null) {
            List<SendCarEntity> list = sendCarEntity.getList();
            if (list.size() < 10) {

                orderTakeRefreshLayout.setNoMoreData(true);
            }
            mAdapter.setNewData(list);
        }
    }


    public void getSendCarOrderList(boolean isRefresh, boolean flag) {

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
        mPresenter.getSendCarOrderList(myActivity, params, isRefresh, flag);

    }

    class MyAdapter extends BaseQuickAdapter<SendCarEntity, BaseViewHolder> {
        public MyAdapter(@Nullable List<SendCarEntity> data) {
            super(R.layout.send_car_order_item, data);
        }

        //给UI赋值
        @Override
        protected void convert(BaseViewHolder helper, SendCarEntity item) {

            int pos= helper.getAdapterPosition();
            View view_line =  helper.getView(R.id.divider);
            if (pos == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }

            helper.setText(R.id.tv_car_plate, item.getPlateNumber());
            LogUtil.i("getPlateNumberxxx",item.getPlateNumber());
            Button btnSendSMS =  helper.getView(R.id.btn_send_sms);

            //0、待处理(派车中)；1、装车中；2、已起运；3、已完成；100、取消
            if ("0".equals(item.getOrderState())) {
                helper.setText(R.id.tv_status, "待处理");
            } else if ("1".equals(item.getOrderState())) {
                helper.setText(R.id.tv_status, "装车中");
            } else if ("2".equals(item.getOrderState())) {
                helper.setText(R.id.tv_status, "已装车");
            } else if ("3".equals(item.getOrderState())) {
                helper.setText(R.id.tv_status, "起运");
            } else if ("100".equals(item.getOrderState())) {
                helper.setText(R.id.tv_status, "已取消");
            }

          //  String userId = SharedTools.getStringValue(myActivity,"userId","-1");
            String userId = "80000720";

            if ("3".equals(item.getOrderState()) && userId.equals(item.getUserId())){
                helper.getView(R.id.amount_of_payment_view).setVisibility(View.VISIBLE);
                helper.setText(R.id.cend_car_settlement_price, item.getPrice());
                helper.setText(R.id.cend_car_suttle, item.getCarryWeight());
                helper.setText(R.id.amount_of_payment_number, StringUtils.fmtMicrometer(item.getPaymentAmount()));
            }else{
                helper.getView(R.id.amount_of_payment_view).setVisibility(View.GONE);
            }

            //发送短信按钮是否显示
//            if (!currentUserPhone.equals(sendCarEntity.getDriverPhone()) && "0".equals(sendCarEntity.getOrderState())) {
//                //买家或者信息部身份进入
//                btnSendSMS.setVisibility(View.VISIBLE);
//                btnSendSMS.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showPayDialog(sendCarEntity);
//                    }
//                });
//            } else {
//                btnSendSMS.setVisibility(View.GONE);
//            }

            if ("0".equals(item.getOrderState())){
                helper.getView(R.id.tv_driver_status).setVisibility(View.GONE);
            }else{
                //司机认证状态
                if (item.getDriverAuthState().equals("2")) {
                    helper.getView(R.id.tv_driver_status).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.tv_driver_status).setVisibility(View.GONE);
                }
            }

            helper.setText(R.id.tv_time, item.getCreateTime());

            helper.setText(R.id.tv_driver_name, item.getDriverRealName());
            helper.setText(R.id.tv_phone, item.getDriverPhone());

            //提货码显示与否判断
            if ("0".equals(item.getOrderState())){
                //已送达
                helper.getView(R.id.code_view).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_code, "提货码：" + item.getCertificate());
            }else{
                helper.getView(R.id.code_view).setVisibility(View.GONE);
            }

        }


    }


}

package com.sxhalo.PullCoal.ui.freight.book;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.RouteEntity;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.common.Config;
import com.sxhalo.PullCoal.common.Constant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.ui.freight.AddBookRouteActivity;
import com.sxhalo.PullCoal.ui.freight.MyFreightActivity;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.UIHelper;
import com.sxhalo.PullCoal.weight.AppEmptyView;
import com.sxhalo.PullCoal.weight.dialog.RLAlertDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.Constant.ACCOUNT_CONFLICT;
import static com.sxhalo.PullCoal.common.Constant.REFRESH_CODE;

/**
 * 货运订阅界面布局
 */
public class BookFreightFragment extends BaseFragment <FreightBookPresenter> implements FreightBookContract.View{


    @BindView(R.id.layout_no_route)
    RelativeLayout relativeLayout;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;

    private Activity myActivity;
    private List<RouteEntity> routeEntities = new ArrayList<RouteEntity>();
    private int currentPage = 1;
    private MyRefreshReceiver myRefreshReceiver;
    private BaseQuickAdapter<RouteEntity, BaseViewHolder> baseQuickAdapter;

    final int TYPE_0 = 0;//没有订阅路线
    final int TYPE_1 = 1;//自己添加的订阅路线
    final int TYPE_2 = 2;//自己的分割线分割线
    final int TYPE_3 = 3;//推荐的订阅路线路线

    private RouteEntity delectRouteEntity; //将要删除的订阅货运

    public static Fragment newInstance() {
        return new BookFreightFragment();
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_book_freight;
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
        myActivity = getActivity();
        registerMyReceiver();
        initView();
    }

    @Override
    public void initData() {
        getNetData(true);
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        baseQuickAdapter = new BaseQuickAdapter<RouteEntity, BaseViewHolder>(R.layout.view_freight_book_list_item,routeEntities){

            @Override
            protected void convert(BaseViewHolder helper, RouteEntity item) {
                int position = helper.getLayoutPosition();
                setViewItem(helper,item,position);
            }
        };
        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (routeEntities.get(position).getType() == 1 || routeEntities.get(position).getType() == 3){
                    Map<String,Serializable> map = new HashMap<>();
                    map.put("RouteEntity",routeEntities.get(position));
                    UIHelper.jumpAct(myActivity, MyFreightActivity.class,map,false);
                }
            }
        });

        baseQuickAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                RouteEntity routeEntity = routeEntities.get(position);
                if (routeEntity.getType() == 1) {
                    showDeleteDialog(routeEntities.get(position));
                }
                return false;
            }
        });

        AppEmptyView emptyView = new AppEmptyView(getActivity(),new AppEmptyView.ClickDoView() {
            @Override
            public void onClick() {
                getNetData(true);
            }
        });
        //添加空视图
        baseQuickAdapter.setEmptyView(emptyView.getEmptyView());
        mRecyclerView.setAdapter(baseQuickAdapter);
    }

    private void setViewItem(BaseViewHolder helper, RouteEntity item, int position) {
        try {
            RelativeLayout layoutNoRoute = (RelativeLayout)helper.getView(R.id.layout_no_route);
            layoutNoRoute.setVisibility(View.GONE);

            LinearLayout bookFreightItem = (LinearLayout)helper.getView(R.id.book_freight_item);
            bookFreightItem.setVisibility(View.GONE);

            LinearLayout recommendBookFreightItem = (LinearLayout)helper.getView(R.id.recommend_book_freight_item);
            recommendBookFreightItem.setVisibility(View.GONE);

            switch (item.getType()) {
                case TYPE_0:
                    layoutNoRoute.setVisibility(View.VISIBLE);
                    helper.getView(R.id.btn_add_immediately).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(getActivity(), AddBookRouteActivity.class), REFRESH_CODE);
                        }
                    });
                    break;
                case TYPE_1:
                    bookFreightItem.setVisibility(View.VISIBLE);
                    helper.setText(R.id.book_freight_tv_start,item.getFromPlaceName());
                    helper.setText(R.id.book_freight_tv_end,item.getToPlaceName());
                    break;
                case TYPE_2:

                    break;
                case TYPE_3:
                    recommendBookFreightItem.setVisibility(View.VISIBLE);
                    helper.setText(R.id.recommend_book_freight_tv_start,item.getFromPlaceName());
                    helper.setText(R.id.recommend_book_freight_tv_end,item.getToPlaceName());

                    helper.getView(R.id.recommend_book_freight_layout_add).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (position < routeEntities.size()){
                                addBookFreight(routeEntities.get(position));
                            }
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteDialog(final RouteEntity routeEntity) {
        new RLAlertDialog(myActivity, "系统提示", getString(R.string.delete_freight_route), "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                delectTransportSubscribe(routeEntity);
            }
        }).show();
    }

    private void delectTransportSubscribe(final RouteEntity routeEntity) {
        this.delectRouteEntity = routeEntity;
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("fromPlace", routeEntity.getFromPlace());
        params.put("toPlace", routeEntity.getToPlace());
        mPresenter.getTransportSubscribeDelete(getActivity(),params,true);
    }

    private void getNetData(boolean flage) {
        try {
            if (!PaperUtil.get(AppConstant.USER_ID_KEY, "-1").equals("-1")) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("currentPage", currentPage + "");
                params.put("pageSize", "-1");
                mPresenter.getTransportSubscribeList(getActivity(), params,flage);
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                layoutBottom.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTransportSubscribeList(List<APPDataList<RouteEntity>> appDataLists, Throwable e) {
        if (appDataLists == null) {
            displayToast("网络连接失败");
            return;
        }else{
            resetData(appDataLists);
        }
    }

    /**
     * 将返回的数据重新组合
     */
    private void resetData(List<APPDataList<RouteEntity>> dataMemager) {
        routeEntities.clear();
        for (APPDataList<RouteEntity> appDataList : dataMemager) {
            if ("coal090006".equals(appDataList.getDataType())) {
                //货运订阅列表
                if (appDataList.getList().size() != 0) {
                    //有货运订阅列表
                    for (RouteEntity routeEntity : appDataList.getList()) {
                        routeEntity.setType(1);
                    }
                    routeEntities.addAll(appDataList.getList());
                    layoutBottom.setVisibility(View.VISIBLE);
                } else {
                    //没有货运订阅列表 增添一个默认无数据的item
                    addNoDataItem();
                    layoutBottom.setVisibility(View.GONE);
                }
            } else if ("coal090009".equals(appDataList.getDataType())) {
                //推荐的货运列表
                RouteEntity routeEntity = new RouteEntity();
                routeEntity.setType(2);
                routeEntities.add(routeEntity);
                //有推荐的路线
                for (RouteEntity routeEntity1 : appDataList.getList()) {
                    routeEntity1.setType(3);
                }
                routeEntities.addAll(appDataList.getList());
            }
        }
        baseQuickAdapter.notifyDataSetChanged();
    }

    /**
     * 没有货运路线时默认添加一条无数据的item
     */
    private void addNoDataItem() {
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setType(0);
        routeEntities.add(routeEntity);
    }

    /**
     * 推荐路线添加
     */
    private void addBookFreight(final RouteEntity routeEntity) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("fromPlace",routeEntity.getFromPlace());
            params.put("toPlace",routeEntity.getToPlace());
            mPresenter.getTransportSubscribeCreate(myActivity,params,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTransportSubscribeCreate(RouteEntity routeEntity, Throwable e) {
        if (routeEntity == null) {
            //网络连接失败

        }else{
            displayToast(getString(R.string.add_freight_success));
            routeEntities.add(routeEntity);
            baseQuickAdapter.notifyDataSetChanged();
            getNetData(true);
        }
    }

    @Override
    public void getTransportSubscribeDelete(String message, Throwable e) {
        if (message == null) {
            return;
        }
        displayToast(message);
        if (delectRouteEntity != null){
            routeEntities.remove(delectRouteEntity);
        }
        baseQuickAdapter.notifyDataSetChanged();
        getNetData(true);
    }

    @OnClick({R.id.btn_add_immediately, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
            case R.id.btn_add_immediately:
                String userId = PaperUtil.get(AppConstant.USER_ID_KEY, "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(myActivity);
                } else {
                    startActivityForResult(new Intent(getActivity(), AddBookRouteActivity.class), REFRESH_CODE);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REFRESH_CODE) {
            getNetData(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myActivity.unregisterReceiver(myRefreshReceiver);
    }

    /**
     * 注册登录成功后刷新界面的广播
     */
    public void registerMyReceiver() {
        myRefreshReceiver = new MyRefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(REFRESH_CODE + "");
        filter.addAction(ACCOUNT_CONFLICT);
        myActivity.registerReceiver(myRefreshReceiver, filter);
    }

    /**
     * 登录成功后的广播接收者
     */
    class MyRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((REFRESH_CODE+"").equals(intent.getAction())) {
                getNetData(true);
            }else if (ACCOUNT_CONFLICT.equals(intent.getAction())) {
                relativeLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                layoutBottom.setVisibility(View.GONE);
                routeEntities.clear();
            }
        }
    }

}

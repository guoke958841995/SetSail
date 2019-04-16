package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.AddBookRouteActivity;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.MyFreightActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.RouteEntity;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.TowButtonDialog;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.sxhalo.PullCoal.common.base.Constant.ACCOUNT_CONFLICT;
import static com.sxhalo.PullCoal.common.base.Constant.REFRESH_CODE;

/**
 * 订阅货运界面展示 implements BaseAdapterUtils.BaseAdapterBack<RouteEntity>
 */
public class BookFreightFragment extends Fragment {

    @Bind(R.id.layout_no_route)
    RelativeLayout relativeLayout;
    @Bind(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @Bind(R.id.listView)
    ListView listView;
    private Activity myActivity;
    private List<RouteEntity> routeEntities = new ArrayList<RouteEntity>();
    private int currentPage = 1;
    private MyAdapter myAdapter;
    private MyRefreshReceiver myRefreshReceiver;

    final int TYPE_0 = 0;//没有订阅路线
    final int TYPE_1 = 1;//自己添加的订阅路线
    final int TYPE_2 = 2;//自己的分割线分割线
    final int TYPE_3 = 3;//推荐的订阅路线路线

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_book_freight, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e("货运功能界面展示", e.toString());
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        myActivity.unregisterReceiver(myRefreshReceiver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        registerMyReceiver();
        initView();
        getNetData();
    }

    private void initView() {
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (routeEntities.get(position).getType() == 1 || routeEntities.get(position).getType() == 3){
                    Intent intent = new Intent(myActivity, MyFreightActivity.class);
                    intent.putExtra("RouteEntity", routeEntities.get(position));
                    startActivity(intent);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                RouteEntity routeEntity = routeEntities.get(position);
                if (routeEntity.getType() == 1) {
                    showDeleteDialog(routeEntities.get(position));
                }
                return true;
            }
        });
    }

    private void showDeleteDialog(final RouteEntity routeEntity) {
        new TowButtonDialog(myActivity, getString(R.string.delete_freight_route), "取消",
                "确定", new TowButtonDialog.Listener() {
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
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("fromPlace", routeEntity.getFromPlace());
            params.put("toPlace", routeEntity.getToPlace());
            new DataUtils(getActivity(), params).getTransportSubscribeDelete(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String message) {
                    try {
                        if (message == null) {
                            return;
                        }
                        ((BaseActivity) myActivity).displayToast(message);
                        routeEntities.remove(routeEntity);
                        myAdapter.notifyDataSetChanged();
                        getNetData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求数据
     */
    public void getNetData() {
        try {
            if (!SharedTools.getStringValue(getActivity(), "userId", "-1").equals("-1")) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("currentPage", currentPage + "");
                params.put("pageSize", "-1");
                new DataUtils(getActivity(), params).getTransportSubscribeList(new DataUtils.DataBack<List<APPDataList<RouteEntity>>>() {
                    @Override
                    public void getData(List<APPDataList<RouteEntity>> dataMemager) {
                        try {
                            if (dataMemager == null) {
                                return;
                            }
                            resetData(dataMemager);
                            ((BaseActivity)myActivity).showEmptyView(dataMemager.size(), relativeLayout, listView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                layoutBottom.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            e.printStackTrace();
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
            myAdapter.notifyDataSetChanged();
    }

    /**
     * 没有货运路线时默认添加一条无数据的item
     */
    private void addNoDataItem() {
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setType(0);
        routeEntities.add(routeEntity);
    }

    @OnClick({R.id.btn_add_immediately, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
            case R.id.btn_add_immediately:
                String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
                if (userId.equals("-1")) {
                    //未登录点击跳转登录界面
                    UIHelper.jumpActLogin(myActivity,false);
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
            getNetData();
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return routeEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return routeEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return routeEntities.get(position).getType();
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder0 viewHolder0 = null;
            ViewHolder1 viewHolder1 = null;
            ViewHolder2 viewHolder2 = null;
            ViewHolder3 viewHolder3 = null;
            int type = getItemViewType(position);
            if (convertView == null) {
                switch (type) {
                    case TYPE_0:
                        convertView = LayoutInflater.from(myActivity).inflate(R.layout.layout_no_route_item, parent, false);
                        viewHolder0 = new ViewHolder0();
                        viewHolder0.addImmediately = (Button) convertView.findViewById(R.id.btn_add_immediately);
                        convertView.setTag(viewHolder0);
                        break;
                    case TYPE_1:
                        convertView = LayoutInflater.from(myActivity).inflate(R.layout.book_freight_item, parent, false);
                        viewHolder1 = new ViewHolder1();
                        viewHolder1.tvStart = (TextView) convertView.findViewById(R.id.tv_start);
                        viewHolder1.tvEnd = (TextView) convertView.findViewById(R.id.tv_end);
                        convertView.setTag(viewHolder1);
                        break;
                    case TYPE_2:
                        convertView = LayoutInflater.from(myActivity).inflate(R.layout.devider_book_freight_item, parent, false);
                        viewHolder2 = new ViewHolder2();
                        convertView.setTag(viewHolder2);
                        break;
                    case TYPE_3:
                        convertView = LayoutInflater.from(myActivity).inflate(R.layout.recommend_book_freight_item, parent, false);
                        viewHolder3 = new ViewHolder3();
                        viewHolder3.tvStart = (TextView) convertView.findViewById(R.id.tv_start);
                        viewHolder3.tvEnd = (TextView) convertView.findViewById(R.id.tv_end);
                        viewHolder3.layoutAdd = (LinearLayout) convertView.findViewById(R.id.layout_add);
                        convertView.setTag(viewHolder3);
                        break;
                }
            } else {
                switch (type) {
                    case TYPE_0:
                        viewHolder0 = (ViewHolder0) convertView.getTag();
                        break;
                    case TYPE_1:
                        viewHolder1 = (ViewHolder1) convertView.getTag();
                        break;
                    case TYPE_3:
                        viewHolder3 = (ViewHolder3) convertView.getTag();
                        break;
                }
            }
            //设置资源
            switch (type) {
                case TYPE_0:
                    viewHolder0.addImmediately.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(getActivity(), AddBookRouteActivity.class), REFRESH_CODE);
                        }
                    });
                    break;
                case TYPE_1:
                    viewHolder1.tvStart.setText(routeEntities.get(position).getFromPlaceName());
                    viewHolder1.tvEnd.setText(routeEntities.get(position).getToPlaceName());
                    break;
                case TYPE_3:
                    viewHolder3.tvStart.setText(routeEntities.get(position).getFromPlaceName());
                    viewHolder3.tvEnd.setText(routeEntities.get(position).getToPlaceName());
                    viewHolder3.layoutAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (position < routeEntities.size()){
                                addBookFreight(routeEntities.get(position));
                            }
                        }
                    });
                    break;
            }
            return convertView;
        }
    }

    //各个布局的控件资源
    class ViewHolder0 {
        private Button addImmediately;
    }

    class ViewHolder1 {
        private TextView tvStart;
        private TextView tvEnd;
    }

    class ViewHolder2 {
    }

    class ViewHolder3 {
        private TextView tvStart;
        private TextView tvEnd;
        private LinearLayout layoutAdd;
    }


    /**
     * 推荐路线添加
     */
    private void addBookFreight(final RouteEntity routeEntity) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("fromPlace",routeEntity.getFromPlace());
            params.put("toPlace",routeEntity.getToPlace());
            new DataUtils(myActivity,params).getTransportSubscribeCreate(new DataUtils.DataBack<RouteEntity>() {
                @Override
                public void getData(RouteEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        ((BaseActivity)myActivity).displayToast(getString(R.string.add_freight_success));
                        routeEntities.add(routeEntity);
                        myAdapter.notifyDataSetChanged();
                        getNetData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册登录成功后刷新界面的广播
     */
    public void registerMyReceiver() {
        myRefreshReceiver = new MyRefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Constant.REFRESH_CODE + "");
        filter.addAction(ACCOUNT_CONFLICT);
        myActivity.registerReceiver(myRefreshReceiver, filter);
    }

    /**
     * 登录成功后的广播接收者
     */
    class MyRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constant.REFRESH_CODE+"").equals(intent.getAction())) {
                getNetData();
            }else if (ACCOUNT_CONFLICT.equals(intent.getAction())) {
                relativeLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                layoutBottom.setVisibility(View.GONE);
                routeEntities.clear();
            }
        }
    }
}

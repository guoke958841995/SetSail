package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.BuyCoalActivity;
import com.sxhalo.PullCoal.activity.DriverListAcrivity;
import com.sxhalo.PullCoal.activity.GoingToPullCoalActivity;
import com.sxhalo.PullCoal.activity.InformationDepartmentListActivity;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.FriendActivity;
import com.sxhalo.PullCoal.activity.MessageCenterActivity;
import com.sxhalo.PullCoal.activity.MineProductListActivity;
import com.sxhalo.PullCoal.activity.MyOrderActivity;
import com.sxhalo.PullCoal.activity.MyTransportOrdersListActivity;
import com.sxhalo.PullCoal.activity.PurchasingReservationActivity;
import com.sxhalo.PullCoal.adapter.MyViewPagerAdapter;
import com.sxhalo.PullCoal.model.Function;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amoldZhang on 2016/12/7.
 */
public class FunctionListFragment extends Fragment implements MyViewPagerAdapter.UpdateAble{

    @Bind(R.id.funiction_gv)
    GridView funictionGv;

    private Activity myActivity;
    private List<Map<String, String>> data_list = new ArrayList<Map<String, String>>();
    private QuickAdapter<Map<String, String>> simAdapter;

//    // 图片封装为一个数组
//    private int[] icon = { R.mipmap.ic_launcher, R.mipmap.ic_launcher,
//            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
//            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher };
//    private String[] iconName = { "通讯录", "日历", "照相机", "时钟", "游戏", "短信", "铃声",
//            "设置" };

    List<Function> functionList = new ArrayList<Function>();
    private int index;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            functionList = (List<Function>)getArguments().getSerializable("functionList");
            index = getArguments().getInt("index");
        }catch (Exception e){
            GHLog.e("Bundle传值",e.toString());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_funiction_list_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        initView();
        upDataView();
    }

    @Override
    public void update(Bundle data) {
        functionList = (List<Function>)data.getSerializable("functionList");
        index = data.getInt("index");

        upDataView();
    }

    private void upDataView(){
        data_list.clear();
        //获取数据
        data_list = getData();
        simAdapter.replaceAll(data_list);
    }

    private void initView() {
        //新建适配器
        simAdapter = new QuickAdapter<Map<String, String>>(myActivity, R.layout.function_list_item,data_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> shop, final int pos) {
                try{
                    helper.getView().findViewById(R.id.function_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectActivity(pos);
                        }
                    });
                    ImageView funictionIV = (ImageView)helper.getView().findViewById(R.id.funiction_item_iv);
//                    funictionIV.setImageBitmap(null);
                    ((BaseActivity)myActivity).getImageManager().loadUrlImageView(shop.get("image"),funictionIV);
                    helper.setText(R.id.funiction_item_tv, shop.get("name"));
                }catch (Exception e){
                    GHLog.e("适配器赋值",e.toString());
                }
            }
        };
        //配置适配器
        funictionGv.setAdapter(simAdapter);
        funictionGv.setNumColumns(4);
    }

    private void selectActivity(int position) {
        int functionID = Integer.valueOf(data_list.get(position).get("id"));
        switch (functionID) {
            case 1: //我要拉煤
                togo(GoingToPullCoalActivity.class,-1);
                break;
            case 2: //接单拉煤
//                togo(PullCoalActivity.class,0);
                ((MainActivity) myActivity).setShowFragment(true);
                break;
            case 3: //我要买煤
                togo(BuyCoalActivity.class, -1);
                break;
            case 5:  //找司机
                Intent myIntent = new Intent();
                myIntent.setClass(myActivity, DriverListAcrivity.class);
                myIntent.putExtra("Invite", "driver");
                myIntent.putExtra("title", "找司机");
                startActivity(myIntent);
                break;
            case 6:  //我的好友
                togo(FriendActivity.class, 0);
                break;
            case 7:  //查找信息部
//                togo(InformationDepartmentActivity.class,-1);
                // 点击后跳转到查找信息部列表界面
//                UIHelper.showFindPunctuation(myActivity, SearchPunctuationActivity.class,"查信息部","");
                UIHelper.jumpAct(myActivity, InformationDepartmentListActivity.class, false);
                break;
            case 8: //查找矿口
//                togo(DiscoveryMineActivity.class,-1);
//                UIHelper.showFindPunctuation(myActivity, SearchPunctuationActivity.class,"查找矿口","");
                UIHelper.jumpAct(myActivity, MineProductListActivity.class, false);
                break;
            case 9:  //资料认证
//                togo(DataAuthenticationActivity.class,0);
                break;
            case 10:  // 消息中心
                togo(MessageCenterActivity.class, -1);
                break;
            case 11:  //活动中心
                ((MainActivity) myActivity).displayToast("暂未开放，敬请期待！");
//                togo(DiscoveryMineActivity.class,-1);
                break;
            case 12:  //帮助与反馈

                break;
            case 13:  // 我的订单
                togo(MyOrderActivity.class, 0);
                break;
            case 14:  // 我的货运单列表
                togo(MyTransportOrdersListActivity.class, 0);
                break;
            case 15:  //买煤求购
                togo(PurchasingReservationActivity.class, -1);
                break;
            case 16:  //打开快煤宝或打开快煤宝下载页
                    if (BaseUtils.isAppInstalled(myActivity, "com.kmbao")) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.putExtra("userId", SharedTools.getStringValue(myActivity, "user_mobile", "-1"));
                        ComponentName cn = new ComponentName("com.kmbao", "com.kmbao.MainActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.setComponent(cn);
                        startActivity(intent);
                    } else {
                        // 快煤宝没安装
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse("http://www.17lm.com/web/appDownload.html");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                break;
        }
    }

    private void togo(Class toActivity,int index){
        String userId = SharedTools.getStringValue(myActivity,"userId","-1");
        Intent myIntent = new Intent();
        if (!userId.equals("-1")){
            myIntent.setClass(myActivity,toActivity);
            myActivity.startActivity(myIntent);
        }else{
            if (index == 0){
                UIHelper.jumpActLogin(myActivity,false);
            }else{
                myIntent.setClass(myActivity,toActivity);
                myActivity.startActivity(myIntent);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public List<Map<String, String>> getData(){
        try {
            int count = index * 8;
            int length = count + 8;
            if(length >= functionList.size()){
                length = functionList.size();
            }
            for(int i = count;i<length;i++){
                Map<String, String> map = new HashMap<String, String>();
                map.put("image", functionList.get(i).getImageUrl());
                map.put("name", functionList.get(i).getModuleName());
                map.put("id", functionList.get(i).getModuleId());
                data_list.add(map);
            }
            //当数据不够一屏时，将数据补齐
            if (data_list.size()%8 != 0){
                setVerification();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data_list;
    }

    /**
     * 将数据补满一屏
     */
    private void setVerification(){
        int k = 8 - (data_list.size()%8);
        for (int i = 0;i<k;i++){
            Map<String, String> map = new HashMap<String, String>();
            map.put("image", "0");
            map.put("name", " ");
            map.put("id", "0");
            data_list.add(map);
        }
    }

}

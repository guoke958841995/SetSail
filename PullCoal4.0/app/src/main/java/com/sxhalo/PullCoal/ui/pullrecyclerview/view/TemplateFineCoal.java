//package com.sxhalo.PullCoal.ui.pullrecyclerview.view;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.TextView;
//
//import com.sxhalo.PullCoal.R;
//import com.sxhalo.PullCoal.activity.BaseActivity;
//import com.sxhalo.PullCoal.activity.BuyCoalActivity;
//import com.sxhalo.PullCoal.activity.DetailsWebActivity;
//import com.sxhalo.PullCoal.model.Coal;
//import com.sxhalo.PullCoal.model.HomeData;
//import com.sxhalo.PullCoal.ui.UIHelper;
//import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;
//import com.sxhalo.PullCoal.utils.GHLog;
//import com.sxhalo.PullCoal.utils.SharedTools;
//import com.zhy.adapter.recyclerview.CommonAdapter;
//import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
//import com.zhy.adapter.recyclerview.base.ViewHolder;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// *  精品煤炭布局
// * Created by amoldZhang on 2018/12/18.
// */
//public class TemplateFineCoal extends BaseView{
//
//    @Bind(R.id.recycler_view)
//    RecyclerView recyclerView;
//
//    private List<Coal> myListData = new ArrayList<>();
//    private CommonAdapter<Coal> recyAdapter;
//    private View mRootView;
//    public TemplateFineCoal(Activity context) {
//        super(context);
//        initView();
//    }
//
//    private void initView() {
//        mRootView = View.inflate(mContext, R.layout.header_horizontal_view, null);
//        ButterKnife.bind(this, mRootView);
//
//        LinearLayoutManager ms= new LinearLayoutManager(mContext);
//        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
//        // LinearLayoutManager 种 含有3 种布局样式  第一个就是最常用的 1.横向 , 2. 竖向,3.偏移
//        recyclerView.setLayoutManager(ms); //给RecyClerView 添加设置好的布局样式
//
//        recyAdapter = new CommonAdapter<Coal>(mContext, R.layout.fragment_fine_coal_item, myListData){
//            @Override
//            protected void convert(ViewHolder holder, Coal coals, int position){
////                if (position == 0){
////                    holder.getView(R.id.left_view).setVisibility(View.VISIBLE);
//////                    holder.getView(R.id.more_coal_list_title).setVisibility(View.VISIBLE);
////                    holder.getView(R.id.left_view).setOnClickListener(new OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////
////                        }
////                    });
////                    holder.getView(R.id.more_coal_list_title).setOnClickListener(new OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////
////                        }
////                    });
////                }else{
////                    holder.getView(R.id.left_view).setVisibility(View.GONE);
////                    holder.getView(R.id.more_coal_list_title).setVisibility(View.GONE);
////                }
//
////                if (myListData.size() != 0 && myListData.size()-1 == position){
////                    holder.getView(R.id.more_coal_list).setVisibility(View.VISIBLE);
////                    holder.getView(R.id.more_coal_list).setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            Intent myIntent = new Intent();
////                            myIntent.setClass(mContext, BuyCoalActivity.class);
////                            mContext.startActivity(myIntent);
////                        }
////                    });
////                }else{
////                    holder.getView(R.id.more_coal_list).setVisibility(View.GONE);
////                }
//
//                holder.setText(R.id.coal_title,coals.getCoalName());
//                holder.setText(R.id.coal_type_grid,"(" + coals.getCoalCategoryName() + ")");
//                holder.setText(R.id.coal_hot,"热量：" + coals.getCalorificValue().replace(".0","") + "卡");
//                holder.setText(R.id.coal_moth,"矿口：" + coals.getMineMouthName());
//
//                TextView typeText = (TextView)holder.getView(R.id.free_type);
//                typeText.setVisibility(View.VISIBLE);
//                String payName = "";
//                //当资讯信息是免费的
//                if (coals.getConsultingFee().equals("0")) {
//                    holder.getView(R.id.coal_price).setVisibility(View.VISIBLE);
//                    holder.getView(R.id.if_fell).setVisibility(View.GONE);
//
//                    typeText.setText("免费信息");
//                } else {
//                    //当前登录用户和发布人同属于一个信息部
//                    if (SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coals.getCoalSalesId())){
//                        payName = "" + "¥" + (Double.valueOf(coals.getConsultingFee()) / 100); //付费信息
//                        holder.getView(R.id.coal_price).setVisibility(View.GONE);
//                        holder.getView(R.id.if_fell).setVisibility(View.VISIBLE);
//                        typeText.setText("发布人："+ coals.getPublishUser());
//                    }else if (coals.getIsPay().equals("1")&& coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {  //0未支付，1已支付
//                        //当资讯信息已经付过费
//                        payName = "(已支付)收费信息";
//                        holder.getView(R.id.if_fell).setVisibility(View.VISIBLE);
//                        holder.getView(R.id.coal_price).setVisibility(View.VISIBLE);
//                        typeText.setText(coals.getLicenseMinute());
//                    } else {
//                        //当资讯信息尚未付费
//                        payName = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + ""; // 收费信息
//                        holder.getView(R.id.if_fell).setVisibility(View.VISIBLE);
//                        holder.getView(R.id.coal_price).setVisibility(View.GONE);
//                        if (coals.getLicenseMinute().contains("已失效")){
//                            typeText.setText("支付后可查看更多");
//                        }else{
//                            typeText.setText(coals.getLicenseMinute().contains("已失效")?"支付后可查看更多":coals.getLicenseMinute());
//                        }
//                    }
//                }
//                holder.setText(R.id.coal_price, "¥"+coals.getOneQuote());
//                holder.setText(R.id.if_fell, payName);
//            }
//        };
//
//        recyAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                try {
//                    Coal coal = myListData.get(position);
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, DetailsWebActivity.class);
//                    intent.putExtra("Coal", coal);
//                    intent.putExtra("inforDepartId", "煤炭详情");
//                    String userId = SharedTools.getStringValue(mContext, "userId", "-1");
//                    //免费信息
//                    if (userId.equals("-1")) {
//                        //未登录 先去登录
//                        UIHelper.jumpActLogin(mContext,false);
//                    } else {
//                    if (coal.getConsultingFee().equals("0") ||
//                            SharedTools.getStringValue(mContext,"coalSalesId","-1").equals(coal.getCoalSalesId())) {
//                        //免费信息
//                            mContext.startActivity(intent);
//                    } else {
//                            //已登录 判断是否支付
//                            if (coal.getIsPay().equals("1")&& coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
//                                //已支付 直接查看
//                                mContext.startActivity(intent);
//                            } else {
//                                //未支付 弹框提示
//                                ((BaseActivity) mContext).showPayDialog(coal, "0");
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    GHLog.e("煤炭列表点击", e.toString());
//                }
//            }
//
//            @Override
//            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
//                return false;
//            }
//        });
//        recyclerView.setAdapter(recyAdapter);
//        addTemplateView();
//    }
//
//    @Override
//    public void setData(BaseModule module) {
//        fillData(module);
//    }
//
//    @Override
//    public void fillData(BaseModule module) {
//        if (module == null || !(module instanceof HomeData))
//        {
//            return;
//        }
//
//        if (((HomeData) module).getCoalList().size() != 0){
//            mRootView.setVisibility(VISIBLE);
//            myListData.clear();
//            recyAdapter.notifyDataSetChanged();
//            myListData.addAll(((HomeData) module).getCoalList());
//            recyAdapter.notifyDataSetChanged();
//        }else {
//            mRootView.setVisibility(GONE);
//            return;
//        }
////        this.setOnClickListener(new OnClickListener()
////        {
////            @Override
////            public void onClick(View view)
////            {
////                onItemClick(module);
////            }
////        });
//        invalidate();
//    }
//
//    @Override
//    public void addTemplateView() {
////        LayoutParams lp = new LayoutParams(-1, (int) (mContext.getResources().getDisplayMetrics().density * 40));
////        mRootView.setLayoutParams(lp);
//        addView(mRootView);
//    }
//
//    @Override
//    public void reFresh() {
//
//    }
//}

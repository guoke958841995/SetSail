package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.MyLayoutManage;
import com.sxhalo.PullCoal.adapter.RecyclerviewHorizontalViewAdapter;
import com.sxhalo.PullCoal.adapter.SpacingItemDecoration;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.CouponsEntity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.PayMent;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 *  代金券选择界面
 * Created by amoldZhang on 2018/1/4.
 */
public class PaymentConfirmationActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;

    //RecyclerView  实现方式
    @Bind(R.id.id_recyclerview_horizontal)
    RecyclerView recycLerviewHorizontal;

    private PayMent data;
    private ArrayList<CouponsEntity> couponsEntityList = new ArrayList<CouponsEntity>();

    private  RecyclerviewHorizontalViewAdapter adapter;


    private int deductibleAmoount; //抵扣券总额
    private int inforCost; //资讯费金额

    private String couponId = ""; //当前选用代金券的id
    private String usePage = ""; //当前选用代金券的张数
    private int appropriateAmount; //默认计算出的合理使用金额

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_payment_confirmation);
    }

    @Override
    protected void initTitle() {
        title.setText("代金券");
        data = (PayMent)getIntent().getSerializableExtra("PayMent");

        if (!"0".equals(data.getAvailableQuantity().toString())){
            mapType.setText("获取更多");
            mapType.setVisibility(View.VISIBLE);
        }else{
            mapType.setVisibility(View.GONE);
        }

        ArrayList<CouponsEntity> list = (ArrayList<CouponsEntity>)getIntent().getSerializableExtra("couponsEntityList");
        couponsEntityList.clear();
        couponsEntityList.addAll(list);
    }

    @Override
    protected void getData() {
        inforCost = Integer.valueOf(data.getCostAmount());
        getCoupons();
    }

    private void getCoupons(){
        if (couponsEntityList.size() != 0){
            appropriateAmount = data.getAppropriateAmount();
            setView(false);
            setVoucherView();
        }else {
            getPaymentCoupons(true); //使用默认代金券的计算方式
        }
    }

    /**
     *  联网重新获取我可使用的代金券
     * @param flage 值为false时，不执行数据默认选择，否则，使用当前已经选择的数据
     */
    private void getPaymentCoupons(final boolean flage) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("consultingFeeType",data.getConsultingFeeType());
            params.put("targetId",data.getTargetId());
            params.put("currentPage", "1");
            params.put("pageSize", "-1");
            new DataUtils(this,params).getUserCouponList(new DataUtils.DataBack<APPData<CouponsEntity>>() {

                @Override
                public void getData(APPData<CouponsEntity> couponsEntity) {
                    try {
                        List<CouponsEntity> couponsList = new ArrayList<>();
                        if (couponsEntityList.size() != 0){
                            couponsList.addAll(couponsEntityList);
                            couponsEntityList.clear();
                            adapter.notifyDataSetChanged();
                        }

                        //去掉当前可使用张数为0 的数据
                        if (couponsEntity.getList() != null){
                            for (CouponsEntity entity : couponsEntity.getList()){
                                if (!entity.getNumber().equals("0")){
                                    couponsEntityList.add(entity);
                                }
                            }
                        }

                        if (couponsEntityList.size() != 0){
                            initViewDataConfirmation(couponsList,flage);
                            setView(flage);
                            if (adapter == null){
                                setVoucherView();
                            }else{
                                adapter.notifyDataSetChanged();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 代金券确认支付界面初始化
     */
    private void setVoucherView() {
        try {
        /*
          传入所有列数的最小公倍数，1和4的最小公倍数为4，即意味着每一列将被分为4格
        */
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, couponsEntityList.size());
            //设置表格，根据position计算在该position处1列占几格数据
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override public int getSpanSize(int position) {
                    //计算在哪个position时要显示1列数据，即columnCount / 1列 = 4格，即1列数据占满4格
                    if (position == 0 || position == couponsEntityList.size()) {
                        return couponsEntityList.size();
                    }
                    //return 2即为columnCount / 2 = 2列，一格数据占2列，该行显示2列

                    //1格1列，即改行有columnCount / 1 = 4列，该行显示4列
                    return 1;
                }
            });
            recycLerviewHorizontal.setLayoutManager(gridLayoutManager);  // 设置列数量=列表集合数

            int length = BaseUtils.getWindowsWidth(this);
            // 得到像素密度
            DisplayMetrics outMetrics = new DisplayMetrics();
            mContext.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            float density = outMetrics.density; // 像素密度
            int itemWidth = (int) (length * density);

            //设置布局管理器
            MyLayoutManage linearLayoutManager = new MyLayoutManage(this,itemWidth);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycLerviewHorizontal.setLayoutManager(linearLayoutManager);

            recycLerviewHorizontal.addItemDecoration(new SpacingItemDecoration(this,BaseUtils.dip2px(this,15f)));//间隔为12dp

            adapter = new RecyclerviewHorizontalViewAdapter(mContext, couponsEntityList);
            adapter.setOnItemClickListener(new RecyclerviewHorizontalViewAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position){
                }

                @Override
                public void onItemAddClick(RecyclerviewHorizontalViewAdapter.ViewHolder holder, int position) {
                    CouponsEntity payMent = couponsEntityList.get(position);
                    int usePage = payMent.getUsePage();
                    if (usePage != Integer.valueOf(payMent.getNumber())){
                        usePage ++;
                        payMent.setUsePage(usePage);
                        payMent.setCurrentAmountUse(usePage * Integer.valueOf(payMent.getDenomination()));
                        holder.voucherSelectedNumber.setText(usePage + "");
                    }
                    setView(false);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onItemReduceClick(RecyclerviewHorizontalViewAdapter.ViewHolder holder, int position) {
                    CouponsEntity payMent = couponsEntityList.get(position);
                    int usePage = payMent.getUsePage();
                    if (usePage != 0){
                        usePage --;
                        payMent.setUsePage(usePage);
                        payMent.setCurrentAmountUse(usePage * Integer.valueOf(payMent.getDenomination()));
                        holder.voucherSelectedNumber.setText(usePage + "");
                    }
                    setView(false);
                    adapter.notifyDataSetChanged();
                }
            });
            recycLerviewHorizontal.setAdapter(adapter);
        } catch (Exception e) {
            GHLog.e("界面初始化",e.toString());
        }
    }

    /**
     * 刷新当前界面数据显示
     * @param flage 是否是默认数据计算
     */
    private void setView(boolean flage){
        deductibleAmoount = 0;
        couponId = "";
        usePage = "";
        for (CouponsEntity couponsEntity : couponsEntityList){
            if (couponsEntity.getCurrentAmountUse() != 0){
                if (StringUtils.isEmpty(couponId)){
                    couponId = couponsEntity.getCouponId();
                    usePage = couponsEntity.getUsePage() + "";
                }else{
                    couponId = couponId + "," + couponsEntity.getCouponId();
                    usePage = usePage + "," + couponsEntity.getUsePage();
                }
            }
            deductibleAmoount = deductibleAmoount + couponsEntity.getCurrentAmountUse();
        }
        if (flage){
            appropriateAmount = deductibleAmoount;
        }
    }

    /**
     * 初始化当前使用代金卷显示布局
     * @param flage  为true 时 保持用户选择，为false时 并且用户有选择代金券时，智能计算代金券的使用
     */
    private void initViewDataConfirmation(List<CouponsEntity> couponsList,boolean flage) {
        if (!flage && couponsList.size() != 0){
            //保持用户原来选择的代金券不变
            for (CouponsEntity entity : couponsList){
                for (int i = 0;i < couponsEntityList.size(); i++){
                    if (entity.getCouponId().equals(couponsEntityList.get(i).getCouponId())){
                        couponsEntityList.set(i,entity);
                    }
                }
            }
        }else{
            int surplusAmount = inforCost;
            for (CouponsEntity couponsEntity : couponsEntityList){
                if (surplusAmount <= 0){
                    break;
                }
                int totalCost = Integer.valueOf(couponsEntity.getDenomination()) * Integer.valueOf(couponsEntity.getNumber());

                if (surplusAmount >= totalCost){
                    couponsEntity.setUsePage(Integer.valueOf(couponsEntity.getNumber()));
                    couponsEntity.setCurrentAmountUse(totalCost);
                    surplusAmount = surplusAmount - totalCost;
                    continue;
                }else if (surplusAmount < totalCost){
                    int pageSize =  surplusAmount/Integer.valueOf(couponsEntity.getDenomination());
                    if (surplusAmount %  Integer.valueOf(couponsEntity.getDenomination()) > 0){
                        pageSize ++ ;
                    }
                    couponsEntity.setUsePage(pageSize);
                    couponsEntity.setCurrentAmountUse(pageSize * Integer.valueOf(couponsEntity.getDenomination()));
                    surplusAmount = surplusAmount - pageSize * Integer.valueOf(couponsEntity.getDenomination());
                }
            }
        }
    }


    @OnClick({R.id.title_bar_left, R.id.goto_payment,R.id.map_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                // 获取更多优惠券
                Intent intent = new Intent(PaymentConfirmationActivity.this,GetMoreCouponsActivity.class);
                startActivityForResult(intent, Constant.REFRESH_CODE);
                break;
            case R.id.goto_payment:
                if (deductibleAmoount - appropriateAmount > 0) {
                    showDaiLog("您当前使用代金券的抵扣金额已超出需要支付的金额，是否确认使用？");
                } else {
                    doCouponPayment();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK && requestCode == Constant.REFRESH_CODE) {
            getPaymentCoupons(false);  //不使用默认代金券的计算方式
        }
    }

    private void showDaiLog(String message) {
        new RLAlertDialog(this, "系统提示", message, "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                doCouponPayment();
            }
        }).show();
    }

    /**
     * 代金券全额支付
     */
    private void doCouponPayment() {
        try {
            HashMap<String, String> selectParams = (HashMap<String, String>)getIntent().getSerializableExtra("selectParams");
            selectParams.put("targetId", data.getTargetId());
            selectParams.put("consultingFeeType", data.getConsultingFeeType());
            selectParams.put("couponId", couponId);
            selectParams.put("useNumber", usePage);
            selectParams.put("appropriateAmount", appropriateAmount + "");

            Intent intent = new Intent(this,PaymentInformationActivity.class);
            intent.putExtra("selectParams",selectParams);
            intent.putExtra("couponsEntityList",couponsEntityList);
            intent.putExtra("deductibleAmoount",deductibleAmoount); //代金券抵扣总金额

            setResult(RESULT_OK,intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

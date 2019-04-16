package com.sxhalo.PullCoal.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.CouponsEntity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 获取更多优惠券
 * Created by liz on 2018/1/6.
 */
public class GetMoreCouponsActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.custom_list_view)
    CustomListView customListView;

    private List<CouponsEntity> couponsEntityList = new ArrayList<>();
    private QuickAdapter<CouponsEntity> mAdaptet;
    private boolean flage = false; //是否反回刷新界面

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_get_more_coupons);
    }

    @Override
    protected void initTitle() {
        title.setText("获取更多代金券");
        initView();
    }

    @Override
    public void getData() {
        try {
            new DataUtils(this).getUserCouponMore(new DataUtils.DataBack<APPData<CouponsEntity>>() {

                @Override
                public void getData(APPData<CouponsEntity> couponsEntity) {
                    try {
                        if (couponsEntity.getList().size() != 0) {
                            mAdaptet.replaceAll(couponsEntity.getList());
                            couponsEntityList = couponsEntity.getList();
                        }
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    private void initView(){
        mAdaptet = new QuickAdapter<CouponsEntity>(mContext, R.layout.item_coupons, couponsEntityList) {
            @Override
            protected void convert(BaseAdapterHelper helper, CouponsEntity couponsEntity, final int pos) {
                try {
                    helper.setText(R.id.coupons_title_tv,couponsEntity.getTitle());
                    TextView tvStatusLeft = (TextView)helper.getView().findViewById(R.id.tv_status_left);
                    TextView tvStatus = (TextView)helper.getView().findViewById(R.id.tv_status);
                    if (couponsEntity.getTitle().equals("注册登录")){
                        setDataView(couponsEntity,tvStatusLeft,tvStatus,true);
                    }else{
                        setDataView(couponsEntity,tvStatusLeft,tvStatus,false);
                    }
                    if (pos == couponsEntityList.size() - 1){
                        helper.getView().findViewById(R.id.line_view).setVisibility(View.GONE);
                    }else{
                        helper.getView().findViewById(R.id.line_view).setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        customListView.setAdapter(mAdaptet);
        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CouponsEntity couponsEntity = mAdaptet.getItem(position);
                TextView tvStatusLeft = (TextView)view.findViewById(R.id.tv_status_left);
                String tvStatus = couponsEntity.getIndex();
                gotoNext(tvStatusLeft,tvStatus);
            }
        });
    }

    private void gotoNext(TextView tvStatusLeft, String tvStatus) {
        if (tvStatusLeft.getText().toString().contains("未获得")){
            if (tvStatus.equals("1")){
                displayToast("还未发放请耐心等待");
            }else if (tvStatus.equals("2")){
                //登陆后跳转到要去的界面
                UIHelper.jumpAct(this, BuyerCertificationActivity.class, false);
            }else{
                UIHelper.jumpAct(this, DriverCertificationActivity.class, false);
            }
        }else if (tvStatusLeft.getText().toString().contains("未领取")){
            getUserCouponObtainType(tvStatus);
        }else if (tvStatusLeft.getText().toString().contains("已领取")){
            displayToast("您已经领取过了");
        }
    }

    /**
     *
     * @param couponsEntity
     * @param leftTV
     * @param rightTv
     */
    private void setDataView(CouponsEntity couponsEntity,TextView leftTV,TextView rightTv,boolean flage){
        int status = Integer.valueOf(couponsEntity.getStatus());  //状态：0 未获得 1 未领取 2 已领取
        switch (status){
            case 0:
//                Drawable img0 = mContext.getResources().getDrawable(R.mipmap.icon_about_app);
//                // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//                img0.setBounds(0, 0, img0.getMinimumWidth(), img0.getMinimumHeight());
//                leftTV.setCompoundDrawables(img0,null,null,null);
                if (flage){
                    leftTV.setText(couponsEntity.getNumber() + "个(未获得)");
                    rightTv.setText("");
                }else{
                    leftTV.setText(couponsEntity.getNumber() + "个(未获得)");
                    rightTv.setText("   去完善");
                }
                leftTV.setTextColor(getResources().getColor(R.color.button_golden_lg));
                Drawable imgRight0 = mContext.getResources().getDrawable(R.mipmap.arrow_listpage);
                // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                imgRight0.setBounds(0, 0, imgRight0.getMinimumWidth(), imgRight0.getMinimumHeight());
                rightTv.setCompoundDrawables(null,null,imgRight0,null);
                break;
            case 1:
//                Drawable img1 = mContext.getResources().getDrawable(R.mipmap.icon_about_app);
//                // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//                img1.setBounds(0, 0, img1.getMinimumWidth(), img1.getMinimumHeight());
//                leftTV.setCompoundDrawables(img1,null,null,null);
                leftTV.setText(couponsEntity.getNumber() + "个(未领取)");
                rightTv.setText("");
                leftTV.setTextColor(getResources().getColor(R.color.button_golden_lg));
                rightTv.setCompoundDrawables(null,null,null,null);
                break;
            case 2:
//                Drawable img3 = mContext.getResources().getDrawable(R.mipmap.icon_about_app);
//                // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//                img3.setBounds(0, 0, img3.getMinimumWidth(), img3.getMinimumHeight());
//                leftTV.setCompoundDrawables(img3,null,null,null);
                leftTV.setText(couponsEntity.getNumber() + "个(已领取)");
                rightTv.setText("");
                leftTV.setTextColor(getResources().getColor(R.color.search_text_color_green));
                rightTv.setCompoundDrawables(null,null,null,null);
                break;
        }
    }




    @OnClick({R.id.title_bar_left, R.id.btn_one_key})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                onBackPressed();
                break;
            case R.id.btn_one_key:
                boolean flage = false;
                for (CouponsEntity couponsEntity : couponsEntityList){
                    if (couponsEntity.getStatus().equals("1")){
                        flage = true;
                        break;
                    }
                }
                if (flage){
                    getUserCouponObtainType("0");
                }else{
                    displayToast("您当前没有可领取的代金券！");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (flage){
            setResult(RESULT_OK);
        }
        finish();
    }

    /**
     * 领取优惠卷
     * 领用类型：0全部、1注册、2实名认证、3司机认证40%,4司机认证60%、5司机认证90%、6司机认证100%
     */
    private void getUserCouponObtainType(final String obtainType){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("obtainType", obtainType);
            new DataUtils(this,params).getUserCouponObtainType(new DataUtils.DataBack<APPData<CouponsEntity>>() {

                @Override
                public void getData(APPData<CouponsEntity> couponsEntity) {
                    try {
                        if (!"0".equals(couponsEntity.getObtainQuantity())){
                            flage = true;
                        }
                        if (couponsEntity.getList().size() != 0) {
                            mAdaptet.replaceAll(couponsEntity.getList());
                            couponsEntityList = couponsEntity.getList();

                        }else{
                            onBackPressed();
                        }
                        if ("0".equals(couponsEntity.getObtainQuantity())){
                            displayToast("代金券已被领完或领取未成功");
                        }else{
                            flage = true;
                            displayToast("已成功领取 "+couponsEntity.getObtainQuantity()+" 张！");
                        }
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("服务器繁忙，请稍候再试！");
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

}

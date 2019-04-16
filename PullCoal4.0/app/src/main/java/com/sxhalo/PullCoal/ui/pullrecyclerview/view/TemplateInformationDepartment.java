package com.sxhalo.PullCoal.ui.pullrecyclerview.view;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.DetaileInformationDepartmentActivity;
import com.sxhalo.PullCoal.activity.InformationDepartmentListActivity;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 信息部列表展示
 * Created by amoldZhang on 2018/12/18.
 */

public class TemplateInformationDepartment extends BaseView{

    @Bind(R.id.recommend_title)
    TextView recommendTitle;
    @Bind(R.id.home_recommend_lv)
    CustomListView homeRecommendLv;
    private View mRootView;
    private QuickAdapter<InformationDepartment> mAdapter;
    private List<InformationDepartment> informationDepartments = new ArrayList<>();

    public TemplateInformationDepartment(Activity context) {
        super(context);
        initView();
    }

    private void initView() {
        mRootView = View.inflate(mContext, R.layout.home_pager_item, null);
        ButterKnife.bind(this, mRootView);

        dealWithTheView();
        addTemplateView();
    }

    @Override
    public void setData(BaseModule module) {
        fillData(module);
    }

    @Override
    public void fillData(BaseModule module) {
        if (module == null || !(module instanceof HomeData))
        {
            return;
        }

        if (((HomeData) module).getInfoDepartsList().size() != 0){
            mAdapter.replaceAll(((HomeData) module).getInfoDepartsList());
        }
//        this.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onItemClick(module);
//            }
//        });
        invalidate();
    }

    @Override
    public void addTemplateView() {
//        LayoutParams lp = new LayoutParams(-1, (int) (mContext.getResources().getDisplayMetrics().density * 40));
//        mRootView.setLayoutParams(lp);
        addView(mRootView);
    }

    @Override
    public void reFresh() {

    }

    private void dealWithTheView() {
        GHLog.i("信息部展示", informationDepartments.size() + "");
        recommendTitle.setText("推荐信息部");
        mAdapter = new QuickAdapter<InformationDepartment>(mContext, R.layout.home_recommend_item, informationDepartments) {
            @Override
            protected void convert(BaseAdapterHelper helper, InformationDepartment item, int position) {
                try {
                    RelativeLayout relativeLayout = (RelativeLayout) helper.getView().findViewById(R.id.recommend_infoDeparts_rl);
                    relativeLayout.setVisibility(View.VISIBLE);
                    helper.setText(R.id.recommend_name, item.getCompanyName());
                    helper.setText(R.id.recommend_postal_address_v, item.getAddress());
                    ResetRatingBar recommend_ratingBar = (ResetRatingBar) helper.getView().findViewById(R.id.recommend_room_ratingbar1);
                    recommend_ratingBar.setStar(Integer.valueOf(StringUtils.isEmpty(item.getCreditRating())?"1":item.getCreditRating()));
                    ((BaseActivity) mContext).getImageManager().loadMinebgUrlImage(item.getCoalSalePic(), (ImageView) helper.getView().findViewById(R.id.recommend_IV));

                    helper.setText(R.id.coal_size, item.getGoodsTotal()+"个");
                    helper.setText(R.id.freight_size, item.getTransTotal() + "个");
                    ImageView mineBusinessType = (ImageView)helper.getView().findViewById(R.id.recommend_business_type);
                    TextView doBusinessTime = (TextView)helper.getView().findViewById(R.id.recommend_do_business_time);
                    //1（营业）、2（停业）、3（关闭）、4（筹建）、5（其他）',
                    if ("1".equals(item.getOperatingStatus())){
                        mineBusinessType.setImageResource(R.mipmap.do_business_status);
                        mineBusinessType.setVisibility(View.VISIBLE);
                        doBusinessTime.setVisibility(View.GONE);
                    }else if ("2".equals(item.getOperatingStatus())){
                        mineBusinessType.setImageResource(R.mipmap.out_of_business_status);
                        mineBusinessType.setVisibility(View.VISIBLE);
                        doBusinessTime.setVisibility(View.VISIBLE);
                        String time = StringUtils.isEmpty(item.getReportEndDate())? "" : DateUtil.strToStrType(item.getReportEndDate(),"MM-dd").replace("-",".");
                        doBusinessTime.setText("预计"+time+"营业");
                    } else {
                        doBusinessTime.setVisibility(View.GONE);
                        mineBusinessType.setVisibility(View.GONE);
                    }

//                    //是否可派车 0 不可派车
//                    if ("0".equals(item.getProvideTransport())){
//                        helper.getView().findViewById(R.id.information_department_send_car).setVisibility(View.GONE);
//                    }else{
//                        helper.getView().findViewById(R.id.information_department_send_car).setVisibility(View.VISIBLE);
//                    }

                    String typeId = item.getTypeId();
                    Dictionary sys100003 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100003"}).get(0);
                    String carModeName = "";
                    for (FilterEntity filterEntity : sys100003.list) {
                        if (filterEntity.dictCode.equals(typeId)) {
                            carModeName = filterEntity.dictValue;
                            break;
                        }
                    }
                    helper.setText(R.id.recommend_type, "  " + carModeName);
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("信息部展示", e.toString());
                }
            }
        };
        homeRecommendLv.setAdapter(mAdapter);
        homeRecommendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                GHLog.i("信息部展示",position+"被点击");
                intent.setClass(mContext,DetaileInformationDepartmentActivity.class);
                String inforDepartId = mAdapter.getDataList().get(position).getCoalSalesId();
                intent.putExtra("InfoDepartId",inforDepartId);
                mContext.startActivity(intent);
            }
        });
    }

    @OnClick({R.id.more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more:
                // 点击后跳转到信息部列表界面
                UIHelper.jumpAct(mContext, InformationDepartmentListActivity.class,false);
                break;
        }
    }

}

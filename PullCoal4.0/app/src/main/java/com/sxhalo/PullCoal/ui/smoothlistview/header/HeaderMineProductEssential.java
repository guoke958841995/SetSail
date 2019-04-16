package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.DetaileMineActivity;
import com.sxhalo.PullCoal.activity.ImageBrowseActivity;
import com.sxhalo.PullCoal.activity.MapActivity;
import com.sxhalo.PullCoal.activity.MineDynamicActivity;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.model.MineDynamic;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.tools.usertextview.StretchUtil;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
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
 * Created by amoldZhang on 2017/2/28.
 */
public class HeaderMineProductEssential extends HeaderViewInterface<MineMouth> {

    @Bind(R.id.mine_name)
    TextView mineName;
    @Bind(R.id.imageView_navi)
    ImageView imageViewNavi;
    @Bind(R.id.mine_business_type)
    ImageView mineBusinessType;
    @Bind(R.id.do_business_time)
    TextView doBusinessTime;
    @Bind(R.id.information_department_imageView_listview)
    ListView information_department_imageView_listview;

    @Bind(R.id.mine_type_tv)
    TextView mineTypeTV;
    @Bind(R.id.mine_address)
    TextView mineAddress;
    @Bind(R.id.title_ll)
    LinearLayout titleLl;
    @Bind(R.id.introduction_content_tv)
    TextView introductionContentTv;
    @Bind(R.id.more_comment_ll)
    LinearLayout moreCommentLL;
    @Bind(R.id.mine_profile)
    LinearLayout mineProfile;
    @Bind(R.id.mine_news_dynamic_ll)
    LinearLayout mineNewsDynamicLL;
    @Bind(R.id.tv_more_comment)
    TextView tvMoreComment;
    @Bind(R.id.home_openclass_open)
    ImageView homeOpenclassOpen;

    @Bind(R.id.news_gridview)
    NoScrollGridView newsGridview;
    @Bind(R.id.view_grid)
    View viewGrid;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.time_refresh)
    TextView timeRefresh;
    @Bind(R.id.news_content)
    TextView newsContent;


    private View view;
    private boolean isFirst = true;
    private ArrayList<String> newsImageUrlList = new ArrayList<String>();
    //轮播图链接列表
    private List<Slide> adList = new ArrayList<Slide>();
    private ArrayList<String> adListString = new ArrayList<String>();

    public MineMouth mineProduct;
    private HeaderAdViewView listViewAdHeaderView;
    private QuickAdapter<String> gridViewAdapter;
    private StretchUtil stretchUtil;


    public HeaderMineProductEssential(Activity mActivity) {
        super(mActivity);
    }

    @Override
    protected void getView(MineMouth mineProduct, ListView listView) {
        try {
            view = mInflate.inflate(R.layout.mine_product_manager, listView, false);
            ButterKnife.bind(this, view);
            this.mineProduct = mineProduct;

            dealWithTheView(mineProduct);
            listView.addHeaderView(view);
        } catch (Exception e) {
            GHLog.e("界面控件控制", e.toString());
        }
    }


    public void dealWithTheView(MineMouth mineProduct) {

        //设置矿口名称和营业状态以及导航按钮的设置
        dealWithTheMineView(mineProduct);

        setAdList(mineProduct);
        //设置轮播图ui展示
        if (adList.size() != 0){
            //矿口的图片轮播
            createADView(mineProduct);
        }else{
            information_department_imageView_listview.setVisibility(View.GONE);
            mineTypeTV.setVisibility(View.VISIBLE);
            String typeId = StringUtils.isEmpty(mineProduct.getTypeId()) ? "0" : mineProduct.getTypeId();
            Dictionary sys100004 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100004"}).get(0);
            String carModeName = "";
            for (FilterEntity filterEntity : sys100004.list) {
                if (filterEntity.dictCode.equals(typeId)){
                    carModeName = filterEntity.dictValue;
                    break;
                }
            }
            mineTypeTV.setText("矿口类型："+ carModeName);
        }
        mineAddress.setText("详细地址：" + mineProduct.getAddress());

        if (mineProduct.getProfile().equals("")){
//            introductionContentTv.setText("  暂无");
//            moreCommentLL.setVisibility(View.GONE);

            mineProfile.setVisibility(View.GONE);
        }else{
            mineProfile.setVisibility(View.VISIBLE);
            stretchUtil =  new StretchUtil(introductionContentTv, 4, homeOpenclassOpen,tvMoreComment,moreCommentLL, R.drawable.sort_common_up, R.drawable.sort_common_down);
            stretchUtil.initStretch();
            introductionContentTv.setText(mineProduct.getProfile());
        }

        refreshData(mineProduct);

        if (mineProduct.getInformationDepartmentList().size() == 0) {
            titleLl.setVisibility(View.GONE);
        } else {
            titleLl.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 创建广告视图
     */
    private void createADView(MineMouth mineProductData) {
        // 设置广告数据
        listViewAdHeaderView = new HeaderAdViewView(mContext);
        listViewAdHeaderView.fillView(adList,information_department_imageView_listview);

        listViewAdHeaderView.setADItemOnClickListener(new HeaderAdViewView.ADOnClickListener() {
            @Override
            public void adItemOnClickListener(View view, int position) {
                try {
                    Intent intent = new Intent(mContext,ImageBrowseActivity.class);
                    intent.putExtra("position",position);
                    intent.putStringArrayListExtra("list",adListString);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    GHLog.e("头条点击", e.toString());
                }
            }
        });
        listViewAdHeaderView.setLlIndexContainer(Gravity.RIGHT);
        String typeId = StringUtils.isEmpty(mineProductData.getTypeId()) ? "0" : mineProductData.getTypeId();
        Dictionary sys100004 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100004"}).get(0);
        String carModeName = "";
        for (FilterEntity filterEntity : sys100004.list) {
            if (filterEntity.dictCode.equals(typeId)){
                carModeName = filterEntity.dictValue;
                break;
            }
        }
        listViewAdHeaderView.setBottomRlView(carModeName);
        QuickAdapter<String> gridViewAdapter = new QuickAdapter<String>(mContext, R.layout.news_gridview_item, newsImageUrlList) {
            @Override
            protected void convert(BaseAdapterHelper helper, String itemMap, int position) {
//                ((DetaileMineActivity) mContext).getImageManager().newsloadUrlImageLong(itemMap, (ImageView) helper.getView().findViewById(R.id.imageView));
            }
        };
        information_department_imageView_listview.setAdapter(gridViewAdapter);
    }

    private void setAdList(MineMouth dataList){
        if (!StringUtils.isEmpty(dataList.getMineMouthPic())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getMineMouthPic());
            adList.add(slide);
            adListString.add(dataList.getMineMouthPic());
        }
        if (!StringUtils.isEmpty(dataList.getMineMouthPicUrl1())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getMineMouthPicUrl1());
            adList.add(slide);
            adListString.add(dataList.getMineMouthPicUrl1());
        }
        if (!StringUtils.isEmpty(dataList.getMineMouthPicUrl2())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getMineMouthPicUrl2());
            adList.add(slide);
            adListString.add(dataList.getMineMouthPicUrl2());
        }
        if (!StringUtils.isEmpty(dataList.getMineMouthPicUrl3())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getMineMouthPicUrl3());
            adList.add(slide);
            adListString.add(dataList.getMineMouthPicUrl3());
        }
    }

    public void refreshData(MineMouth mineProduct){
        newsImageUrlList = setImageUrl(mineProduct.getMineDynamic());
        if (gridViewAdapter == null){
            setNewsGradView();
        }else{
            gridViewAdapter.replaceAll(newsImageUrlList);
        }
        if (StringUtils.isEmpty(mineProduct.getMineDynamic().getSummary())){
//            mineNewsDynamicLL.setVisibility(View.GONE);
            newsContent.setText("暂未发布");
        }else{
            mineNewsDynamicLL.setVisibility(View.VISIBLE);
            newsContent.setText(mineProduct.getMineDynamic().getSummary());
            timeRefresh.setText(mineProduct.getMineDynamic().getReportTime());
        }
    }

    /**
     * 设置矿口动态图片数据ui
     */
    private void setNewsGradView() {
        int count = newsImageUrlList.size();
        if (count == 0 || count == 1) {
            newsGridview.setVisibility(View.GONE);
            if (count == 1) {
                viewGrid.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext,ImageBrowseActivity.class);
                        intent.putExtra("position",0);
                        intent.putStringArrayListExtra("list",newsImageUrlList);
                        mContext.startActivity(intent);
                    }
                });
                ((DetaileMineActivity) mContext).getImageManager().newsloadUrlImageLong(newsImageUrlList.get(0), imageView);
            }
        } else {
            viewGrid.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            newsGridview.setVisibility(View.VISIBLE);
            if (count == 4) {
                count = 2;
                viewGrid.setVisibility(View.VISIBLE);
            } else {
                viewGrid.setVisibility(View.GONE);
                count = 3;
            }
            newsGridview.setNumColumns(count); // 设置列数量=列表集合数
            gridViewAdapter = new QuickAdapter<String>(mContext, R.layout.news_gridview_item, newsImageUrlList) {
                @Override
                protected void convert(BaseAdapterHelper helper, String itemMap, int position) {
                    ((DetaileMineActivity) mContext).getImageManager().newsloadUrlImageLong(itemMap, (ImageView) helper.getView().findViewById(R.id.imageView));
                }
            };
            newsGridview.setAdapter(gridViewAdapter);
            newsGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext,ImageBrowseActivity.class);
                    intent.putExtra("position",position);
                    intent.putStringArrayListExtra("list",newsImageUrlList);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    /**
     * 获取矿口最新动态图片数据
     * @param mineDynamic
     * @return
     */
    private ArrayList<String> setImageUrl(MineDynamic mineDynamic){
        ArrayList<String> newsImageUrlList = new ArrayList<String>();
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl1())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl1());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl2())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl2());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl3())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl3());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl4())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl4());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl5())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl5());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl6())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl6());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl7())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl7());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl8())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl8());
        }
        if (!StringUtils.isEmpty(mineDynamic.getScenePicUrl9())){
            newsImageUrlList.add(mineDynamic.getScenePicUrl9());
        }
        return newsImageUrlList;
    }

    /**
     * 矿口界面标题和营业状态以及导航按钮的显示
     * @param mineProduct
     */
    public void dealWithTheMineView(MineMouth mineProduct) {
        //矿口名称
        mineName.setText(mineProduct.getMineMouthName());
        //导航按钮显示
        setRightStatus(mineProduct);

        //1（营业）、2（停业）、3（关闭）、4（筹建）、5（其他）',
        if (mineProduct.getOperatingStatus().equals("1")){
            mineBusinessType.setImageResource(R.mipmap.do_business_status);
            mineBusinessType.setVisibility(View.VISIBLE);
            doBusinessTime.setVisibility(View.GONE);
        }else if (mineProduct.getOperatingStatus().equals("2")){
            mineBusinessType.setImageResource(R.mipmap.out_of_business_status);
            mineBusinessType.setVisibility(View.VISIBLE);
            doBusinessTime.setVisibility(View.VISIBLE);
            String time = StringUtils.isEmpty(mineProduct.getReportEndDate())? "" : DateUtil.strToStrType(mineProduct.getReportEndDate(),"MM-dd").replace("-",".");
            doBusinessTime.setText("预计"+time+"营业");
        } else {
            mineBusinessType.setVisibility(View.GONE);
            doBusinessTime.setVisibility(View.GONE);
        }
    }

    /**
     * 导航按钮的是否显示
     * @param data
     */
    private void setRightStatus(MineMouth data) {
        try {
            String latitude = data.getLatitude();
            String longitude = data.getLongitude();
            if (latitude.isEmpty() & latitude.equals("0.0") & latitude.equals("null")) {
                imageViewNavi.setVisibility(View.GONE);
            } else {
                if (longitude.isEmpty() & longitude.equals("0.0") & longitude.equals("null")) {
                    imageViewNavi.setVisibility(View.GONE);
                } else {
                    imageViewNavi.setVisibility(View.VISIBLE);
                }
            }
            String  TYPE = mContext.getIntent().getStringExtra("TYPE") == null ? "0" : mContext.getIntent().getStringExtra("TYPE");
            if(TYPE.equals("0")){
                imageViewNavi.setVisibility(View.VISIBLE);
            }else{
                imageViewNavi.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.more_comment_ll, R.id.more_dynamic,R.id.imageView_navi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.more_comment_ll:
                if (isFirst) {
                    isFirst = false;
                    tvMoreComment.setText("展开");
                } else {
                    isFirst = true;
                    tvMoreComment.setText("收起");
                }
                stretchUtil.setOnClock(homeOpenclassOpen);
                break;
            case R.id.more_dynamic:
                Intent intent = new Intent(mContext,MineDynamicActivity.class);
                intent.putExtra("InfoDepartId",mContext.getIntent().getStringExtra("InfoDepartId"));
                mContext.startActivityForResult(intent,200);
                break;
            case R.id.imageView_navi:
                new PermissionUtil().requestPermissions(mContext,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
                    @Override
                    public void onGranted() { //用户同意授权
                        Intent intent1 = new Intent(mContext, MapActivity.class);
                        intent1.putExtra("Entity", mineProduct);
                        intent1.putExtra("Type", "MineMouth");
//                // TYPE == 0  跳转  TYPE != 0 直接结束
//                String  TYPE = mContext.getIntent().getStringExtra("TYPE") == null ? "0" : mContext.getIntent().getStringExtra("TYPE");
//                if (!TYPE.equals("0")){
//                    mContext.setResult(RESULT_OK,intent1);
//                    mContext.finish();
//                }else{
                        mContext.startActivity(intent1);
//                }
//                UIHelper.showMap(mContext,mineProduct,"MineMouth");
                    }

                    @Override
                    public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                    }
                });
                break;
        }
    }
}

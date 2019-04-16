package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.DetaileInformationDepartmentActivity;
import com.sxhalo.PullCoal.activity.ImageBrowseActivity;
import com.sxhalo.PullCoal.activity.MapActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.Slide;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2016/12/13.
 */
public class HeaderInformationDepartmentEssential {
    private Activity mContext;

//    @Bind(R.id.information_department_imageView)
//    ImageView informationDepartmentImageView;

    @Bind(R.id.information_department_imageView_listview)
    ListView information_department_imageView_listview;
    @Bind(R.id.information_department_name)
    TextView informationDepartmentName;
    @Bind(R.id.information_department_addss)
    TextView informationDepartmentAddss;
    @Bind(R.id.information_department_ratingbar1)
    ResetRatingBar informationDepartmentRatingbar1;
    @Bind(R.id.information_department_follow_not)
    TextView informationDepartmentFollowNot;
    @Bind(R.id.information_department_follow_yes)
    TextView informationDepartmentFollowYes;
    @Bind(R.id.management_mode_image)
    TextView managementModeImage;
    @Bind(R.id.all_order_num)
    TextView allOrderNum;
    @Bind(R.id.all_freight_num)
    TextView allFreightNum;
    @Bind(R.id.botton_order)
    LinearLayout bottonOrder;
    @Bind(R.id.iv_phone)
    ImageView ivPhone;

    @Bind(R.id.bar_right_imageview)
    ImageView barRightImageView;

    @Bind(R.id.information_department_business_type)
    ImageView mineBusinessType;
    @Bind(R.id.information_department_do_business_time)
    TextView doBusinessTime;

    @Bind(R.id.information_department_send_car)
    TextView informationDepartmentSendCar;




    private View view;
    public InformationDepartment data;
    //轮播图链接列表
    private List<Slide> adList = new ArrayList<Slide>();
    private ArrayList<String> adListString = new ArrayList<String>();
    private List<String> newsImageUrlList = new ArrayList<String>();
    private HeaderAdViewView listViewAdHeaderView;
    private QuickAdapter<String> gridViewAdapter;

    public HeaderInformationDepartmentEssential(Activity mActivity) {
        this.mContext = mActivity;
    }


    public View getView(InformationDepartment data,String TYPE) {
        try {
            view = LayoutInflater.from(mContext).inflate(R.layout.information_department_essential, null, false);
            ButterKnife.bind(this, view);

            dealWithTheView(data,TYPE);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
      return view;
    }

    private void dealWithTheView(final InformationDepartment data,String TYPE) {
        this.data = data;
        setAdList(data);
        if (adList.size() != 0){
            //矿口的图片轮播
            createADView();
        }

//        if (StringUtils.isEmpty(data.getCoalSalePic())) {
//            informationDepartmentImageView.setVisibility(View.GONE);
//        } else {
//            ((BaseActivity) mContext).getImageManager().loadMinebgUrlImage(data.getCoalSalePic(), informationDepartmentImageView);
//        }

        informationDepartmentName.setText(data.getCompanyName());
        informationDepartmentAddss.setText("详细地址：" + data.getAddress());
        informationDepartmentRatingbar1.setStar(Integer.valueOf(data.getCreditRating()));
        setFollow(data);
        String typeId = data.getTypeId();
        Dictionary sys100003 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100003"}).get(0);
        String carModeName = "";
        for (FilterEntity filterEntity : sys100003.list) {
            if (filterEntity.dictCode.equals(typeId)) {
                carModeName = filterEntity.dictValue;
                break;
            }
        }
        managementModeImage.setText("  " + carModeName);
        allOrderNum.setText(data.getOrderTotal() + "单");
        allFreightNum.setText(data.getTransOrderTotal() + "车");

        //1（营业）、2（停业）、3（关闭）、4（筹建）、5（其他）',
        if ("1".equals(data.getOperatingStatus())){
            mineBusinessType.setImageResource(R.mipmap.do_business_status);
            mineBusinessType.setVisibility(View.VISIBLE);
            doBusinessTime.setVisibility(View.GONE);
        }else if ("2".equals(data.getOperatingStatus())){
            mineBusinessType.setImageResource(R.mipmap.out_of_business_status);
            mineBusinessType.setVisibility(View.VISIBLE);
            doBusinessTime.setVisibility(View.VISIBLE);
            String time = StringUtils.isEmpty(data.getReportEndDate())? "" : DateUtil.strToStrType(data.getReportEndDate(),"MM-dd").replace("-",".");
            doBusinessTime.setText("预计"+time+"营业");
        } else {
            mineBusinessType.setVisibility(View.GONE);
            doBusinessTime.setVisibility(View.GONE);
        }

//        //是否可派车 0 不可派车
//        if ("0".equals(data.getProvideTransport())){
//            informationDepartmentSendCar.setVisibility(View.GONE);
//        }else{
//            informationDepartmentSendCar.setVisibility(View.VISIBLE);
//        }
        String latitude = data.getLatitude();
        String longitude = data.getLongitude();
        if (latitude.isEmpty() & "0.0".equals(latitude) & "null".equals(latitude)) {
            barRightImageView.setVisibility(View.GONE);
        } else {
            if (longitude.isEmpty() & "0.0".equals(longitude) & "null".equals(longitude)) {
                barRightImageView.setVisibility(View.GONE);
            } else {
                barRightImageView.setVisibility(View.VISIBLE);
                barRightImageView.setImageResource(R.mipmap.location_map);
            }
        }

        if ("0".equals(TYPE)) {
            barRightImageView.setVisibility(View.VISIBLE);
        } else {
            barRightImageView.setVisibility(View.GONE);
        }
        barRightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PermissionUtil().requestPermissions(mContext,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
                    @Override
                    public void onGranted() { //用户同意授权
                        Intent intent = new Intent(mContext, MapActivity.class);
                        intent.putExtra("Entity", data);
                        intent.putExtra("Type", "InformationDepartment");
//                if (!TYPE.equals("0")){
//                    setResult(RESULT_OK,intent);
//                    finish();
//                }else{
                        mContext.startActivity(intent);
//                }
                    }

                    @Override
                    public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

                    }
                });
            }
        });

    }

    private void setAdList(InformationDepartment dataList){
        if (!StringUtils.isEmpty(dataList.getCoalSalePic())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getCoalSalePic());
            adList.add(slide);
            adListString.add(dataList.getCoalSalePic());
        }
        if (!StringUtils.isEmpty(dataList.getCoalSalePicUrl1())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getCoalSalePicUrl1());
            adList.add(slide);
            adListString.add(dataList.getCoalSalePicUrl1());
        }
        if (!StringUtils.isEmpty(dataList.getCoalSalePicUrl2())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getCoalSalePicUrl2());
            adList.add(slide);
            adListString.add(dataList.getCoalSalePicUrl2());
        }
        if (!StringUtils.isEmpty(dataList.getCoalSalePicUrl3())){
            Slide slide = new Slide();
            slide.setImageUrl(dataList.getCoalSalePicUrl3());
            adList.add(slide);
            adListString.add(dataList.getCoalSalePicUrl3());
        }
//        if (adList.size() == 0){
//            Slide slide = new Slide();
//            slide.setInagePath(R.mipmap.infomaster_image);
//            adList.add(slide);
//        }
    }

    /**
     * 创建广告视图
     */
    private void createADView() {
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
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("头条点击", e.toString());
                }
            }
        });
        listViewAdHeaderView.setLlIndexContainer();
        gridViewAdapter = new QuickAdapter<String>(mContext, R.layout.news_gridview_item, newsImageUrlList) {
            @Override
            protected void convert(BaseAdapterHelper helper, String itemMap, int position) {
//                ((DetaileMineActivity) mContext).getImageManager().newsloadUrlImageLong(itemMap, (ImageView) helper.getView().findViewById(R.id.imageView));
            }
        };
        information_department_imageView_listview.setAdapter(gridViewAdapter);
    }

    public void setFollow(InformationDepartment data) {
        //1 已关注  0未关注
        if ("1".equals(data.getIsFollow())) {
            informationDepartmentFollowNot.setVisibility(View.GONE);
            informationDepartmentFollowYes.setVisibility(View.VISIBLE);
        } else {
            informationDepartmentFollowNot.setVisibility(View.VISIBLE);
            informationDepartmentFollowYes.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.information_department_follow_yes, R.id.information_department_follow_not,R.id.iv_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.information_department_follow_yes:
                if (OnClickListener != null) {
                    OnClickListener.HeaderOnClick(data.getIsFollow());
                }
                break;
            case R.id.information_department_follow_not:
                if (OnClickListener != null) {
                    OnClickListener.HeaderOnClick(data.getIsFollow());
                }
                break;
            case R.id.iv_phone:
                // 打电话
                Map<String,String> map = new HashMap<String, String>();
                map.put("tel",data.getContactPhone());
                map.put("callType", Constant.CALE_TYPE_INFORMATION_DEPARTENT);
                map.put("targetId",data.getCoalSalesId());
                UIHelper.showCollTel(mContext,map,true);
                break;
        }
    }

    /**
     * 添加点击外接接口
     */
    private HeaderOnClickListener OnClickListener;

    public void setOnClickListener(HeaderOnClickListener onClickListener) {
        this.OnClickListener = onClickListener;
    }

    public interface HeaderOnClickListener {
        void HeaderOnClick(String follow);
    }

}

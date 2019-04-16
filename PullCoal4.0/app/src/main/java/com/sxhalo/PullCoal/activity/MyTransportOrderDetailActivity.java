package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.map.search.MapAddressTranslation;
import com.sxhalo.PullCoal.ui.JustifyTextView;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 货运单详情
 */
public class MyTransportOrderDetailActivity extends BaseActivity {


    @Bind(R.id.view_pound_top)
    View viewPoundTop;
    @Bind(R.id.view_pound_bottom)
    View viewPoundBottom;
    @Bind(R.id.linearlayout_pound)
    LinearLayout linearlayoutPound;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.layout_top)
    LinearLayout layoutAccept;
    @Bind(R.id.tv_my_transport_order_num)
    TextView tvMyTransportNum;
    @Bind(R.id.tv_start_area)
    TextView tvStartArea;
    @Bind(R.id.tv_start_address)
    TextView tvStartAddress;
    @Bind(R.id.tv_end_area)
    TextView tvEndArea;
    @Bind(R.id.tv_end_address)
    TextView tvEndAddress;
    @Bind(R.id.tv_transport_cost)
    TextView tvTransportCost;
    @Bind(R.id.tv_transport_name)
    TextView tvTransportName;
    @Bind(R.id.tv_bottom_time)
    TextView tvBottomTime;
    @Bind(R.id.tv_normal_time)
    TextView tvNormalTime;
    @Bind(R.id.tv_cancel_status_1)
    TextView tvCancelStatus;
    @Bind(R.id.tv_normal_status_1)
    TextView tvNormalStatus1;
    @Bind(R.id.iv_normal_status_1)
    ImageView ivNormalStatus1;
    @Bind(R.id.tv_normal_status_2)
    TextView tvNormalStatus2;
    @Bind(R.id.iv_normal_status_2)
    ImageView ivNormalStatus2;
    @Bind(R.id.tv_normal_status_3)
    TextView tvNormalStatus3;
    @Bind(R.id.iv_normal_status_3)
    ImageView ivNormalStatus3;
    @Bind(R.id.tv_normal_status_4)
    TextView tvNormalStatus4;
    @Bind(R.id.iv_normal_status_4)
    ImageView ivNormalStatus4;
    @Bind(R.id.tv_source)
    TextView tvSource;
    @Bind(R.id.tv_info_from_detail)
    TextView tvInfoFromDetail;
    @Bind(R.id.quality_rating)
    ResetRatingBar qualityRating;
    @Bind(R.id.tv_contact)
    TextView tvContact;
    @Bind(R.id.tv_detail_address)
    JustifyTextView tvDetailAddress;
    @Bind(R.id.tv_tips)
    TextView tvTips;
    @Bind(R.id.tv_reason)
    TextView tvReason;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.linearlayout)
    LinearLayout linearlayout;
    @Bind(R.id.tv_cargo_weight)
    TextView tvCargoWeight;
    @Bind(R.id.iv_navi)
    LinearLayout ivNavi;
    @Bind(R.id.tv_distance)
    TextView tvDistance;
    @Bind(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @Bind(R.id.layout_cancel)
    LinearLayout layoutCancel;
    @Bind(R.id.tv_cancel_time)
    TextView tvCancelTime;
    @Bind(R.id.layout_normal)
    LinearLayout layoutNormal;
    @Bind(R.id.layout_scroll_bottom)
    LinearLayout layoutScrollBottom;
    @Bind(R.id.btn_pound_list)
    Button btnPoundList;
    @Bind(R.id.btn_add)
    Button btnCalcel;
    @Bind(R.id.btn_complete)
    Button btnComplete;
    @Bind(R.id.scroll_line)
    View scrollView;
    @Bind(R.id.tv_transport_remark)
    TextView tvTransportRemark;
    @Bind(R.id.remark_ll_view)
    LinearLayout remarkView;
    @Bind(R.id.iv_pound_list)
    ImageView ivPoundList;

    @Bind(R.id.publishTag0)
    TextView publishTag0;
    @Bind(R.id.publishTag1)
    TextView publishTag1;
    @Bind(R.id.publishTag2)
    TextView publishTag2;
    @Bind(R.id.publishTag3)
    TextView publishTag3;

    @Bind(R.id.label_ll)
    LinearLayout label;

    private String phoneNum;
    private String waybilNumber;//货运单id
    private TransportMode transport;
    private TransportMode transportData;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_transport_order_detail);
    }

    @Override
    protected void initTitle() {
        title.setText("运单详情");
    }

    @Override
    protected void getData() {
        try {
            waybilNumber = getIntent().getStringExtra("waybilNumber");
            LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
            params.put("transportOrderCode",waybilNumber);
            new DataUtils(this,params).getUserTransportOrderInfo(new DataUtils.DataBack<List<TransportMode>>() {
                @Override
                public void getData(List<TransportMode> transportModeList) {
                    if (transportModeList == null){
                        return;
                    }
                    transport = transportModeList.get(0);
                    transportData = transportModeList.get(1);
                    initData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        String acceptanceStatus = transport.getOrderState();
        String type = transport.getOrderType();//货运单类型：0，主动接单；1，邀请接单
        //货运单状态：0、待处理；1、同意；2、拒绝；3、司机确认拉货   4、信息部确认司机到达；100、取消
        switch (Integer.valueOf(acceptanceStatus)){
            case 0:
                //等待中 显示待接受
                if ("0".equals(type)) {
                    //主动 显示已抢单 待审核是对号 其余是点
                    tvNormalStatus1.setText("已抢单");
                    tvNormalStatus2.setText("待审核");
                    tvNormalTime.setText("抢单时间：" + transport.getUpdateTime());
                } else {
                    //被动 显示已邀请 待接受是对号 其余是点
                    scrollView.setVisibility(View.GONE);
                    layoutAccept.setVisibility(View.VISIBLE);
                    tvNormalStatus1.setText("已邀请");
                    tvNormalStatus2.setText("待接受");
                    tvNormalTime.setText("接单时间：" + transport.getUpdateTime());
                }
                btnCalcel.setVisibility(View.VISIBLE);
                viewPoundTop.setVisibility(View.GONE);
                viewPoundBottom.setVisibility(View.GONE);
                linearlayoutPound.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
                layoutBottom.setVisibility(View.VISIBLE);
                tvNormalStatus3.setText("进行中");
                tvNormalStatus4.setText("完成");
                ivNormalStatus1.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_2));
                ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_0));
                tvNormalStatus2.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_0));
                layoutNormal.setVisibility(View.VISIBLE);
                break;
            case 1:
                //审核通过显示进行中 进行中显示对号 其余显示点
                if ("0".equals(type)) {
                    //主动 显示已抢单
                    tvNormalStatus1.setText("已抢单");
                    tvNormalStatus2.setText("已受理");
                    layoutAccept.setVisibility(View.GONE);
                    btnComplete.setVisibility(View.VISIBLE);
                    btnCalcel.setVisibility(View.VISIBLE);
                } else {
                    //被动 显示已邀请 进行中显示对号 其余显示点
                    layoutAccept.setVisibility(View.GONE);
                    tvNormalStatus1.setText("已邀请");
                    tvNormalStatus2.setText("已接受");
                    btnCalcel.setVisibility(View.VISIBLE);
                    btnComplete.setVisibility(View.VISIBLE);
                }
                tvNormalTime.setText("审核时间：" + transport.getUpdateTime());
                layoutBottom.setVisibility(View.VISIBLE);
                viewPoundTop.setVisibility(View.VISIBLE);
                viewPoundBottom.setVisibility(View.VISIBLE);
                linearlayoutPound.setVisibility(View.VISIBLE);
                tvNormalStatus3.setText("进行中");
                tvNormalStatus4.setText("完成");
                ivNormalStatus1.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_2));
                tvNormalStatus3.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_0));
                layoutNormal.setVisibility(View.VISIBLE);
                break;
            case 2:
                if("0".equals(type)){
                    // 主动  已拒绝
                    tvCancelTime.setText("拒绝时间：" + transport.getUpdateTime());
                    tvTips.setText("抢单审核未通过！");
                    //拒绝原因
                    if (!StringUtils.isEmpty(transport.getTreatMemo())) {
                        tvReason.setText(transport.getTreatMemo());//拒绝原因
                        tvReason.setVisibility(View.VISIBLE);
                    }
                    layoutCancel.setVisibility(View.VISIBLE);
                    layoutNormal.setVisibility(View.GONE);
                }else{
                    //被动接单  自己拒绝
                    tvCancelTime.setText("取消时间：" + transport.getUpdateTime());
                    tvTips.setText("您已拒绝货运邀请！");
                    tvCancelStatus.setText("已邀请");
                    layoutCancel.setVisibility(View.VISIBLE);
                    layoutNormal.setVisibility(View.GONE);
                }
                viewPoundTop.setVisibility(View.GONE);
                viewPoundBottom.setVisibility(View.GONE);
                linearlayoutPound.setVisibility(View.GONE);
                layoutScrollBottom.setVisibility(View.VISIBLE);
                tvBottomTime.setText(transport.getArriveTime());
                break;
            case 3:
            case 4:
                //完成   显示完成时间轴
                if (StringUtils.isEmpty(transport.getCarryWeightDocPicUrl())) {
                    viewPoundTop.setVisibility(View.GONE);
                    viewPoundBottom.setVisibility(View.GONE);
                    linearlayoutPound.setVisibility(View.GONE);
                } else {
                    viewPoundTop.setVisibility(View.VISIBLE);
                    viewPoundBottom.setVisibility(View.VISIBLE);
                    linearlayoutPound.setVisibility(View.VISIBLE);
                    btnPoundList.setVisibility(View.GONE);
                }
                layoutNormal.setVisibility(View.VISIBLE);
                tvNormalTime.setText("完成时间:" + transport.getUpdateTime());
                layoutScrollBottom.setVisibility(View.VISIBLE);
                tvBottomTime.setText(transport.getArriveTime());
                tvTips.setText("恭喜您，运单已完成！");
                if ("0".equals(type)) {
                    //主动 显示已抢单
                    tvNormalStatus1.setText("已抢单");
                    tvNormalStatus2.setText("已受理");
                    layoutAccept.setVisibility(View.GONE);
                } else {
                    //被动 显示已邀请 进行中显示对号 其余显示点
                    layoutAccept.setVisibility(View.GONE);
                    tvNormalStatus1.setText("已邀请");
                    tvNormalStatus2.setText("已接受");
                }
                tvNormalStatus3.setText("进行中");
                tvNormalStatus4.setText("完成");
                ivNormalStatus1.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                ivNormalStatus2.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                ivNormalStatus3.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_1));
                ivNormalStatus4.setImageDrawable(getResources().getDrawable(R.drawable.icon_status_2));
                tvNormalStatus4.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                break;
            case 100:
                //取消   显示取消时间轴
                layoutCancel.setVisibility(View.VISIBLE);
                layoutScrollBottom.setVisibility(View.VISIBLE);
                if (StringUtils.isEmpty(transport.getCarryWeightDocPicUrl())) {
                    viewPoundTop.setVisibility(View.GONE);
                    viewPoundBottom.setVisibility(View.GONE);
                    linearlayoutPound.setVisibility(View.GONE);
                } else {
                    viewPoundTop.setVisibility(View.VISIBLE);
                    viewPoundBottom.setVisibility(View.VISIBLE);
                    linearlayoutPound.setVisibility(View.VISIBLE);
                    btnPoundList.setVisibility(View.GONE);
                }
                tvTips.setText("取消成功！");
                tvCancelTime.setText("取消时间:" + transport.getUpdateTime());//取消时间
                //TODO 未传
//                if (!StringUtils.isEmpty(transport.getReason())) {
//                    tvReason.setText(transport.getReason());//拒绝原因
//                    tvReason.setVisibility(View.VISIBLE);
//                }
                tvBottomTime.setText(transport.getArriveTime());
                break;
        }

        //判断当前界面的导航按钮显示
        setNaviView();

        if (linearlayoutPound.getVisibility() == View.VISIBLE ) {
            if (!StringUtils.isEmpty(transport.getCarryWeightDocPicUrl())) {
                getImageManager().loadUrlImageCertificationView(transport.getCarryWeightDocPicUrl(), ivPoundList);//照片重新上传
                btnPoundList.setText(getString(R.string.upload_again));
            } else {
                btnPoundList.setText(getString(R.string.upload_pound_list));
            }
        }

        if (!StringUtils.isEmpty(transport.getCarryWeight())) {
            linearlayout.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            tvCargoWeight.setText(transport.getCarryWeight() + "吨");
        } else {
            textView.setVisibility(View.VISIBLE);
            linearlayout.setVisibility(View.GONE);
        }

        tvMyTransportNum.setText(transport.getTransportOrderCode());//运单编号

        tvStartArea.setText(transport.getFromAddress());//始发地区
//        if(!StringUtils.isEmpty( transport.getFromAddress())) {
//            tvStartAddress.setText("(" + transport.getFromAddress() + ")");//始发地址
//        } else {
//            tvStartAddress.setText("(未提供)");
//        }

        tvEndArea.setText(transport.getToAddress());//终点地区
//        if(!StringUtils.isEmpty( transport.getToAddress())) {
//            tvEndAddress.setText("(" + transport.getToAddress() + ")");//终点地址
//        } else {
//            tvEndAddress.setText("(未提供)");
//        }

        tvTransportCost.setText(transport.getCost());//运输费用
        tvTransportName.setText(transport.getGoodsName());//货物名称

        tvSource.setText("信息部");
        tvSource.setText(transportData.getCompanyName());
//        if ("个人".equals(transport.getSource())) {
//            // 信息来源 个人
//            tvInfoFromDetail.setVisibility(View.GONE);
//            qualityRating.setStar(Integer.valueOf(transport.getCreditRating()));
//            tvContact.setText(transport.getContactPerson());
//            tvDetailAddress.setText(transport.getUserAdress());
//            phoneNum = transport.getContactNumber();
//        } else {
        //信息来源 信息部
//        tvInfoFromDetail.setText(transportData.getCompanyName());
//        tvInfoFromDetail.setVisibility(View.VISIBLE);
        qualityRating.setStar(Integer.valueOf(StringUtils.isEmpty(transportData.getCreditRating())?"1":transportData.getCreditRating()));
        phoneNum = transport.getPublishUserPhone();
        tvContact.setText(transport.getPublishUser());
        tvDetailAddress.setText(transportData.getAddress());
//        }

        if (StringUtils.isEmpty(transport.getRemark()) && StringUtils.isEmpty(transport.getPublishTag())){
            remarkView.setVisibility(View.GONE);
        } else {
            remarkView.setVisibility(View.VISIBLE);
            Drawable image =  getDrawable();
            setTextViewBatmap(tvTransportRemark,image,transport.getRemark());
            label.setVisibility(View.GONE);
        }
    }

    private void setNaviView() {
        LatLng endLatLng = null;
        LatLng startLatLng = null;
        if (!StringUtils.isEmpty(transport.getFromLongitude()) && !StringUtils.isEmpty(transport.getFromLongitude())) {
            startLatLng = new LatLng(Double.valueOf(transport.getFromLatitude()),Double.valueOf(transport.getFromLongitude()));
        }else{
            setIvNavi("startLatLng",transport.getFromPlace(),transport.getFromAddress());
        }
        if (!StringUtils.isEmpty(transport.getToLatitude()) && !StringUtils.isEmpty(transport.getToLongitude())) {
            endLatLng = new LatLng(Double.valueOf(transport.getToLatitude()),Double.valueOf(transport.getToLongitude()));
        }else{
            setIvNavi("endLatLng",transport.getToPlace(),transport.getToAddress());
        }

        if (startLatLng != null && endLatLng != null){
            if ( "0.0".equals(startLatLng.latitude + "")  &  "0.0".equals(startLatLng.longitude + "")){
                setIvNavi("startLatLng",transport.getFromPlace(),transport.getFromAddress());
                return;
            }
            if ( "0.0".equals(endLatLng.latitude + "") &  "0.0".equals(endLatLng.longitude + "")){
                setIvNavi("endLatLng",transport.getToPlace(),transport.getToAddress());
                return;
            }
            setIvNaviData(startLatLng,endLatLng);
        }
    }

    /**
     * 创建工具类将地址转换为经纬度
     */
    boolean flage = true;
    private void setIvNavi(final String tage,final String searchPlace,final String searchAddress){
        final MapAddressTranslation mapAddressTranslation = new MapAddressTranslation(this);
        mapAddressTranslation.searchListener(new MapAddressTranslation.AddressTranslation(){
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                super.onGeocodeSearched(geocodeResult, i);
                if (i == 1000){
                    LatLonPoint latLonPoint = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                    if (tage.equals("endLatLng")){
                        transport.setToLatitude(latLonPoint.getLatitude() + "");
                        transport.setToLongitude(latLonPoint.getLongitude() + "");
                    }else{
                        transport.setFromLatitude(latLonPoint.getLatitude() + "");
                        transport.setFromLongitude(latLonPoint.getLongitude() + "");
                    }
                    setNaviView();
                    flage = true;
                }else{
                    if (flage){
                        //当地址搜索反回失败时在以行政区划搜索
                        mapAddressTranslation.searchAddressToCoordinate(searchPlace,"");
                        flage = false;
                    }
                }
            }
        });
        //默认先以地址搜索
        mapAddressTranslation.searchAddressToCoordinate(searchAddress,"");
    }

    /**
     * 在界面显示路径规划按钮并显示距离数
     * @param endLatLng
     */
    private void setIvNaviData(LatLng startLatLng,LatLng endLatLng){
        try {
            ivNavi.setVisibility(View.VISIBLE);
            float distance = AMapUtils.calculateLineDistance(startLatLng,endLatLng) / 1000;
            String distanceString;
            if (distance >0){
                distanceString = StringUtils.setNumLenth(distance,1) + "km";
            }else{
                distanceString = (distance * 1000) + "m";
            }
            tvDistance.setText(distanceString);
        } catch (Exception e) {
            ivNavi.setVisibility(View.GONE);
            GHLog.e("地址转换经纬度赋值异常",e.toString());
        }
    }

    /**
     * 将布局生成图片格式
     * @return
     */
    public Drawable getDrawable() {
        String publishTag = transport.getPublishTag() == "" ? null : transport.getPublishTag();
        if (!StringUtils.isEmpty(publishTag)) {
            String[] strings = publishTag.split(",");
            int size = strings.length;
            try {
                if (strings.length > 0) {
                    if (size >= 1 && !StringUtils.isEmpty(strings[0])) {
                        publishTag0.setVisibility(View.VISIBLE);
                        publishTag0.setText(strings[0]);
                    }
                    if (size >= 2 && !StringUtils.isEmpty(strings[1])) {
                        publishTag1.setVisibility(View.VISIBLE);
                        publishTag1.setText(strings[1]);
                    }
                    if (size >= 3 && !StringUtils.isEmpty(strings[2])) {
                        publishTag2.setVisibility(View.VISIBLE);
                        publishTag2.setText(strings[2]);
                    }
                    if (size >= 4 && !StringUtils.isEmpty(strings[3])) {
                        publishTag3.setVisibility(View.VISIBLE);
                        publishTag3.setText(strings[3]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            label.setDrawingCacheEnabled(true);
            label.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            label.layout(0, 0, label.getMeasuredWidth(), label.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(label.getDrawingCache());
            label.setDrawingCacheEnabled(false);
            bitmap  = big(bitmap,2.5);
            Drawable drawable = new BitmapDrawable(bitmap);
            return drawable;
        }else{
            label.setVisibility(View.GONE);
        }
        return null;
    }

    /**
     *  放大生成的图片
     * @param bitmapp  要放大的图片
     * @param scale    设置图片放大的比例
     * @return
     */
    private Bitmap big(Bitmap bitmapp,double scale) {
        float scaleWidth=1;
        float scaleHeight=1;
        int bmpWidth=bitmapp.getWidth();
        int bmpHeight=bitmapp.getHeight();

        /* 计算这次要放大的比例 */
        scaleWidth = (float)(scaleWidth * scale);
        scaleHeight = (float)(scaleHeight * scale);
        /* 产生reSize后的Bitmap对象 */
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmapp,0,0,bmpWidth,
                bmpHeight,matrix,true);
        return resizeBmp;
    }

    /**
     * 给TextView 设置图文混排布局
     * @param tvSpan
     * @param drawable
     * @param receiverName
     */
    private void setTextViewBatmap(TextView tvSpan,Drawable drawable,String receiverName){
        String name = "备注：";
        String expristion = "  b/12";
        int len;//记录长度
        //初始化对象
        SpannableStringBuilder sb = new SpannableStringBuilder();

        //设置名称
        sb.append(name);
//        sb.setSpan(new StyleSpan(Typeface.BOLD), len, sb.length(), Spannable.SPAN_POINT_POINT);//加粗
        sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.actionsheet_gray)), 0, sb.length(), Spannable.SPAN_POINT_POINT);//颜色
        len = sb.length();

        if (drawable != null){
            //设置图片
            drawable.setBounds(0, 0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());//设置图片大小
            sb.append(expristion);
            sb.setSpan(new ImageSpan(drawable){
                @Override
                public void draw(Canvas canvas, CharSequence text, int start, int end,
                                 float x, int top, int y, int bottom, Paint paint) {

                    Paint.FontMetricsInt fm = paint.getFontMetricsInt();
                    Drawable drawable = getDrawable();
                    int transY = (y + fm.descent + y + fm.ascent) / 2
                            - drawable.getBounds().bottom / 2;
                    canvas.save();
                    canvas.translate(x, transY);
                    drawable.draw(canvas);
                    canvas.restore();
                }
            }, len, sb.length(), Spannable.SPAN_COMPOSING);
            len = sb.length();
        }
        if (!StringUtils.isEmpty(receiverName)){
            //设置后面显示的文字
            sb.append("   " + receiverName);
            //sb.setSpan(new StyleSpan(Typeface.BOLD), len, sb.length(), Spannable.SPAN_POINT_POINT);//加粗
            sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.actionsheet_gray)), len, sb.length(), Spannable.SPAN_POINT_POINT);//颜色
        }
        tvSpan.setText(sb);
    }

    @OnClick({R.id.title_bar_left, R.id.iv_navi, R.id.iv_phone, R.id.iv_pound_list, R.id.btn_pound_list, R.id.btn_complete, R.id.btn_add, R.id.tv_accept})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.iv_navi:
                // 跳转到导航页面
                //当用户成功接单后，通过货运单进行路径规划时，如果未上传磅单，则进行路径规划时仍显示三个点的导航；
                if (!StringUtils.isEmpty(transport.getCarryWeightDocPicUrl())) {
                    UIHelper.showRoutNavi(this, transport.getFromLatitude(),transport.getFromLongitude(),transport.getFromPlace());
                } else { // 如果已经上传了榜单，则按照当前位置和目的地进行路径规划；
                    UIHelper.showRoutNavi(this, transport);
                }
                break;
            case R.id.iv_phone:
                Map<String,String> map = new HashMap<String, String>();
                map.put("tel",phoneNum);
                UIHelper.showCollTel(MyTransportOrderDetailActivity.this, map,false);
                break;
            case R.id.iv_pound_list:
                if (!StringUtils.isEmpty(transport.getCarryWeightDocPicUrl())) {
                    Intent intent = new Intent();
                    intent.putExtra("url", transport.getCarryWeightDocPicUrl());//磅单图片链接
                    intent.setClass(MyTransportOrderDetailActivity.this, PicturePreviewActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_pound_list:
                Intent intent = new Intent(this, PoundListActivity.class);
                intent.putExtra("entity", transport);
                intent.putExtra("type", 0);
                startActivityForResult(intent, Constant.REFRESH_CODE);
                break;
            case R.id.btn_add:
                //货运单状态：0、待处理；1、同意；2、拒绝；3、司机确认拉货   4、信息部确认司机到达；100、取消
                String acceptanceStatus = transport.getOrderState();
                //货运单类型：0，主动接单；1，邀请接单
                String type = transport.getOrderType();
                if (acceptanceStatus.equals("0") && type.equals("1")){  //待审核 并且 是被动接单时
                    //拒绝接单
                    switchHttp(0);
                }else{
                    // 取消货运订单
                    switchHttp(1);
                }
                break;
            case R.id.btn_complete:
                // 完成订单
                switchHttp(2);
                break;
            case R.id.tv_accept:
                //接受邀请
                switchHttp(3);
                break;
        }
    }


    private void switchHttp(final int flage){
        try {
            //货运单状态：0、待处理；1、同意；2、拒绝；3、司机确认拉货   4、信息部确认司机到达；100、取消
            String orderState = "";
            switch (flage){
                case 0: //拒绝接单
                    orderState = "2";
                    break;
                case 1: //取消货运订单
                    orderState = "100";
                    break;
                case 2:  // 完成订单
                    orderState = "3";
                    break;
                case 3: // 接受邀请
                    orderState = "1";
                    break;
            }
            LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
            params.put("transportOrderCode",waybilNumber);
            params.put("orderState",orderState);
            new DataUtils(this,params).getUserTransportOrderHandle(new DataUtils.DataBack<List<TransportMode>>() {
                @Override
                public void getData(List<TransportMode> transportModeList) {
                    if (transportModeList == null){
                        return;
                    }
                    transport = transportModeList.get(0);
                    transportData = transportModeList.get(1);
                    switch (flage){
                        case 0:
                            //拒绝接单时刷新界面
                            setRefreshRefuse();
                            break;
                        case 1:
                            //取消时刷新界面
                            setRefreshCancel();
                            break;
                        case 2:
                            //完成订单联网成功
                            setRefreshComplete();
                            break;
                        case 3:
                            //接受货运订单联网成功
                            setRefreshAgree();
                            break;
                    }
                    sendBroadcast();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接受货运订单邀请联网成功
     */
    public void setRefreshAgree() {
        displayToast(getString(R.string.accept_transport_success));
        layoutAccept.setVisibility(View.GONE);
        MyTransportOrderDetailActivity.this.getData();
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 取消货运订单联网成功
     */
    private void setRefreshCancel() {
        if (layoutNormal.getVisibility() == View.VISIBLE) {
            layoutNormal.setVisibility(View.GONE);
        }
        if (layoutCancel.getVisibility() == View.VISIBLE) {
            layoutNormal.setVisibility(View.GONE);
        }
        layoutBottom.setVisibility(View.GONE);
        layoutAccept.setVisibility(View.GONE);
        MyTransportOrderDetailActivity.this.getData();
        setResult(RESULT_OK);
    }

    /**
     * 拒绝接单联网成功
     */
    private void setRefreshRefuse() {
        if (layoutNormal.getVisibility() == View.VISIBLE) {
            layoutNormal.setVisibility(View.GONE);
        }
        if (layoutCancel.getVisibility() == View.VISIBLE) {
            layoutNormal.setVisibility(View.GONE);
        }
        layoutBottom.setVisibility(View.GONE);
        layoutAccept.setVisibility(View.GONE);
        MyTransportOrderDetailActivity.this.getData();
        setResult(RESULT_OK);
    }

    /**
     * 完成订单联网成功
     */
    private void setRefreshComplete() {
        if (layoutNormal.getVisibility() == View.VISIBLE) {
            layoutNormal.setVisibility(View.GONE);
        }
        if (layoutCancel.getVisibility() == View.VISIBLE) {
            layoutNormal.setVisibility(View.GONE);
        }
        layoutBottom.setVisibility(View.GONE);
        MyTransportOrderDetailActivity.this.getData();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REFRESH_CODE:
                    getData();
                    break;
            }
        }
    }

    /**
     * 提交成功，发送广播，货运单列表刷新
     */
    private void sendBroadcast() {
                Intent intent = new Intent(Constant.UPDATE_MY_TRANSPORT_ORDER + "");
                sendBroadcast(intent);
            }
}

package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amoldzhang.bottomdialog.BottomDialog;
import com.amoldzhang.bottomdialog.Item;
import com.amoldzhang.bottomdialog.OnItemClickListener;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.MineDynamic;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.map.search.MapAddressTranslation;
import com.sxhalo.PullCoal.ui.JustifyTextView;
import com.sxhalo.PullCoal.ui.NoLineClickSpan;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.popwin.SelectSharePopupWindow;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.wxapi.QQShareUtils;
import com.sxhalo.PullCoal.wxapi.WXSceneUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 货运详情
 * Created by liz on 2017/4/17.
 */

public class TransportDetailActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_bar_right_imageview)
    ImageView rightImage;
    @Bind(R.id.tv_declare_1)
    TextView tvDeclare1;
    @Bind(R.id.tv_declare_3)
    TextView tvDeclare3;
    @Bind(R.id.iv_navi)
    LinearLayout ivNavi;
    @Bind(R.id.tv_distance)
    TextView tvDistance;
    @Bind(R.id.tv_start_area)
    TextView tvStartArea;
    @Bind(R.id.tv_end_area)
    TextView tvEndArea;
    @Bind(R.id.tv_transport_name)
    TextView tvTransportName;
    @Bind(R.id.tv_transport_cost)
    TextView tvTransportCost;
    @Bind(R.id.tv_source)
    TextView tvSource;
    @Bind(R.id.tv_info_from_detail)
    TextView tvInfoFromDetail;
    @Bind(R.id.quality_rating)
    ResetRatingBar qualityRating;
    @Bind(R.id.tv_contact)
    TextView tvContact;
    @Bind(R.id.tv_transport_remark)
    TextView tvTransportRemark;
    @Bind(R.id.tv_detail_address)
    JustifyTextView tvDetailAddress;
    @Bind(R.id.iv_phone)
    ImageView ivPhone;
    @Bind(R.id.remark_ll_view)
    LinearLayout remarkView;

    @Bind(R.id.publishTag0)
    TextView publishTag0;
    @Bind(R.id.publishTag1)
    TextView publishTag1;
    @Bind(R.id.publishTag2)
    TextView publishTag2;
    @Bind(R.id.publishTag3)
    TextView publishTag3;

    @Bind(R.id.tv_information_fee)
    TextView tvInformationFee;
    @Bind(R.id.tv_loading_charges)
    TextView tvLoadingCharges;
    @Bind(R.id.tv_unloading_charge)
    TextView tvUnloadingCharge;
    @Bind(R.id.requirement_num)
    TextView requirementNum;
    @Bind(R.id.production_source)
    TextView productionSource;
    @Bind(R.id.news_content)
    TextView newsContent;
    @Bind(R.id.time_refresh)
    TextView timeRefresh;
    @Bind(R.id.label_ll)
    LinearLayout label;
    @Bind(R.id.news_content_ll)
    LinearLayout newsContentLL;

    @Bind(R.id.transport_calculator)
    LinearLayout transportCalculator;

    @Bind(R.id.news_gridview)
    NoScrollGridView newsGridview;
    @Bind(R.id.view_grid)
    View viewGrid;
    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @Bind(R.id.address_view)
    LinearLayout addressView;
    @Bind(R.id.say_view_1)
    LinearLayout sayView1;
    @Bind(R.id.say_text_2)
    TextView sayText2;

    private QuickAdapter<String> gridViewAdapter;
    private ArrayList<String> newsImageUrlList = new ArrayList<String>();


    private TransportMode transport;
    private String transportId; //货运id

    private String phoneNum;
    private TransportMode transportData;
    private MineDynamic mineDynamicData;

    private QQShareUtils qqShareUtils;  //qq 分享工具
    private WXSceneUtils wxSceneUtils;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_transport_detail);
        transportId = getIntent().getStringExtra("waybillId");
    }

    @Override
    protected void initTitle() {
        title.setText("货运详情");
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("transportId", transportId);
            params.put("regionCode",SharedTools.getStringValue(this, "adCode",""));
            params.put("longitude",SharedTools.getStringValue(this, "longitude", ""));
            params.put("latitude",SharedTools.getStringValue(this, "latitude", ""));
            new DataUtils(this, params).getCoalTransportInfo(new DataUtils.DataBack<List<MineDynamic>>() {
                @Override
                public void getData(List<MineDynamic> appDataList) {
                    if (appDataList == null) {
                        return;
                    }
                    transport = appDataList.get(0);
                    transportData = appDataList.get(1);
                    mineDynamicData = appDataList.get(2);
                    initData();
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            //分享按钮
            if ("0".equals(transport.getConsultingFee())){
                rightImage.setVisibility(View.VISIBLE);
                rightImage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_share));
            }

            //当前货运信息是否发布
            if("1".equals(transport.getPublishState()) || "1.0".equals(transport.getPublishState())){
                layoutBottom.setVisibility(View.VISIBLE);
            }else{
                layoutBottom.setVisibility(View.GONE);
            }
            //判断当前界面的导航按钮显示
            setNaviView();

            tvStartArea.setText(transport.getFromAddress());//始发地区

            tvEndArea.setText(transport.getToAddress());//终点地区

            if ("电话详谈".equals(transport.getCost().replace(".0", "") + transport.getCostUnit())){
                tvTransportCost.setText("运费"+ transport.getCost().replace(".0", "") + transport.getCostUnit());//运输费用
            }else{
                tvTransportCost.setText(transport.getCost().replace(".0", "") + transport.getCostUnit());//运输费用
            }

            //当前运费计算只支持 元/吨 为计算单位，故，若是单位是其他的话，不显示运费计算器按钮
            if (transport.getCostUnit().contains("元/吨")){
                transportCalculator.setVisibility(View.VISIBLE);
            }else{
                transportCalculator.setVisibility(View.INVISIBLE);
            }

            tvTransportName.setText(transport.getGoodsName());//货物名称

            if (StringUtils.isEmpty(transport.getInformationCharge())){
                tvInformationFee.setText("——"); //信息费
                tvInformationFee.setTextColor(getResources().getColor(R.color.gray));
            }else{
                tvInformationFee.setText(transport.getInformationCharge() + "元"); //信息费
            }

            if (StringUtils.isEmpty(transport.getLoadingCharge())){
                tvLoadingCharges.setText("——"); //装货费
                tvLoadingCharges.setTextColor(getResources().getColor(R.color.gray));
            }else{
                tvLoadingCharges.setText(transport.getLoadingCharge() + "元"); //装货费
            }

            if (StringUtils.isEmpty(transport.getUnloadingCharge())){
                tvUnloadingCharge.setText("——"); //卸货费
                tvUnloadingCharge.setTextColor(getResources().getColor(R.color.gray));
            }else{
                tvUnloadingCharge.setText(transport.getUnloadingCharge() + "元"); //卸货费
            }

            Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
            String carModeName = "";
            for (FilterEntity filterEntity : sys100005.list) {
                if (filterEntity.dictCode.equals(transport.getVehicleMode())) {
                    carModeName = filterEntity.dictValue;
                    break;
                }
            }
            String tvCarType = (StringUtils.isEmpty(carModeName) ? "" : carModeName);//车型
            String vehiclelength = StringUtils.isEmpty(transport.getVehicleLength()) ? "" :"," +  transport.getVehicleLength() + "米";
            if (",0米".equals(vehiclelength)){
                vehiclelength = "";
            }
            String vehicleLoad = StringUtils.isEmpty(transport.getVehicleLoad()) ? "" : ",载重" + transport.getVehicleLoad() + "吨";

            // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
            String transportType = transport.getTransportType();
            String tvLeftCarNum = "";
            if (transportType.equals("1")) {
                tvLeftCarNum = "长期货运";
            }else{
                tvLeftCarNum = ((StringUtils.isEmpty(transport.getSurplusNum()) ? "" : "还需" + transport.getSurplusNum())) + "车";//剩余卡车
            }
            requirementNum.setText( tvLeftCarNum + vehicleLoad + vehiclelength + tvCarType);

            if (StringUtils.isEmpty(transport.getRemark()) && StringUtils.isEmpty(transport.getPublishTag())){
                remarkView.setVisibility(View.GONE);
            } else {
                remarkView.setVisibility(View.VISIBLE);
                Drawable image =  getDrawable();
                setTextViewBatmap(tvTransportRemark,image,transport.getRemark());
                label.setVisibility(View.GONE);
            }

            if (mineDynamicData != null ){
                setNewsData(mineDynamicData);
                setNewsGradView();
            }else{
                newsContentLL.setVisibility(View.GONE);
            }

            if ("1".equals(transport.getLmbPublish())){
                //            tvSource.setText("信息部");
                tvSource.setText(transportData.getCompanyName());
                //信息来源 信息部
                tvInfoFromDetail.setVisibility(View.GONE);
                tvInfoFromDetail.setText(transportData.getCompanyName());

                qualityRating.setStar(Integer.valueOf(transportData.getCreditRating()));
                tvContact.setText(transport.getPublishUser());
                tvDetailAddress.setText(transportData.getAddress());
                phoneNum = transport.getPublishUserPhone();
                layoutBottom.setVisibility(View.VISIBLE);
            }else{
                tvSource.setText("个人");

                qualityRating.setStar(3);
                tvContact.setText(transport.getPublishUser());
                phoneNum = transport.getPublishUserPhone();
                addressView.setVisibility(View.GONE);

                layoutBottom.setVisibility(View.GONE);
            }
            setTextVeiew();
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("货运详情赋值", e.toString());
        }
    }

    private void setNaviView() {
        LatLng endLatLng = null;
        LatLng startLatLng = null;
        if (!StringUtils.isEmpty(transport.getFromLongitude()) && !StringUtils.isEmpty(transport.getFromLatitude())) {
            NaviLatLng wayPoint = new NaviLatLng();
            wayPoint.setLatitude(Double.valueOf(transport.getFromLatitude()));
            wayPoint.setLongitude(Double.valueOf(transport.getFromLongitude()));
            startLatLng = new LatLng(wayPoint.getLatitude(),wayPoint.getLongitude());
        }else{
            setIvNavi("startLatLng",transport.getFromPlace(),transport.getFromAddress());
        }
        if (!StringUtils.isEmpty(transport.getToLatitude()) && !StringUtils.isEmpty(transport.getToLongitude())) {
            NaviLatLng wayPoint = new NaviLatLng();
            wayPoint.setLatitude(Double.valueOf(transport.getToLatitude()));
            wayPoint.setLongitude(Double.valueOf(transport.getToLongitude()));
            endLatLng = new LatLng(wayPoint.getLatitude(),wayPoint.getLongitude());
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
            if (startLatLng.equals(endLatLng)){
                ivNavi.setVisibility(View.GONE);
                return;
            }
            ivNavi.setVisibility(View.VISIBLE);
            float distance = AMapUtils.calculateLineDistance(startLatLng,endLatLng)/1000;
            String distanceString;
            if (distance >0){
                distanceString = StringUtils.setNumLenth(distance,1) + "km";
            }else{
                distanceString = (distance * 1000) + "m";
            }
            tvDistance.setText(distanceString);
            transport.setDistance(tvDistance.getText().toString());
        } catch (Exception e) {
            ivNavi.setVisibility(View.GONE);
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("地址转换经纬度赋值异常",e.toString());
        }
    }

    private void setNewsData(MineDynamic mineDynamic){
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
    }

    /**
     * 矿口动态
     */
    private void setNewsGradView() {
        int count = newsImageUrlList.size();
        if (count == 0 || count == 1){
            newsGridview.setVisibility(View.GONE);
            if (count == 1){
                viewGrid.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                getImageManager().newsloadUrlImageLong(newsImageUrlList.get(0),imageView);
            }
            if (StringUtils.isEmpty(mineDynamicData.getMineMouthName()) && StringUtils.isEmpty(mineDynamicData.getSurplusNum()) && StringUtils.isEmpty(mineDynamicData.getReportTime())){
                newsContentLL.setVisibility(View.GONE);
            }else{
                productionSource.setText("货  源  地：" + mineDynamicData.getMineMouthName());
                newsContent.setText("最新动态：" + mineDynamicData.getSummary());
                timeRefresh.setText(mineDynamicData.getReportTime());
            }
        }else {
            viewGrid.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            newsGridview.setVisibility(View.VISIBLE);
            if (count == 4){
                count = 2;
                viewGrid.setVisibility(View.VISIBLE);
            }else {
                count = 3;
            }
            newsGridview.setNumColumns(count); // 设置列数量=列表集合数
            gridViewAdapter = new QuickAdapter<String>(this, R.layout.news_gridview_item, newsImageUrlList) {
                @Override
                protected void convert(BaseAdapterHelper helper, String itemMap, int position) {
                    getImageManager().newsloadUrlImageLong(itemMap,(ImageView) helper.getView().findViewById(R.id.imageView));
                }
            };
            newsGridview.setAdapter(gridViewAdapter);
            newsGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(TransportDetailActivity.this,ImageBrowseActivity.class);
                    intent.putExtra("position",position);
                    intent.putStringArrayListExtra("list",newsImageUrlList);
                    startActivity(intent);
                }
            });
        }
        if (StringUtils.isEmpty(mineDynamicData.getMineMouthName()) && StringUtils.isEmpty(mineDynamicData.getSurplusNum()) && StringUtils.isEmpty(mineDynamicData.getReportTime())){
            newsContentLL.setVisibility(View.GONE);
        }else{
            productionSource.setText("货  源  地：" + mineDynamicData.getMineMouthName());
            newsContent.setText("最新动态：" + mineDynamicData.getSummary());
            timeRefresh.setText(mineDynamicData.getReportTime());
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
                MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                e.printStackTrace();
            }
            label.setDrawingCacheEnabled(true);
            label.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            label.layout(0, 0, label.getMeasuredWidth(), label.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(label.getDrawingCache());
            label.setDrawingCacheEnabled(false);
            bitmap  = big(bitmap,2.3);
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


    @OnClick({R.id.title_bar_left, R.id.btn_pull_coal, R.id.iv_navi, R.id.iv_phone, R.id.transport_calculator,R.id.title_bar_right_imageview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.title_bar_right_imageview:
                String title = transport.getFromPlace() + "到" + transport.getToPlace();
                String targetUrl = new Config().getSHARE_RELEASE_TRAN() + "?transportId=" + transportId;
                GHLog.e("分享路径",targetUrl);
                //捷达综合信息部块煤发热量5200KCal/kg一票价2元/吨
                String summary = transport.getFromPlace() + "到" + transport.getToPlace() + "，货物：" + transport.getGoodsName() + "，运费" + transport.getCost().replace(".0", "") + transport.getCostUnit();
                if(BaseUtils.isNetworkConnected(getApplicationContext())){
                    shareDailog(title,targetUrl,summary);
                }else{
                    displayToast(getString(R.string.unable_net_work));
                }
                break;
            case R.id.transport_calculator:
                if (ivNavi.getVisibility() == View.GONE){
                    displayToast("出发地和目的地相同，不能实时计算！");
                }else{
                    //跳转到运费计算界面
                    UIHelper.showFreightCharge(this, transport, 1);
                }
                break;
            case R.id.iv_navi:
                //跳转到导航页面
                UIHelper.showRoutNavi(this, transport);
                break;
            case R.id.iv_phone:
                // 打电话
                Map<String, String> map = new HashMap<String, String>();
                map.put("tel", phoneNum);
                map.put("callType", Constant.CALE_TYPE_FREIGHT);
                map.put("targetId", transport.getTransportId());
                UIHelper.showCollTel(TransportDetailActivity.this, map, true);
                break;
            case R.id.btn_pull_coal:
                String userId = SharedTools.getStringValue(getApplicationContext(), "userId", "-1");
                // 判断是否登录
                if (!userId.equals("-1")) { // 已登录并且信息获取成功
                    if ("1".equals(transport.getIsSelfRelease())) {//"0"不是自己  1是自己
                        displayToast(getString(R.string.unable_get_transport));
                        return;
                    }
                    if (SharedTools.getStringValue(this,"coalSalesId","-1").equals(transport.getCoalSalesId())){
                        showDaiLogToast("您不能抢同一个信息部下的货运！");
                        return;
                    }
                    String transportType = transport.getTransportType();
                    if ( !"1".equals(transportType) && Integer.parseInt(transport.getSurplusNum()) <= 0) {
                        displayToast(getString(R.string.regret_tips));
                        return;
                    }
                    ifWhow();
                } else {
                    UIHelper.jumpActLogin(TransportDetailActivity.this, false);
                }
                break;
        }
    }

    private void setTextVeiew() {
        if ("1".equals(transport.getLmbPublish())){
            SpannableString spannableString = new SpannableString(getString(R.string.transport_detail_instruction_1));
            NoLineClickSpan noLineClickSpan = new NoLineClickSpan(mContext);
            noLineClickSpan.setMyClickListener(new NoLineClickSpan.MyClickListener() {
                @Override
                public void onClick() {
                    String URL = new Config().getPULL_COAL_QUESTIONS();
                    UIHelper.showWEB(TransportDetailActivity.this, URL, "接单拉煤常见问题帮助");
                }

                @Override
                public void setTextColorAndUnderline(TextPaint ds) {
                    ds.setColor(getResources().getColor(R.color.actionsheet_blue));// 设置字体颜色
                    ds.setUnderlineText(false); //去掉下划线
                }
            });
            spannableString.setSpan(noLineClickSpan, 12, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDeclare1.setText(spannableString);
            tvDeclare1.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            sayView1.setVisibility(View.GONE);
            sayText2.setText(getString(R.string.transport_detail_instruction_2_1));
        }

        SpannableString spannableString1 = new SpannableString(getString(R.string.order_detail_instruction_3));
        final String phoneNum = getString(R.string.order_detail_instruction_3).substring(20, 32).replace("-", "");
        NoLineClickSpan noLineClickSpan1 = new NoLineClickSpan(mContext);
        noLineClickSpan1.setMyClickListener(new NoLineClickSpan.MyClickListener() {
            @Override
            public void onClick() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("tel", phoneNum);
                UIHelper.showCollTel(TransportDetailActivity.this, map, false);
            }

            @Override
            public void setTextColorAndUnderline(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.actionsheet_blue));// 设置字体颜色
                ds.setUnderlineText(false); //去掉下划线
            }
        });
        spannableString1.setSpan(noLineClickSpan1, 20, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDeclare3.setText(spannableString1);
        tvDeclare3.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void ifWhow() {
        try {
            new DataUtils(this, new LinkedHashMap<String, String>()).getUserDriverAuthInfo(new DataUtils.DataBack<APPData<UserAuthenticationEntity>>() {
                @Override
                public void getData(APPData<UserAuthenticationEntity> data) {
                    if (data == null) {
                        return;
                    }
                    setDataView(data.getEntity());
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    private void setDataView(UserAuthenticationEntity dataView) {
        //0未认证
        String vehicleState = StringUtils.isEmpty(dataView.getDriverAuthState()) ? "0" : dataView.getDriverAuthState();  //司机认证状态
        if ("0".equals(vehicleState)){
            showDaiLog(TransportDetailActivity.this, getString(R.string.no_submit_vehicle_data));
        }else{
            // 0 未参与 1 已参与
            if (transport.getIsParticipant().equals("0")) {
                startGrabOrder();
            } else {
                showDaiLogToast(getString(R.string.already_get_transport));
            }
        }
    }

    private void showDaiLog(Activity mActivity, String message) {
//        LayoutInflater inflater1 = mActivity.getLayoutInflater();
//        View layout = inflater1.inflate(R.layout.dialog_updata, null);
//        ((TextView) layout.findViewById(R.id.updata_message)).setText(message);
        new RLAlertDialog(mActivity, "系统提示", message, "稍后提示",
                "立刻前往", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                UIHelper.togoSubmitAuthentication(TransportDetailActivity.this, DriverCertificationActivity.class);
            }
        }).show();
    }

    private void showDaiLogToast(String message) {
//        LayoutInflater inflater1 = getLayoutInflater();
//        View layout = inflater1.inflate(R.layout.dialog_updata, null);
//        ((TextView) layout.findViewById(R.id.updata_message)).setText(message);
        new RLAlertDialog(this, "系统提示", message, "确 定",
                "", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
            }
        }).show();
    }

    /**
     * 抢单
     */
    private void startGrabOrder() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("transportId", transport.getTransportId());
            new DataUtils(this, params).getUserTransportOrderCreate(new DataUtils.DataBack<List<TransportMode>>() {
                @Override
                public void getData(List<TransportMode> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        //成功
                        displayToast(getString(R.string.get_transport_success));
                        // 跳转至货运单详情页面 需要waybilNumber参数
                        Intent intent = new Intent(TransportDetailActivity.this, MyTransportOrderDetailActivity.class);
                        intent.putExtra("waybilNumber", dataMemager.get(0).getTransportOrderCode());
                        startActivity(intent);
                        sendBroadcast();
                        finish();
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
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

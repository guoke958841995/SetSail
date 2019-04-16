package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.map.ErrorInfo;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2017/1/11.
 */
public class RouteNaviActivity extends BaseActivity implements AMapNaviListener, AMapNaviViewListener {


    @Bind(R.id.navi_view)
    AMapNaviView mAMapNaviView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.navi_stata)
    TextView naviStata;
    @Bind(R.id.navi_ned)
    TextView naviNed;
    @Bind(R.id.mileage)
    TextView mileage;
    @Bind(R.id.when_used)
    TextView whenUsed;
    @Bind(R.id.average)
    TextView average;
    @Bind(R.id.highest)
    TextView highest;
    @Bind(R.id.title_bar)
    RelativeLayout titleBar;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;

    AMapNavi mAMapNavi;
    protected List<NaviLatLng> mWayPointList;
    private int maxSpeed = 0;
    private String type;
    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setView(savedInstanceState);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_route_navi);
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void getData() {

    }

    public void setView(Bundle savedInstanceState) {
        try {
            mAMapNaviView.onCreate(savedInstanceState);
            mAMapNaviView.setAMapNaviViewListener(this);

            mAMapNavi = AMapNavi.getInstance(this);
            mAMapNavi.addAMapNaviListener(this);
            mAMapNavi.setUseInnerVoice(true); //开启高德内置语音导航

            type = getIntent().getStringExtra("type") == null ? "" : getIntent().getStringExtra("type");
            boolean gps = Config.if_NAVI;
            if (!gps) {  //正式导航
                if (StringUtils.isEmpty(type)){
                    mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
                }else{
                    AMapNaviViewOptions options = new AMapNaviViewOptions();
                    options.setTilt(0);
                    mAMapNaviView.setViewOptions(options);
                }
            } else {  //模拟导航
                mAMapNavi.setEmulatorNaviSpeed(300);
                mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
            }
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("导航界面初始化", e.toString());
        }
    }

    private void setView() {
        try {
            title.setText("导航结束");
            titleBar.setVisibility(View.VISIBLE);
            llBottom.setVisibility(View.VISIBLE);
            String stataAddress = SharedTools.getStringValue(this,"address",mAMapNavi.getNaviGuideList().get(0).getName());
            String endAddress = getIntent().getStringExtra("endAddress");
            naviStata.setText("起点(" + stataAddress + ")");
            naviNed.setText("终点(" + endAddress + ")");
            int allLength = mAMapNavi.getNaviPath().getAllLength()/1000 ;
            mileage.setText(allLength +"");
            int time = mAMapNavi.getNaviPath().getAllTime()/3600 ;
            whenUsed.setText(time + "");
            int averageelocity = (allLength /time);
            average.setText(averageelocity+"");
            highest.setText(maxSpeed +"");  //最高时速
        } catch (Exception e){
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("导航结束",e.toString());
        }
    }

    //初始化成功
    @Override
    public void onInitNaviSuccess() {
//        Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
        if (StringUtils.isEmpty(type)){
            mAMapNavi.startNavi(AMapNavi.GPSNaviMode);
        }else{
            AMapNaviViewOptions options = new AMapNaviViewOptions();
            options.setTilt(0);
            mAMapNaviView.setViewOptions(options);

            sList.add(new NaviLatLng(getIntent().getDoubleExtra("startLatlag",0.0),getIntent().getDoubleExtra("startLatlog",0.0)));
            eList.add(new NaviLatLng(getIntent().getDoubleExtra("endLatlag",0.0),getIntent().getDoubleExtra("endLatlog",0.0)));
//            eList.add(mEndLatlng);
            mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);
        }
    }

    /**
     * 多路径算路成功回调
     * @param ints
     */
    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        //路线计算成功
        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();

//        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
//        mAMapNavi.stopNavi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAMapNaviView != null){
            mAMapNaviView.onDestroy();
        }

        if (mAMapNavi != null){
            //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
        }
    }

    @Override
    public void onInitNaviFailure() {
        Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartNavi(int type) {
        //开始导航回调
        GHLog.i("onStartNavi", "开始导航");
    }

    @Override
    public void onTrafficStatusUpdate() {
        //
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        //当前位置回调

    }

    @Override
    public void onGetNavigationText(int type, String text) {
        //播报类型和播报文字回调

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {
        //结束模拟导航
        GHLog.i("onEndEmulatorNavi", "结束模拟导航");
        setView();
    }

    @Override
    public void onArriveDestination() {
        //到达目的地
        GHLog.i("onArriveDestination", "到达目的地");
        setView();
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        //路线计算失败
        Log.e("dm", "--------------------------------------------");
        Log.i("dm", "路线计算失败：错误码=" + errorInfo + ",Error Message= " + ErrorInfo.getError(errorInfo));
        Log.i("dm", "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/");
        Log.e("dm", "--------------------------------------------");
//        Toast.makeText(this, "errorInfo：" + errorInfo + ",Message：" + ErrorInfo.getError(errorInfo), Toast.LENGTH_LONG).show();
        displayToast(getString(R.string.route_failed));
        finish();
    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
        displayToast(getString(R.string.departure_tips));
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //拥堵后重新计算路线回调
    }

    @Override
    public void onArrivedWayPoint(int wayID) {
        //到达途径点
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
        //GPS开关状态回调
    }

    @Override
    public void onNaviSetting() {
        //底部导航设置点击回调
    }

    @Override
    public void onNaviMapMode(int isLock) {
        //地图的模式，锁屏或锁车
    }

    @Override
    public void onNaviCancel() {
        finish();
    }


    @Override
    public void onNaviTurnClick() {
        //转弯view的点击回调
    }

    @Override
    public void onNextRoadClick() {
        //下一个道路View点击回调
    }


    @Override
    public void onScanViewButtonClick() {
        //全览按钮点击回调
    }

    @Deprecated
    @Override
    public void onNaviInfoUpdated(AMapNaviInfo naviInfo) {
        //过时
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        //导航过程中的信息更新，请看NaviInfo的具体说明
        int carSpeed = naviinfo.getCurrentSpeed();
        if (maxSpeed < carSpeed ){
            maxSpeed = carSpeed;
        }
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
        //已过时
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        //已过时
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        //显示转弯回调
    }

    @Override
    public void hideCross() {
        //隐藏转弯回调
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
        //显示车道信息

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {
        //隐藏车道信息
    }

    @Override
    public void notifyParallelRoad(int i) {
        if (i == 0) {
//            Toast.makeText(this, "当前在主辅路过渡", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在主辅路过渡");
            return;
        }
        if (i == 1) {
//            Toast.makeText(this, "当前在主路", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在主路");
            return;
        }
        if (i == 2) {
//            Toast.makeText(this, "当前在辅路", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在辅路");
        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        //更新交通设施信息
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        //更新巡航模式的统计信息
    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        //更新巡航模式的拥堵信息
    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }


    @Override
    public void onLockMap(boolean isLock) {
        //锁地图状态发生变化时回调
    }

    @Override
    public void onNaviViewLoaded() {
        Log.d("wlx", "导航页面加载成功");
        Log.d("wlx", "请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }
}

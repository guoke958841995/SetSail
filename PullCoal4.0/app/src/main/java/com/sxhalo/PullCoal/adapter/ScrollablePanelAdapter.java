package com.sxhalo.PullCoal.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amoldzhang.scrollablepanellibrary.PanelAdapter;
import com.autonavi.rtbt.IFrameForRTBT;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.tools.image.ImageManager;
import com.sxhalo.PullCoal.tools.map.NaviRoutePlanning;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *  自定义表格适配器
 * Created by amoldZhang on 2018/8/13.
 */
public class ScrollablePanelAdapter extends PanelAdapter{
    private static final int TITLE_TYPE = 4;
    private static final int ROOM_TYPE = 0;
    private static final int COAL_NAME_TYPE = 1;
    private static final int OTHER_DATA_TYPE = 2;
    private Activity mContext;
    private ImageManager imageManager;

    private List<String> roomInfoList=new ArrayList<>();
    private List<Coal> coalNameInfoList = new ArrayList<>();
    private List<List<Coal>> otherDataList =new ArrayList<>();

    public ScrollablePanelAdapter(Activity mContext, ImageManager imageManager) {
        this.mContext = mContext;
        this.imageManager = imageManager;
    }


    @Override
    public int getRowCount() {
        return roomInfoList.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return coalNameInfoList.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
        int viewType = getItemViewType(row, column);
        switch (viewType) {
            case COAL_NAME_TYPE:
                setCoalNameView(column, (CoalNameViewHolder) holder);
                break;
            case ROOM_TYPE:
                setRoomView(row, (RoomViewHolder) holder);
                break;
            case OTHER_DATA_TYPE:
                setOtherDataView(row, column, (OtherDataViewHolder) holder);
                break;
            case TITLE_TYPE:
                break;
            default:
                setOtherDataView(row, column, (OtherDataViewHolder) holder);
        }
    }

    public int getItemViewType(int row, int column) {
        if (column == 0 && row == 0) {
            return TITLE_TYPE;
        }
        if (column == 0) {
            return ROOM_TYPE;
        }
        if (row == 0) {
            return COAL_NAME_TYPE;
        }
        return OTHER_DATA_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case COAL_NAME_TYPE:
                return new CoalNameViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contrast_item_coal_name, parent, false));
            case ROOM_TYPE:
                return new RoomViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contrast_item_title, parent, false));
            case OTHER_DATA_TYPE:
                return new OtherDataViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contrast_item_coal_other_data, parent, false));
            case TITLE_TYPE:
                return new TitleViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contrast_item_title, parent, false));
            default:
                break;
        }
        return new OtherDataViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contrast_item_title, parent, false));
    }


    private void setCoalNameView(int pos, CoalNameViewHolder viewHolder) {
        Coal dateInfo = coalNameInfoList.get(pos - 1);
        if (dateInfo != null && pos > 0) {
            viewHolder.coalNameTextView.setText(dateInfo.getCoalName());
        }
    }

    private void setRoomView(int pos, RoomViewHolder viewHolder) {
        String roomInfo = roomInfoList.get(pos - 1);
        if (roomInfo != null && pos > 0) {
            if ("化验单".equals(roomInfo)){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(BaseUtils.dip2px(mContext,70), BaseUtils.dip2px(mContext,100));
                viewHolder.view.setLayoutParams(layoutParams);
            }
            viewHolder.roomTypeTextView.setText(roomInfo);
        }
    }

    private void setOtherDataView(final int row, final int column, OtherDataViewHolder viewHolder) {
        final Coal coal = otherDataList.get(row - 1).get(column - 1);
        if (coal != null) {
            String roomName = roomInfoList.get(row -1 );

            if ("发热量".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText(coal.getCalorificValue() + "");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("粒度".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText("粒度");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("挥发分".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText(coal.getVolatileValue() + "");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("全水分".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText(coal.getTotalMoisture() + "");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("全硫分".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText(coal.getTotalSulfur() + "");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("固定碳".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText(coal.getFixedCarbon() + "");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("灰分".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText("灰分");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("工艺".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText("工艺");
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("产地".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText(coal.getMineMouthRegionName());
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("距离".equals(roomName)){
                if (!StringUtils.isEmpty(coal.getLatitude()) && !"0.0".equals(coal.getLatitude()) && !StringUtils.isEmpty(coal.getLongitude()) && !"0.0".equals(coal.getLongitude())){
                    LatLng endLatLng = new LatLng(Double.valueOf(coal.getLatitude()),Double.valueOf(coal.getLongitude()));
                    getDistance(endLatLng,viewHolder.coalOtherDataTextView);
                }else{
                    viewHolder.coalOtherDataTextView.setText("未知");
                }
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }else if("价格".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText(coal.getOneQuote());
                viewHolder.coalOtherDataTextView.setTextSize(12);
            }

            if ("化验单".equals(roomName)){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(BaseUtils.dip2px(mContext,90), BaseUtils.dip2px(mContext,100));
                viewHolder.view.setLayoutParams(layoutParams);

                viewHolder.coalOtherdataIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_lmb));
                viewHolder.coalOtherDataTextView.setVisibility(View.GONE);
            }else{
                viewHolder.coalOtherdataIv.setVisibility(View.GONE);
                viewHolder.coalOtherDataTextView.setVisibility(View.VISIBLE);
            }

            if("".equals(roomName)){
                viewHolder.coalOtherDataTextView.setText("移除");
                viewHolder.coalOtherDataTextView.setBackgroundResource(R.drawable.background_blue_shape);
                viewHolder.coalOtherDataTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            }else{
                viewHolder.coalOtherDataTextView.setTextColor(mContext.getResources().getColor(R.color.gary));
            }
        }
    }

    /**
     * 计算两点之间的距离
     * @param endLatLng
     */
    private void getDistance(final LatLng endLatLng,final TextView coalOtherDataTextView){
        new PermissionUtil().requestPermissions(mContext,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                //定位
                NaviRoutePlanning navi = new NaviRoutePlanning(mContext, new NaviRoutePlanning.NaviDataCoalBack() {
                    @Override
                    public void getNaviData(AMapLocation loc) {
                        LatLng startLatLng = new LatLng(loc.getLatitude(),loc.getLongitude());
                        float distance = AMapUtils.calculateLineDistance(startLatLng,endLatLng)/1000;
                        String distanceString;
                        if (distance >0){
                            distanceString = StringUtils.setNumLenth(distance,1) + "km";
                        }else{
                            distanceString = (distance * 1000) + "m";
                        }
                        coalOtherDataTextView.setText(distanceString);
                    }
                });
                navi.startLocation();
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }


    private static class CoalNameViewHolder extends RecyclerView.ViewHolder {
        public TextView coalNameTextView;

        public CoalNameViewHolder(View itemView) {
            super(itemView);
            this.coalNameTextView = (TextView) itemView.findViewById(R.id.coal_name);
        }

    }

    private static class RoomViewHolder extends RecyclerView.ViewHolder {
        public TextView roomTypeTextView;
        public View view;

        public RoomViewHolder(View view) {
            super(view);
            this.view = view;
            this.roomTypeTextView = (TextView) view.findViewById(R.id.title);
        }
    }

    private static class OtherDataViewHolder extends RecyclerView.ViewHolder {
        public TextView coalOtherDataTextView;
        public ImageView coalOtherdataIv;
        public View view;

        public OtherDataViewHolder(View view) {
            super(view);
            this.view = view;
            this.coalOtherDataTextView = (TextView) view.findViewById(R.id.coal_other_data);
            this.coalOtherdataIv = (ImageView) view.findViewById(R.id.coal_other_data_iv);
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.title);
        }
    }


    public void setRoomInfoList(List<String> roomInfoList) {
        this.roomInfoList = roomInfoList;
    }

    public void setCoalNameInfoList(List<Coal> coalNameInfoList) {
        this.coalNameInfoList = coalNameInfoList;
    }

    public void setOrdersList(List<List<Coal>> otherDataList) {
        this.otherDataList = otherDataList;
    }
}

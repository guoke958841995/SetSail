package com.sxhalo.PullCoal.ui;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BuyCoalActivity;
import com.sxhalo.PullCoal.activity.SelectAreaActivity;
import com.sxhalo.PullCoal.adapter.GoodsAttrListAdapter;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.db.entity.Dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.ielse.view.SwitchView;

import static com.sxhalo.PullCoal.common.base.Constant.AREA_CODE;


/**
 * 筛选商品属性选择的popupwindow
 */
public class FilterPopupWindow extends PopupWindow{
    private View contentView;
    private Activity context;
    private CustomListView selectionList;
    private TextView filterReset;
    private TextView filterSure;
    private TextView tvArea;
    private RelativeLayout layoutSwitch;
    private RelativeLayout layoutFreeInfor;
    private RelativeLayout originAddress;
    private SwitchView switchView;
    private SwitchView switchfreeInfor;
    private TextView tvTitle;

    private boolean isOpened = false;
    private boolean isInforOpened = false;

    public boolean isOpened() {
        return isOpened;
    }
    public boolean isInforOpened() {
        return isInforOpened;
    }

    public Map<String, FilterEntity> getMap() {
        return adapter.getMap();
    }

    public GoodsAttrListAdapter getAdapter() {
        return adapter;
    }

    private GoodsAttrListAdapter adapter;
    private List<Dictionary> itemData;

    /**
     * 筛选的popupwindow
     * type=0  煤炭   type=1 司机  type=2 货运
     * hideArea 地址是否显示
     * hideSwitch  价格是否优先
     */
    public FilterPopupWindow(final Activity context, List<Dictionary> itemData, final boolean hideArea, boolean hideSwitch,boolean freeSwitch, int height) {
        this.context = context;
        this.itemData = itemData;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_goods_details, null);
        selectionList = (CustomListView) contentView.findViewById(R.id.selection_list);

        layoutSwitch = (RelativeLayout) contentView.findViewById(R.id.layout_switch);
        layoutFreeInfor = (RelativeLayout) contentView.findViewById(R.id.layout_freeInfor);
        originAddress = (RelativeLayout) contentView.findViewById(R.id.origin_addess);
        switchView = (SwitchView) contentView.findViewById(R.id.switchView);
        switchfreeInfor = (SwitchView) contentView.findViewById(R.id.switch_freeInfor);
        tvTitle = (TextView) contentView.findViewById(R.id.tv_title);

        filterReset = (TextView) contentView.findViewById(R.id.filter_reset);
        filterSure = (TextView) contentView.findViewById(R.id.filter_sure);
        tvArea = (TextView) contentView.findViewById(R.id.tv_area);
        if (hideSwitch) {
            layoutSwitch.setVisibility(View.VISIBLE);
            switchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isOpened = switchView.isOpened();

                }
            });
            switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    view.toggleSwitch(true); // or false
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    view.toggleSwitch(false); // or true
                }
            });
        }

        if (freeSwitch){
            layoutFreeInfor.setVisibility(View.VISIBLE);
            switchfreeInfor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isInforOpened = switchfreeInfor.isOpened();
                }
            });
            switchfreeInfor.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn(SwitchView view) {
                    view.toggleSwitch(true); // or false
                }

                @Override
                public void toggleToOff(SwitchView view) {
                    view.toggleSwitch(false); // or true
                }
            });
        }

        if (hideArea) {
            originAddress.setVisibility(View.VISIBLE);
            originAddress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivityForResult(new Intent(context, SelectAreaActivity.class), AREA_CODE);
                }
            });
        }
        contentView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return true;
            }
        });
        adapter = new GoodsAttrListAdapter(context, itemData);
        selectionList.setAdapter(adapter);
        // 重置的点击监听，将所有选项全设为false
        filterReset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.getMap().clear();
                resetFilter();
                if (!hideArea) {
                    tvArea.setText("全国");
                    if (resetRegionCode != null) {
                        resetRegionCode.reset("0");
                    }

                }
            }
        });
        // 确定的点击监听，将所有已选中项列出
        filterSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setAnimationStyle(R.style.AnimationFromRight);
        this.setContentView(contentView);
        this.setWidth(LayoutParams.MATCH_PARENT);
//        if (checkDeviceHasNavigationBar()) {
//            Resources res = context.getResources();
//            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
//            if (resourceId > 0) {
//                height = height -  res.getDimensionPixelSize(resourceId);
//            }
//        }
        this.setHeight(height);
        ColorDrawable dw = new ColorDrawable(00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
//        this.update();

    }

    /**
     * 重置筛选
     */
    public void resetFilter() {
        for (int i = 0; i < itemData.size(); i++) {
            for (int j = 0; j < itemData.get(i).list.size(); j++) {
                if (j== 0) {
                    itemData.get(i).list.get(j).setChecked(true);
                } else {
                    itemData.get(i).list.get(j).setChecked(false);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 将字符串转为json并设置到界面
     * @param json
     * @throws JSONException
     */
    public void setData(JSONArray json) throws JSONException {
        itemData.clear();
        for (int i = 0; i < json.length(); i++) {
            Dictionary saleName = new Dictionary();
            JSONObject obj = (JSONObject) json.opt(i);
            saleName.title = obj.getString("title");
            ArrayList<FilterEntity> list = new ArrayList<FilterEntity>();
            JSONArray array = obj.optJSONArray("list");
            for (int j = 0; j < array.length(); j++) {
                JSONObject object = array.getJSONObject(j);
                FilterEntity vo = new FilterEntity();
                vo.dictValue = object.getString("dictValue");
                vo.dictCode = object.getString("dictCode");
                if ("1".equals(object.getString("checked"))) {
                    vo.setChecked(true);
                } else {
                    vo.setChecked(false);
                }
                list.add(vo);
            }
            saleName.list = list;
            // 是否展开
            saleName.setNameIsChecked(true);
            itemData.add(saleName);
        }
        adapter.notifyDataSetChanged();
    }

//    public void showFilterPopup(View parent) {
//        if (Build.VERSION.SDK_INT < 24) {
//            if (!this.isShowing()) {
//                this.showAsDropDown(parent);
//            } else {
//                this.dismiss();
//            }
//        } else {
//            if (!this.isShowing()) {
//                int[] a = new int[2];
//                parent.getLocationInWindow(a);
//                this.showAtLocation(((BaseActivity)context).getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, parent.getHeight()+a[1]);
//                this.update();
//            } else {
//                this.dismiss();
//            }
//        }
//    }

    public void setAreaText(String text) {
        tvArea.setText(text);
    }

    public void onRefresh(List<Dictionary> itemData){
        this.itemData = itemData;
        adapter.notifyDataSetChanged();
    }

    public void setTitle(String string) {
        tvTitle.setText(string);
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
    }



    /**
     * 添加点击外接接口
     */
    private ResetRegionCode resetRegionCode;

    public void setOnClickListener(ResetRegionCode resetRegionCode) {
        this.resetRegionCode = resetRegionCode;
    }

    public interface ResetRegionCode {
        void reset(String follow);
    }
}

package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.FreightChargeEntity;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.tools.map.search.MapAddressTranslation;
import com.sxhalo.PullCoal.ui.popwin.SelectAreaPopupWindow;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.RegexpUtils;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * 计算运费界面
 * Created by liz on 2017/11/21.
 */
public class FreightChargeActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.layout_navi)
    LinearLayout layoutNavi;
    @Bind(R.id.layout_custome)
    LinearLayout layoutCustome;
    @Bind(R.id.iv_start_arrow)
    ImageView startArrow;
    @Bind(R.id.iv_end_arrow)
    ImageView endArrow;
    @Bind(R.id.tv_select_start)
    TextView tvSelectStart;
    @Bind(R.id.tv_select_end)
    TextView tvSelectEnd;
    @Bind(R.id.tv_start_area)
    TextView tvStartArea;
    @Bind(R.id.tv_end_area)
    TextView tvEndArea;
    @Bind(R.id.tv_distance)
    TextView tvDistance;
    @Bind(R.id.et_cargo_price)
    EditText etCargoPrice;
    @Bind(R.id.et_cargo_weight)
    EditText etCargoWeight;
    @Bind(R.id.et_oil_price)
    EditText etOilPrice;
    @Bind(R.id.et_oil_consumption)
    EditText etOilConsumption;
    @Bind(R.id.et_information_price)
    EditText etInformationPrice;
    @Bind(R.id.et_load_price)
    EditText etLoadPrice;
    @Bind(R.id.et_unload_price)
    EditText etUnloadPrice;
    @Bind(R.id.btn_calculate)
    Button btnCalculate;
    @Bind(R.id.layout_bottom)
    LinearLayout layoutBottom;

    private TransportMode transportMode;
    private SelectAreaPopupWindow selectAreaPopupWindow;
    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地
    private int currentType = 0;//用来判断点击的是出发地还是目的地

    /**
     * TYPE == 0  两点计算
     * TYPE == 1  三点计算
     */
    private String TYPE;

    private String lastSelectStartArea;//记录最后一次选择出发地的字段(当选全国的时候不记录)
    private String lastSelectEndArea;//记录最后一次选择目的地的字段(当选全国的时候不记录)

    private FreightChargeEntity freightChargeEntity = new FreightChargeEntity();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_freight_charge);
    }

    @Override
    protected void initTitle() {
        title.setText("运费计算器");
    }

    @Override
    protected void getData() {
        initListener();
        switch (getIntent().getIntExtra("type", -1)) {
            case 0:
                TYPE = "0";
                layoutCustome.setVisibility(View.VISIBLE);
                createWindow();
                break;
            case 1:
                transportMode = (TransportMode) getIntent().getSerializableExtra("Entity");
                tvStartArea.setText(transportMode.getFromAddress());//始发地区
                tvEndArea.setText(transportMode.getToAddress());//终点地区
                tvDistance.setText(transportMode.getDistance());//距离
                layoutNavi.setVisibility(View.VISIBLE);
//                if (StringUtils.isEmpty(transportMode.getTransportOrderCode())){
//                    TYPE = "1";
//                }else{
                    TYPE = "0";
//                }
                etCargoPrice.setText(StringUtils.isEmpty(transportMode.getCost())? "0":transportMode.getCost().replace(".0", ""));
                etCargoPrice.setSelection(etCargoPrice.getText().toString().length());
                if (!StringUtils.isEmpty(transportMode.getInformationCharge())){
                    etInformationPrice.setText(transportMode.getInformationCharge());
                    etInformationPrice.setSelection(etInformationPrice.getText().toString().length());
                }
                if (!StringUtils.isEmpty(transportMode.getLoadingCharge())){
                    etLoadPrice.setText(transportMode.getLoadingCharge());
                    etLoadPrice.setSelection(etLoadPrice.getText().toString().length());
                }
                if (!StringUtils.isEmpty(transportMode.getUnloadingCharge())){
                    etUnloadPrice.setText(transportMode.getUnloadingCharge());
                    etUnloadPrice.setSelection(etUnloadPrice.getText().toString().length());
                }
//                etInformationPrice.setText(StringUtils.isEmpty(transportMode.getInformationCharge())? "0":transportMode.getInformationCharge());
//                etLoadPrice.setText(StringUtils.isEmpty(transportMode.getLoadingCharge())? "0":transportMode.getLoadingCharge());
//                etUnloadPrice.setText(StringUtils.isEmpty(transportMode.getUnloadingCharge())? "0":transportMode.getUnloadingCharge());
                break;
        }
        etOilConsumption.setText(StringUtils.isEmpty(SharedTools.getStringValue(this,"oil_consumption",""))== true ? "":SharedTools.getStringValue(this,"oil_consumption",""));
        etOilConsumption.setSelection(etOilConsumption.getText().toString().length());
        etOilPrice.setText(StringUtils.isEmpty(SharedTools.getStringValue(this,"oil_price",""))== true ? "":SharedTools.getStringValue(this,"oil_price",""));
        etOilPrice.setSelection(etOilPrice.getText().toString().length());
    }

    private void initListener() {
        etCargoPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int len = s.toString().length();
                if (len > 1 && text.startsWith("0")) {
                    s.replace(0,1,"");
                }
            }
        });
//        etCargoWeight.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String text = s.toString();
//                int len = s.toString().length();
//                if (len > 1 && text.startsWith("0")) {
//                    s.replace(0,1,"");
//                }
//            }
//        });
        etOilPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String string = etOilPrice.getText().toString().trim();
                if (!StringUtils.isEmpty(string) && !hasFocus) {
                    float f1 =Float.parseFloat(string);
                    etOilPrice.setText(f1 + "");
                }
            }
        });
        etOilPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //删除“.”后面超过2位后的数据
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etOilPrice.setText(s);
                        etOilPrice.setSelection(s.length()); //光标移到最后
                    }
                }

                //如果"."在起始位置,则起始位置自动补0
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etOilPrice.setText(s);
                    etOilPrice.setSelection(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
        etOilConsumption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String string = etOilConsumption.getText().toString().trim();
                if (!StringUtils.isEmpty(string) && !hasFocus) {
                    float f1 =Float.parseFloat(string);
                    etOilConsumption.setText(f1 + "");
                }
            }
        });
        etOilConsumption.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //删除“.”后面超过2位后的数据
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etOilConsumption.setText(s);
                        etOilConsumption.setSelection(s.length()); //光标移到最后
                    }
                }

                //如果"."在起始位置,则起始位置自动补0
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etOilConsumption.setText(s);
                    etOilConsumption.setSelection(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    private void createWindow() {
        selectAreaPopupWindow = new SelectAreaPopupWindow(this, 1);
        selectAreaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String startCode = selectAreaPopupWindow.getStartCode();//出发地code
                String endCode = selectAreaPopupWindow.getEndCode();//目的地code
                switch (currentType) {
                    case TYPE_START:
                        if ("0".equals(startCode)) {
                            displayToast("出发地不能选\"全国\",请重新选择");
                            if (StringUtils.isEmpty(lastSelectStartArea)) {
                                tvSelectStart.setText("请选择出发地");
                            } else {
                                tvSelectStart.setText(lastSelectStartArea);
                            }
                        } else {
                            lastSelectStartArea = tvSelectStart.getText().toString();
                            setIvNavi("startCode",tvSelectStart.getText().toString().trim());
                        }
                        break;
                    case TYPE_END:
                        if ("0".equals(endCode)) {
                            displayToast("目的地不能选\"全国\",请重新选择");
                            if (StringUtils.isEmpty(lastSelectEndArea)) {
                                tvSelectEnd.setText("请选择目的地");
                            } else {
                                tvSelectEnd.setText(lastSelectEndArea);
                            }
                        }else{
                            lastSelectEndArea = tvSelectEnd.getText().toString();
                            setIvNavi("endCode",tvSelectEnd.getText().toString().trim());
                        }
                        break;
                }
            }
        });
    }

    @OnClick({R.id.title_bar_left, R.id.iv_navi, R.id.btn_calculate, R.id.layout_start, R.id.layout_end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.iv_navi:
                //跳转到导航页面
                UIHelper.showRoutNavi(this, transportMode);
                break;
            case R.id.btn_calculate:
                //运费详情
                checkEmpty();
                break;
            case R.id.layout_start:
                currentType = TYPE_START;
                showPopupWindow(tvSelectStart);
                break;
            case R.id.layout_end:
                currentType = TYPE_END;
                showPopupWindow(tvSelectEnd);
                break;
        }
    }

    /**
     * 创建工具类将地址转换为经纬度
     */
    boolean flage = true;
    private void setIvNavi(final String tage,final String searchPlaceCode){
        final MapAddressTranslation mapAddressTranslation = new MapAddressTranslation(this);
        mapAddressTranslation.searchListener(new MapAddressTranslation.AddressTranslation(){
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                super.onGeocodeSearched(geocodeResult, i);
                if (i == 1000){
                    try {
                        LatLonPoint latLonPoint = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                        if (tage.equals("startCode")){
                            freightChargeEntity.setFromLatitude(latLonPoint.getLatitude() + "");
                            freightChargeEntity.setFromLongitude(latLonPoint.getLongitude() + "");
                        }else {
                            freightChargeEntity.setToLatitude(latLonPoint.getLatitude() + "");
                            freightChargeEntity.setToLongitude(latLonPoint.getLongitude() + "");
                        }
                        flage = true;
                    } catch (Exception e) {
                        if (flage){
                            //当地址搜索反回失败时在以行政区划搜索
                            mapAddressTranslation.searchAddressToCoordinate(searchPlaceCode,"");
                            flage = false;
                        }
                    }
                }else{
                    if (flage){
                        //当地址搜索反回失败时在以行政区划搜索
                        mapAddressTranslation.searchAddressToCoordinate(searchPlaceCode,"");
                        flage = false;
                    }
                }
            }
        });
        //默认先以地址搜索
        mapAddressTranslation.searchAddressToCoordinate(searchPlaceCode,"");
    }

    /**
     * 校验格式
     */
    private void checkEmpty() {
        if (getIntent().getIntExtra("type", -1) == 0){
            String startCode = selectAreaPopupWindow.getStartCode();//出发地code
            String endCode = selectAreaPopupWindow.getEndCode();//目的地code
            if (StringUtils.isEmpty(startCode) || "0".equals(startCode)) {
                //出发地未选择
                displayToast(getString(R.string.select_start_area));
                return;
            }
            if (StringUtils.isEmpty(endCode) || "0".equals(endCode)) {
                //目的地未选择
                displayToast(getString(R.string.select_end_area));
                return;
            }
            if (startCode.equals(endCode)){
                displayToast("出发地和目的地不能相同");
                return;
            }
        }else{
            freightChargeEntity.setFromLatitude(transportMode.getFromLatitude());
            freightChargeEntity.setFromLongitude(transportMode.getFromLongitude());
            freightChargeEntity.setToLatitude(transportMode.getToLatitude());
            freightChargeEntity.setToLongitude(transportMode.getToLongitude());
        }
        String cargoPrice = etCargoPrice.getText().toString().trim();//货物单价
        String cargoWeight = etCargoWeight.getText().toString().trim();//货物重量
        String gasPrice = etOilPrice.getText().toString().trim();//油价
        String oilConsumption = etOilConsumption.getText().toString().trim();//油耗
        String informationPrice = StringUtils.isEmpty(etInformationPrice.getText().toString().trim())?"0":etInformationPrice.getText().toString().trim();//信息费
        String loadPrice = StringUtils.isEmpty(etLoadPrice.getText().toString().trim())?"0":etLoadPrice.getText().toString().trim();//装车费
        String unloadPrice = StringUtils.isEmpty(etUnloadPrice.getText().toString().trim())?"0":etUnloadPrice.getText().toString().trim();//卸车费

        if (StringUtils.isEmpty(cargoPrice)) {
            //未输入货物单价
            displayToast(getString(R.string.please_input_cargo_price));
            return;
        }
        if (!RegexpUtils.validatePositiveInteger(cargoPrice)) {
            //请输入正确的货物单价
            displayToast(getString(R.string.please_input_correct_cargo_price));
            return;
        }
        if (StringUtils.isEmpty(cargoWeight)) {
            //未输入货物重量
            displayToast(getString(R.string.please_input_cargo));
            return;
        }
        if (cargoWeight.equals("0") || cargoWeight.equals("0.0") || cargoWeight.equals("0.00")) {
            //请输入正确的货物重量
            displayToast(getString(R.string.please_input_correct_cargo_weight));
            return;
        }
        if (StringUtils.isEmpty(gasPrice)) {
            //未输入当前油价
            displayToast(getString(R.string.please_input_gas_price));
            return;
        }
        if (gasPrice.equals("0") || gasPrice.equals("0.0") || gasPrice.equals("0.00")) {
            //请输入正确的油价
            displayToast(getString(R.string.please_input_correct_gas_weight));
            return;
        }
        if (StringUtils.isEmpty(oilConsumption)) {
            //未输入车辆油耗
            displayToast(getString(R.string.please_input_oil_consumption));
            return;
        }
        if (oilConsumption.equals("0") || oilConsumption.equals("0.0") || oilConsumption.equals("0.00")) {
            //请输入正确的车辆油耗
            displayToast(getString(R.string.please_input_correct_oil_consumption));
            return;
        }
        SharedTools.putStringValue(this,"oil_consumption",oilConsumption);
        SharedTools.putStringValue(this,"oil_price",gasPrice);
        freightChargeEntity.setCargoPrice(Integer.parseInt(cargoPrice));

        freightChargeEntity.setCargoWeight(Float.parseFloat(cargoWeight));

        freightChargeEntity.setGasPrice(Float.parseFloat(gasPrice));
        freightChargeEntity.setOilConsumption(Float.parseFloat(oilConsumption));
        freightChargeEntity.setInformationPrice(Long.parseLong(informationPrice));
        freightChargeEntity.setLoadPrice(Long.parseLong(loadPrice));
        freightChargeEntity.setUnloadPrice(Long.parseLong(unloadPrice));

        Intent intent = new Intent(this, FreightChargeDetailActivity.class);
        intent.putExtra("Entity",freightChargeEntity);
        intent.putExtra("type",TYPE);
        startActivity(intent);
    }

    private void showPopupWindow(TextView textView) {
        selectAreaPopupWindow.showPopupWindow(textView, currentType, tvSelectEnd);
        if (currentType == TYPE_START) {
            startArrow.setImageResource(R.drawable.sort_common_up);
        } else {
            endArrow.setImageResource(R.drawable.sort_common_up);
        }
    }
}

package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.SamplingTest;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 采样化验界面
 * Created by amoldZhang on 2019/3/19.
 */
public class SamplingTestActivity extends BaseActivity {


    @Bind(R.id.sampling_test_ninemouth_name)
    TextView samplingTestNinemouthName;
    @Bind(R.id.sampling_test_ninemouth_value)
    TextView samplingTestNinemouthValue;
    @Bind(R.id.sampling_test_coal_name)
    TextView samplingTestCoalName;
    @Bind(R.id.sampling_test_coal_value)
    TextView samplingTestCoalValue;
    @Bind(R.id.sampling_test_address_name)
    TextView samplingTestAddressName;
    @Bind(R.id.sampling_test_address_value)
    TextView samplingTestAddressValue;
    @Bind(R.id.title)
    TextView title;

    private SamplingTest samplingTest;

    private UserAddress userAddress = new UserAddress();

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sampling_test);
    }

    @Override
    protected void initTitle() {
        samplingTest = (SamplingTest)getIntent().getSerializableExtra("Entity");
        title.setText("煤炭" + samplingTest.getTypeName());
        initView();
    }

    private void initView() {
        samplingTestNinemouthName.setText(samplingTest.getTypeName().contains("采")?"待采样矿口":"待化验矿口");
        samplingTestNinemouthValue.setText(samplingTest.getMineMouthName());
        samplingTestCoalName.setText(samplingTest.getTypeName().contains("采")?"待采样煤种":"待化验煤种");
        samplingTestCoalValue.setText(samplingTest.getCoalName());
        samplingTestAddressName.setText("收  取 地 址");
        samplingTestAddressValue.setText("选择收取地址");
    }

    @Override
    protected void getData(){

    }


    @OnClick({R.id.title_bar_left, R.id.sampling_test_ninemouth, R.id.sampling_test_coal, R.id.sampling_test_address,R.id.sampling_test_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.sampling_test_ninemouth:  // 重新选择矿口
                UIHelper.jumpActForResult(mContext,MineMouthChoiceActivity.class,samplingTest,Constant.AREA_CODE_MINE);
                break;
            case R.id.sampling_test_coal:   //重新选择煤种
                UIHelper.jumpActForResult(mContext,CoalNameChoiceActivity.class,samplingTest,Constant.AREA_CODE_COAL_NAME);
                break;
            case R.id.sampling_test_address:
                Intent intent = new Intent(this, UpDataAddressActivity.class);
                intent.putExtra("UserAddress",userAddress);
                startActivityForResult(intent, Constant.AREA_CODE);
                break;
            case R.id.sampling_test_submit:
                submit();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case Constant.AREA_CODE: //选择地址或者修改地址返回
                    if (data != null){
                        userAddress = (UserAddress) data.getSerializableExtra("addressEntity");
                        samplingTestAddressValue.setText(
                                (StringUtils.isEmpty(userAddress.getFullRegionName())?"":userAddress.getFullRegionName())
                                        + userAddress.getAddress()); //详细地址 : 省市区+详细地址
                    }
                    break;
                case Constant.AREA_CODE_COAL_NAME_CHOICE_PAY: //支付成功返回
                    finish();
                    break;
                case Constant.AREA_CODE_COAL_NAME:  //更换煤种返回，
                    samplingTest = (SamplingTest) data.getSerializableExtra("Entity");
                    samplingTestCoalValue.setText(samplingTest.getCoalName());
                    break;
                case Constant.AREA_CODE_MINE:  //更换矿口返回
                    samplingTest = (SamplingTest) data.getSerializableExtra("Entity");
                    samplingTestNinemouthValue.setText(samplingTest.getMineMouthName());
                    samplingTestCoalValue.setText("请选择煤种名称");
                    break;
            }

        }
    }

    /**
     * 提交选择的内容
     */
    private void submit() {
        if (samplingTestAddressValue.getText().toString().equals("选择收取地址")){
            displayToast("请选择收取地址");
            return;
        }else if (samplingTestCoalValue.getText().toString().equals("请选择煤种名称")){
            displayToast("请重新选择煤种名称");
            return;
        }else {
            getDataType(true);
        }
    }

    /**
     * 采样化验数据提交
     * @param flage
     */
    private void getDataType(boolean flage) {
        try{
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

            params.put("mineMouthId", samplingTest.getMineMouthId());
            //type 类型  1化验  2采样
            params.put("type", ((samplingTest.getTypeName().contains("采"))?"2":"1"));
            params.put("goodsId", samplingTest.getGoodsId());
            params.put("address", (StringUtils.isEmpty(userAddress.getFullRegionName())?"":userAddress.getFullRegionName())+userAddress.getAddress());
            params.put("contactPerson", userAddress.getContactPerson());
            params.put("contactPhone", userAddress.getContactPhone());
            new DataUtils(this, params).getOperatesampleCreate(new DataUtils.DataBack<SamplingTest>() {
                @Override
                public void getData(SamplingTest appData) {
                    try {
                        if (appData == null) {
                            return;
                        }
                        samplingTest.setTradeNo(appData.getTradeNo());
                        samplingTest.setMoney(appData.getMoney());
                        samplingTest.setPrepayId(appData.getPrepayId());
                        samplingTest.setDenomination(appData.getDenomination());
                        samplingTest.setSampleId(appData.getSampleId());
                        Intent intent = new Intent(mContext, PaymentSamplingTestActivity.class);
                        intent.putExtra("SamplingTest",samplingTest);
                        intent.putExtra("UserAddress",userAddress);
                        startActivityForResult(intent, Constant.AREA_CODE_COAL_NAME_CHOICE_PAY);
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {

                }
            },flage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

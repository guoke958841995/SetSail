package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.SamplingTest;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  采样化验基础数据
 * Created by amoldZhang on 2019/3/23.
 */
public class HeaderSamplingAnalysis {

    @Bind(R.id.sampling_test_name)
    TextView samplingTestName;
    @Bind(R.id.sampling_test_value)
    TextView samplingTestValue;
    @Bind(R.id.sampling_test_coal_name)
    TextView samplingTestCoalName;
    @Bind(R.id.sampling_test_coal_value)
    TextView samplingTestCoalValue;
    @Bind(R.id.sampling_test_cost)
    TextView samplingTestCost;
    @Bind(R.id.sampling_test_cost_value)
    TextView samplingTestCostValue;
    @Bind(R.id.sampling_test_cost_type)
    TextView samplingTestCostType;
    @Bind(R.id.sampling_test_cost_type_value)
    TextView samplingTestCostTypeValue;
    @Bind(R.id.sampling_test_recipient)
    TextView samplingTestRecipient;
    @Bind(R.id.sampling_test_recipient_value)
    TextView samplingTestRecipientValue;
    @Bind(R.id.sampling_test_address)
    TextView samplingTestAddress;
    @Bind(R.id.sampling_test_address_value)
    TextView samplingTestAddressValue;

    @Bind(R.id.layout_express)
    LinearLayout layoutExpress;
    @Bind(R.id.express_name)
    TextView expressName;
    @Bind(R.id.express_num)
    TextView expressNum;

    private View view;

    public HeaderSamplingAnalysis(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.header_sampling_analysis,null,false);
        ButterKnife.bind(this, view);
    }

    public View getView(SamplingTest samplingTest) {
        samplingTestName.setText(samplingTest.getTypeName().contains("采") ? "待采样矿口：" : "待化验矿口：");
        samplingTestValue.setText(samplingTest.getMineMouthName());
        samplingTestCoalName.setText(samplingTest.getTypeName().contains("采") ? "待采样煤种：" : "待化验煤种：");
        samplingTestCoalValue.setText(samplingTest.getCoalName());
        samplingTestCost.setText(samplingTest.getTypeName().contains("采") ? "采  样 费 用：" : "化  验 费 用：");
        samplingTestCostValue.setText(samplingTest.getMoney() + "元");
        if ("100".equals(samplingTest.getSampleState())) {
            samplingTestCostType.setText("支  付 状  态：");
            samplingTestCostTypeValue.setText("用户取消");
        } else {
            samplingTestCostType.setText("支  付 方 式：");
            samplingTestCostTypeValue.setText(samplingTest.getPayType());
        }

        samplingTestRecipient.setText(samplingTest.getTypeName().contains("采") ? "采样收件人：" : "化验收件人：");
        String recipientValue = "<font color=\"#333333\">" + samplingTest.getContactPerson() + "</font>";
        recipientValue = recipientValue + "   " + "<font color=\"#e55f48\">" + samplingTest.getContactPhone() + "</font>";
        samplingTestRecipientValue.setText(Html.fromHtml(recipientValue));
        samplingTestAddress.setText(samplingTest.getTypeName().contains("采") ? "采样收件地址：" : "化验收件地址：");
        samplingTestAddressValue.setText(samplingTest.getAddress());

        //采样状态： 0已提交（待受理）  1采样中（已受理）  2化验中 3 拒绝 4已邮寄  5完成 100用户取消
        if ("4".equals(samplingTest.getSampleState()) || "5".equals(samplingTest.getSampleState())) {
            layoutExpress.setVisibility(View.VISIBLE);
            expressName.setText(samplingTest.getExpressName().trim());
            expressNum.setText(samplingTest.getExpressNum().trim());
        } else {
            layoutExpress.setVisibility(View.GONE);
        }
        return view;
    }
}

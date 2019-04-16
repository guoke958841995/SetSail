package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.EscrowAccount;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的煤款托管账户
 * Created by amoldZhang on 2018/5/4.
 */
public class CoalMoneyActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_total_amount)
    TextView tvTotalAmount;
    @Bind(R.id.tv_freezing_amount)
    TextView tvFreezingAmount;
    @Bind(R.id.tv_refundable_amount)
    TextView tvRefundableAmount;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_coal_money);
    }

    @Override
    protected void initTitle() {
        title.setText("我的煤款");
//        StatusBarUtils.with(this).setIsActionBar(false).setStatusBar(false,false);
    }

    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            new DataUtils(this, params).getUserEscrowAccount(new DataUtils.DataBack<EscrowAccount>() {
                @Override
                public void getData(EscrowAccount appData) {
                    try {
                        if (appData == null) {
                            return;
                        }
                        initView(appData);
                    } catch (Exception e) {
                        GHLog.e("煤款托管账户", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    GHLog.e("煤款托管账户", e.toString());
                }
            },true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 界面初始化
     *
     *  总金额 amount
     *  冻结金额 freeze_uncash_amount
     *  可退款金额 = uncash_amount
     */
    private void initView(EscrowAccount appData) {
        // 总金额
        tvTotalAmount.setText(StringUtils.fmtMicrometer(StringUtils.setNumLenth(Float.valueOf(appData.getAmount())/100, 2)));
        // 冻结金额
        tvFreezingAmount.setText(StringUtils.fmtMicrometer(StringUtils.setNumLenth(Float.valueOf(appData.getFreezeUncashAmount())/100, 2)));
        // 可退款金额
        tvRefundableAmount.setText(StringUtils.fmtMicrometer(StringUtils.setNumLenth(Float.valueOf(appData.getUncashAmount())/100, 2)));
    }


    @OnClick({R.id.title_bar_left, R.id.layout_account_details, R.id.layout_coal_orders,R.id.layout_tel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_account_details:
                UIHelper.jumpAct(this,AccountDetailsActivity.class,false);
                break;
            case R.id.layout_coal_orders:
                Intent intent = new Intent(this, CoalOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_tel:
                Intent intent1 = new Intent(this, CustomerServiceActivity.class);
                startActivity(intent1);
                break;
        }
    }
}

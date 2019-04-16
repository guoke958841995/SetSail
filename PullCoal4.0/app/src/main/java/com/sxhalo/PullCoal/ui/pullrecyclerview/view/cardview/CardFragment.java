package com.sxhalo.PullCoal.ui.pullrecyclerview.view.cardview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.DetailsWebActivity;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.textview.SlantedTextView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CardFragment extends Fragment {


    @Bind(R.id.cardView)
    CardView mCardView;
    @Bind(R.id.coal_title)
    TextView coalTitle;
    @Bind(R.id.coal_type_grid)
    TextView coalTypeGrid;
    @Bind(R.id.coal_price)
    TextView coalPrice;
    @Bind(R.id.coal_hot)
    TextView coalHot;
    @Bind(R.id.coal_moth)
    TextView coalMoth;
    @Bind(R.id.slanted_text_view)
    SlantedTextView slantedTextView;


    private Coal coals;
    private FragmentActivity myActivity;


    public static CardFragment instantiate(Coal coal) {
        CardFragment cardFragment = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Coal", coal);
        cardFragment.setArguments(bundle);
        return cardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            myActivity = getActivity();
            coals = (Coal) getArguments().getSerializable("Coal");
        } catch (Exception e) {
            GHLog.e("Bundle传值", e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_fine_coal_item, container, false);
        ButterKnife.bind(this, mRootView);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Coal coal = coals;
                    Intent intent = new Intent();
                    intent.setClass(myActivity, DetailsWebActivity.class);
                    intent.putExtra("Coal", coal);
                    intent.putExtra("inforDepartId", "煤炭详情");
                    String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
                    //免费信息
                    if (userId.equals("-1")) {
                        //未登录 先去登录
                        UIHelper.jumpActLogin(myActivity,false);
                    } else {
                        if (coal.getConsultingFee().equals("0") ||
                                SharedTools.getStringValue(myActivity,"coalSalesId","-1").equals(coal.getCoalSalesId())) {
                            //免费信息
                            myActivity.startActivity(intent);
                        } else {
                            //已登录 判断是否支付
                            if (coal.getIsPay().equals("1")&& coal.getLicenseMinute() != null && !coal.getLicenseMinute().contains("已失效")) {
                                //已支付 直接查看
                                myActivity.startActivity(intent);
                            } else {
                                //未支付 弹框提示
                                ((BaseActivity) myActivity).showPayDialog(coal, "0");
                            }
                        }
                    }
                } catch (Exception e) {
                    GHLog.e("煤炭列表点击", e.toString());
                }
            }
        });
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            setViewData();
//            mCardView.setMaxCardElevation(mCardView.getCardElevation()
//                    * CardAdapter.MAX_ELEVATION_FACTOR); // 设置最大z轴高度
            mCardView.setRadius(0);//设置图片圆角的半径大小
            mCardView.setCardElevation(0); //设置阴影

        } catch (Exception e) {
            GHLog.e("界面初始化", e.toString());
        }
    }

    private void setViewData() {
        coalTitle.setText(coals.getCoalName());
        coalTypeGrid.setText("(" + coals.getCoalCategoryName() + ")");
        coalHot.setText("热量：" + coals.getCalorificValue().replace(".0","") + "卡");
        coalMoth.setText(coals.getMineMouthName()); // 矿口

        slantedTextView.setVisibility(View.VISIBLE);
        String payName = "";
        String priceNum = "";
        //当资讯信息是免费的
        if (coals.getConsultingFee().equals("0")) {
            coalPrice.setVisibility(View.VISIBLE);
            priceNum = "¥"+coals.getOneQuote();
            payName = "免费信息";
        } else {
            //当前登录用户和发布人同属于一个信息部
            if (SharedTools.getStringValue(myActivity,"coalSalesId","-1").equals(coals.getCoalSalesId())){
                priceNum = "" + "¥" + (Double.valueOf(coals.getConsultingFee()) / 100); //付费信息
                payName = "收费信息";
            }else if (coals.getIsPay().equals("1")&& coals.getLicenseMinute() != null && !coals.getLicenseMinute().contains("已失效")) {  //0未支付，1已支付
                //当资讯信息已经付过费
                payName = "已支付";
                priceNum = "¥"+coals.getOneQuote();
            } else {
                //当资讯信息尚未付费
                if (coals.getLicenseMinute().contains("已失效")){
                    payName = "已失效";
                    priceNum = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + ""; // 收费信息;
                }else{
                    payName = "收费信息";
                    priceNum = "¥" + (Double.valueOf(coals.getConsultingFee()) / 100) + ""; // 收费信息
                }
            }
        }
        slantedTextView.setText(payName);
        coalPrice.setText(priceNum);
    }


    public CardView getCardView() {
        return mCardView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

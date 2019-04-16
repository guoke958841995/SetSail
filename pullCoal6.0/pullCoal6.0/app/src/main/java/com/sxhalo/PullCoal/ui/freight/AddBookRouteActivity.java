package com.sxhalo.PullCoal.ui.freight;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.bean.APPDataList;
import com.sxhalo.PullCoal.bean.RouteEntity;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.ui.freight.book.FreightBookContract;
import com.sxhalo.PullCoal.ui.freight.book.FreightBookPresenter;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.weight.popwidow.SelectAreaPopupWindow;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *  添加货运订阅界面
 * Created by amoldZhang on 2019/4/14
 */
public class AddBookRouteActivity extends BaseActivity <FreightBookPresenter> implements FreightBookContract.View{

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_start_area)
    TextView tvStartArea;
    @BindView(R.id.layout_start)
    RelativeLayout layoutStart;
    @BindView(R.id.tv_end_area)
    TextView tvEndArea;
    @BindView(R.id.layout_end)
    RelativeLayout layoutEnd;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.iv_start_arrow)
    ImageView startArrow;
    @BindView(R.id.iv_end_arrow)
    ImageView endArrow;

    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地
    private int currentType = 0;//用来判断点击的是出发地还是目的地
    private SelectAreaPopupWindow selectAreaPopupWindow;//地区选择窗口
    private String lastSelectStartArea;//记录最后一次选择出发地的字段(当选全国的时候不记录)
    private String lastSelectEndArea;//记录最后一次选择目的地的字段(当选全国的时候不记录)


    @Override
    public int getContentLayout() {
        return R.layout.activity_add_book_route;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        title.setText("添加订阅路线");
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
                                tvStartArea.setText("请选择出发地");
                            } else {
                                tvStartArea.setText(lastSelectStartArea);
                            }
                        } else {
                            lastSelectStartArea = tvStartArea.getText().toString();
                        }
                        break;
                    case TYPE_END:
                        if ("0".equals(endCode)) {
                            displayToast("目的地不能选\"全国\",请重新选择");
                            if (StringUtils.isEmpty(lastSelectEndArea)) {
                                tvEndArea.setText("请选择目的地");
                            } else {
                                tvEndArea.setText(lastSelectEndArea);
                            }
                        }else{
                            lastSelectEndArea = tvEndArea.getText().toString();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.title_bar_left, R.id.layout_start, R.id.layout_end, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_start:
                currentType = TYPE_START;
                showPopupWindow(tvStartArea);
                break;
            case R.id.layout_end:
                currentType = TYPE_END;
                showPopupWindow(tvEndArea);
                break;
            case R.id.btn_add:
                String startCode = selectAreaPopupWindow.getStartCode();
                String endCode = selectAreaPopupWindow.getEndCode();
                if (StringUtils.isEmpty(startCode) || "0".equals(startCode)) {
                    displayToast(getString(R.string.select_start_area));
                    return;
                }
                if (StringUtils.isEmpty(endCode) || "0".equals(endCode)) {
                    displayToast(getString(R.string.select_end_area));
                    return;
                }
                if (startCode.equals(endCode)){
                    displayToast("出发地和目的地不能相同");
                    return;
                }
                addBookFreight(startCode,endCode);
                break;
        }
    }

    private void addBookFreight(String fromPlaceCode,String toPlaceCode) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("fromPlace",fromPlaceCode);
            params.put("toPlace",toPlaceCode);
            mPresenter.getTransportSubscribeCreate(this,params,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopupWindow(TextView textView) {
        selectAreaPopupWindow.showPopupWindow(textView, currentType, tvEndArea);
        if (currentType == TYPE_START) {
            startArrow.setImageResource(R.mipmap.sort_common_up);
        } else {
            endArrow.setImageResource(R.mipmap.sort_common_up);
        }
    }

    @Override
    public void getTransportSubscribeCreate(RouteEntity routeEntity, Throwable e) {
        if (routeEntity == null) {
            displayToast("添加失败");
            return;
        }
        displayToast(getString(R.string.add_freight_success));
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void getTransportSubscribeList(List<APPDataList<RouteEntity>> appDataListList, Throwable e) {
    }

    @Override
    public void getTransportSubscribeDelete(String message, Throwable e) {
    }
}

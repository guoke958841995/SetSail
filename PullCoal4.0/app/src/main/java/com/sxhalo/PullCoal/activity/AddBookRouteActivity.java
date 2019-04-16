package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.RouteEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.popwin.SelectAreaPopupWindow;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * 添加订阅路线界面
 * Created by liz on 2017/8/10.
 */
public class AddBookRouteActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_start_area)
    TextView tvStartArea;
    @Bind(R.id.layout_start)
    RelativeLayout layoutStart;
    @Bind(R.id.tv_end_area)
    TextView tvEndArea;
    @Bind(R.id.layout_end)
    RelativeLayout layoutEnd;
    @Bind(R.id.btn_add)
    Button btnAdd;
    @Bind(R.id.iv_start_arrow)
    ImageView startArrow;
    @Bind(R.id.iv_end_arrow)
    ImageView endArrow;

    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地
    private int currentType = 0;//用来判断点击的是出发地还是目的地
    private  SelectAreaPopupWindow selectAreaPopupWindow;//地区选择窗口
    private String lastSelectStartArea;//记录最后一次选择出发地的字段(当选全国的时候不记录)
    private String lastSelectEndArea;//记录最后一次选择目的地的字段(当选全国的时候不记录)

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_book_route);
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
    protected void initTitle() {
        title.setText("添加订阅路线");
    }

    @Override
    protected void getData() {

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
            new DataUtils(this,params).getTransportSubscribeCreate(new DataUtils.DataBack<RouteEntity>() {
                @Override
                public void getData(RouteEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        displayToast(getString(R.string.add_freight_success));
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    private void showPopupWindow(TextView textView) {
        selectAreaPopupWindow.showPopupWindow(textView, currentType, tvEndArea);
        if (currentType == TYPE_START) {
            startArrow.setImageResource(R.drawable.sort_common_up);
        } else {
            endArrow.setImageResource(R.drawable.sort_common_up);
        }
    }
}

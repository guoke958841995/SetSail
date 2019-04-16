package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.ui.popwin.SelectAreaPopupWindow;
import com.sxhalo.PullCoal.utils.StringUtils;
import butterknife.Bind;
import butterknife.OnClick;

/**
 *  专线司机添加管理
 * Created by amoldZhang on 2017/5/10.
 */
public class DriverAddressUpDataActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_start_area)
    TextView tvStartArea;
    @Bind(R.id.tv_end_area)
    TextView tvEndArea;
    @Bind(R.id.iv_start_arrow)
    ImageView startArrow;
    @Bind(R.id.iv_end_arrow)
    ImageView endArrow;

    private UserEntity userEntity;
    private SelectAreaPopupWindow areaPopupWindow;

    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地
    private int currentType = 0;//用来判断点击的是出发地还是目的地
    private String lastSelectStartArea;//记录最后一次选择出发地的字段(当选全国的时候不记录)
    private String lastSelectEndArea;//记录最后一次选择目的地的字段(当选全国的时候不记录)

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_drivers_address_updata);
    }

    @Override
    protected void initTitle() {
        title.setText("专线信息");
        areaPopupWindow = new SelectAreaPopupWindow(this, 1);
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String startCode = areaPopupWindow.getStartCode();//出发地code
                String endCode = areaPopupWindow.getEndCode();//目的地code
                switch (currentType) {
                    case TYPE_START:
                        if ("0".equals(startCode)) {
                            displayToast("出发地不能选\"全国\",请重新选择");
                            if (StringUtils.isEmpty(lastSelectStartArea)) {
                                tvStartArea.setText("请选择出发地");
                            } else {
                                tvStartArea.setText(lastSelectStartArea);
                                userEntity.setStartRegion(startCode);
                                userEntity.setStartRegionName(lastSelectStartArea);
                            }
                        } else {
                            lastSelectStartArea = tvStartArea.getText().toString();
                            userEntity.setStartRegion(startCode);
                            userEntity.setStartRegionName(lastSelectStartArea);
                        }
                        break;
                    case TYPE_END:
                        if ("0".equals(endCode)) {
                            displayToast("目的地不能选\"全国\",请重新选择");
                            if (StringUtils.isEmpty(lastSelectEndArea)) {
                                tvEndArea.setText("请选择目的地");
                            } else {
                                tvEndArea.setText(lastSelectEndArea);
                                userEntity.setEndRegion(endCode);
                                userEntity.setEndRegionName(lastSelectEndArea);
                            }
                        }else{
                            lastSelectEndArea = tvEndArea.getText().toString();
                            userEntity.setEndRegion(endCode);
                            userEntity.setEndRegionName(lastSelectEndArea);
                        }
                        break;
                }





            }
        });
    }

    @Override
    protected void getData() {
        userEntity = (UserEntity) getIntent().getSerializableExtra("UserEntity");
        if (userEntity != null) {
           if (!StringUtils.isEmpty(userEntity.getStartRegionName())){
               tvStartArea.setText(userEntity.getStartRegionName());
           }
           if (!StringUtils.isEmpty(userEntity.getEndRegionName())){
               tvEndArea.setText(userEntity.getEndRegionName());
           }
        }
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
            case R.id.layout_end://目的地
                currentType = TYPE_END;
                showPopupWindow(tvEndArea);
                break;
            case R.id.btn_add://添加
                if (StringUtils.isEmpty(userEntity.getStartRegion())) {
                    displayToast(getString(R.string.select_start_area));
                    return;
                }
                if (StringUtils.isEmpty(userEntity.getEndRegion())) {
                    displayToast(getString(R.string.select_end_area));
                    return;
                }
                if (userEntity.getStartRegion().equals(userEntity.getEndRegion())){
                    displayToast("出发地和目的地不能相同");
                    return;
                }
                Intent intent = new Intent(this, DriverCertificationSubmissionActivity.class);
                intent.putExtra("UserEntity",userEntity);
                setResult(DriverCertificationSubmissionActivity.DRIVER_REGISTRATION,intent);
                displayToast("添加成功！");
                finish();
                break;
        }
    }

    private void showPopupWindow(TextView textView) {
        areaPopupWindow.showPopupWindow(textView, currentType, tvEndArea);
        if (currentType == TYPE_START) {
            startArrow.setImageResource(R.drawable.sort_common_up);
        } else {
            endArrow.setImageResource(R.drawable.sort_common_up);
        }
    }
}

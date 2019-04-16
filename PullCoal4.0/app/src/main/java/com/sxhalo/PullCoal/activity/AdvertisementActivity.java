package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.AdvertisementEntity;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.RxCountDown;
import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

/**
 * Created by liz on 2017/10/30.
 */
public class AdvertisementActivity extends BaseActivity {
    final int COUT_DOWN_TIME = 5;
    @Bind(R.id.splash_view)
    ImageView mSplashView;
    @Bind(R.id.ad_click_small)
    ImageView mAdClickSmall;
    @Bind(R.id.skip_real)
    TextView mSkipReal;
    @Bind(R.id.ad_ignore)
    FrameLayout mAdIgnore;
    private Subscription mSubscription;
    private AdvertisementEntity advertisementEntity;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_advertisement);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //设置沉浸式
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void initTitle() {
        advertisementEntity = (AdvertisementEntity) getIntent().getSerializableExtra("Entity");
    }

    @Override
    protected void getData() {
        mSubscription = RxCountDown.countDown(COUT_DOWN_TIME)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        getImageManager().displayBigImage(advertisementEntity.getAdPicUrl(), mSplashView);
                        mAdClickSmall.setVisibility(View.VISIBLE);
                        mSplashView.setVisibility(View.VISIBLE);
                        mAdIgnore.setVisibility(View.VISIBLE);
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        goMain();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        mSkipReal.setText(TextUtils.concat(integer.intValue() + "s", getResources().getString(R.string.splash_ad_ignore)));
                    }
                });
    }

    @OnClick({R.id.skip_real, R.id.ad_click_small, R.id.splash_view})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.skip_real:
                goMain();
                break;
            case R.id.ad_click_small:
            case R.id.splash_view:
                Intent[] intents = new Intent[2];
                intents[0] = new Intent(AdvertisementActivity.this, MainActivity.class);
                intents[1] = new Intent(AdvertisementActivity.this, WebViewActivity.class);
                intents[1].putExtra("URL", advertisementEntity.getAdUrl())   ;
                intents[1].putExtra("title", "广告详情")   ;
                startActivities(intents);
                finish();
                 break;
        }
    }

    private void goMain() {
        UIHelper.jumpAct(AdvertisementActivity.this, MainActivity.class, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
    }
}

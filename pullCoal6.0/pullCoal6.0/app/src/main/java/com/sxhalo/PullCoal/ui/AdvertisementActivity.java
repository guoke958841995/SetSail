package com.sxhalo.PullCoal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.bean.AdvertisementBean;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.utils.ImageLoaderUtil;
import com.sxhalo.PullCoal.utils.RxCountDown;
import com.sxhalo.PullCoal.utils.UIHelper;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

/**
 * 广告页面
 *
 * @author Xiao_
 * @date 2019/4/13 0013
 */
public class AdvertisementActivity extends BaseActivity {

    @BindView(R.id.iv_advert_bottom_banner)
    ImageView ivAdvertBottomBanner;
    @BindView(R.id.iv_advert_view)
    ImageView ivAdvertView;
    @BindView(R.id.iv_look_advert)
    ImageView ivLookAdvert;
    @BindView(R.id.tv_skip)
    TextView tvSkip;
    @BindView(R.id.fl_ad_ignore)
    FrameLayout flAdIgnore;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    //倒计时
    private final int COUT_DOWN_TIME = 5;

    private AdvertisementBean advertisementBean;

    //跳过广告
    private String skipText;

    @Override
    public int getContentLayout() {
        return R.layout.activity_advertisement;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            advertisementBean = (AdvertisementBean) intent.getSerializableExtra("advertisement_data");
        }

        skipText = getResources().getString(R.string.splash_ad_ignore);
    }

    @Override
    public void initData() {

        if (advertisementBean != null) {

            String adUrl = advertisementBean.getAdUrl();
            String adPicUrl = advertisementBean.getAdPicUrl();

            mCompositeDisposable.add(RxCountDown.countDown(COUT_DOWN_TIME)
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            ivLookAdvert.setVisibility(View.VISIBLE);
                            ivAdvertView.setVisibility(View.VISIBLE);
                            flAdIgnore.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(adPicUrl)) {
                                ImageLoaderUtil.loadImage(AdvertisementActivity.this, adPicUrl, ivAdvertView);
                            }
                        }
                    })
                    .subscribeWith(new DisposableObserver<Integer>() {
                        @Override
                        public void onNext(Integer integer) {
                            tvSkip.setText(TextUtils.concat(integer.intValue() + "s", skipText));
                        }

                        @Override
                        public void onError(Throwable e) {
                            toMain();
                        }

                        @Override
                        public void onComplete() {
                            toMain();
                        }
                    }));
        }

    }


    @OnClick({R.id.iv_advert_view, R.id.iv_look_advert, R.id.tv_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_advert_view:
            case R.id.iv_look_advert:
                //广告详情
                displayToast("广告详情");
                break;
            case R.id.tv_skip:
                //跳过广告,去主页
                toMain();
                break;
        }
    }

    /**
     * 跳转至主页
     */
    private void toMain() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        UIHelper.jumpAct(this, MainActivity.class, true);
    }

    @Override
    protected void onDestroy() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        super.onDestroy();
    }
}

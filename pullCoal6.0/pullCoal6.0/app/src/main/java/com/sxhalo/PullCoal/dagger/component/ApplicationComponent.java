package com.sxhalo.PullCoal.dagger.component;

import android.content.Context;

import com.sxhalo.PullCoal.common.MyApplication;
import com.sxhalo.PullCoal.dagger.module.ApplicationModule;
import com.sxhalo.PullCoal.dagger.module.HttpModule;
import com.sxhalo.PullCoal.net.api.FreightApi;
import com.sxhalo.PullCoal.net.api.HomeApi;
import com.sxhalo.PullCoal.net.api.OrderApi;

import dagger.Component;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
@Component(modules = {ApplicationModule.class, HttpModule.class})
public interface ApplicationComponent {

    MyApplication getApplication();

    Context getConext();

    HomeApi getHomeApi();

    OrderApi getOrderApi();

    FreightApi getFreightApi();

}

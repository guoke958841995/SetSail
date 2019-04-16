package com.sxhalo.PullCoal.dagger.component;

import com.sxhalo.PullCoal.ui.SelectAreaActivity;
import com.sxhalo.PullCoal.ui.SplashActivity;
import com.sxhalo.PullCoal.ui.freight.AddBookRouteActivity;
import com.sxhalo.PullCoal.ui.freight.MyFreightActivity;
import com.sxhalo.PullCoal.ui.freight.book.BookFreightFragment;
import com.sxhalo.PullCoal.ui.freight.search.FreightSearchFragment;
import com.sxhalo.PullCoal.ui.order.coalorder.CoalOrderFragment;
import com.sxhalo.PullCoal.ui.order.myreleaseFragment.MyReleaseFragment;
import com.sxhalo.PullCoal.ui.order.sendcarorder.SendCarOrderFragment;
import com.sxhalo.PullCoal.ui.order.transportorder.TransportOrderFragment;

import dagger.Component;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
@Component(dependencies = ApplicationComponent.class)
public interface HttpComponent {

    void inject(SplashActivity splashActivity);

    void inject(CoalOrderFragment coalOrderFragment);
    void inject(SendCarOrderFragment sendCarOrderFragment);
    void inject(MyReleaseFragment myReleaseFragment);
    void inject(TransportOrderFragment transportOrderFragment);

//    void inject(MainActivity mainActivity);

    void inject(FreightSearchFragment freightSearchFragment);

    void inject(BookFreightFragment bookFreightFragment);

    void inject(MyFreightActivity myFreightActivity);

    void inject(AddBookRouteActivity addBookRouteActivity);

    void inject(SelectAreaActivity selectAreaActivity);
}

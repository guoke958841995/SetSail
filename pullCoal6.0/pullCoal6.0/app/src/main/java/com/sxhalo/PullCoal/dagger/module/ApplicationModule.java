package com.sxhalo.PullCoal.dagger.module;

import android.content.Context;

import com.sxhalo.PullCoal.common.MyApplication;

import dagger.Module;
import dagger.Provides;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/2 0002
 */
@Module
public class ApplicationModule {

    private Context mContext;

    public ApplicationModule(Context context) {
        this.mContext = context;
    }

    @Provides
    MyApplication provideMyApplication() {
        return (MyApplication) mContext.getApplicationContext();
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}

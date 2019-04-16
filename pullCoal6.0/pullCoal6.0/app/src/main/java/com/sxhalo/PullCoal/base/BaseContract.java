package com.sxhalo.PullCoal.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public interface BaseContract {

    interface BasePresenter<T extends BaseContract.BaseView> {

        void attachView(T view);

        void detachView();
    }


    interface BaseView {

        /**
         * 绑定生命周期
         *
         * @param <T>
         * @return
         */
        <T> LifecycleTransformer<T> bindToLife();

    }
}

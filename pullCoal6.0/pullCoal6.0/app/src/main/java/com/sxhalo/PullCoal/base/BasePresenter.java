package com.sxhalo.PullCoal.base;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public class BasePresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    protected T mView;

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        if (mView != null) {
            mView = null;
        }
    }
}

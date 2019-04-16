package com.sxhalo.PullCoal.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public interface IBaseView {

    /**
     * 根据layoutId,加载页面布局
     *
     * @return 布局对象
     */
    View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 获取当前布局对象
     *
     * @return 布局对象
     */
    View getView();

    /**
     * 获取页面布局文件
     *
     * @return 布局id
     */
    int getContentLayout();

    /**
     * 初始化Dagger的注解器
     */
    void initInjector(ApplicationComponent appComponent);

    void bindView(View view, Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    void initData();

}

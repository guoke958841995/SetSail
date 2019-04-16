package com.sxhalo.PullCoal.ui.pullrecyclerview.uihelper;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.sxhalo.PullCoal.model.HomeData;
import com.sxhalo.PullCoal.ui.pullrecyclerview.view.BaseView;
import com.sxhalo.amoldzhang.library.PullToRefreshListView;

import java.lang.ref.WeakReference;

/**
 * 模板构建实现
 * Created by amoldZhang on 2018/12/18.
 */
public class TemplateContainerImpl
{
    private Activity context;

    private PullToRefreshListView listView;

    private PullToRefreshListViewAdapter adapter;

    public TemplateContainerImpl(Activity context)
    {
        this.context = context;
    }

    private void initListView()
    {
        if (listView == null)
        {
            return;
        }
        adapter = new PullToRefreshListViewAdapter();
        listView.setAdapter(adapter);
    }

    public void setListView(PullToRefreshListView listView)
    {
        this.listView = listView;
        initListView();
    }

    public void startConstruct(HomeData pageModule)
    {
        if (pageModule == null || pageModule.getHomeDataList() == null || pageModule.getHomeDataList().size() == 0)
        {
            return;
        }
        adapter.setData(pageModule);
        adapter.notifyDataSetChanged();
    }

    public class PullToRefreshListViewAdapter extends BaseAdapter
    {
        private HomeData homeData;

        public void setData(HomeData module)
        {
            if (module == null)
            {
                return;
            }
            homeData = module;
        }

        @Override
        public int getItemViewType(int position)
        {
            if (homeData == null || homeData.getHomeDataList() == null || homeData.getHomeDataList().size() <= 0)
            {
                return 1;
            }
            return TemplateManager.getViewType(homeData.getHomeDataList().get(position).getTemplateId());
        }

        @Override
        public int getViewTypeCount()
        {
            return 6;
        }

        @Override
        public int getCount()
        {
            if (homeData == null || homeData.getHomeDataList() == null)
            {
                return 0;
            }
            return homeData.getHomeDataList().size();
        }

        @Override
        public Object getItem(int i)
        {
            if (homeData == null || homeData.getHomeDataList() == null)
            {
                return null;
            }
            return homeData.getHomeDataList().get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (homeData == null || homeData.getHomeDataList() == null)
            {
                return null;
            }
            if (view == null)
            {
                view = TemplateManager.findViewById(new WeakReference<>(context).get(),
                        homeData.getHomeDataList().get(i).getTemplateId());
            }
            if (view instanceof BaseView)
            {
                ((BaseView) view).setData(homeData.getHomeDataList().get(i));
            }
            view.invalidate();
            return view;
        }
    }
}

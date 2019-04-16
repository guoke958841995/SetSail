package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.CustomListView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2017/5/9.
 */
public class HeaderDataView<T> extends HeaderViewInterface<List<T>> {

    @Bind(R.id.title_search)
    TextView titleSearch;
    @Bind(R.id.search_layout_line)
    View line;
    @Bind(R.id.layout_search_listview)
    CustomListView listView;

    private int layout;
    private View view;
    private QuickAdapter<T> mAdaptet;
    private String title;

    public HeaderDataView(Activity context, int layout, String title) {
        super(context);
        this.layout = layout;
        this.title = title;
    }


    @Override
    protected void getView(List<T> coals, ListView listView) {
        try {
            view = mInflate.inflate(R.layout.search_layout_all_view, listView, false);
            ButterKnife.bind(this, view);
            dealWithTheView(coals);
            listView.addHeaderView(view);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("界面控件控制", e.toString());
        }
    }

    @OnClick(R.id.layout_search_more)
    public void onViewClicked() {
        if (OnListener != null) {
            OnListener.HeaderSearchMore();
        }
    }

    public void setViewLine(boolean flage){
        if (line != null){
            if (flage){
                line.setVisibility(View.VISIBLE);
            }else{
                line.setVisibility(View.GONE);
            }
        }
    }

    public void dealWithTheView(List<T> coals) {
        titleSearch.setText(title);
        GHLog.i("数据处理前","数据个数 "+coals.size()+"");
        if (coals.size()>3){
            List<T> t = new ArrayList<T>();
            for (int i=0;i<3;i++){
                t.add(i,coals.get(i));
            }
            coals = t;
        }
        GHLog.i("数据处理后","数据个数 "+coals.size()+"");
        mAdaptet = new QuickAdapter<T>(mContext, layout, coals) {
            @Override
            protected void convert(BaseAdapterHelper helper, T data, int pos) {
                try {
                    if (OnListener != null) {
                        OnListener.HeaderSetView(helper, data, pos);
                    }
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("赋值", e.toString());
                }
            }
        };
        listView.setAdapter(mAdaptet);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (OnListener != null) {
                    OnListener.HeaderItemOnClick(view, position);
                }
            }
        });
    }

    /**
     * 添加点击外接接口
     */
    private HeaderOnListener OnListener;

    public void setOnListener(HeaderOnListener OnListener) {
        this.OnListener = OnListener;
    }
    public interface HeaderOnListener<T> {
        void HeaderItemOnClick(View view, int pos);
        void HeaderSearchMore();
        void HeaderSetView(BaseAdapterHelper helper, T data, int pos);
    }

    public void setVisiable (boolean flag) {
        if (flag) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}

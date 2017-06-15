package com.sxhalo.myexpandablelistview.view;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.sxhalo.myexpandablelistview.R;
import com.sxhalo.myexpandablelistview.adapter.FirstAdapter;
import com.sxhalo.myexpandablelistview.model.DownloadData;
import com.sxhalo.myexpandablelistview.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by amoldZhang on 2016/10/22.
 */
public class HeaderFirstView extends HeaderViewInterface<ArrayList<String>> {


    @BindView(R.id.downlonding_lv)
    CustomListView downlondingLv;

    private FirstAdapter firstAdapter;

    public HeaderFirstView(Activity context) {
        super(context);
    }

    @Override
    protected void getView(ArrayList<String> fatherList, ExpandableListView listView) {
        View view = mInflate.inflate(R.layout.header_first_layout, listView, false);
        ButterKnife.bind(this, view);
        dealWithTheView(fatherList);
        listView.addHeaderView(view);
    }


    private void dealWithTheView(ArrayList<String> fatherList) {
        firstAdapter =  new FirstAdapter(mContext,fatherList);
        downlondingLv.setAdapter(firstAdapter);
        downlondingLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToastUtils.showText(mContext,"您点击了第 "+i+" 选项"+"内容为"+mEntity.get(i));
            }
        });
    }


}

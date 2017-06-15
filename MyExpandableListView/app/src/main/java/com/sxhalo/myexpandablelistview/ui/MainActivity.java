package com.sxhalo.myexpandablelistview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.sxhalo.myexpandablelistview.R;
import com.sxhalo.myexpandablelistview.adapter.ExpandableAdpter;
import com.sxhalo.myexpandablelistview.model.DownloadData;
import com.sxhalo.myexpandablelistview.tools.BaseDataTools;
import com.sxhalo.myexpandablelistview.view.HeaderFirstView;
import com.sxhalo.myexpandablelistview.view.SmoothListView.SmoothExpandableListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.listView)
    ExpandableListView smoothListView;

    private ArrayList<String> fatherList;   //放置父类数据
    private List<List<String>> childList;  //子类数据
    private List<String> list;   //中间数据保存量

    private HeaderFirstView headerFirstView;

    private ExpandableAdpter mAdapter;  //绑定数据的adpter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setData();
        setView();
    }

    private void setData() {
        fatherList = new ArrayList<String>();
        childList = new ArrayList<List<String>>();

        fatherList.add("资讯");
        fatherList.add("新闻");
        fatherList.add("视频");
        fatherList.add("搞笑");

        for(int i=0;i<fatherList.size();i++){
            initChild(i);
        }
    }

    private void initChild(int i) {
        list = new ArrayList<String>();
        list.add(i+"  资讯");
        list.add(i+"  新闻");
        list.add(i+"  视频");
        list.add(i+"  搞笑");
        childList.add(list);

    }


    private void setView() {

        // 设置筛选数据
        headerFirstView = new HeaderFirstView(this);
        headerFirstView.fillView(fatherList, smoothListView);

        // 设置ListView数据
        mAdapter = new ExpandableAdpter(this,fatherList,childList);
        smoothListView.setAdapter(mAdapter);

    }


}

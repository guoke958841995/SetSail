package com.sxhalo.myexpandablelistview.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sxhalo.myexpandablelistview.R;
import java.util.List;

/**
 * Created by amoldZhang on 2016/10/23.
 */

public class ExpandableAdpter extends BaseExpandableListAdapter{


    private final Activity mActivity;
    public List<String> faList;
    public List<List<String>> chList;

    public ExpandableAdpter(Activity mActivity, List<String> faList, List<List<String>> chList) {
        this.mActivity = mActivity;
        this.faList = faList;
        this.chList = chList;
    }

    @Override
    public int getGroupCount() {
        return faList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return chList.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return faList.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return chList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
        View view = LayoutInflater.from(mActivity).inflate(
                R.layout.item_download_child, null);
        TextView textView = (TextView) view.findViewById(R.id.child_textView);
        textView.setText(faList.get(groupPosition));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        View view = LayoutInflater.from(mActivity).inflate(
                R.layout.item_download_child, null);
        TextView textView = (TextView) view.findViewById(R.id.child_textView);
        textView.setText(chList.get(groupPosition).get(childPosition));
        return view;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {  //点击子类触发事件
        Toast.makeText(mActivity,
                "第" + groupPosition + "大项，第" + childPosition + "小项被点击了",
                Toast.LENGTH_SHORT).show();
        return true;
    }
}

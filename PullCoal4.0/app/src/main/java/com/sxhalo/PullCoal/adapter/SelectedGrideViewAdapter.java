package com.sxhalo.PullCoal.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by amoldZhang on 2017/1/3.
 */
public class SelectedGrideViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
    // 点击的位置，初始化默认选择第一个
    private int clickStatus = 0;

    public SelectedGrideViewAdapter(Activity mActivity, List<Map<String, String>> mapList) {
        this.mapList = mapList;
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
    }

    //  定义一个公有方法，在activity中调用
    public void setSeclection(int position) {
        this.clickStatus = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return mapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.selected_gride_view_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map<String, String> maps = getItem(position);
        viewHolder.selectText.setText(maps.get("name"));

        if (clickStatus == position) {
            viewHolder.selectImage.setImageResource(R.mipmap.coal_type_pressed);
        } else {
            viewHolder.selectImage.setImageResource(R.mipmap.coal_type_normal);
        }
        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.select_image)
        ImageView selectImage;
        @Bind(R.id.select_text)
        TextView selectText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

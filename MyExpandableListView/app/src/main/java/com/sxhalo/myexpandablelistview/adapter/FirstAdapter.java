package com.sxhalo.myexpandablelistview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxhalo.myexpandablelistview.R;
import com.sxhalo.myexpandablelistview.model.DownloadData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amoldZhang on 2016/10/22.
 */
public class FirstAdapter extends MyBaseListAdapter<String> {


    public FirstAdapter(Context mContext, ArrayList<String> stringList) {
        super(mContext, stringList);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        View contentView = view;
        if (contentView == null) {
            contentView = mInflate.inflate(R.layout.item_download_child, null);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        String name = getItem(position);
        holder.child_textView.setText(name);

        return contentView;
    }

    static class ViewHolder {
        @BindView(R.id.child_textView)
        TextView child_textView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}

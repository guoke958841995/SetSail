package com.sxhalo.PullCoal.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.CoalOrderStatusEntity;
import com.sxhalo.PullCoal.model.Orders;
import com.sxhalo.PullCoal.utils.DeviceUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liz on 2018/5/10.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private Activity mContext;
    private LayoutInflater mInflater;
    private List<CoalOrderStatusEntity> list;
    private int itemWidth;

    public TimeLineAdapter(Activity context, List<CoalOrderStatusEntity> list) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        itemWidth = (DeviceUtil.getWidth(context) - DeviceUtil.dp2px(context,30))/ list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.time_line_item_horizontal, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = itemWidth;
        view.setLayoutParams(params);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0) {
            holder.startLine.setVisibility(View.GONE);
        } else {
            holder.startLine.setVisibility(View.VISIBLE);
        }
        if (position == list.size() - 1) {
            holder.endLine.setVisibility(View.GONE);
        } else {
            holder.endLine.setVisibility(View.VISIBLE);
        }
        holder.tvStatus.setText(list.get(position).getMemo());
        if (list.get(position).isCurrentStatus()) {
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.actionsheet_blue));
            holder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_status_1));
        } else {
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.line_color));
            holder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_status_0));
        }
        if (list.get(position).isStartLineIsBlue()) {
            holder.startLine.setBackgroundColor(mContext.getResources().getColor(R.color.actionsheet_blue));
        } else {
            holder.startLine.setBackgroundColor(mContext.getResources().getColor(R.color.lighter_gray));
        }
        if (list.get(position).isEndLineIsBlue()) {
            holder.endLine.setBackgroundColor(mContext.getResources().getColor(R.color.actionsheet_blue));
        } else {
            holder.endLine.setBackgroundColor(mContext.getResources().getColor(R.color.lighter_gray));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.start_line)
        View startLine;
        @Bind(R.id.tv_status)
        TextView tvStatus;
        @Bind(R.id.imageView)
        ImageView imageView;
        @Bind(R.id.end_line)
        View endLine;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

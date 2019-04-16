package com.sxhalo.PullCoal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;

import java.util.List;


/**
 *
 * popupwindow适配器
 *
 */
public class PopupwindowListAdapter extends BaseListAdapter<String> {

	private int pos;

	public PopupwindowListAdapter(Context context, List<String> string){
		super(context, string);
	}

	public void PopupwindowListAdapter(int pos){
		this.pos = pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView != null && convertView instanceof LinearLayout) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.popupwindow_list_item, parent,false);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}
		if(pos == position){
			viewHolder.image.setVisibility(View.VISIBLE);
		}else{
			viewHolder.image.setVisibility(View.GONE);
		}
		viewHolder.text.setText(getItem(position)+"公里");
		return convertView;
	}

	public void setItem(int pos){
		this.pos = pos;
		notifyDataSetChanged();
	}

	public static class ViewHolder{
		TextView text;
		ImageView image;

		public ViewHolder(View view) {
			text = (TextView) view.findViewById(R.id.tv_list_item);
			image = (ImageView) view.findViewById(R.id.img_select);
		}

	}

}

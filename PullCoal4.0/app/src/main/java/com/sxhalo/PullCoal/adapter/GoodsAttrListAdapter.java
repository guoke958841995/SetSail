package com.sxhalo.PullCoal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.db.entity.Dictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 属性listview的适配器
 */
public class GoodsAttrListAdapter extends BaseAdapter {

    private Context context;
    private List<Dictionary> data;

    public Map<String, FilterEntity> getMap() {
        return map;
    }

    public void setMap(Map<String, FilterEntity> map) {
        this.map = map;
    }

    private Map<String, FilterEntity> map  = new HashMap<String, FilterEntity>();

    public GoodsAttrListAdapter(Context context, List<Dictionary> data) {
        this.context = context;
        this.data = data;
    }

    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View v, ViewGroup parent) {
        final MyView myView;
        if (v == null) {
            myView = new MyView();
            v = View.inflate(context, R.layout.item_goods_attr_list, null);
            myView.name = (TextView) v.findViewById(R.id.attr_list_name);
//            myView.img = (ImageView) v.findViewById(R.id.attr_list_img);
            myView.grid = (GridView) v.findViewById(R.id.attr_list_grid);
            myView.grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
            v.setTag(myView);
        } else {
            myView = (MyView) v.getTag();
        }
//        if (data.get(position).getSaleVo().size() > 3) {
//            myView.img.setVisibility(View.VISIBLE);
//        } else {
//            myView.img.setVisibility(View.GONE);
//        }
        myView.name.setText(data.get(position).title);
        final GoodsAttrsAdapter adapter = new GoodsAttrsAdapter(context);
        myView.grid.setAdapter(adapter);
        adapter.notifyDataSetChanged(data.get(position).isNameIsChecked(), data.get(position).list);
//        myView.img.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (data.get(position).isNameIsChecked()) {
//                    ((ImageView) v).setImageResource(R.drawable.sort_common_down);
//                } else {
//                    ((ImageView) v).setImageResource(R.drawable.sort_common_up);
//                }
//                data.get(position).setNameIsChecked(!data.get(position).isNameIsChecked());
//                adapter.notifyDataSetChanged(data.get(position).isNameIsChecked(), data.get(position).getSaleVo());
//            }
//        });
        myView.grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //设置当前选中的位置的状态为非。
                if (!data.get(position).list.get(arg2).isChecked()) {
                    data.get(position).list.get(arg2).setChecked(true);
                    map.put(data.get(position).title, data.get(position).list.get(arg2));
                }
//                if (data.get(position).getList().get(arg2).isChecked()) {
//                    map.put(data.get(position).getTitle(), data.get(position).getList().get(arg2));
//                } else {
//                    map.remove(data.get(position).getTitle());
//                }
//                data.get(position).getList().get(arg2).setChecked(!data.get(position).getList().get(arg2).isChecked());
                for (int i = 0; i < data.get(position).list.size(); i++) {
                    //跳过已设置的选中的位置的状态
                    if (i == arg2) {
                        continue;
                    }
                    data.get(position).list.get(i).setChecked(false);
                }
//                if (!data.get(position).isNameIsChecked()) {
//                    myView.img.setImageResource(R.drawable.sort_common_up);
//                } else {
//                    myView.img.setImageResource(R.drawable.sort_common_down);
//                }
//                adapter.notifyDataSetChanged(!data.get(position).isNameIsChecked(), data.get(position).getSaleVo());
                adapter.notifyDataSetChanged();
            }
        });
        return v;
    }

    static class MyView {
        public TextView name;
//        public ImageView img;
        public GridView grid;
    }

}

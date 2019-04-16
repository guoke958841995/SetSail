package com.sxhalo.PullCoal.ui.recyclerviewmode;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 *   悬浮布局中 煤种筛选
 * Created by amoldZhang on 2018/7/25.
 */
public class StickySuspensionGroupView extends PopupWindow {


    @Bind(R.id.left_class_recyvler)
    RecyclerView leftClassRecyvler;
    @Bind(R.id.right_class_recyvler)
    RecyclerView rightClassRecyvler;

    private Activity mActivity;
    private View contentView;
    private CommonAdapter<FilterEntity> spinerAdapter;
    private CommonAdapter<FilterEntity> spinerSublayoutAdapter;


    private String type;
    private List<FilterEntity> mLeftRvData;
    private int age1,age2;

    public StickySuspensionGroupView(Activity mContext,View view,int hight) {
        this.mActivity = mContext;
        contentView = LayoutInflater.from(mActivity).inflate(R.layout.screening_suspension_group_layout, null, false);
        ButterKnife.bind(this, contentView);

        //设置背景透明才能显示
        setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationTop 是自己事先定义好的]
        this.setAnimationStyle(R.style.AnimationTop);
        this.setContentView(contentView);
        this.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
//        this.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        this.setHeight(hight);
        // 使其聚集
        this.setFocusable(true);
        // 设置允许在外点击消失
        this.setOutsideTouchable(true);
        this.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onDissmiss != null){
                    onDissmiss.onDismissListener(age1,age2,type);
                }
//                darkenBackground(1f);
            }
        });
        //刷新状态（必须刷新否则无效）
        this.update();
    }

    public void showPopWindow(View view,List<FilterEntity> filterEntityList,int pos,String type){
        this.type = type;
        try {
            if (spinerAdapter == null){
                fillView(filterEntityList,pos);
            }else{
                this.mLeftRvData = filterEntityList;

                spinerAdapter.notifyDataSetChanged();

                spinerSublayoutAdapter.notifyDataSetChanged();
            }
            showAsDropDown(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fillView(List<FilterEntity> mLeftRvData,int pos) {
        this.mLeftRvData = mLeftRvData;
        //设置布局管理器
        leftClassRecyvler.setLayoutManager(new LinearLayoutManager(mActivity));
        //设置adapter
        spinerAdapter = new CommonAdapter<FilterEntity>(mActivity, R.layout.group_layout_item, mLeftRvData){
            @Override
            protected void convert(ViewHolder holder, FilterEntity item, final int position){
                holder.setText(R.id.tv_list_item, item.dictValue);
                if (item.isChecked()) {
                    ((TextView)holder.getView(R.id.tv_list_item)).setTextColor(mActivity.getResources().getColor(R.color.app_title_text_color));
                } else {
                    ((TextView)holder.getView(R.id.tv_list_item)).setTextColor(mActivity.getResources().getColor(R.color.app_title_text_color_normal));
                }
            }
        };
        spinerAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                try {
                    List<FilterEntity> list = spinerAdapter.getDatas();
                    for (FilterEntity filterEntity : list){
                        filterEntity.setChecked(false);
                    }
                    age1 = position;
                    spinerAdapter.getDatas().get(position).setChecked(true);
                    spinerAdapter.notifyDataSetChanged();

                    spinerSublayoutAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });


        leftClassRecyvler.setAdapter(spinerAdapter);

        //设置布局管理器
        rightClassRecyvler.setLayoutManager(new LinearLayoutManager(mActivity));
        //设置adapter
        spinerSublayoutAdapter = new CommonAdapter<FilterEntity>(mActivity, R.layout.group_layout_item, mLeftRvData.get(pos).list){
            @Override
            protected void convert(ViewHolder holder, FilterEntity item, final int position){
                holder.setText(R.id.tv_list_item, item.dictValue);
                if (item.isChecked()) {
                    ((TextView)holder.getView(R.id.tv_list_item)).setTextColor(mActivity.getResources().getColor(R.color.app_title_text_color));
                } else {
                    ((TextView)holder.getView(R.id.tv_list_item)).setTextColor(mActivity.getResources().getColor(R.color.app_title_text_color_normal));
                }
            }
        };
        spinerSublayoutAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                List<FilterEntity> list = spinerSublayoutAdapter.getDatas();
                for (FilterEntity filterEntity : list){
                    filterEntity.setChecked(false);
                }

                age2 = position;
                spinerSublayoutAdapter.getDatas().get(position).setChecked(true);

                spinerSublayoutAdapter.notifyDataSetChanged();
                dismiss();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        rightClassRecyvler.setAdapter(spinerSublayoutAdapter);
    }

    /**
     * 改变背景颜色
     */
    private void darkenBackground(Float bgcolor) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgcolor;
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setAttributes(lp);
    }

    private DismissListener onDissmiss;
    public void setDismissListener(DismissListener onDissmiss) {
        this.onDissmiss = onDissmiss;
    }
    public static abstract class DismissListener {
        public void onDismissListener(int age1,int age2,String type) {
        }
    }
}

package com.sxhalo.PullCoal.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.CouponsEntity;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 代金券功能展示
 * Created by amoldZhang on 2018/1/5.
 */
public class RecyclerviewHorizontalViewAdapter extends RecyclerView.Adapter<RecyclerviewHorizontalViewAdapter.ViewHolder>{

    private Activity mContext;
    private LayoutInflater mInflater;
    private List<CouponsEntity> payMentList;

    public RecyclerviewHorizontalViewAdapter(Activity context, List<CouponsEntity> datats){
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        payMentList = datats;
    }
    @Override
    public RecyclerviewHorizontalViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.recommend_gallery_item,viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerviewHorizontalViewAdapter.ViewHolder holder,final int position) {
        try {
            CouponsEntity payMent = payMentList.get(position);
            int usePage = payMent.getUsePage();
            if (usePage == 0){
                if (usePage == Integer.valueOf(payMent.getNumber())){
                    holder.addUse.setEnabled(false);
                }else{
                    holder.addUse.setEnabled(true);
                }
                holder.reduceUse.setEnabled(false);
                holder.voucherSelectedNumber.setText("0");
            }else{
                if (usePage < Integer.valueOf(payMent.getNumber())){
                    holder.addUse.setEnabled(true);
                }else{
                    holder.addUse.setEnabled(false);
                }
                holder.reduceUse.setEnabled(true);
                holder.voucherSelectedNumber.setText(usePage + "");
            }
            holder.tvTitle.setText(payMent.getCouponName());
            holder.tvPrice.setText((Double.valueOf(payMent.getDenomination()) / 100) + "");
            holder.tvTime.setText(payMent.getPeriodValidity());
            holder.tvNumber.setText(payMent.getNumber() + "张");

            holder.reduceUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemReduceClick(holder,position);
                    }
                }
            });
            holder.addUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemAddClick(holder,position);
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            GHLog.e("代金券条目展示", e.toString());
        }
    }


    @Override
    public int getItemCount() {
        return payMentList.size();
    }


    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
    /**
     * RecyclerView的Item点击事件监听接口
     *
     * @author liyunlong
     * @date 2016/11/21 9:43
     */
    public interface  OnItemClickListener {

        /**
         * 当ItemView的单击事件触发时调用
         */
        void onItemClick(View view, int position);
        /**
         * 当ItemView的单击事件触发时调用
         */
        void onItemAddClick(RecyclerviewHorizontalViewAdapter.ViewHolder holder,int position);
        /**
         * 当ItemView的单击事件触发时调用
         */
        void onItemReduceClick(RecyclerviewHorizontalViewAdapter.ViewHolder holder,int position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_price)
        TextView tvPrice;
        @Bind(R.id.tv_number)
        TextView tvNumber;
        @Bind(R.id.reduce_use)
        public Button reduceUse;
        @Bind(R.id.add_use)
        public Button addUse;
        @Bind(R.id.voucher_selected_number)
        public TextView voucherSelectedNumber;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(itemView,getAdapterPosition());
                    }
                }
            });
        }
    }
}

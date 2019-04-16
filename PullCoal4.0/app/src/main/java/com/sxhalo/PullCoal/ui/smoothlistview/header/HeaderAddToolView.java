package com.sxhalo.PullCoal.ui.smoothlistview.header;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.MineMouthChoiceActivity;
import com.sxhalo.PullCoal.model.ActivityData;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 服务中动态功能添加空间
 * Created by amoldZhang on 2019/3/5.
 */
public class HeaderAddToolView extends HeaderViewInterface<ActivityData> {

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ActivityData mData;
    private List<Integer> myListData = new ArrayList<>();
    private CommonAdapter<Integer> myAdapter;


    public HeaderAddToolView(Activity context) {
        super(context);
        this.mContext = context;

        myListData.add(0);
        myListData.add(R.mipmap.icon_coal_sampling);
        myListData.add(R.mipmap.icon_coal_laboratory);
        myListData.add(1);
        myListData.add(R.mipmap.icon_coal_online_customer);
        myListData.add(R.mipmap.icon_coal_hotline);
    }

    @Override
    protected void getView(ActivityData data, ListView listView) {
        View view = mInflate.inflate(R.layout.header_add_tool_view, listView, false);
        ButterKnife.bind(this, view);
        this.mData = data;
        initView();
        listView.addHeaderView(view);
    }


    private void initView() {
        try {
            final GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
            mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int count = 1;
                    if (myListData.get(position) == 0 || myListData.get(position) == 1) {
                        count = mLayoutManager.getSpanCount();//独占一行
                    } else {
                        count = 1;//只占一行中的一列
                    }
                    return count;
                }
            });
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.addItemDecoration(new MySpacingItemDecoration(mContext,10));//边距和分割线，需要自己定义

            mRecyclerView.setNestedScrollingEnabled(false); //设置mRecyclerView 是否可滑动
            int  mRecyclerViewHeight = mRecyclerView.computeVerticalScrollRange(); //获取空件在屏幕占的高度

            myAdapter = new CommonAdapter<Integer>(mContext,R.layout.list_item_header_add_view_item,myListData){
                @Override
                protected void convert(ViewHolder holder, Integer str, int position) {
                    if (str == 0 || str == 1){
                        holder.setText(R.id.title,str == 0?"增值服务":"服务中心");
                        holder.getView(R.id.title).setVisibility(View.VISIBLE);
                        holder.getView(R.id.iv_image).setVisibility(View.GONE);
                    }else{
                        holder.getView(R.id.title).setVisibility(View.GONE);
                        holder.getView(R.id.iv_image).setVisibility(View.VISIBLE);
                        ((BaseActivity)mContext).getImageManager().loadResImage(str,(ImageView)holder.getView(R.id.iv_image));
                    }
                }
            };

            myAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                      switch (position){
                          case 0:
                              break;
                          case 1:
                              gotoSamplingTest("采样");
                              break;
                          case 2:
                              gotoSamplingTest("化验");
                              break;
                          case 3:
                              break;
                          case 4:
                              if (checkAppExist("com.tencent.mobileqq")) {
                                  mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + mContext.getResources().getString(R.string.service_qq) + "&version=1")));
                              } else {
                                  ((BaseActivity)mContext).displayToast("本机未安装QQ应用！");
                              }
                              break;
                          case 5:
                              Map<String,String> map = new HashMap<String, String>();
                              map.put("tel",mContext.getResources().getString(R.string.service_hot_line));
                              UIHelper.showCollTel(mContext,map,false);
                              break;

                      }
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
            mRecyclerView.setAdapter(myAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoSamplingTest(String title) {
        String userId = SharedTools.getStringValue(mContext,"userId","-1");
        if ("-1".equals(userId)){
            UIHelper.jumpActLogin(mContext, false);
        }else{
            UIHelper.jumpAct(mContext, MineMouthChoiceActivity.class, title,false);
        }
    }

    private boolean checkAppExist (String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    class MySpacingItemDecoration extends RecyclerView.ItemDecoration{
        private Activity mActivity;
        private int halfSpaceInPx;

        public MySpacingItemDecoration(Activity mActivity, int horizontalSpaceInPx) {
            halfSpaceInPx = horizontalSpaceInPx;
            this.mActivity = mActivity ;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent != null) {
                int childIndex = parent.getChildAdapterPosition(view);
                RecyclerView.Adapter adapter = parent.getAdapter();
                if (adapter != null) {
                   if (childIndex != 0 && childIndex != 3){
                       if (childIndex >0 && childIndex < 3){
                           if (childIndex % 2 == 0) {
                               outRect.left = BaseUtils.dip2px(mActivity, 2);
                               outRect.right = BaseUtils.dip2px(mActivity, halfSpaceInPx);
                           } else {
                               outRect.left = BaseUtils.dip2px(mActivity, halfSpaceInPx);
                               outRect.right = BaseUtils.dip2px(mActivity, 2);
                           }
                       }else if (childIndex > 3 && childIndex < adapter.getItemCount()){
                           if (childIndex % 2 == 0) {
                               outRect.left = BaseUtils.dip2px(mActivity, halfSpaceInPx);
                               outRect.right = BaseUtils.dip2px(mActivity, 2);
                           } else {
                               outRect.left = BaseUtils.dip2px(mActivity, 2);
                               outRect.right = BaseUtils.dip2px(mActivity, halfSpaceInPx);
                           }
                       }

                   }
                }
            }
        }
    }
}

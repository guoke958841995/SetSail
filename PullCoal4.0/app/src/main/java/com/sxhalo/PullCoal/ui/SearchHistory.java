package com.sxhalo.PullCoal.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.UserSearchHistory;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户搜索历史记录列表
 * Created by amoldZhang on 2019/3/25.
 */
public class SearchHistory {

    @Bind(R.id.layout_search_history)
    LinearLayout searchHistory;
    @Bind(R.id.recycler_search_list)
    RecyclerView recyclerSearchList;
    private CommonAdapter<UserSearchHistory> mAdapter;
    private View view;
    private Context context;
    private String TYPE; // 当前搜索历史记录位置
    private SearchHistoryListener searchHistoryListener; // 历史记录界面监听器
    private List<UserSearchHistory> queryAll = new ArrayList<>();

    public SearchHistory(Context context,String TYPE,SearchHistoryListener searchHistoryListener){
        this.context = context;
        this.TYPE = TYPE;
        this.searchHistoryListener = searchHistoryListener;
        ViewGroup parentView = (ViewGroup)((Activity)context).getWindow().getDecorView();
        view = LayoutInflater.from(context).inflate(R.layout.header_search_history,parentView,false);
        ButterKnife.bind(this, view);
    }

    /**
     *  布局初始化
     * @return
     */
    public View getView() {
        notifyHistoryView();
        return view;
    }

    /**
     * 布局刷新
     */
    public void notifyHistoryView() {
        if (mAdapter != null){
            queryAll.clear();
            mAdapter.notifyDataSetChanged();
        }
        queryAll.addAll(OrmLiteDBUtil.getQueryByWhere(UserSearchHistory.class,"search_type",new String[]{TYPE}));
        if (queryAll == null || queryAll.size() == 0){
            searchHistory.setVisibility(View.GONE);
        }else{
            searchHistory.setVisibility(View.VISIBLE);
            Collections.reverse(queryAll);
            if (mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }else{
                initView();
            }
            mAdapter.notifyDataSetChanged();
        }
        GHLog.e("历史记录个数",queryAll.size() + "");
    }

    private void initView() {
        RecyclerView.LayoutManager manager = new GridLayoutManager(context, 3);
        recyclerSearchList.setLayoutManager(manager);

        recyclerSearchList.addItemDecoration(new RecyclerViewSpacesItemDecoration(5,5,5,5));

        mAdapter = new CommonAdapter<UserSearchHistory>(context, R.layout.layout_search_history_item, queryAll){
            @Override
            protected void convert(ViewHolder holder, UserSearchHistory userSearchHistory, final int position){
                holder.setText(R.id.item_search_text, userSearchHistory.getSearchText());
            }
        };
        recyclerSearchList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                try {
                    String keyWord = mAdapter.getDatas().get(position).getSearchText();
                    if (StringUtils.isEmpty(keyWord)) {
                        return;
                    }
                    //返回历史搜索内容
                    if (searchHistoryListener != null){
                        searchHistoryListener.onItemClick(keyWord);
                    }
                } catch (Exception e) {
                    Log.i("搜索历史点击", e.toString());
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    /**
     * 保存用户搜索记录
     * @param keyWord
     */
    public void savekeyWord(String keyWord){
        UserSearchHistory userSearchHistory = new UserSearchHistory();
        userSearchHistory.setSearchText(keyWord);
        userSearchHistory.setSearchType(TYPE);
        OrmLiteDBUtil.insert(userSearchHistory);

        List<UserSearchHistory> queryAll = OrmLiteDBUtil.getQueryByWhere(UserSearchHistory.class,"search_type",new String[]{TYPE});
        if (queryAll.size() == 10){
            UserSearchHistory userSh = queryAll.get(0);
            OrmLiteDBUtil.deleteWhere(UserSearchHistory.class,"search_text",new String[]{userSh.getSearchText()+""});
        }
        //布局刷新
        notifyHistoryView();
    }


    @OnClick({R.id.clear_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_view: //删除历史记录
                OrmLiteDBUtil.deleteWhere(UserSearchHistory.class,"search_type",new String[]{TYPE});
                notifyHistoryView();
                if (searchHistoryListener != null){
                    searchHistoryListener.onClearData();
                }
                break;
        }
    }

    public interface SearchHistoryListener{
         void onItemClick(String keyWord);
         void onClearData();
    }
}

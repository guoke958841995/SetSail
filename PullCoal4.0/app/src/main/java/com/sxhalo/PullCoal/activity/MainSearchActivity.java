package com.sxhalo.PullCoal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.UserContrast;
import com.sxhalo.PullCoal.db.entity.UserSearchHistory;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 首页跳转后的搜索界面
 */
public class MainSearchActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.tv_search)
    TextView tvSearch;
    @Bind(R.id.re_search)
    RelativeLayout reSearch;
    @Bind(R.id.tv_coal)
    TextView tvCoal;
    @Bind(R.id.tv_information_department)
    TextView tvInformationDepartment;
    @Bind(R.id.tv_mine)
    TextView tvMine;
    @Bind(R.id.layout_appoint_content)
    LinearLayout layoutAppointContent;

    @Bind(R.id.layout_search_history)
    LinearLayout layoutSearchHistory;
    @Bind(R.id.recycler_search_list)
    RecyclerView recyclerSearchList;
    private CommonAdapter<UserSearchHistory> mAdapter;


    Map<String, String> params = new HashMap<String, String>();
    private String keyWord;  //搜索内容
    private List<UserSearchHistory> queryAll = new ArrayList<>();


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main_search);
    }

    @Override
    protected void initTitle() {
        initListener();
    }

    @Override
    protected void getData() {

    }

    /**
     * 初始化首页搜索记录界面
     */
    private void initSearchHistory(){
        queryAll.addAll(OrmLiteDBUtil.getQueryByWhere(UserSearchHistory.class,"search_type",new String[]{"1"}));
        if (queryAll.size() != 0){
            layoutSearchHistory.setVisibility(View.VISIBLE);
        }else{
            layoutSearchHistory.setVisibility(View.GONE);
        }
        Collections.reverse(queryAll);
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }else{
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, 3);
            recyclerSearchList.setLayoutManager(manager);

            recyclerSearchList.addItemDecoration(new RecyclerViewSpacesItemDecoration(5,5,5,5));

            mAdapter = new CommonAdapter<UserSearchHistory>(this, R.layout.layout_search_history_item, queryAll){
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
                        keyWord = mAdapter.getDatas().get(position).getSearchText();
                        if (StringUtils.isEmpty(keyWord)) {
                            return;
                        }
                        //搜索
                        Intent intent = new Intent(MainSearchActivity.this, SearchResultActivity.class);
                        intent.putExtra("keyword", keyWord);
                        startActivity(intent);
                        etSearch.setText("");
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        Log.i("搜索历史点击", e.toString());
                    }
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null){
            queryAll.clear();
            mAdapter.notifyDataSetChanged();
        }
        initSearchHistory();
    }

    private void initListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().trim().length() > 0) {
                    reSearch.setVisibility(View.VISIBLE);
                    layoutAppointContent.setVisibility(View.GONE);
                    layoutSearchHistory.setVisibility(View.GONE);
                    tvSearch.setText(etSearch.getText().toString().trim());
                } else {
                    layoutAppointContent.setVisibility(View.VISIBLE);
                    reSearch.setVisibility(View.GONE);
                    tvSearch.setText("");
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyWord = etSearch.getText().toString().trim();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    if (!StringUtils.isEmpty(keyWord)) {
                        Intent intent = new Intent(MainSearchActivity.this, SearchResultActivity.class);
                        intent.putExtra("keyword", keyWord);
                        startActivity(intent);
                        layoutAppointContent.setVisibility(View.VISIBLE);
                        reSearch.setVisibility(View.GONE);
                        etSearch.setText("");

                        savekeyWord(keyWord);
                    }
                    return true;
                }
                return false;
            }
        });

        reSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                keyWord = etSearch.getText().toString().trim();
                if (StringUtils.isEmpty(keyWord)) {
                    return;
                }
                //搜索
                Intent intent = new Intent(MainSearchActivity.this, SearchResultActivity.class);
                intent.putExtra("keyword", keyWord);
                startActivity(intent);
                layoutAppointContent.setVisibility(View.VISIBLE);
                reSearch.setVisibility(View.GONE);
                etSearch.setText("");

                savekeyWord(keyWord);
            }
        });
    }

    /**
     * 将搜索内容存储入数据库中，并更新界面
     * @param keyWord
     */
    private void savekeyWord(String keyWord){
        // 将数据存入数据库中
        UserSearchHistory userSearchHistory = new UserSearchHistory();
        userSearchHistory.setSearchText(keyWord);
        userSearchHistory.setSearchType("1");
        OrmLiteDBUtil.insert(userSearchHistory);

        // 搜索历史不能超过10条
        List<UserSearchHistory> queryAll = OrmLiteDBUtil.getQueryByWhere(UserSearchHistory.class,"search_type",new String[]{"1"});
        if (queryAll.size() == 10){
            UserSearchHistory userSh = queryAll.get(0);
            OrmLiteDBUtil.deleteWhere(UserSearchHistory.class,"search_text",new String[]{userSh.getSearchText()+""});
        }
        //更新界面
        onResume();
    }

    @OnClick({R.id.iv_back, R.id.tv_coal, R.id.tv_information_department, R.id.tv_mine,R.id.tv_feirght,R.id.tv_driver, R.id.tv_purchase,R.id.clear_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_coal:
                UIHelper.showSearch(MainSearchActivity.this, Constant.Search_Coal);
                break;
            case R.id.tv_feirght:
                UIHelper.showSearch(MainSearchActivity.this, Constant.Search_Freight);
                break;
            case R.id.tv_driver:
                UIHelper.showSearch(MainSearchActivity.this, Constant.Search_Driver);
                break;
            case R.id.tv_purchase:
                UIHelper.showSearch(MainSearchActivity.this, Constant.Search_Purchase);
                break;
            case R.id.tv_information_department:
                UIHelper.showFindPunctuation(this, SearchPunctuationActivity.class,"查信息部",null);
                break;
            case R.id.tv_mine:
                UIHelper.showFindPunctuation(this, SearchPunctuationActivity.class,"查找矿口",null);
                break;
            case R.id.clear_view:
                OrmLiteDBUtil.deleteAll(UserSearchHistory.class);
                List<UserSearchHistory> queryAll = OrmLiteDBUtil.getQueryByWhere(UserSearchHistory.class,"search_type",new String[]{"1"});
                if (queryAll.size() != 0){
                    layoutSearchHistory.setVisibility(View.VISIBLE);
                }else{
                    layoutSearchHistory.setVisibility(View.GONE);
                }
                break;
        }
    }
}

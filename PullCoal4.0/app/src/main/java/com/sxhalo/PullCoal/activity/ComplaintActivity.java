package com.sxhalo.PullCoal.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.ComplaintEntity;
import com.sxhalo.PullCoal.model.PayMentOrder;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 投诉界面
 * Created by amoldZhang on 2018/3/12.
 */
public class ComplaintActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;
    @Bind(R.id.complaint_listview)
    ListView complaintListview;
    @Bind(R.id.complaint_editText)
    EditText complaintEditText;


    private MyAdapter myAdapter;
    private List<ComplaintEntity> complaintEntities = new ArrayList<ComplaintEntity>();
    private String tradeNo;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_complaint);
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected void initTitle() {
        tradeNo =  getIntent().getStringExtra("Entity");
        title.setText("投诉");
        initView();
    }

    private void initView() {
        myAdapter = new MyAdapter(this, complaintEntities);
        complaintListview.setAdapter(myAdapter);
    }


    @Override
    protected void getData() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("tradeNo", tradeNo);
            new DataUtils(this, params).getUserConsultingFeeComplaintList(new DataUtils.DataBack<APPData<ComplaintEntity>>() {
                @Override
                public void getData(APPData<ComplaintEntity> appDataList) {
                    try {
                        if (appDataList == null) {
                            return;
                        }
                        complaintEntities.addAll(appDataList.getList());

                        //数据从新排序
                        sortClass sort = new sortClass();
                        Collections.sort(complaintEntities,sort);

                        myAdapter.notifyDataSetChanged();
                        scrollMyListViewToBottom();
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        GHLog.e("货运列表联网", e.toString());
                    }
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(this,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    /**
     * 定义比较器
     */
    public class sortClass implements Comparator {
        public int compare(Object arg0,Object arg1){
            ComplaintEntity user0 = (ComplaintEntity)arg0;
            ComplaintEntity user1 = (ComplaintEntity)arg1;
            int flag = user0.getCreateTime().compareTo(user1.getCreateTime());
            return flag;
        }
    }


    @OnClick({R.id.title_bar_left, R.id.send_complaint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.send_complaint:
                //提交
                String content = complaintEditText.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    displayToast("对不起，您还未发送任何内容！");
                    return;
                }
                commit(content);
                break;
        }
    }

    /**
     * 提交留言咨询
     * @param content
     */
    private void commit(String content) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("tradeNo", tradeNo);
            params.put("complaintMemo", content);
            new DataUtils(this, params).getUserConsultingFeeComplaintCreate(new DataUtils.DataBack<APPData<Object>>() {
                @Override
                public void getData(APPData<Object> data) {
                    try {
                        setData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    super.getError(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        complaintEntities.clear();
        myAdapter.notifyDataSetChanged();
        getData();
        complaintEditText.setText("");
    }

    /**
     * 当数据变动时，将数据滚动到最后一行
     */
    private void scrollMyListViewToBottom() {
        complaintListview.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                complaintListview.setSelection(myAdapter.getCount() - 1);
            }
        });
    }

    public class MyAdapter extends BaseAdapter {

        private Activity myActivity;
        private List<ComplaintEntity> complaintEntities;

        public MyAdapter(Activity myActivity, List<ComplaintEntity> complaintEntities) {
            this.myActivity = myActivity;
            this.complaintEntities = complaintEntities;
        }

        @Override
        public int getCount() {
            return complaintEntities.size();
        }

        @Override
        public ComplaintEntity getItem(int position) {
            return complaintEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            ComplaintEntity entity = getItem(position);
            return Integer.valueOf(entity.getType());
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            int type = getItemViewType(position);
            if (convertView != null) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(myActivity).inflate(R.layout.layout_complaint_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            ComplaintEntity entity = getItem(position);
            String currentDate =  DateUtil.getNewTimeType("yyyy-MM-dd HH:mm:ss");
            if (position == 0){
                currentDate = getItem(position).getCreateTime();
            }else{
                currentDate = getItem(position - 1).getCreateTime();
            }
            if (DateUtil.getDayOfTheDay(entity.getCreateTime(),currentDate,"yyyy-MM-dd HH:mm:ss") && position != 0){
                viewHolder.complaintTime.setVisibility(View.GONE);
            }else{
                viewHolder.complaintTime.setVisibility(View.VISIBLE);
                viewHolder.complaintTime.setText(entity.getCreateTime());
            }
            switch (type) {
                case 0:
                    viewHolder.complaintRightView.setVisibility(View.VISIBLE);
                    viewHolder.complaintLeftView.setVisibility(View.GONE);

                    if (StringUtils.isEmpty(entity.getHeadPic())) {
                        viewHolder.rightTitleImg.setImageResource(R.mipmap.icon_login);
                    } else {
                        getImageManager().loadItemUrlImage(entity.getHeadPic(), viewHolder.rightTitleImg); //个人圆图加载
                    }
                    viewHolder.rightCountText.setText(entity.getComplaintMemo());
                    break;
                case 1:
                    viewHolder.complaintRightView.setVisibility(View.GONE);
                    viewHolder.complaintLeftView.setVisibility(View.VISIBLE);

                    if (StringUtils.isEmpty(entity.getHeadPic())) {
                        viewHolder.leftTitleImg.setImageResource(R.mipmap.icon);
                    } else {
                        getImageManager().loadItemUrlImage(entity.getHeadPic(), viewHolder.leftTitleImg);//个人圆图加载
                    }
                    viewHolder.leftCountText.setText(entity.getComplaintMemo());
                    break;
            }
            return convertView;
        }

        class ViewHolder {

            @Bind(R.id.complaint_time)
            TextView complaintTime;

            @Bind(R.id.right_title_img)
            CircleImageView rightTitleImg;
            @Bind(R.id.right_count_text)
            TextView rightCountText;
            @Bind(R.id.complaint_right_view)
            RelativeLayout complaintRightView;

            @Bind(R.id.left_title_img)
            CircleImageView leftTitleImg;
            @Bind(R.id.left_count_text)
            TextView leftCountText;
            @Bind(R.id.complaint_left_view)
            RelativeLayout complaintLeftView;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


}

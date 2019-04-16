package com.sxhalo.PullCoal.ui.addrightview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.CoalContrastActivity;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.UserContrast;
import com.sxhalo.PullCoal.model.Coal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *   在布局的7右上角添加一个图片或布局
 *
 * Created by amoldZhang on 2018/8/15.
 */
public class LayoutContrastView extends LinearLayout {

    @Bind(R.id.contrast_view1)
    RightTopView contrastView1;
    @Bind(R.id.contrast_view2)
    RightTopView contrastView2;
    @Bind(R.id.contrast_view3)
    RightTopView contrastView3;
    @Bind(R.id.contrast_text1)
    TextView contrastText1;
    @Bind(R.id.contrast_text2)
    TextView contrastText2;
    @Bind(R.id.contrast_text3)
    TextView contrastText3;

    private Context mContext;


    public LayoutContrastView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view =  LayoutInflater.from(context).inflate(R.layout.coal_contrast_layout, this,false);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
             //解决当点击对比布局时，响应下面监听bug
            }
        });
        ButterKnife.bind(this,view);
        addView(view);
    }


    public void initView(CoalBack coalBack) {
        if (coalBack != null){
            setCoalBack(coalBack);
        }
        setContrastLayout();
    }

    /**
     * 更新煤炭列表添加的对比数据显示
     * @param coals
     */
    public void setAddContrastView(Coal coals){
        List<UserContrast> contrastList =  getContrastList();
        if(contrastList != null){
            UserContrast userContrast = new UserContrast();
            userContrast.setGoodsId(coals.getGoodsId());
            userContrast.setCoalName(coals.getCoalName());
            if (contrastList.size() == 0){
                OrmLiteDBUtil.insert(userContrast);
            }else{
                if (getifContrast(coals)){
                    int num = OrmLiteDBUtil.getQueryAllNum(UserContrast.class);
                    if (num >=1){
                        OrmLiteDBUtil.deleteWhere(UserContrast.class,"goods_id",new String[]{coals.getGoodsId()});
                    }
                }else{
                    int num = OrmLiteDBUtil.getQueryAllNum(UserContrast.class);
                    if (num < 3){
                        OrmLiteDBUtil.insert(userContrast);
                    }else{
                        ((BaseActivity)mContext).displayToast("您最多只能添加三个");
                    }
                }
            }
        }
        setContrastLayout();
    }

    public List<UserContrast> getContrastList(){
        return (List<UserContrast>)OrmLiteDBUtil.getQueryAll(UserContrast.class);
    }

    /**
     * 是否已经添加进来
     * @param coals
     * @return
     */
    public boolean getifContrast(Coal coals){
        UserContrast userContrast = new UserContrast();
        userContrast.setGoodsId(coals.getGoodsId());
        userContrast.setCoalName(coals.getCoalName());
        for (UserContrast userContrast1 : getContrastList()){
            if (userContrast1.equals(userContrast)){
                return true;
            }
        }
        return false;
    }

    /**
     * 更新对比布局
     */
    public void setContrastLayout(){
        List<UserContrast> contrastList =  getContrastList();
        if (contrastList.size() == 0){
            if (coalBack != null){
                coalBack.setLayout(false);
            }
        }else{
            if (coalBack != null){
                coalBack.setLayout(true);
            }
            if (contrastList.size() == 1){
                contrastView1.setVisibility(View.VISIBLE);
                contrastView2.setVisibility(View.INVISIBLE);
                contrastView3.setVisibility(View.INVISIBLE);

                contrastText1.setText(contrastList.get(0).getCoalName());

            }else if (contrastList.size() == 2){
                contrastView1.setVisibility(View.VISIBLE);
                contrastView2.setVisibility(View.VISIBLE);
                contrastView3.setVisibility(View.INVISIBLE);

                contrastText1.setText(contrastList.get(0).getCoalName());
                contrastText2.setText(contrastList.get(1).getCoalName());

            }else if (contrastList.size() == 3){
                contrastView1.setVisibility(View.VISIBLE);
                contrastView2.setVisibility(View.VISIBLE);
                contrastView3.setVisibility(View.VISIBLE);

                contrastText1.setText(contrastList.get(0).getCoalName());
                contrastText2.setText(contrastList.get(1).getCoalName());
                contrastText3.setText(contrastList.get(2).getCoalName());
            }
        }
    }

    @OnClick({R.id.state_contrast,R.id.contrast_iv1,R.id.contrast_iv2,R.id.contrast_iv3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.state_contrast:
                String URL = "";
                for (int i = 0;i<getContrastList().size();i++){
                    String goodsId = getContrastList().get(i).getGoodsId();
                    if (i == 0){
                        URL = "goodsId1="+ goodsId;
                    }else if (i == 1){
                        URL = URL + "&goodsId2="+ goodsId;
                    }else if (i == 2){
                        URL = URL + "&goodsId3="+ goodsId;
                    }
                }
                Intent intent = new Intent(mContext,CoalContrastActivity.class);
                intent.putExtra("contrastList",(Serializable)getContrastList());
                intent.putExtra("URL",URL);
                mContext.startActivity(intent);
                break;
            case R.id.contrast_iv1:
                OrmLiteDBUtil.deleteWhere(UserContrast.class,"goods_id",new String[]{getContrastList().get(0).getGoodsId()});
                setContrastLayout();
                break;
            case R.id.contrast_iv2:
                OrmLiteDBUtil.deleteWhere(UserContrast.class,"goods_id",new String[]{getContrastList().get(1).getGoodsId()});
                setContrastLayout();
                break;
            case R.id.contrast_iv3:
                OrmLiteDBUtil.deleteWhere(UserContrast.class,"goods_id",new String[]{getContrastList().get(2).getGoodsId()});
                setContrastLayout();
                break;
        }
    }

    public CoalBack coalBack;
    public void setCoalBack(CoalBack coalBack){
        this.coalBack = coalBack;
    }
    public static abstract class CoalBack{
        public abstract void setLayout(boolean flage);
    }

}

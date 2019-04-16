package com.sxhalo.PullCoal.tools;

import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.daiglog.TowButtonDialog;
import com.sxhalo.PullCoal.utils.GHLog;
import java.util.LinkedHashMap;

/**
 * Created by amoldZhang on 2017/5/22.
 */

public class DeleteMessage {

    private BaseActivity mActivity;
    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

    public DeleteMessage(BaseActivity mActivity){
        this.mActivity = mActivity;
    }

    /**
     *   1.弹出对话框.
     *   2.当点击确定按钮时，执行删除操作.
     * @param text    提示语
     * @param logType    消息类型
     * @param itemId  单个消息的id
     */
    public void setDeleteMessage(String text,final int logType,final String itemId){
        new TowButtonDialog(mActivity, text, "取消",
                "确定", new TowButtonDialog.Listener() {
            @Override
            public void onLeftClick() {
            }
            @Override
            public void onRightClick() {
                //消息类型1订单2货运单100系统消息
                params.clear();
                if (logType == 0){ //删除一条消息
                    params.put("logId", itemId);
                }else{  //删除一种类型的数据  type的值为 1订单 2货运单 100系统消息
                    params.put("logType", logType + "");

                }
                deleteMessage(logType);
            }
        }).show();
    }

    private void deleteMessage(final int logType) {
        try {
            new DataUtils(mActivity,params).getPushMessageDelete(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        if(coalBack != null){
                            coalBack.setBack(dataMemager,logType);
                        }
                    } catch (Exception e) {
                        GHLog.e("好友列表赋值", e.toString());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    public CoalBack coalBack;
    public void setCoalBack(CoalBack coalBack){
        this.coalBack = coalBack;
    }
    public interface CoalBack{
        void setBack(String data,int itemId);
    }
}

package com.sxhalo.PullCoal.bean;

import java.io.Serializable;

/**
 * Created by liz on 2018/5/10.
 */
public class CoalOrderStatusEntity implements Serializable {

    private String createTime;//时间
    private String memo;//提示内容
    private String demandFlow;// 当前进度状态下添加流程
    private boolean isCurrentStatus;//当前状态是否显示
    private boolean startLineIsBlue;
    private boolean endLineIsBlue;

    public String getDemandFlow() {
        return demandFlow;
    }

    public void setDemandFlow(String demandFlow) {
        this.demandFlow = demandFlow;
    }

    public boolean isCurrentStatus() {
        return isCurrentStatus;
    }

    public void setCurrentStatus(boolean currentStatus) {
        isCurrentStatus = currentStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public boolean isStartLineIsBlue() {
        return startLineIsBlue;
    }

    public void setStartLineIsBlue(boolean startLineIsBlue) {
        this.startLineIsBlue = startLineIsBlue;
    }

    public boolean isEndLineIsBlue() {
        return endLineIsBlue;
    }

    public void setEndLineIsBlue(boolean endLineIsBlue) {
        this.endLineIsBlue = endLineIsBlue;
    }
}

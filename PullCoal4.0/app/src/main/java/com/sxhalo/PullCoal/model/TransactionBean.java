package com.sxhalo.PullCoal.model;

import java.io.Serializable;
import java.util.List;

/**
 *  账户流水实体
 * Created by liz on 2017/8/17.
 */

public class TransactionBean implements Serializable {

//     "seqFlag": "0",
//             "changeType": "02",
//             "amount": "200",
//             "uncashAmount": "100",//不可提现发生额，单位：分
//             "memo": "备注",
//             "createTime": "2018-08-02 12:32:56"

    private String seqFlag;           //账务变动方向：0、来帐；1、往帐
    private String changeType;         //变动方式：00、充值；01、支付；02、提现；03、内部调帐；04、原交易退款；05、原交易撤销；06、退款；07、红包
    private String amount;           //变动后总额，单位：分
    private String uncashAmount;      //不可提现发生额，单位：分   用于煤款账户明细
    private String cashAmount;       //可提现发生额，单位：分      用于余额账户明细
    private String memo;             //备注
    private String createTime;

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getSeqFlag() {
        return seqFlag;
    }

    public void setSeqFlag(String seqFlag) {
        this.seqFlag = seqFlag;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUncashAmount() {
        return uncashAmount;
    }

    public void setUncashAmount(String uncashAmount) {
        this.uncashAmount = uncashAmount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

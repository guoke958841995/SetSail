package com.sxhalo.PullCoal.model;

/**
 * 煤款实体
 * Created by amoldZhang on 2018/5/7.
 */

public class EscrowAccount {

//    "accountName": "货款现金托管账户",//账户名称
//            "amount": "200",//总金额，单位：分
//            "cashAmount": "200",//可提现金额，单位：分
//            "uncashAmount": "200",//不可提现金额，单位：分
//            "freezeCashAmount": "200",//可提现冻结金额，单位：分
//            "freezeUncashAmount": "200",//不可提现冻结金额，单位：分
//            "state": "00"//账户状态：00、生效；01、冻结；02、注销

    private String accountName;         //账户名称
    private String amount;             //总金额，单位：分
    private String cashAmount;         //可提现金额，单位：分
    private String uncashAmount;       //可退款金额，单位：分
    private String freezeCashAmount;   //可提现冻结金额，单位：分
    private String freezeUncashAmount; //不可提现冻结金额，单位：分  冻结金额
    private String empowerAmount;      //白条授权金额，单位：分
    private String state;              //账户状态：00、生效；01、冻结；02、注销


    public String getEmpowerAmount() {
        return empowerAmount;
    }

    public void setEmpowerAmount(String empowerAmount) {
        this.empowerAmount = empowerAmount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getUncashAmount() {
        return uncashAmount;
    }

    public void setUncashAmount(String uncashAmount) {
        this.uncashAmount = uncashAmount;
    }

    public String getFreezeCashAmount() {
        return freezeCashAmount;
    }

    public void setFreezeCashAmount(String freezeCashAmount) {
        this.freezeCashAmount = freezeCashAmount;
    }

    public String getFreezeUncashAmount() {
        return freezeUncashAmount;
    }

    public void setFreezeUncashAmount(String freezeUncashAmount) {
        this.freezeUncashAmount = freezeUncashAmount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

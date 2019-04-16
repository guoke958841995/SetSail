package com.sxhalo.PullCoal.db.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2017/1/9.
 */
@Table("user_model")
public class User implements Serializable{
//    code：0-登录成功 1-你已被禁用 2、密码错误 3、没有权限  4用户不存在,
//            "headerImage":"http://localhost:8080/lamweb/upload/1483063130596.jpg",
//            "vehicleCertificationState":"2",  (0-未提交，1-审核中， 2-审核成功， 3-审核失败）
//            "phoneNumber":"18706860934",
//            "address":"",
//            "nickname":"111",
//            "diverState":"3",
//            "userId":"3",
//            "certificationState":"2",(0-未提交，1-审核中， 2-审核成功， 3-审核失败）
//            "userRealName":"t"


    @PrimaryKey(AssignType.BY_MYSELF ) //主键自己设定的
    @Column(value = "user_id")
    private String userId;     //userId
    @Column(value = "user_role")
    private String userRole;     //角色Id   0光环测试用户 1普通用户 2卡车司机 3信息部用户 4信息部管理员
    @Column(value = "user_name")
    private String userName;     //userName
    @Column(value = "user_image")
    private String headerImage;  //头像路径
    @Column(value = "user_phone_number")
    private String phoneNumber;  //用户名
    @Column(value = "user_nick_name")
    private String nickname;    //昵称
    @Column(value = "user_real_name")
    private String userRealName;    //昵称
    @Column(value = "user_address")
    private String address;    //地址
    @Column(value = "user_drver_state")
    private String diverState;     //司机状态
    @Column(value = "user_vehicle_certification_state")
    private String vehicleCertificationState;  //车辆审核状态
    @Column(value = "user_certification_state")
    private String certificationState;     //实名认证状态
    @Column(value = "info_depart_id")
    private String infoDepartId;     //关联的信息部

    public String getFollowNum() {
        return followNum;
    }

    public String getInfoDepartId() {
        return infoDepartId;
    }

    public void setInfoDepartId(String infoDepartId) {
        this.infoDepartId = infoDepartId;
    }

    public void setFollowNum(String followNum) {
        this.followNum = followNum;
    }

    public String getMyOrderNum() {
        return myOrderNum;
    }

    public void setMyOrderNum(String myOrderNum) {
        this.myOrderNum = myOrderNum;
    }

    public String getMyWaybillNum() {
        return myWaybillNum;
    }

    public void setMyWaybillNum(String myWaybillNum) {
        this.myWaybillNum = myWaybillNum;
    }

    @Column(value = "my_order_num")
    private String myOrderNum;     //订单数量
    @Column(value = "follow_num")
    private String followNum;     //关注的信息部数量
    @Column(value = "my_way_bill_num")
    private String myWaybillNum;     //货运单数量

    private String code;    //登陆返回状态

    public User(){}

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiverState() {
        return diverState;
    }

    public void setDiverState(String diverState) {
        this.diverState = diverState;
    }

    public String getVehicleCertificationState() {
        return vehicleCertificationState;
    }

    public void setVehicleCertificationState(String vehicleCertificationState) {
        this.vehicleCertificationState = vehicleCertificationState;
    }

    public String getCertificationState() {
        return certificationState;
    }

    public void setCertificationState(String certificationState) {
        this.certificationState = certificationState;
    }
}

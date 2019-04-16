package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * 运费计算实体
 * Created by liz on 2017/11/22.
 */

public class FreightChargeEntity implements Serializable {

    private String startCode;
    private String endCode;
    private float cargoPrice;
    private float cargoWeight;
    private float gasPrice;
    private float oilConsumption;
    private long informationPrice;
    private long loadPrice;
    private long unloadPrice;

    private String fromLatitude;
    private String fromLongitude;

    private String toLongitude;
    private String toLatitude;

    public String getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(String fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public String getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(String fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public String getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(String toLongitude) {
        this.toLongitude = toLongitude;
    }

    public String getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(String toLatitude) {
        this.toLatitude = toLatitude;
    }

    public String getStartCode() {
        return startCode;
    }

    public void setStartCode(String startCode) {
        this.startCode = startCode;
    }

    public String getEndCode() {
        return endCode;
    }

    public void setEndCode(String endCode) {
        this.endCode = endCode;
    }

    public float getCargoPrice() {
        return cargoPrice;
    }

    public void setCargoPrice(long cargoPrice) {
        this.cargoPrice = cargoPrice;
    }

    public float getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(float cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public float getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(float gasPrice) {
        this.gasPrice = gasPrice;
    }

    public float getOilConsumption() {
        return oilConsumption;
    }

    public void setOilConsumption(float oilConsumption) {
        this.oilConsumption = oilConsumption;
    }

    public long getInformationPrice() {
        return informationPrice;
    }

    public void setInformationPrice(long informationPrice) {
        this.informationPrice = informationPrice;
    }

    public long getLoadPrice() {
        return loadPrice;
    }

    public void setLoadPrice(long loadPrice) {
        this.loadPrice = loadPrice;
    }

    public long getUnloadPrice() {
        return unloadPrice;
    }

    public void setUnloadPrice(long unloadPrice) {
        this.unloadPrice = unloadPrice;
    }
}

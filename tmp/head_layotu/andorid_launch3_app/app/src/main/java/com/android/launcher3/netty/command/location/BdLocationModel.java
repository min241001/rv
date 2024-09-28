package com.android.launcher3.netty.command.location;

import com.android.launcher3.common.utils.StringUtils;
import com.baehug.lib.nettyclient.annotation.MyUnicodeSign;
import com.baidu.location.BDLocation;

/**
 * @Author: jamesfeng
 * @Description：
 * @CreateDate：2024/1/30 17:26
 * @UpdateUser: jamesfeng
 */
public class BdLocationModel {

    /**
     * 定位坐标系 (1 百度坐标系即bd09II、2 高德坐标系即gcj02、3 wgs84坐标系)
     */
    private Integer s01CoordinateType;
    /**
     * 经度
     */
    private String s02Lng;
    /**
     * 纬度
     */
    private String s03Lat;
    /**
     * 定位精度（单位米）
     */
    private Integer s04Radius;
    /**
     * 是否室内（1 未知、2 室内、3 室外）
     */
    private Integer s05IsIndoor;
    /**
     * 省
     */
    @MyUnicodeSign
    private String s06Province;
    /**
     * 市
     */
    @MyUnicodeSign
    private String s07City;
    /**
     * 区县/乡镇
     */
    @MyUnicodeSign
    private String s08District;
    /**
     * 街道
     */
    @MyUnicodeSign
    private String s09Street;
    /**
     * 路
     */
    @MyUnicodeSign
    private String s10Road;
    /**
     * 地址信息
     */
    @MyUnicodeSign
    private String s11Address;
    /**
     * 位置语义化信息
     */
    @MyUnicodeSign
    private String s12LocationDescribe;
    /**
     * 定位时间
     */
    private Long s13LocationTime;
    /**
     * 电量
     */
    private Integer s14Battery;
    /**
     * 步数
     */
    private Integer s15Step;
    /**
     * 信号强度
     */
    private Integer s16Rssi;
    /**
     * 设备状态
     */
    private String s17DeviceState;

    public BdLocationModel(BDLocation bdLocation) {
        this.s01CoordinateType = 1;
        this.s02Lng = String.valueOf(bdLocation.getLongitude());
        this.s03Lat = String.valueOf(bdLocation.getLatitude());
        this.s04Radius = (int) bdLocation.getRadius();
        if (bdLocation.getUserIndoorState() == BDLocation.USER_INDOOR_UNKNOW) {
            this.s05IsIndoor = 1;
        } else if (bdLocation.getUserIndoorState() == BDLocation.USER_INDDOR_TRUE) {
            this.s05IsIndoor = 2;
        } else {
            this.s05IsIndoor = 3;
        }
        this.s06Province = bdLocation.getProvince();
        this.s07City = bdLocation.getCity();
        this.s08District = StringUtils.isBlank(bdLocation.getDistrict()) ? bdLocation.getTown() : bdLocation.getDistrict();
        this.s09Street = bdLocation.getStreet();
        this.s10Road = "";
        this.s11Address = bdLocation.getAddrStr();
        this.s12LocationDescribe = bdLocation.getLocationDescribe();
        this.s13LocationTime = System.currentTimeMillis();
    }

    public Integer getS01CoordinateType() {
        return s01CoordinateType;
    }

    public void setS01CoordinateType(Integer s01CoordinateType) {
        this.s01CoordinateType = s01CoordinateType;
    }

    public String getS02Lng() {
        return s02Lng;
    }

    public void setS02Lng(String s02Lng) {
        this.s02Lng = s02Lng;
    }

    public String getS03Lat() {
        return s03Lat;
    }

    public void setS03Lat(String s03Lat) {
        this.s03Lat = s03Lat;
    }

    public Integer getS04Radius() {
        return s04Radius;
    }

    public void setS04Radius(Integer s04Radius) {
        this.s04Radius = s04Radius;
    }

    public Integer getS05IsIndoor() {
        return s05IsIndoor;
    }

    public void setS05IsIndoor(Integer s05IsIndoor) {
        this.s05IsIndoor = s05IsIndoor;
    }

    public String getS06Province() {
        return s06Province;
    }

    public void setS06Province(String s06Province) {
        this.s06Province = s06Province;
    }

    public String getS07City() {
        return s07City;
    }

    public void setS07City(String s07City) {
        this.s07City = s07City;
    }

    public String getS08District() {
        return s08District;
    }

    public void setS08District(String s08District) {
        this.s08District = s08District;
    }

    public String getS09Street() {
        return s09Street;
    }

    public void setS09Street(String s09Street) {
        this.s09Street = s09Street;
    }

    public String getS10Road() {
        return s10Road;
    }

    public void setS10Road(String s10Road) {
        this.s10Road = s10Road;
    }

    public String getS11Address() {
        return s11Address;
    }

    public void setS11Address(String s11Address) {
        this.s11Address = s11Address;
    }

    public String getS12LocationDescribe() {
        return s12LocationDescribe;
    }

    public void setS12LocationDescribe(String s12LocationDescribe) {
        this.s12LocationDescribe = s12LocationDescribe;
    }

    public Long getS13LocationTime() {
        return s13LocationTime;
    }

    public void setS13LocationTime(Long s13LocationTime) {
        this.s13LocationTime = s13LocationTime;
    }

    public Integer getS14Battery() {
        return s14Battery;
    }

    public void setS14Battery(Integer s14Battery) {
        this.s14Battery = s14Battery;
    }

    public Integer getS15Step() {
        return s15Step;
    }

    public void setS15Step(Integer s15Step) {
        this.s15Step = s15Step;
    }

    public Integer getS16Rssi() {
        return s16Rssi;
    }

    public void setS16Rssi(Integer s16Rssi) {
        this.s16Rssi = s16Rssi;
    }

    public String getS17DeviceState() {
        return s17DeviceState;
    }

    public void setS17DeviceState(String s17DeviceState) {
        this.s17DeviceState = s17DeviceState;
    }

}

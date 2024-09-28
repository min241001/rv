package com.android.launcher3.common.bean;

/**
 * 位置
 */
public class UdBean {
    //时间戳
    public String timestamp;
    //是否gps
    public String validGps;

    //纬度
    public String latitude;
    public String latitudeSign;
    //经度
    public String longitude;
    public String longitudeSign;

    //精度
    public String precision;

    //速度
    public String speed;
    //方向
    public String direction;
    //海拔
    public String elevation;
    //卫星个数
    public String satelliteNumber;
    //gsm信号强度
    public String gmsSignalStrength;
    //电量
    public String electricQuantity;
    //计步数
    public String stepCount;
    //翻转次数
    public String turnNumber;

    //终端状态
    public String terminalState;

    //无线网络类型 GSM/GPRS/EDGE/HSUPA/HSDPA/WCDMA (注意大写)
    public String network;

    //是否为cdma。 非cdma：0; cdma：1 默认值为：0
    public String cdma;

    /**
     * 基站信息，非CDMA格式为：mcc, mnc,lac,cellid,signal；其中lac，cellid必须填写，signal如无法获取请填写50，前两位mcc, mnc 如无法获取，请填写-1
     * CDMA格式为：sid,nid,bid,lon,lat,signal 其中lon,lat可为空，格式为：sid,nid,bid,,,signal
     * 为保证定位效果，请尽量全部填写
     */
    public String bts;
    /**
     * 周边基站信息 基站信息1|基站信息2|基站信息3….
     */
    public String nearbts;

    /**
     * 已连热点mac信息 mac,signal,ssid。 如：f0:7d:68:9e:7d:18,-41,TPLink 非必选，但强烈建议填写
     */
    public String mmac;
    /**
     * WI-FI列表中mac信息 单mac信息同mmac，mac之间使用“|”分隔。 必须填写 2 个及 2 个以上,30 个 以内的方可正常定位。
     * 请不要包含移动WI-FI信息
     */
    public String macs;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getValidGps() {
        return validGps;
    }

    public void setValidGps(String validGps) {
        this.validGps = validGps;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitudeSign() {
        return latitudeSign;
    }

    public void setLatitudeSign(String latitudeSign) {
        this.latitudeSign = latitudeSign;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitudeSign() {
        return longitudeSign;
    }

    public void setLongitudeSign(String longitudeSign) {
        this.longitudeSign = longitudeSign;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public String getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(String satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }

    public String getGmsSignalStrength() {
        return gmsSignalStrength;
    }

    public void setGmsSignalStrength(String gmsSignalStrength) {
        this.gmsSignalStrength = gmsSignalStrength;
    }

    public String getElectricQuantity() {
        return electricQuantity;
    }

    public void setElectricQuantity(String electricQuantity) {
        this.electricQuantity = electricQuantity;
    }

    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) {
        this.stepCount = stepCount;
    }

    public String getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(String turnNumber) {
        this.turnNumber = turnNumber;
    }

    public String getTerminalState() {
        return terminalState;
    }

    public void setTerminalState(String terminalState) {
        this.terminalState = terminalState;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getCdma() {
        return cdma;
    }

    public void setCdma(String cdma) {
        this.cdma = cdma;
    }

    public String getBts() {
        return bts;
    }

    public void setBts(String bts) {
        this.bts = bts;
    }

    public String getNearbts() {
        return nearbts;
    }

    public void setNearbts(String nearbts) {
        this.nearbts = nearbts;
    }

    public String getMmac() {
        return mmac;
    }

    public void setMmac(String mmac) {
        this.mmac = mmac;
    }

    public String getMacs() {
        return macs;
    }

    public void setMacs(String macs) {
        this.macs = macs;
    }
}
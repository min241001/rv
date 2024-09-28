package com.android.launcher3.common.network.resp;

public class Now {

    public String text;
    public String textCode;
    public int temp;
    public int high;
    public int low;
    public int feelsLike;
    public String windClass;
    public String windDir;

    public void setText(String text) {
        this.text = text;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setWindClass(String windClass) {
        this.windClass = windClass;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getText() {
        return text;
    }

    public int getTemp() {
        return temp;
    }

    public String getWindClass() {
        return windClass;
    }

    public String getWindDir() {
        return windDir;
    }

    public String getTextCode() {
        return textCode;
    }

    public int getHigh() {
        return high;
    }

    public int getLow() {
        return low;
    }

    public int getFeelsLike() {
        return feelsLike;
    }

    public void setTextCode(String textCode) {
        this.textCode = textCode;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public void setFeelsLike(int feelsLike) {
        this.feelsLike = feelsLike;
    }

    @Override
    public String toString() {
        return "Now{" +
                "text='" + text + '\'' +
                ", textCode='" + textCode + '\'' +
                ", temp=" + temp +
                ", high=" + high +
                ", low=" + low +
                ", feelsLike=" + feelsLike +
                ", windClass='" + windClass + '\'' +
                ", windDir='" + windDir + '\'' +
                '}';
    }
}

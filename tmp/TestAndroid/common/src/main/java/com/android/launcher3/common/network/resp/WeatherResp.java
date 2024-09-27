package com.android.launcher3.common.network.resp;

public class WeatherResp {

    private Object location;
    private Now now;
    private Object forecasts;

    public void setLocation(Object location) {
        this.location = location;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public Object getLocation() {
        return location;
    }

    public void setForecasts(Object forecasts) {
        this.forecasts = forecasts;
    }

    public Object getForecasts() {
        return forecasts;
    }

    public Now getNow() {
        return now;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "location=" + location +
                ", forecasts=" + forecasts +
                ", now=" + now +
                '}';
    }
}

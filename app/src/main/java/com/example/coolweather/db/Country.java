package com.example.coolweather.db;

import org.litepal.crud.LitePalSupport;

public class Country extends LitePalSupport {
    private int id;
    private String countyName;//县名
    private String weatherId;//县所对应天气ID
    private int cityId;//县所属城市

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

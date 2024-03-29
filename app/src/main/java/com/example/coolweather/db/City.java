package com.example.coolweather.db;

import org.litepal.crud.LitePalSupport;//Datasupport被litePalSupport取代

public class City extends LitePalSupport {
    private int id;
    private String cityName;//城市名称
    private int cityCode;//城市代号
    private int provinceId;//城市所属省
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }



}

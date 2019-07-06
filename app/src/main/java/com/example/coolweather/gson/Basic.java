package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    public String cityName;//JSON中有些字段不太适合直接作为java字段命名？？
    // 因此用了@SerializedName注解方式

    @SerializedName("id")
    public  String weatherId;

    public Update update;

    public  class Update{
        @SerializedName("loc")
        public String updateTime;

    }


}

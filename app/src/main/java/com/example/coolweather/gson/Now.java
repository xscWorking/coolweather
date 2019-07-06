package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Modifier;

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;
    public  class  More{
        @SerializedName("txt")
        public String info;
    }
}

package com.example.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.coolweather.db.City;
import com.example.coolweather.db.Country;
import com.example.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){//返回数据不为空
            try {
                JSONArray allProvinces=new JSONArray(response);//根据字符串获取json数组
                for(int i=0;i<allProvinces.length();i++){
                    //获取json数组里面的json对象(某一条数据)
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    //获得省名并放入数据库
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //获得省代号放入数据库
                    province.save();
                }
                Log.d("MainActivity", "拿到了数据 ");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的城市数据
     */
  public static boolean  handleCityResponse(String response,int provinceId){
      if(!TextUtils.isEmpty(response)){//返回数据不为空
          try {
              JSONArray allCities=new JSONArray(response);//根据字符串获取json数组
              for(int i=0;i<allCities.length();i++){
                  //获取json数组里面的json对象(某一条数据)
                  JSONObject cityObject=allCities.getJSONObject(i);
                  City city=new City();
                  city.setCityName(cityObject.getString("name"));
                  city.setCityCode(cityObject.getInt("id"));
                  city.setProvinceId(provinceId);
                  city.save();
              }
              return true;
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }
    return false;
  }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean  handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){//返回数据不为空
            try {
                JSONArray allCounties=new JSONArray(response);//根据字符串获取json数组
                for(int i=0;i<allCounties.length();i++){
                    //获取json数组里面的json对象(某一条数据)
                    JSONObject countryObject=allCounties.getJSONObject(i);
                    Country county=new Country();
                    county.setCountyName(countryObject.getString("name"));
                    county.setWeatherId(countryObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

package com.example.yangliang.myweatherapp.util;

import android.text.TextUtils;

import com.example.yangliang.myweatherapp.db.City;
import com.example.yangliang.myweatherapp.db.County;
import com.example.yangliang.myweatherapp.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 创建日期：2017/9/25 on 上午10:30
 * 描述:解析和处理json数据的代码封装，这里返回的json数据结构比较简单，不用GSON进行解析。
 * 直接使用JsonArray和JsonObject将数据解析出来，然后组装成实体类对象，再调用save()方法将数据存储到数据库当中。
 * 作者:yangliang
 */
public class JsonParserUtility {

    /**
     * 解析和处理服务器返回的省级数据，并存储到数据库中
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                //获取Provinces数组
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    //获取Provinces对象
                    JSONObject allProvincesJSONObject = allProvinces.getJSONObject(i);
                    //将json数据组成实体类对象
                    Province province = new Province();
                    province.setProvinceName(allProvincesJSONObject.getString("name"));
                    province.setProvinceCode(allProvincesJSONObject.getInt("id"));
                    //调用save()方法将数据存储到数据库当中
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据(市级数据是根据省对应的id来获取到的)，并存储到数据库中
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject allCitiesJSONObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(allCitiesJSONObject.getString("name"));
                    city.setCityCode(allCitiesJSONObject.getInt("id"));
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
     * 解析和处理服务器返回的区县级数据，并存储到数据库中
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject allCountiesJSONObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(allCountiesJSONObject.getString("name"));
                    county.setWeatherId(allCountiesJSONObject.getString("weather_id"));
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

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    /*
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
     */
}

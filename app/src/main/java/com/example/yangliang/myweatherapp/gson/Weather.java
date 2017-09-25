package com.example.yangliang.myweatherapp.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 创建日期：2017/9/25 on 上午10:55
 * 描述:创建总的实体类，引用创建的各个天气实体类
 * 作者:yangliang
 */
public class Weather {

    //请求的状态
    public String status;

    //城市的一些基本信息
    public Basic basic;

    //当前空气质量情况
    public AQI aqi;

    //当前天气信息
    public Now now;

    //生活建议
    public Suggestion suggestion;

    //未来天气预报，daily_forecast包含的是一个数组，因此使用list集合来引用Forecast实体类
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;


}

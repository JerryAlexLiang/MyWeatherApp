package com.example.yangliang.myweatherapp.gson;

/**
 * 创建日期：2017/9/25 on 下午3:19
 * 描述:当前空气质量的情况
 * 作者:yangliang
 */
public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}

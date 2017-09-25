package com.example.yangliang.myweatherapp.db;

import org.litepal.crud.DataSupport;

/**
 * 创建日期：2017/9/25 on 上午9:48
 * 描述:
 * 作者:yangliang
 */
public class County extends DataSupport {

    //id为每个实体类都应该具有的字段
    private int id;

    //当前区县的名称
    private String countyName;

    //当前区县的天气id
    private String weatherId;

    //当前区县所属市的id
    private int cityId;

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

package com.example.yangliang.myweatherapp.db;

import org.litepal.crud.DataSupport;

/**
 * 创建日期：2017/9/25 on 上午9:46
 * 描述:
 * 作者:yangliang
 */
public class City extends DataSupport {

    //id为每个实体类都应该有的字段
    private int id;

    //城市的名字
    private String cityName;

    //城市的代码
    private int cityCode;

    //当前市所属的省的id
    private int provinceId;

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

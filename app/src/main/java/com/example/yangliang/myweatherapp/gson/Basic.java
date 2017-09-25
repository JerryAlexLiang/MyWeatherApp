package com.example.yangliang.myweatherapp.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/9/25 on 下午3:13
 * 描述:Basic包含城市中的一些基本信息
 * 注意：由于JSON中的一些字段可能不太适合直接作为Java字段来命名,因此这里使用@SerializedName注解的方式来让JSON字段和
 * Java字段之间建立映射关系
 * 作者:yangliang
 */
public class Basic {

    @SerializedName("city")
    public String cityName;//城市名称

    @SerializedName("id")
    public String weatherId;//城市对应天气的id

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;//天气的更新时间
    }
}

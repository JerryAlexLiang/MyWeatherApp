package com.example.yangliang.myweatherapp.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/9/25 on 下午3:23
 * 描述:当前天气信息
 * 作者:yangliang
 */
public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}

package com.example.yangliang.myweatherapp.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/9/25 on 下午3:33
 * 描述:未来天气预报
 * 作者:yangliang
 */
public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;

        public String min;

    }

    public class More {

        @SerializedName("txt_d")
        public String info;

    }

}

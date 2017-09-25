package com.example.yangliang.myweatherapp.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2017/9/25 on 下午3:28
 * 描述:生活建议
 * 作者:yangliang
 */
public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        @SerializedName("txt")
        public String info;

    }
}

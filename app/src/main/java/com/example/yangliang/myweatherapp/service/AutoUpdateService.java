package com.example.yangliang.myweatherapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.example.yangliang.myweatherapp.gson.Weather;
import com.example.yangliang.myweatherapp.util.HttpUtil;
import com.example.yangliang.myweatherapp.util.JsonParserUtility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 创建日期：2017/9/26 on 下午2:33
 * 描述: 创建一个服务，在后台运行的定时任务，可以后台自动更新天气
 * 作者: liangyang
 */
public class AutoUpdateService extends Service {

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新天气信息
        updateWeather();
        //更新每日背景图片
        updateBingPic();
        //创建定时任务，8小时后AutoUpdateReceiver的onStartCommand()方法就会重新执行，这样也就实现了后台定时更新的功能
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //这是8小时的毫秒数，为不耗费过多的流量，将时间间隔定位8小时，
//        int anHour = 8 * 60 * 60 * 1000;
        int anHour = 60 * 60 * 1000;//一小时更新一次
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新每日背景图片
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });

    }

    /**
     * 更新天气信息
     * 将更新后的数据直接存储到SharedPreferences文件中，因为打开WeatherActivity优先从本地数据库缓存中读取数据
     */
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = JsonParserUtility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=ca200b5fb881484eab6616caa96d6922";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = JsonParserUtility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }

    }
}

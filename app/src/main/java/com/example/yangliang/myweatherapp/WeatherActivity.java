package com.example.yangliang.myweatherapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangliang.myweatherapp.gson.Forecast;
import com.example.yangliang.myweatherapp.gson.Weather;
import com.example.yangliang.myweatherapp.util.HttpUtil;
import com.example.yangliang.myweatherapp.util.JsonParserUtility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 创建日期：2017/9/25 on 下午3:44
 * 描述: 用于显示天气的界面
 * 作者: liangyang
 */
public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化控件
        initView();
        //尝试从本地数据库中获取天气信息
        readDataByLocal();
    }

    /**
     * 尝试从本地数据库中获取天气信息
     * 第一次没有缓存，因此会从intent中读取天气id，并调用requestWeather()方法从服务器获取天气数据
     */
    private void readDataByLocal() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        String weatherId;
        if (weatherString != null) {
            //有缓存时直接从本地数据库读取数据，直接解析天气数据
            Weather weather = JsonParserUtility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            //请求数据时隐藏ScrollView
            weatherLayout.setVisibility(View.INVISIBLE);
            //服务器请求数据
            requestWeather(weatherId);
        }

    }

    /**
     * 服务器请求数据：根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    private void requestWeather(String weatherId) {
        //和风天气API
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=ca200b5fb881484eab6616caa96d6922";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回JSON数据
                final String responseText = response.body().string();
                //解析JSON数据,将JSON数据转换为Weather对象
                final Weather weather = JsonParserUtility.handleWeatherResponse(responseText);
                //在主线程中更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果服务器返回的status状态是OK,说明天气请求成功，此时返回的数据缓存到SharedPreference中，
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            //并调用showWeatherInfo()方法来显示内容
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败~", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败~", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    /**
     * 处理并显示Weather实体类中的数据
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        //获取天气数据
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        //数据映射
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        //未来天气预报使用for循环来处理每天的天气信息，再循环中动态加载forecast_item.xml布局，并设置相应的数据
        for (Forecast forecast : weather.forecastList) {
            //加载未来天气布局
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            //将forecast_item.xml布局添加到父布局当中
            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        //设置完所有数据后，将ScrollView可见
        weatherLayout.setVisibility(View.VISIBLE);

    }

    /**
     * 初始化控件
     */
    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
    }
}

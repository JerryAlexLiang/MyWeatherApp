package com.example.yangliang.myweatherapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yangliang.myweatherapp.R;
import com.example.yangliang.myweatherapp.gson.Forecast;
import com.example.yangliang.myweatherapp.gson.Weather;
import com.example.yangliang.myweatherapp.service.AutoUpdateService;
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

    private ImageView bingPicImg;

    private ScrollView weatherLayout;

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

    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;

    private Button navButton;

    private String mWeatherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Android5.0及以上系统支持用此简单方法实现沉浸式状态栏
        //注意：还应在布局文件中相应位置添加android:fitsSystemWindows="true"来处理布局问题
        if (Build.VERSION.SDK_INT >= 21) {
            //得到当前活动的DecorView
            View decorView = getWindow().getDecorView();
            //改变系统UI的显示(活动的布局会显示在状态栏上面)
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //调用setStatusBarColor()方法将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化控件
        initView();
        //尝试从本地数据库中获取天气信息
        readDataByLocal();
        //监听事件
        initListener();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        //下拉刷新控件
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //服务器请求天气网络数据
                requestWeather(mWeatherId);
            }
        });

        //主页菜单点击事件
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开左侧滑动菜单
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 尝试从本地数据库中获取天气信息
     * 第一次没有缓存，因此会从intent中读取天气id，并调用requestWeather()方法从服务器获取天气数据
     */
    private void readDataByLocal() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接从本地数据库读取数据，直接解析天气数据
            Weather weather = JsonParserUtility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            //请求数据时隐藏ScrollView
            weatherLayout.setVisibility(View.INVISIBLE);
            //服务器请求数据
            requestWeather(mWeatherId);
        }

        //有缓存时直接从本地数据库读取数据，直接加载背景图片
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            //无缓存时去服务器请求图片数据
            loadBingPic();
        }

    }

    /**
     * 加载必应每日一图
     * 注意在requestWeather()方法的最后，也要调用一下loadBingPic()
     * 这样在每次请求天气信息的时候同时也会刷新背景图片
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取网络JSON数据
                final String bingPic = response.body().string();
                //将获取到的数据存储在本地数据库SharedPreferences中
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();

                //切换到主线程更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //加载背景图片
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });

    }

    /**
     * 服务器请求数据：根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(String weatherId) {
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
                            mWeatherId = weather.basic.weatherId;
                            //并调用showWeatherInfo()方法来显示内容
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败~", Toast.LENGTH_SHORT).show();
                        }
                        //当请求结束，设置setRefreshing(false)表示刷新事件结束，并隐藏刷新进度条
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, "数据更新成功~", Toast.LENGTH_SHORT).show();
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
                        //当请求结束，设置setRefreshing(false)表示刷新事件结束，并隐藏刷新进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        //注意在requestWeather()方法的最后，也要调用一下loadBingPic()
        //这样在每次请求天气信息的时候同时也会刷新背景图片
        loadBingPic();
    }

    /**
     * 处理并显示Weather实体类中的数据，并开启后台服务
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

        //开启服务，实现后台定时更新天气功能
        //这样，只要一旦选中了某个城市并成功更新天气后，AutoUpdateService就会一直在后台运行，实现每8小时更新一次天气数据
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

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
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //设置下拉刷新进度条的颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
    }

    /**
     * 返回键退出应用(连按两次)
     */
    private long waitTime = 2000;
    private long touchTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                Toast.makeText(WeatherActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                finish();
                System.exit(0);
            }
            return true;
        } else if (KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

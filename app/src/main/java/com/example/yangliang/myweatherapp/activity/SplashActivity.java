package com.example.yangliang.myweatherapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yangliang.myweatherapp.R;
/**
 * 创建日期：2017/9/26 on 下午4:47
 * 描述: 欢迎页
 * 作者: liangyang
 */
public class SplashActivity extends AppCompatActivity {

    public static final int START_ACTIVITY = 0X1; //启动主页面
    public static final int TIME_COUNTDOWN = 0X2; //倒计时
    public static final int RANDOM_CHANGE = 0X3; //切换背景图
    public static final int STOP_HANDLER = 0X4; //关闭handler

    private int[] bacgroundIcon = {R.drawable.bacground1, R.drawable.bacground2,
            R.drawable.bacground3, R.drawable.bacground4};

    private int recLen_count = 11;
    private int recLen_change = 11;
    private int bacgroundNum = 0;


    private TextView mTimeCountDown;
    private RelativeLayout splashBackground;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case START_ACTIVITY:
                    //设置跳转到主页面
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    //关闭SplashActivity欢迎页
                    finish();
                    break;

                case TIME_COUNTDOWN:

                    recLen_count--;
                    mTimeCountDown.setText("倒计时： " + recLen_count + "  秒");

                    //发送消息逻辑判断
                    if (recLen_count > 0) {
                        //发送消息
                        handler.sendEmptyMessageDelayed(TIME_COUNTDOWN, 1000);
                    } else {
                        mTimeCountDown.setVisibility(View.GONE);
                    }

                    break;

                case RANDOM_CHANGE:

//                    recLen_change -= 2;
//                    //每两秒随机切换欢迎页背景图
//                    Random random = new Random();
//                    int index = random.nextInt(bacgroundIcon.length);
//                    splashBackground.setBackgroundResource(bacgroundIcon[index]);
//
//                    if (recLen_change > 2){
//                        //发送消息
//                        handler.sendEmptyMessageDelayed(RANDOM_CHANGE,2000);
//                    }

                    //每秒切换欢迎页背景图
                    recLen_change--;
                    splashBackground.setBackgroundResource(bacgroundIcon[bacgroundNum++]);
                    if (bacgroundNum >= bacgroundIcon.length) {
                        bacgroundNum = 0;
                    }

                    if (recLen_change > 0) {
                        //发送消息
                        handler.sendEmptyMessageDelayed(RANDOM_CHANGE, 1000);
                    }
                    break;

                case STOP_HANDLER:
                    //关闭SplashActivity欢迎页
                    finish();
                    handler.removeMessages(START_ACTIVITY);
                    break;

            }

        }
    };

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
        setContentView(R.layout.activity_splash);
        //发送消息-欢迎页10秒后结束
        handler.sendEmptyMessageDelayed(START_ACTIVITY, 10000);
        //发送消息-倒计时
        handler.sendEmptyMessageDelayed(TIME_COUNTDOWN, 1000);
        //每秒随机切换背景图
        handler.sendEmptyMessageDelayed(RANDOM_CHANGE, 1000);

        //初始化视图
        splashBackground = (RelativeLayout) findViewById(R.id.activity_splash);

        Button mSkipBtn = (Button) findViewById(R.id.skip_splash);
        mTimeCountDown = (TextView) findViewById(R.id.tv_time_countdown);

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到主页面
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
//                //关闭SplashActivity欢迎页
//                finish();
//                handler.sendEmptyMessage(START_ACTIVITY);
                handler.sendEmptyMessage(STOP_HANDLER);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            System.exit(0);
        } else if (KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}

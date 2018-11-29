package com.round0.ballgame;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mAccelerSensor;
    BallView mMainBall;
    FoodView mFoodView;
    BarrierView mBarrierView;
    TextView textView1;

    GameManager mGameManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //传感器初始化
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //重力传感器
        mAccelerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mAccelerSensor == null){
            finish();
            return;
        }
        mMainBall = (BallView)findViewById(R.id.ball_main);
        mFoodView = (FoodView)findViewById(R.id.food);
        mBarrierView = (BarrierView)findViewById(R.id.barriers);
        textView1 = (TextView)findViewById(R.id.text1);


        //获取屏幕大小
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mGameManager = new GameManager(mMainBall, mFoodView, mBarrierView,textView1,
                dm.heightPixels, dm.widthPixels);
        mGameManager.init();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        mGameManager.updateBall(values[1],values[0]);
        int result = mGameManager.check();

        //这里的处理不太好， 可以考虑改成本地广播的方式实现
        if(result == 2){//碰到障碍物，死了
            mSensorManager.unregisterListener(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage("你挂了");
            builder.setCancelable(false);
            builder.setPositiveButton("在来一次", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mGameManager.init();
                    mSensorManager.registerListener(MainActivity.this, mAccelerSensor,
                            SensorManager.SENSOR_DELAY_GAME);
                }
            });
            builder.setNegativeButton("不来了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}

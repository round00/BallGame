package com.round0.ballgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameOverReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

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

        //初始化和注册本地广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.round0.ballgame.GAMEOVER_BROADCAST");
        localReceiver = new GameOverReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

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
        if(result == 2) {//碰到障碍物，死了
            Intent intent = new Intent("com.round0.ballgame.GAMEOVER_BROADCAST");
            localBroadcastManager.sendBroadcast(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    class GameOverReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
                mSensorManager.unregisterListener(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("游戏结束");
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
}

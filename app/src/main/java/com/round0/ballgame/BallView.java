package com.round0.ballgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import android.view.View;

public class BallView extends View {
    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ballColor = getResources().getColor(R.color.lighBlue);
    }

    //球位置
    private int posX = 0;
    private int posY = 0;
    //球半径
    public static final int RADIUS = 50;
    //球颜色
    private int ballColor;

    public void setPosition(int x, int y){
        posX = x;
        posY = y;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(ballColor);
        canvas.drawCircle(posX, posY, RADIUS, paint);
    }

}

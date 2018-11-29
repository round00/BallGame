package com.round0.ballgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FoodView extends View {
    public FoodView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSizeColor(int posX, int posY, int color){
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        invalidate();
    }
    private int posX;
    private int posY;
    private int color;
    public static final int RADIUS = 15;


    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(posX, posY, RADIUS, paint);
    }
}

package com.round0.ballgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BarrierView extends View {
    public BarrierView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private final float SQRT2 = 1.414f;

    private Position[] positions;

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        for (Position pos : positions){
            int sub = (int)(pos.radius*SQRT2/2);
            //draw a circle
            paint.setColor(Color.BLACK);
            canvas.drawCircle(pos.posX, pos.posY, pos.radius, paint);
            //draw two lines
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2.0f);
            canvas.drawLine(pos.posX-sub, pos.posY-sub,
                            pos.posX+sub, pos.posY+sub, paint);
            canvas.drawLine(pos.posX-sub, pos.posY+sub,
                            pos.posX+sub, pos.posY-sub, paint);
        }
    }

    public void setPositions(Position[] positions) {
        this.positions = positions;
        invalidate();
    }
}

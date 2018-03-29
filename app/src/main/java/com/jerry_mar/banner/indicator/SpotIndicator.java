package com.jerry_mar.banner.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import com.jerry_mar.banner.indicator.Indicator;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class SpotIndicator extends View implements Indicator {
    private int selectedColor;
    private int unselectedColor;
    private int radius;
    private int margin;
    private Paint paint = new Paint(ANTI_ALIAS_FLAG);

    public SpotIndicator(Context context, int selectedColor, int unselectedColor, int radius, int margin) {
        super(context, null);
        this.selectedColor = selectedColor;
        this.unselectedColor = unselectedColor;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(unselectedColor);
        this.radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                radius, getResources().getDisplayMetrics());
        this.margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                margin, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(radius * 2, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(radius * 2, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(radius, radius, radius - margin, getPaint());
    }

    @Override
    public void select(boolean selected) {
        if(selected)
            paint.setColor(selectedColor);
        else
            paint.setColor(unselectedColor);
        invalidate();
    }

    private Paint getPaint(){
        if(paint.getColor() == 0)
            paint.setColor(unselectedColor);
        return paint;
    }
}

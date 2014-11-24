
package com.scx.movemove.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * <h1>My Progress</h1> The MyProgress class is a progress bar class derivative from the ProgressBar
 * class provided in android.widget.
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class MyProgress extends ProgressBar {
    private String mText;
    private Paint mPaint;
    private Rect mRect = new Rect();

    public MyProgress(Context context) {
        super(context);
        this.initText();
    }

    public MyProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initText();
    }

    public MyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        this.setText(progress);
        super.setProgress(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.mPaint.getTextBounds(this.mText, 0, this.mText.length(),
                this.mRect);
        int x = (this.getWidth() / 2) - this.mRect.centerX();
        int y = (this.getHeight() / 2) - this.mRect.centerY();
        this.mPaint.setTextSize(30);
        canvas.drawText(this.mText, x, y, this.mPaint);
    }

    private void initText() {
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.WHITE);
    }

    @SuppressWarnings("unused")
    private void setText() {
        this.setText(this.getProgress());
    }

    private void setText(int progress) {
        int i = progress;
        this.mText = String.valueOf(i) + "%";
    }

}

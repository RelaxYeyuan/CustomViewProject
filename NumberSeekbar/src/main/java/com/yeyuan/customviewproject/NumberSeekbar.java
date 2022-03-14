package com.yeyuan.customviewproject;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

/**
 * User: chenhongrui
 * Date: 2022/3/11
 */
public class NumberSeekbar extends View {

    private static final String TAG = "NumberSeekbar";

    /**
     * 背景画笔
     */
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 进度条画笔
     */
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 数字画笔
     */
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 背景矩形
     */
    private final RectF backgroundRect = new RectF();

    /**
     * 进度条矩形
     */
    private final RectF progressRect = new RectF();

    /**
     * 圆角
     */
    private final int angleRounded = 15;

    /**
     * 进度最大值
     */
    private final int maxValue = 10;

    /**
     * 进度比例
     */
    private float valueScale;

    /**
     * 滑块
     */
    private final Drawable thumb;

    /**
     * 滑块宽度
     */
    private int thumbWidth;

    /**
     * 滑动的距离
     */
    private int left;

    /**
     * 背景宽度
     */
    private int backgroundWidth;

    /**
     * 背景高度
     */
    private int backgroundHeight;

    /**
     * 滑块padding
     */
    private final int thumbPadding = 3;

    /**
     * 当前进度
     */
    private int currentValue;

    /**
     * 数字居中偏移量
     */
    private final int offset;

    /**
     * 数字到滑块的距离
     */
    private int textPadding = 40;

    public NumberSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //背景颜色
        backgroundPaint.setColor(context.getResources().getColor(R.color.teal_700));
        //进度条颜色
        progressPaint.setColor(context.getResources().getColor(R.color.teal_200));
        //数字颜色
        valuePaint.setColor(Color.BLACK);
        valuePaint.setStrokeWidth(15);
        valuePaint.setTextSize(30);
        //计算数字居中值
        Rect rect = new Rect();
        valuePaint.getTextBounds("1", 0, "1".length(), rect);
        offset = (rect.top + rect.bottom) / 2;
        //滑块颜色
        thumb = ResourcesCompat.getDrawable(context.getResources(), R.drawable.thumb, context.getTheme());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //背景默认填充整个View
        backgroundRect.set(0, 0, w, h);
        backgroundWidth = w;
        backgroundHeight = h;
        thumbWidth = h;
        //计算滑动比列
        valueScale = (w - thumbWidth / 2) / maxValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画背景
        canvas.drawRoundRect(backgroundRect, angleRounded, angleRounded, backgroundPaint);
        //画进度条
        progressRect.set(0, 0, left + thumbWidth / 2f + thumbPadding, backgroundHeight);
        canvas.drawRoundRect(progressRect, angleRounded, angleRounded, progressPaint);
        //画滑块
        thumb.setBounds(left + thumbPadding, thumbPadding,
                backgroundHeight + left - thumbPadding,
                backgroundHeight - thumbPadding);
        thumb.draw(canvas);
        //滑块左侧数字
        currentValue = (int) (left / valueScale);
        Log.i(TAG, "onDraw: left " + left);
        Log.i(TAG, "onDraw: currentValue " + currentValue);
        canvas.drawText(checkNumber(currentValue),
                left - textPadding,
                backgroundHeight / 2f - offset,
                valuePaint);
    }

    /**
     * 暂时没想到好办法处理计算0的问题
     * 要考虑的滑块的宽度和初始值的问题
     * 配合139行强行处理
     */
    private String checkNumber(int currentValue) {
        String s;
        if (currentValue == 0) {
            s = "1";
        } else {
            s = String.valueOf(currentValue + 1);
        }
        return s;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                checkRange(x);
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 范围校验
     */
    private void checkRange(float x) {
        if (x > backgroundWidth - backgroundHeight) {
            left = backgroundWidth - backgroundHeight;
        } else if (x < 0) {
            left = 0;
        } else {
            left = (int) x;
        }
    }
}

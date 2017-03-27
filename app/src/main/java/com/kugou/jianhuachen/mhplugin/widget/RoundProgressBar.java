
package com.kugou.jianhuachen.mhplugin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.kugou.jianhuachen.mhplugin.R;


/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 * 
 * @author xiaanming
 */
public class RoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;

    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    private int colorID = Color.parseColor("#33ccff");

    private RectF oval = new RectF();

    private RectF point = new RectF();

    public static final int STROKE = 0;

    public static final int FILL = 1;

    private Context mContext;

    private float mDensity;

    public RoundProgressBar(Context context) {
        this(context, null);
        mContext = context;
        mDensity = mContext.getResources().getDisplayMetrics().density;
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        mDensity = mContext.getResources().getDisplayMetrics().density;

    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mDensity = mContext.getResources().getDisplayMetrics().density;

        paint = new Paint();

        TypedArray mTypedArray = context
                .obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        // 获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor,
                Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_text_Size, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 1000);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable,
                true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; // 获取圆心的x坐标
        int radius = (int) (centre - roundWidth / 2 - 5); // 圆环的半径
        paint.setColor(roundColor); // 设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); // 设置空心
        paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
        paint.setAntiAlias(true); // 消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); // 画出圆环

        // /**
        // * 画进度百分比
        // */
        // paint.setStrokeWidth(0);
        // paint.setColor(textColor);
        // paint.setTextSize(textSize);
        // paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
        // int percent = (int) (((float) progress / (float) max) * 100); //
        // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
        // float textWidth = paint.measureText(percent + "%"); //
        // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        //
        // if (textIsDisplayable && percent != 0 && style == STROKE) {
        // canvas.drawText(percent + "%", centre - textWidth / 2, centre +
        // textSize / 2, paint); // 画出进度百分比
        // }

        /**
         * 画圆弧 ，画圆环的进度
         */

        // 设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
        paint.setColor(roundProgressColor); // 设置进度的颜色
        oval.set(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, -90, -360 * progress / max, false, paint); // 根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paint); // 根据进度画圆弧
                break;
            }
        }
        float trackX = (float) ((double) centre + (Math.sin(Math.toRadians((double) 360
                * (max - progress) / max)) * (double) radius));
        float trackY = (float) ((double) centre - (Math.cos(Math.toRadians((double) 360
                * (max - progress) / max)) * (double) radius));

        point.set(trackX - 1.6f * mDensity, trackY - 1.6f * mDensity, trackX + 1.6f * mDensity,
                trackY + 1.6f * mDensity);
        paint.setColor(colorID);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawOval(point, paint);

    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     * 
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     * 
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
     * 
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }

    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public void setColorID(int id) {
        this.colorID = id;
    }

}

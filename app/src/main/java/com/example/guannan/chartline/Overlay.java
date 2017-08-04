package com.example.guannan.chartline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by guannan on 2017/8/2.
 */

public class Overlay extends View {

    private Context mContext;
    private Paint mDashLinePaint;
    private int mWidth; //当前View的宽度
    private int mHeight; //当前View的高度
    private Paint mCalPaint;
    private int mTextPadding = 15;  //文字和刻度之间的间距
    private float mMultiple = 0.10f;

    private String mMonth[] = new String[]{};
    private float mValues[] = new float[]{200,300,325,500,725};
    private Paint mBottomLinePaint;
    private Paint mChartPaint;
    private float mTotalYPx;        //y轴总像素数
    private float mTotalXPx;        //x轴总像素数
    private String mXCalValues[];   //x轴的坐标值
    private float mMaxYValue;       //y轴的最大值
    private int mYCalCount;         //y轴的刻度数
    private float[] mChartValues;

    private ArrayList<String> mTotalYValue = new ArrayList<>();
    private ArrayList<Point> mTotalPoints;

    public Overlay(Context context) {
        this(context,null);
    }

    public Overlay(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Overlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initPaint();
    }

    /**
     * 设置X和Y轴的刻度值
     * @param maxYValue
     * @param calCount
     * @param totalX
     */
    public void setCalValue(float maxYValue,int calCount,String[] totalX,float[] chartValues){

        this.mMaxYValue = maxYValue;
        this.mYCalCount = calCount;
        this.mXCalValues = totalX;
        this.mChartValues = chartValues;
        initData();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {

        //虚线画笔
        mDashLinePaint = new Paint();
        mDashLinePaint.setStrokeWidth(dipToPx(0.5f));
        mDashLinePaint.setAntiAlias(true);
        mDashLinePaint.setStyle(Paint.Style.STROKE);
        //左边和底部的的刻度值
        mCalPaint = new Paint();
        mCalPaint.setStyle(Paint.Style.STROKE);
        mCalPaint.setTextSize(dipToPx(15));
        mCalPaint.setColor(Color.parseColor("#2b2b2b"));
        //底部的刻度线
        mBottomLinePaint = new Paint();
        mBottomLinePaint.setStrokeWidth(dipToPx(1f));
        mBottomLinePaint.setAntiAlias(true);
        mBottomLinePaint.setStyle(Paint.Style.STROKE);
        //折线图画笔
        mChartPaint = new Paint();
        mChartPaint.setStyle(Paint.Style.STROKE);
        mChartPaint.setAntiAlias(true);
        mChartPaint.setColor(0xff02bbb7);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initData();
    }

    /**
     * 初始化参数
     */
    private void initData() {

        //底部刻度高度为15px，文字占用15dp，文字和刻度间距为15px
        mTotalYPx = mHeight-mHeight*mMultiple - dipToPx(15) - 2 * mTextPadding;
        //视图距离左右间距为宽度的十分之一
        mTotalXPx = mWidth - 2 * mWidth * mMultiple;

        float yPxValue = mTotalYPx / mYCalCount;    //y轴两个刻度之间的像素大小
        for (int i = 0; i < mYCalCount; i++) {

            DecimalFormat decimalFormat = new DecimalFormat(".00");
            mTotalYValue.add(decimalFormat.format(mMaxYValue - i * yPxValue));
        }

        mTotalPoints = new ArrayList<>();
        int mBottomCalWidth = (int) ((mWidth-2*mWidth*mMultiple) / mXCalValues.length);
        float valueOfPx = mTotalYPx / mMaxYValue;   //每像素值的大小

        for (int i = 0; i < mXCalValues.length; i++) {

            Point point = new Point();

            point.x = (int) (mWidth * mMultiple + mBottomCalWidth * (i + 1));
            point.y = (int) (mHeight-dipToPx(15) - 2 * mTextPadding-mChartValues[i] * valueOfPx);
            mTotalPoints.add(point);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDashLine(canvas);
        drawLeftCal(canvas);
        drawBottomLine(canvas);
        drawTimeCal(canvas);
        drawChartLine(canvas);
    }

    /**
     * 画折线图
     * @param canvas
     */
    private void drawChartLine(Canvas canvas) {

        Path path = new Path();
        path.moveTo(mTotalPoints.get(0).x,mTotalPoints.get(0).y);
        for (int i = 1; i < mTotalPoints.size(); i++) {
            path.lineTo(mTotalPoints.get(i).x,mTotalPoints.get(i).y);
        }
        canvas.drawPath(path,mChartPaint);
    }

    /**
     * 画底部刻度横线和刻度
     * @param canvas
     */
    private void drawBottomLine(Canvas canvas) {

        Path path = new Path();
        path.moveTo(mWidth*mMultiple,mHeight-dipToPx(15)-2*mTextPadding);
        path.lineTo(mWidth,mHeight-dipToPx(15)-2*mTextPadding);
        canvas.drawPath(path,mBottomLinePaint);

        int mBottomCalWidth = (int) ((mWidth-2*mWidth*mMultiple) / mXCalValues.length);

        for (int i = 1; i <= mXCalValues.length; i++) {

            float calX = mWidth * mMultiple + mBottomCalWidth * i;
            Path pathCal = new Path();
            pathCal.moveTo(calX,mHeight-dipToPx(15)-2*mTextPadding);
            pathCal.lineTo(calX,mHeight-dipToPx(15)-mTextPadding);
            canvas.drawPath(pathCal,mBottomLinePaint);
        }


    }

    /**
     * 画y轴每一刻度向右的虚线
     * @param canvas
     */
    private void drawDashLine(Canvas canvas) {

        mDashLinePaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));  //设置虚线：20px实线，10px空白，偏移量为0px

        float yPxValue = mTotalYPx / mYCalCount;    //y轴两个刻度之间的像素大小

        for (int i = 0; i < mYCalCount; i++) {
            Path path = new Path();
            path.moveTo(mWidth*mMultiple+mTextPadding,mHeight*mMultiple+i*yPxValue);
            path.lineTo(mWidth-mWidth*mMultiple,mHeight*mMultiple+i*yPxValue);
            canvas.drawPath(path,mDashLinePaint);
        }
    }

    /**
     * 画左边的刻度值
     * @param canvas
     */
    private void drawLeftCal(Canvas canvas) {

        float yPxValue = mTotalYPx / mYCalCount;
        for (int i = 0; i < mYCalCount; i++) {

            Rect rect = new Rect();
            String yCalValue = mTotalYValue.get(i);
            mCalPaint.getTextBounds(yCalValue,0,yCalValue.length(),rect);
            int width = rect.width();
            int height = rect.height();
            canvas.drawText(yCalValue,mWidth*mMultiple-width,mHeight*mMultiple+i*yPxValue+height/2,mCalPaint);
        }
    }

    /**
     * 画底部刻度值
     * @param canvas
     */
    private void drawTimeCal(Canvas canvas) {

        int mBottomCalWidth = (int) ((mWidth-2*mWidth*mMultiple) / mXCalValues.length);
        for (int i = 1; i <= mXCalValues.length; i++) {

            float calX = mWidth * mMultiple + mBottomCalWidth * i;
            String month = mXCalValues[i-1];

            Rect rect = new Rect();
            mCalPaint.getTextBounds(month,0,month.length(),rect);
            int width = rect.width();
            int height = rect.height();
            canvas.drawText(month,calX-width/2,mHeight-dipToPx(15)+height,mCalPaint);
        }

    }

    public int getScreenWidth(){
        WindowManager mWindowManger = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return mWindowManger.getDefaultDisplay().getWidth();
    }

    private int dipToPx(float dip)
    {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }
}

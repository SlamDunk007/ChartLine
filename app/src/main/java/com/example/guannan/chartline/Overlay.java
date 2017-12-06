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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

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

    private Paint mBottomLinePaint;
    private Paint mChartPaint;
    private float mTotalYPx;        //y轴总像素数
    private String mXCalValues[];   //x轴的坐标值
    private float mMaxYValue;       //y轴的最大值
    private int mYCalCount;         //y轴的刻度数
    private float[] mChartValues;

    private ArrayList<String> mTotalYValue = new ArrayList<>();
    private ArrayList<Point> mTotalPoints;
    private Paint mPointPaint;
    private int mBottomCalWidth;
    private float mDiffValue;
    private float mYPxValue;
    private int mPosition = -1;

    //折线的路径
    private Path mPath = new Path();
    private Path mPathBg = new Path();
    private Paint mBgPaint;
    private Paint mBgTextPaint;
    private float mPressX;
    private boolean mShowReticle;
    private Paint mReticlePaint;
    private Point mMinPoint;
    private Paint mChartBgPaint;

    public Overlay(Context context) {
        this(context, null);
    }

    public Overlay(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Overlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initPaint();
    }

    /**
     * 设置X和Y轴的刻度值
     *
     * @param maxYValue
     * @param calCount
     * @param totalX
     */
    public void setCalValue(float maxYValue, int calCount, String[] totalX, float[] chartValues) {

        this.mMaxYValue = maxYValue;
        this.mYCalCount = calCount;
        this.mXCalValues = totalX;
        this.mChartValues = chartValues;
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
        //折线图下方背景画笔
        mChartBgPaint = new Paint();
        mChartBgPaint.setStyle(Paint.Style.FILL);
        mChartBgPaint.setAntiAlias(true);
        mChartBgPaint.setColor(Color.parseColor("#74caf9"));
        //穿过这线小圆点
        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setAntiAlias(true);
        //背景矩形
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(Color.parseColor("#717474"));
        mBgPaint.setAntiAlias(true);
        //点击背景显示的数量大小
        mBgTextPaint = new Paint();
        mBgTextPaint.setAntiAlias(true);
        mBgTextPaint.setTextSize(dipToPx(12));
        mBgTextPaint.setStyle(Paint.Style.FILL);
        mBgTextPaint.setColor(Color.WHITE);
        //十字线
        mReticlePaint = new Paint();
        mReticlePaint.setAntiAlias(true);
        mReticlePaint.setColor(Color.parseColor("#2b2b2b"));
        mReticlePaint.setStyle(Paint.Style.FILL);
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
        mTotalYPx = mHeight - mHeight * mMultiple - dipToPx(15) - 2 * mTextPadding;
        //底部两个刻度之间的大小px
        mBottomCalWidth = (int) ((mWidth - 2 * mWidth * mMultiple) / mXCalValues.length);
        //刻度之外的间距距离
        mDiffValue = mWidth - 2 * mWidth * mMultiple - mBottomCalWidth * (mXCalValues.length - 1);
        //y轴两个刻度之间的像素大小
        mYPxValue = mTotalYPx / mYCalCount;

        for (int i = 0; i < mYCalCount; i++) {

            mTotalYValue.add(String.valueOf((int) (mMaxYValue - i * mYPxValue)));
        }

        mTotalPoints = new ArrayList<>();
        float valueOfPx = mTotalYPx / mMaxYValue;   //每像素值的大小

        //初始化这线上每个点的坐标位置
        for (int i = 0; i < mXCalValues.length; i++) {
            Point point = new Point();
            if (i == 0) {
                point.x = (int) (mWidth * mMultiple + mDiffValue / 2);
            } else {
                point.x = (int) (mWidth * mMultiple + mBottomCalWidth * i + mDiffValue / 2);
            }

            point.y = (int) (mHeight - dipToPx(15) - 2 * mTextPadding - mChartValues[i] * valueOfPx);
            mTotalPoints.add(point);
            if (i == 0)  //连接所有的折线转折点
            {
                mPath.moveTo(point.x, point.y);
                mPathBg.moveTo(point.x,point.y);
            } else {
                mPath.lineTo(point.x, point.y);
                mPathBg.lineTo(point.x,point.y);
            }
            if(i == mXCalValues.length -1){ //最后一个点

                mPathBg.lineTo(point.x,mHeight - dipToPx(16) - 2 * mTextPadding);
                mPathBg.lineTo(mWidth * mMultiple,mHeight - dipToPx(16) - 2 * mTextPadding);
                mPathBg.close();
            }
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
        drawPointCircle(canvas);
        drawLongPress(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        long downTime = 0;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = motionEvent.getDownTime();
                break;
            case MotionEvent.ACTION_MOVE:
                mPressX = motionEvent.getX();   //按下的位置的X坐标
                if (motionEvent.getEventTime() - downTime > 700) {
                    drawReticle();
                }

                break;
            case MotionEvent.ACTION_UP:
                hideRecticle();
                break;
        }
        return true;
    }

    /**
     * 隐藏十字线和提示
     */
    private void hideRecticle() {

        postDelayed(new Runnable() {
            @Override
            public void run() {
                mShowReticle = false;
                invalidate();
            }
        },1000);
    }

    /**
     * 绘制十字线
     */
    private void drawReticle() {
        mShowReticle = true;
        invalidate();
    }

    /**
     * 处理长按滑动时候十字线
     * @param canvas
     */
    public void drawLongPress(Canvas canvas) {
        if(!mShowReticle){
            return;
        }
        mMinPoint = null;
        int minX = Integer.MAX_VALUE;
        for (int i = 0; i < mTotalPoints.size(); i++) {
            Point point = mTotalPoints.get(i);
            int pointX = point.x;
            float absX = Math.abs(pointX - mPressX);
            if (absX < minX) {
                minX = (int) absX;
                mMinPoint = point;
                mPosition = i;
            }
        }
        canvas.drawLine(mWidth * mMultiple, mMinPoint.y,mWidth - mWidth * mMultiple, mMinPoint.y,mReticlePaint);
        canvas.drawLine(mMinPoint.x,mHeight*mMultiple, mMinPoint.x,mHeight-mWidth*mMultiple,mReticlePaint);

        int x = mMinPoint.x;
        int y = mMinPoint.y;
        canvas.drawRect(x - dipToPx(15), y - dipToPx(18), x + dipToPx(15), y - dipToPx(6), mBgPaint);
        String value = String.valueOf(mChartValues[mPosition]);
        Rect rect = new Rect();
        mBgTextPaint.getTextBounds(value, 0, value.length(), rect);
        canvas.drawText(value, x - rect.width() / 2, y - dipToPx(12) + rect.height() / 2, mBgTextPaint);
    }

    /**
     * 画穿过折线的原点
     *
     * @param canvas
     */
    private void drawPointCircle(Canvas canvas) {

        for (int i = 0; i < mTotalPoints.size(); i++) {
            mPointPaint.setStyle(Paint.Style.FILL);
            mPointPaint.setColor(Color.GREEN);
            canvas.drawCircle(mTotalPoints.get(i).x, mTotalPoints.get(i).y, dipToPx(2), mPointPaint);
            mPointPaint.setStyle(Paint.Style.STROKE);
            mPointPaint.setColor(Color.GREEN);
            canvas.drawCircle(mTotalPoints.get(i).x, mTotalPoints.get(i).y, dipToPx(4), mPointPaint);
        }
    }

    /**
     * 画折线图
     *
     * @param canvas
     */
    private void drawChartLine(Canvas canvas) {

        canvas.drawPath(mPath, mChartPaint);
        canvas.drawPath(mPathBg,mChartBgPaint);
    }

    /**
     * 画底部刻度横线和刻度
     *
     * @param canvas
     */
    private void drawBottomLine(Canvas canvas) {

        Path path = new Path();
        path.moveTo(mWidth * mMultiple, mHeight - dipToPx(15) - 2 * mTextPadding);
        path.lineTo(mWidth - mWidth * mMultiple, mHeight - dipToPx(15) - 2 * mTextPadding);
        canvas.drawPath(path, mBottomLinePaint);

        for (int i = 0; i < mXCalValues.length; i++) {
            float calX;
            if (i == 0)
                calX = mWidth * mMultiple + mDiffValue / 2;
            else
                calX = mWidth * mMultiple + mBottomCalWidth * i + mDiffValue / 2;

            Path pathCal = new Path();
            pathCal.moveTo(calX, mHeight - dipToPx(15) - 2 * mTextPadding);
            pathCal.lineTo(calX, mHeight - dipToPx(15) - mTextPadding);
            canvas.drawPath(pathCal, mBottomLinePaint);
        }

    }

    /**
     * 画y轴每一刻度向右的虚线
     *
     * @param canvas
     */
    private void drawDashLine(Canvas canvas) {

        mDashLinePaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));  //设置虚线：20px实线，10px空白，偏移量为0px

        for (int i = 0; i < mYCalCount; i++) {
            Path path = new Path();
            path.moveTo(mWidth * mMultiple + mTextPadding, mHeight * mMultiple + i * mYPxValue);
            path.lineTo(mWidth - mWidth * mMultiple, mHeight * mMultiple + i * mYPxValue);
            canvas.drawPath(path, mDashLinePaint);
        }
    }

    /**
     * 画左边的刻度值
     *
     * @param canvas
     */
    private void drawLeftCal(Canvas canvas) {

        for (int i = 0; i < mYCalCount; i++) {

            Rect rect = new Rect();
            String yCalValue = mTotalYValue.get(i);
            mCalPaint.getTextBounds(yCalValue, 0, yCalValue.length(), rect);
            int width = rect.width();
            int height = rect.height();
            canvas.drawText(yCalValue, mWidth * mMultiple - width, mHeight * mMultiple + i * mYPxValue + height / 2, mCalPaint);
        }
    }

    /**
     * 画底部刻度值
     *
     * @param canvas
     */
    private void drawTimeCal(Canvas canvas) {


        for (int i = 0; i < mXCalValues.length; i++) {

            float calX;
            if (i == 0)
                calX = mWidth * mMultiple + mDiffValue / 2;
            else
                calX = mWidth * mMultiple + mBottomCalWidth * i + mDiffValue / 2;

            String month = mXCalValues[i];
            Rect rect = new Rect();
            mCalPaint.getTextBounds(month, 0, month.length(), rect);
            int width = rect.width();
            int height = rect.height();
            canvas.drawText(month, calX - width / 2, mHeight - dipToPx(15) + height, mCalPaint);
        }

    }

    public int getScreenWidth() {
        WindowManager mWindowManger = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return mWindowManger.getDefaultDisplay().getWidth();
    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }
}

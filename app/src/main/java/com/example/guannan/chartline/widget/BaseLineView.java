package com.example.guannan.chartline.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.guannan.chartline.R;
import com.example.guannan.chartline.utils.ScreenUtils;

/**
 * @author guannan
 * @date 2018/4/10 15:48
 */

public class BaseLineView extends View {

    //视图的宽度
    private int mWidth;
    //视图的高度
    private int mHeight;
    //视图内上下左右内边距
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    //实时价格和成交量直接的区域的高度
    private int mCenterAreaHeight;
    //价格区域
    private Rect mPriceRectFrame;
    //成交量区域
    private Rect mVolumeRectFrame;
    //技术指标区域
    private Rect mTecRectFrame;
    //分时图
    private TimeSharingChart mTimeSharingChart;
    //是否已经初始化了宽和高
    private boolean isInitWidthHeight;

    public BaseLineView(Context context) {
        this(context, null);
    }

    public BaseLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BaseLineView, 0, defStyleAttr);
        mPaddingLeft = (int) typedArray.getDimension(R.styleable.BaseLineView_paddingLeft, 0);
        mPaddingTop = (int) typedArray.getDimension(R.styleable.BaseLineView_paddingTop, 0);
        mPaddingRight = (int) typedArray.getDimension(R.styleable.BaseLineView_paddingRight, 0);
        mPaddingBottom = (int) typedArray.getDimension(R.styleable.BaseLineView_paddingBottom, 0);
        mCenterAreaHeight = (int) typedArray.getDimension(R.styleable.BaseLineView_centerAreaSize, ScreenUtils.dip2px(context,20));
        typedArray.recycle();
        //初始化整个视图的价格，成交量和技术指标区域
        mPriceRectFrame = new Rect();
        mTecRectFrame = new Rect();
        mVolumeRectFrame = new Rect();
        isInitWidthHeight = false;
    }

    /**
     * 设置当前行情图类型
     *
     * @param timeSharingChart
     */
    public void setCurrentView(TimeSharingChart timeSharingChart) {
        if (timeSharingChart != null) {
            this.mTimeSharingChart = timeSharingChart;
        }
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initViewSize(right - left, bottom - top);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initViewSize(w, h);
    }

    /**
     * 初始化分时图的宽高以及主图和成交量图的大小
     *
     * @param w
     * @param h
     */
    private void initViewSize(int w, int h) {
        if (!isInitWidthHeight) {
            isInitWidthHeight = true;
            mWidth = w;
            mHeight = h;
            //价格区域占据整个视图的高度的70%
            int priceHeight = (int) (mHeight * 0.7);
            //设置每个区域的范围
            mPriceRectFrame.set(mPaddingLeft, mPaddingTop, mWidth - mPaddingRight, priceHeight);
            mTecRectFrame.set(mPaddingLeft, mPriceRectFrame.bottom, mWidth - mPaddingRight, mPriceRectFrame.bottom + mCenterAreaHeight);
            mVolumeRectFrame.set(mPaddingLeft, mTecRectFrame.bottom, mWidth - mPaddingRight, mHeight-mPaddingBottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTimeSharingChart.draw(canvas, this);
    }

    /**
     * 获取实时价格rect区域
     *
     * @return
     */
    public Rect getPriceRectFrame() {
        return mPriceRectFrame;
    }

    /**
     * 获取技术指标rect区域
     *
     * @return
     */
    public Rect getTecRectFrame() {
        return mTecRectFrame;
    }

    /**
     * 获取成交量rect区域
     *
     * @return
     */
    public Rect getVolumeRectFrame() {
        return mVolumeRectFrame;
    }
}

package com.example.guannan.chartline.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.guannan.chartline.R;
import com.example.guannan.chartline.data.TimeSharingDetail;
import com.example.guannan.chartline.utils.PaintUtil;
import com.example.guannan.chartline.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * @author guannan
 * @date 2018/4/10 15:48
 */

public class TimeSharingView extends View {

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
    //分时图的数据集合
    private ArrayList<TimeSharingDetail> mList;
    //分时图每日总共有多少个点
    private int everyDayColumns;
    //每日的开盘收盘时间点
    private String[] mTimeArrays;
    //当日最大价格
    private float maxPrice;
    //当日最小价格
    private float minPrice;
    //最大成交量
    private float maxVolume;
    //分时价格path
    private Path mPathPrice;
    //平均价格path
    private Path mPathAvgPrice;

    public TimeSharingView(Context context) {
        this(context, null);
    }

    public TimeSharingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSharingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TimeSharingView, 0, defStyleAttr);
        mPaddingLeft = (int) typedArray.getDimension(R.styleable.TimeSharingView_paddingLeft, 0);
        mPaddingTop = (int) typedArray.getDimension(R.styleable.TimeSharingView_paddingTop, 0);
        mPaddingRight = (int) typedArray.getDimension(R.styleable.TimeSharingView_paddingRight, 0);
        mPaddingBottom = (int) typedArray.getDimension(R.styleable.TimeSharingView_paddingBottom, 0);
        mCenterAreaHeight = (int) typedArray.getDimension(R.styleable.TimeSharingView_centerAreaSize, 0);
        typedArray.recycle();
        //初始化整个视图的价格，成交量和技术指标区域
        mPriceRectFrame = new Rect();
        mTecRectFrame = new Rect();
        mVolumeRectFrame = new Rect();
        //总共有多少个点，这里以港股为例，港股共有242
        everyDayColumns = 242;
        mTimeArrays = new String[]{"9:30", "12:00/13:00", "16:00"};
        //初始化最大值最小值
        maxPrice = Float.MIN_VALUE;
        minPrice = Float.MAX_VALUE;
        maxVolume = Float.MIN_VALUE;
    }

    /**
     * 设置分时图的数据
     *
     * @param list
     */
    public void setTimeData(ArrayList<TimeSharingDetail> list) {

        this.mList = list;
        initDrawData(list);
//        invalidate();
    }

    /**
     * 处理分时图所需的数据
     *
     * @param list
     */
    private void initDrawData(ArrayList<TimeSharingDetail> list) {

        if (list == null || list.isEmpty()) {
            return;
        }
        mPathPrice = new Path();
        mPathAvgPrice = new Path();
        mPathPrice.reset();
        mPathAvgPrice.reset();
        //获取价格最大值，最小值和最大成交量
        for (int i = 0; i < list.size(); i++) {
            TimeSharingDetail timeSharingDetail = list.get(i);
            float price = Float.valueOf(timeSharingDetail.getPrice());
            if (price > maxPrice) {
                maxPrice = price;
            }
            if (price < minPrice) {
                minPrice = price;
            }
            float volume = Float.valueOf(timeSharingDetail.getVolume());
            if (volume > maxVolume) {
                maxVolume = volume;
            }
        }
        //最大值和最小值的差值
        float diffPrice = maxPrice - minPrice;
        //初始化分时图线图数据
        for (int i = 0; i < list.size(); i++) {

            TimeSharingDetail timeSharingDetail = list.get(i);
            float scalePrice = (maxPrice - Float.valueOf(timeSharingDetail.getPrice())) / diffPrice;
            Point pointPrice = getPricePoint(mPriceRectFrame, i, scalePrice);
            if (i == 0) {
                mPathPrice.moveTo(pointPrice.x, pointPrice.y);
            } else {
                mPathPrice.lineTo(pointPrice.x, pointPrice.y);
            }

        }
    }

    /**
     * 得到每一个分时点的价格ponit
     *
     * @param rect
     * @param i
     * @param scalePrice
     */
    private Point getPricePoint(Rect rect, int i, float scalePrice) {

        Point point = new Point();
        point.x = (int) (rect.left + rect.width() * (i + 0.5) / everyDayColumns);
        point.y = (int) (rect.top + rect.height() * scalePrice);
        return point;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
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
        mWidth = w;
        mHeight = h;
        //价格区域占据整个视图的高度的70%
        int priceHeight = (int) (mHeight * 0.7);
        //设置每个区域的范围
        mPriceRectFrame.set(mPaddingLeft, mPaddingTop, mWidth - mPaddingRight, priceHeight);
        mTecRectFrame.set(mPaddingLeft, mPriceRectFrame.bottom, mWidth - mPaddingRight, mPriceRectFrame.bottom + mCenterAreaHeight);
        mVolumeRectFrame.set(mPaddingLeft, mTecRectFrame.bottom, mWidth - mPaddingRight, mHeight - mPaddingBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            width = ScreenUtils.getScreenWidth(getContext());
        } else {
            width = widthSize;
        }
        setMeasuredDimension(width, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPathPrice!=null){
            canvas.drawPath(mPathPrice, PaintUtil.PAINT_MINUETE_LINE);
        }

    }
}

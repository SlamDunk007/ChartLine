package com.example.guannan.chartline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.example.guannan.chartline.data.TimeSharingDetail;
import com.example.guannan.chartline.utils.PaintUtil;

import java.util.ArrayList;

/**
 * @author guannan
 * @date 2018/4/26 15:20
 */

public class TimeSharingChart {

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
    //区域外围的边线
    private Path mPathOutLine;
    //分时价格path
    private Path mPathPrice;
    //平均价格path
    private Path mPathAvgPrice;
    //技术区域边线
    private Path mPathTecLine;
    //昨收价线
    private Path mPathPre;
    //分时图的数据集合
    private ArrayList<TimeSharingDetail> mList;
    //价格区域
    private Rect mPriceRectFrame;
    //技术指标区域
    private Rect mTecRectFrame;
    //成交量区域
    private Rect mVolumeRectFrame;
    private BaseLineView mBaseLineView;
    //昨收价
    private float mPrePrice = 0.0f;
    private Context mContext;

    public TimeSharingChart(Context context, ArrayList<TimeSharingDetail> list, float prePrice) {
        this.mContext = context;
        this.mList = list;
        this.mPrePrice = prePrice;
        init();
    }

    private void init() {

        //总共有多少个点，这里以港股为例，港股共有242
        everyDayColumns = 313;
        mTimeArrays = new String[]{"9:30", "12:00/13:00", "16:00"};
        //初始化最大值最小值
        maxPrice = Float.MIN_VALUE;
        minPrice = Float.MAX_VALUE;
        maxVolume = Float.MIN_VALUE;
    }

    /**
     * 分时图具体的绘制流程
     *
     * @param canvas
     * @param baseLineView
     */
    public void draw(Canvas canvas, BaseLineView baseLineView) {
        this.mBaseLineView = baseLineView;
        initDrawData();
        canvas.drawPath(mPathPrice, PaintUtil.PAINT_MINUETE_LINE);
        canvas.drawPath(mPathOutLine,PaintUtil.PAINT_OUT_LINE);
        canvas.drawPath(mPathTecLine,PaintUtil.PAINT_OUT_LINE);
        canvas.drawPath(mPathPre,PaintUtil.PAINT_PREPRICE_LINE);
    }


    /**
     * 处理分时图所需的数据
     */
    private void initDrawData() {

        if (mList == null || mList.isEmpty()) {
            return;
        }
        if (mBaseLineView != null) {
            mPriceRectFrame = mBaseLineView.getPriceRectFrame();
            mTecRectFrame = mBaseLineView.getTecRectFrame();
            mVolumeRectFrame = mBaseLineView.getVolumeRectFrame();
        }
        initPath();
        //获取价格最大值，最小值和最大成交量
        for (int i = 0; i < mList.size(); i++) {
            TimeSharingDetail timeSharingDetail = mList.get(i);
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
        float delta = countMaxMinValue();
        maxPrice = mPrePrice + delta;
        minPrice = mPrePrice - delta;
        //最大值和最小值的差值
        float diffPrice = maxPrice - minPrice;
        //初始化分时图线图数据
        for (int i = 0; i < mList.size(); i++) {

            TimeSharingDetail timeSharingDetail = mList.get(i);
            float scalePrice = (maxPrice - Float.valueOf(timeSharingDetail.getPrice())) / diffPrice;
            Point pointPrice = getPricePoint(mPriceRectFrame, i, scalePrice);
            if (i == 0) {
                mPathPrice.moveTo(pointPrice.x, pointPrice.y);
            } else {
                mPathPrice.lineTo(pointPrice.x, pointPrice.y);
            }
        }
        //链接外围线段
        mPathOutLine.addRect(mPriceRectFrame.left,mPriceRectFrame.top,mPriceRectFrame.right,mPriceRectFrame.bottom, Path.Direction.CCW);
        mPathOutLine.addRect(mVolumeRectFrame.left,mVolumeRectFrame.top,mVolumeRectFrame.right,mVolumeRectFrame.bottom, Path.Direction.CCW);
        //技术区域边线
        mPathTecLine.moveTo(mTecRectFrame.left,mTecRectFrame.top);
        mPathTecLine.lineTo(mTecRectFrame.right,mTecRectFrame.top);
        mPathTecLine.lineTo(mTecRectFrame.right,mTecRectFrame.bottom);
        mPathTecLine.lineTo(mTecRectFrame.left,mTecRectFrame.bottom);
        mPathTecLine.close();
        //昨日收盘价参考线
        mPathPre.moveTo(mPriceRectFrame.left,mPriceRectFrame.height()/2);
        mPathPre.lineTo(mPriceRectFrame.right,mPriceRectFrame.height()/2);
    }

    /**
     * 初始化path
     */
    private void initPath() {
        mPathPrice = new Path();
        mPathAvgPrice = new Path();
        mPathOutLine = new Path();
        mPathTecLine = new Path();
        mPathPre = new Path();
        mPathPrice.reset();
        mPathAvgPrice.reset();
        mPathAvgPrice.reset();
        mPathTecLine.reset();
        mPathPre.reset();
    }

    /**
     * 处理最大值最小值，使其分布在昨日收盘价格上下部分
     *
     * @return
     */
    private float countMaxMinValue() {
        float delta_max = Math.abs(maxPrice - mPrePrice);
        float delta_min = Math.abs(minPrice - mPrePrice);
        float delta = Math.max(delta_max, delta_min);
        delta *= 1.05;
        if (mPrePrice > 10 && delta < 0.02f) {
            delta = 0.02f;
        }
        if (mPrePrice < 10 && delta < 0.002f) {
            delta = 0.002f;
            if (mPrePrice > 0.01f) {
                delta = 0.01f; //避免在价格坐标轴上无法体现出来
            }
        }
        return delta;
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
        point.x = rect.left + (int) (rect.width() * (i + 0.5) / everyDayColumns);
        point.y = rect.top + (int) (rect.height() * scalePrice);
        return point;
    }

}

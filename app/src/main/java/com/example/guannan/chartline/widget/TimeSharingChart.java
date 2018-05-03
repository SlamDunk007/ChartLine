package com.example.guannan.chartline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;

import com.example.guannan.chartline.data.TimeSharingDetail;
import com.example.guannan.chartline.data.TimeSharingVolume;
import com.example.guannan.chartline.utils.DataUtils;
import com.example.guannan.chartline.utils.PaintUtil;
import com.example.guannan.chartline.utils.ScreenUtils;

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
    private Path mPathPriceBg;
    private ArrayList<TimeSharingVolume> mVolumeList;

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
        //成交量集合
        mVolumeList = new ArrayList<>();
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
        //绘制分时图价格线
        canvas.drawPath(mPathPrice, PaintUtil.PAINT_MINUETE_LINE);
        //绘制外围边界线
        canvas.drawPath(mPathOutLine, PaintUtil.PAINT_OUT_LINE);
        //绘制技术区域边界线
        canvas.drawPath(mPathTecLine, PaintUtil.PAINT_OUT_LINE);
        //绘制昨日收盘价参考线
        canvas.drawPath(mPathPre, PaintUtil.PAINT_PREPRICE_LINE);
        //绘制分时线下面的渐变部分
        drawGradient(canvas);
        //绘制时间技术指标参数
        drawTimeCal(canvas);
        //绘制成交量
        drawVolume(canvas);

    }

    /**
     * 绘制成交量
     * @param canvas
     */
    private void drawVolume(Canvas canvas) {
        for (int i = 0; i < mVolumeList.size(); i++) {
            TimeSharingVolume volume = mVolumeList.get(i);
            Point point = volume.point;
            canvas.drawLine(point.x,point.y,point.x,mVolumeRectFrame.bottom,volume.paint);
        }

    }

    /**
     * 绘制时间技术指标参数
     *
     * @param canvas
     */
    private void drawTimeCal(Canvas canvas) {

        for (int i = 0; i < mTimeArrays.length; i++) {
            String text = mTimeArrays[i];
            Rect rect = new Rect();
            PaintUtil.PAINT_TIME_VOLUME.setTextSize(ScreenUtils.dip2px(mContext, 10));
            PaintUtil.PAINT_TIME_VOLUME.getTextBounds(text, 0, text.length(), rect);
            int margin = ScreenUtils.dip2px(mContext, 3);
            if (i == 0) {
                PaintUtil.PAINT_TIME_VOLUME.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(text, mTecRectFrame.left + margin, mTecRectFrame.top + rect.height() + margin, PaintUtil.PAINT_TIME_VOLUME);
            } else if (i == 1) {
                PaintUtil.PAINT_TIME_VOLUME.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(text, mTecRectFrame.width() / 2, mTecRectFrame.top + rect.height() + margin, PaintUtil.PAINT_TIME_VOLUME);
            } else if (i == 2) {
                PaintUtil.PAINT_TIME_VOLUME.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(text, mTecRectFrame.right - margin, mTecRectFrame.top + rect.height() + margin, PaintUtil.PAINT_TIME_VOLUME);
            }
        }

    }

    /**
     * 绘制分时线下面的渐变部分
     *
     * @param canvas
     */
    private void drawGradient(Canvas canvas) {

        LinearGradient linearGradient = new LinearGradient(mPriceRectFrame.left, mPriceRectFrame.top
                , mPriceRectFrame.left, mPriceRectFrame.bottom, PaintUtil.C_PRICE_BG, PaintUtil.C_PRICE_ED, Shader.TileMode.MIRROR);
        Paint paint = new Paint();
        paint.setShader(linearGradient);
        canvas.drawPath(mPathPriceBg, paint);
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
        float prePrice = 0;
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
            //判断每个分时点的涨跌情况
            if(i == 0){
                prePrice = price;
                timeSharingDetail.isFall = DataUtils.compare(mPrePrice,price);
            }else {
                timeSharingDetail.isFall = DataUtils.compare(prePrice,price);
                prePrice = price;
            }
        }
        float delta = countMaxMinDelta();
        maxPrice = mPrePrice + delta;
        minPrice = mPrePrice - delta;
        //最大值和最小值的差值
        float diffPrice = maxPrice - minPrice;
        //获取价格分时线的宽度
        float strokeWidth = PaintUtil.PAINT_MINUETE_LINE.getStrokeWidth();
        //初始化分时图线图数据
        Point pointPrice = null;
        mVolumeList.clear();
        for (int i = 0; i < mList.size(); i++) {
            //计算分时图上面每个点，并且将他们连接起来
            TimeSharingDetail timeSharingDetail = mList.get(i);
            float scalePrice = (maxPrice - Float.valueOf(timeSharingDetail.getPrice())) / diffPrice;
            pointPrice = getPricePoint(mPriceRectFrame, i, scalePrice);
            if (i == 0) {
                mPathPrice.moveTo(pointPrice.x, pointPrice.y);
                mPathPriceBg.moveTo(pointPrice.x, pointPrice.y);
            } else {
                mPathPrice.lineTo(pointPrice.x, pointPrice.y);
                mPathPriceBg.lineTo(pointPrice.x, pointPrice.y - strokeWidth);
            }
            //计算成交量的起始点和线的颜色
            countVolume(i, timeSharingDetail);
        }
        if (pointPrice != null) {
            mPathPriceBg.lineTo(pointPrice.x, mPriceRectFrame.bottom);
            mPathPriceBg.lineTo(mPriceRectFrame.left, mPriceRectFrame.bottom);
            mPathPriceBg.close();
        }
        //链接外围线段
        mPathOutLine.addRect(mPriceRectFrame.left, mPriceRectFrame.top, mPriceRectFrame.right, mPriceRectFrame.bottom, Path.Direction.CCW);
        mPathOutLine.addRect(mVolumeRectFrame.left, mVolumeRectFrame.top, mVolumeRectFrame.right, mVolumeRectFrame.bottom, Path.Direction.CCW);
        //技术区域边线
        mPathTecLine.moveTo(mTecRectFrame.left, mTecRectFrame.top);
        mPathTecLine.lineTo(mTecRectFrame.right, mTecRectFrame.top);
        mPathTecLine.lineTo(mTecRectFrame.right, mTecRectFrame.bottom);
        mPathTecLine.lineTo(mTecRectFrame.left, mTecRectFrame.bottom);
        mPathTecLine.close();
        //昨日收盘价参考线
        mPathPre.moveTo(mPriceRectFrame.left, mPriceRectFrame.height() / 2);
        mPathPre.lineTo(mPriceRectFrame.right, mPriceRectFrame.height() / 2);
    }

    /**
     * 计算成交量的起始点和线的颜色
     * @param i
     * @param timeSharingDetail
     */
    private void countVolume(int i, TimeSharingDetail timeSharingDetail) {
        //计算分时图上面成交量的起始点和线的颜色
        TimeSharingVolume timeSharingVolume = new TimeSharingVolume();
        float scaleVolume = (maxVolume - Float.valueOf(timeSharingDetail.getVolume())) / maxVolume;
        //成交量线的起始点
        timeSharingVolume.point = getVolumePoint(mVolumeRectFrame, i, scaleVolume);
        //成交量线的颜色
        if (timeSharingDetail.isFall == null) {   //和昨日收盘价格一样，灰色
            timeSharingVolume.paint = PaintUtil.PAINT_DARK;
        } else {
            timeSharingVolume.paint = timeSharingDetail.isFall ? PaintUtil.PAINT_GREEN : PaintUtil.PAINT_RED;
        }
        mVolumeList.add(timeSharingVolume);
    }

    /**
     * 初始化path
     */
    private void initPath() {
        mPathPrice = new Path();
        mPathPriceBg = new Path();
        mPathAvgPrice = new Path();
        mPathOutLine = new Path();
        mPathTecLine = new Path();
        mPathPre = new Path();
        mPathPrice.reset();
        mPathPriceBg.reset();
        mPathAvgPrice.reset();
        mPathAvgPrice.reset();
        mPathTecLine.reset();
        mPathPre.reset();
    }

    /**
     * 处理最大值最小值差值，使其分布在昨日收盘价格上下部分
     *
     * @return
     */
    private float countMaxMinDelta() {
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

    /**
     * 得到每一个分时图上的成交量的起始点
     *
     * @param rect
     * @param i
     * @param scaleVolume
     */
    private Point getVolumePoint(Rect rect, int i, float scaleVolume) {

        Point point = new Point();
        point.x = rect.left + (int) (rect.width() * (i + 0.5) / everyDayColumns);
        point.y = rect.top + (int) (rect.height() * scaleVolume);
        return point;
    }

}

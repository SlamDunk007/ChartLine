package com.example.guannan.chartline.widget;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.example.guannan.chartline.data.TimeSharingDetail;

import java.util.ArrayList;

/**
 * @author guannan
 * @date 2018/4/26 15:20
 */

public class TimeSharing {

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
    //分时图的数据集合
    private ArrayList<TimeSharingDetail> mList;

    public TimeSharingView(ArrayList<TimeSharingDetail> list){
        this.mList = list;
        init();
    }

    private void init() {

        //总共有多少个点，这里以港股为例，港股共有242
        everyDayColumns = 242;
        mTimeArrays = new String[]{"9:30", "12:00/13:00", "16:00"};
        //初始化最大值最小值
        maxPrice = Float.MIN_VALUE;
        minPrice = Float.MAX_VALUE;
        maxVolume = Float.MIN_VALUE;
    }

    /**
     * 分时图具体的绘制流程
     * @param canvas
     */
    public void draw(Canvas canvas){

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

}

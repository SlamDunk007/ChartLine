package com.example.guannan.chartline.utils;

import android.graphics.DashPathEffect;
import android.graphics.Paint;

/**
 * @author guannan
 * @date 2018/4/10 15:36
 */

public class PaintUtil {

    //分时图主线画笔颜色
    public final static int COLOR_MINUTE_LINE = 0xff2c5c9b;
    //外围边线画笔颜色
    public final static int COLOR_OUT_LINE = 0xff1b1e28;
    //昨日收盘价参考线颜色
    public final static int COLOR_PREPRICE_LINE = 0xffe7b8c0;
    //分时图下面的渐变色
    public final static int C_PRICE_BG = 0x504486CB; //阴影背景渐变起始色
    public final static int C_PRICE_ED = 0x0051A7FF; //阴影背景渐变高位色
    //日期和成交量技术指标颜色
    public final static int COLOR_TIME_VOLUME = 0xff788497;
    //红涨绿跌,平灰
    public final static int COLOR_RED = 0xfffec1bb;
    public final static int COLOR_GREEN = 0xffa6e1b9;
    public final static int COLOR_DARK = 0xffd4d6da;



    //分时图主线画笔
    public final static Paint PAINT_MINUETE_LINE = new Paint();
    //外围边线画笔
    public final static Paint PAINT_OUT_LINE = new Paint();
    //昨日收盘价参考线画笔
    public final static Paint PAINT_PREPRICE_LINE = new Paint();
    //日期和成交量技术指标参数
    public final static Paint PAINT_TIME_VOLUME = new Paint();
    //红涨绿跌画笔
    public final static Paint PAINT_RED = new Paint();
    public final static Paint PAINT_GREEN = new Paint();
    public final static Paint PAINT_DARK = new Paint();

    static {
        //分时图画笔相关设置
        PAINT_MINUETE_LINE.setColor(COLOR_MINUTE_LINE);
        PAINT_MINUETE_LINE.setStyle(Paint.Style.STROKE);
        PAINT_MINUETE_LINE.setStrokeWidth(2.5f);
        PAINT_MINUETE_LINE.setAntiAlias(true);
        //外围边线画笔设置
        PAINT_OUT_LINE.setColor(COLOR_OUT_LINE);
        PAINT_OUT_LINE.setStyle(Paint.Style.STROKE);
        PAINT_OUT_LINE.setAntiAlias(true);
        PAINT_OUT_LINE.setStrokeWidth(1.0f);
        //昨日收盘价参考线画笔设置
        PAINT_PREPRICE_LINE.setColor(COLOR_PREPRICE_LINE);
        PAINT_PREPRICE_LINE.setStyle(Paint.Style.STROKE);
        PAINT_PREPRICE_LINE.setStrokeWidth(2.0f);
        PAINT_PREPRICE_LINE.setAntiAlias(true);
        PAINT_PREPRICE_LINE.setPathEffect(new DashPathEffect(new float[]{10,10},3));
        //日期和成交量技术指标画笔设置
        PAINT_TIME_VOLUME.setColor(COLOR_TIME_VOLUME);
        PAINT_TIME_VOLUME.setStyle(Paint.Style.STROKE);
        PAINT_TIME_VOLUME.setAntiAlias(true);
        //红涨
        PAINT_RED.setColor(COLOR_RED);
        PAINT_RED.setStyle(Paint.Style.STROKE);
        PAINT_RED.setStrokeWidth(2f);
        PAINT_RED.setAntiAlias(true);
        //绿跌
        PAINT_GREEN.setColor(COLOR_GREEN);
        PAINT_GREEN.setStyle(Paint.Style.STROKE);
        PAINT_GREEN.setStrokeWidth(2f);
        PAINT_GREEN.setAntiAlias(true);
        //平灰
        PAINT_DARK.setColor(COLOR_DARK);
        PAINT_DARK.setStyle(Paint.Style.STROKE);
        PAINT_DARK.setStrokeWidth(2f);
        PAINT_DARK.setAntiAlias(true);
    }

}

package com.example.guannan.chartline.utils;

import android.graphics.DashPathEffect;
import android.graphics.Paint;

/**
 * @author guannan
 * @date 2018/4/10 15:36
 */

public class PaintUtil {

    //分时图主线画笔颜色
    static int COLOR_MINUTE_LINE = 0xff65affc;
    //外围边线画笔颜色
    static int COLOR_OUT_LINE = 0xff555556;
    //昨日收盘价参考线颜色
    static int COLOR_PREPRICE_LINE = 0xffe7b8c0;


    //分时图主线画笔
    public static Paint PAINT_MINUETE_LINE = new Paint();
    //外围边线画笔
    public static Paint PAINT_OUT_LINE = new Paint();
    //昨日收盘价参考线画笔
    public static Paint PAINT_PREPRICE_LINE = new Paint();

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
        PAINT_PREPRICE_LINE.setPathEffect(new DashPathEffect(new float[]{5,5},3));
    }

}

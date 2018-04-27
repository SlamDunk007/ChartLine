package com.example.guannan.chartline.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author guannan
 * @date 2018/4/25 16:13
 */

public class ScreenUtils {

    private static DisplayMetrics dMetrics = null;

    /**
     * 获取屏幕的宽度
     * @param activity
     * @return
     */
    public static int getScreenWidth(Context activity) {
        if (dMetrics == null && activity != null) {
            dMetrics = activity.getApplicationContext().getResources().getDisplayMetrics();
        }
        if (dMetrics != null) {
            return dMetrics.widthPixels;
        } else {
            return 720;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


}

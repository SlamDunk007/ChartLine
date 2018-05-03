package com.example.guannan.chartline.utils;

/**
 * @author guannan
 * @date 2018/4/26 16:16
 */

public class DataUtils {

    public static float EPSILON = 0.0000001f;

    /**
     * 处理最大值和最小值，由于最大值得稍微大一些，最小值稍微小一些，这样行情图画出来，才不会填满整个rect区域
     *
     * @param maxValue
     * @param minValue
     */
    public static float[] parseData(float maxValue, float minValue) {

        float[] floats = new float[2];
        int max = (int) maxValue;
        float difValue = maxValue - max;
        if (difValue < 0.005) {
            maxValue = (float) (maxValue + 0.005);
        } else {
            maxValue = (float) (max + 0.100);
        }
        floats[0] = maxValue;
        int min = (int) minValue;
        float diffMinValue = minValue - min;
        if (diffMinValue < 0.050) {
            minValue = (float) (min +0.000);
        } else {
            minValue = (float) (min + 0.050);
        }
        floats[1] = minValue;
        return floats;
    }

    /**
     * 浮点数比较
     * @param dst
     * @param src
     * @return 第一个数大返回true，小返回false
     */
    public static Boolean compare(float dst, float src) {
        float delta = dst - src;
        if(delta > EPSILON) {
            return true;
        } else if(delta < -EPSILON) {
            return false;
        }
        return null;
    }
}

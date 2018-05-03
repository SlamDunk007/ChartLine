package com.example.guannan.chartline.data;

import com.google.gson.annotations.SerializedName;

/**
 * @author guannan
 * @date 2018/4/10 16:18
 */

public class TimeSharingDetail {

    @SerializedName("m")
    private String time;    //时间
    @SerializedName("p")
    private String price;   //当前价
    @SerializedName("v")
    private String volume;  //成交量
    public Boolean isFall; //false:上涨还是 true:下跌

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}

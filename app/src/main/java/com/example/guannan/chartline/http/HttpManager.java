package com.example.guannan.chartline.http;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.guannan.chartline.data.TimeSharingDetail;
import com.example.guannan.chartline.data.TimeSharingResult;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author guannan
 * @date 2018/4/10 14:17
 */

public class HttpManager {

    public static HttpManager getInstance() {
        return Holder.sHttpManager;
    }

    public static class Holder {
        private static HttpManager sHttpManager = new HttpManager();
    }

    /**
     * 从本地assets文件中获取json数据
     *
     * @param context
     * @return
     */
    public String getMinuteJson(Context context) {

        StringBuilder sb = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("minute.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取分时图数据列表
     * @param context
     * @return
     */
    public List<TimeSharingDetail> getTimeSharingList(Context context) {
        String minuteJson = getMinuteJson(context);

        Gson gson = new Gson();
        TimeSharingResult timeSharingResult = gson.fromJson(minuteJson, TimeSharingResult.class);
        if (timeSharingResult != null) {
            return timeSharingResult.getResult().getData();
        }
        return null;
    }
}

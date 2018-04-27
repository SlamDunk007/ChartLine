package com.example.guannan.chartline;

import android.app.Activity;
import android.os.Bundle;

import com.example.guannan.chartline.data.TimeSharingDetail;
import com.example.guannan.chartline.http.HttpManager;
import com.example.guannan.chartline.widget.BaseLineView;
import com.example.guannan.chartline.widget.TimeSharingChart;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseLineView baseLineView = (BaseLineView) findViewById(R.id.time_sharing);
        ArrayList<TimeSharingDetail> list = (ArrayList<TimeSharingDetail>) HttpManager.getInstance().getTimeSharingList(this);
        TimeSharingChart timeSharingChart = new TimeSharingChart(this,list,5.40f);
        baseLineView.setCurrentView(timeSharingChart);
    }

}

package com.example.guannan.chartline;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Overlay overlay = (Overlay) findViewById(R.id.scoreTrend);
        String[] xParamsValue = {"7月", "8月", "9月", "10月", "11月", "12月"};
        float[] chartValues = new float[xParamsValue.length];

        for (int i = 0; i < xParamsValue.length; i++) {
            chartValues[i] = new Random().nextInt(750);
        }

        overlay.setCalValue(750, 10, xParamsValue, chartValues);
    }
}

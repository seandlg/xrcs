package com.example.android.xrcs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Long>>() {
        }.getType();
        ArrayList<Long> repTimesLongList = gson.fromJson(getIntent().getStringExtra("repTimes"), type);
        //Log.d("ARRAY", repTimesLongList.toString());

        GraphView graph = (GraphView) findViewById(R.id.workout_graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(48); // should allow for 3 digits to fit on screen
        DataPoint[] points = new DataPoint[repTimesLongList.size()];
        long zeroPoint = repTimesLongList.get(0);
        for (int i = 0; i < points.length; i++) {
            points[i] = new DataPoint(i+1,(repTimesLongList.get(i)-zeroPoint)/1000);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        // set manual X and Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY((int) points[points.length - 1].getY()+1);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX((int) points[points.length - 1].getX()+1);
        graph.addSeries(series);
    }
}

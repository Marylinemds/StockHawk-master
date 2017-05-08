package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maryline on 5/8/2017.
 */

public class DetailsActivity extends AppCompatActivity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("MyQuote")) {
                String symbol = intent.getParcelableExtra("MyQuote");
                System.out.println("PROUT " + symbol);

            }}


        setContentView(R.layout.activity_details);
    }

    public void addData(String symbol){

        LineChart chart = (LineChart) findViewById(R.id.chart);

       /* ArrayList[] dataObjects = ;

        List<Entry> entries = new ArrayList<Entry>();

        for (ArrayList data : dataObjects) {

            // turn your data into Entry objects
            entries.add(new Entry(data.getValueX(), data.getValueY()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
      */
    }


}

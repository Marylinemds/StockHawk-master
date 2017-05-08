package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Maryline on 5/8/2017.
 */

public class DetailsActivity extends AppCompatActivity {

    String symbol;

    @BindView(R.id.chart)
    LineChart chart;

    @BindView(R.id.text)
    TextView text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("MyQuote")) {
                 symbol = intent.getStringExtra("MyQuote");

            }
        }

        getHistory(symbol);


    }

    private void getHistory (String symbol){

        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null, null);

        String history = "";
        if(cursor.moveToFirst()){
            history = cursor.getString((cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY)));
            cursor.close();
        }


        List<Entry> entries = new ArrayList<>();

        CSVReader reader = new CSVReader(new StringReader(history));
        String [] nextLine;
        final List<Long> xAxisValues = new ArrayList<>();
        int xAxisPosition = 0;

        try {
            while ((nextLine = reader.readNext()) != null) {
                xAxisValues.add(Long.valueOf(nextLine[0]));
                Entry entry = new Entry(
                        xAxisPosition,
                        Float.valueOf(nextLine[1])
                );
                entries.add(entry);
                xAxisPosition++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LineData lineData = new LineData(new LineDataSet(entries, symbol));
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Date date = new Date(xAxisValues.get(xAxisValues.size()-(int)value-1));
                return new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).format(date);
            }
        });

        text.setText(symbol);
    }


}
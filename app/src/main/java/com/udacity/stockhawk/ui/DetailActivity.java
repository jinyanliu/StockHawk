package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;
import com.udacity.stockhawk.utility.MyXAxisValueFormatter;
import com.udacity.stockhawk.utility.MyYAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jane on 17-5-15.
 */

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private String mCurrentSymbol;
    private String mCurrentHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Retrieve the LineChart defined in activity_detail xml from Detail Activity.
        LineChart chart = (LineChart) findViewById(R.id.history_chart);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("symbol")) {
                mCurrentSymbol = getIntent().getExtras().getString("symbol");
                mCurrentHistory = getCurrentStockHistoryString(mCurrentSymbol);

                String[] valuePair = mCurrentHistory.split("\n");
                Log.i(TAG, valuePair.toString());

                List<Entry> entries = new ArrayList<Entry>();

                String[] millisecondsArray = new String[valuePair.length];

                boolean isRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);

                if (isRightToLeft == false) {
                    int j = -1;

                    // To add data to the chart, wrap each data object I have into an Entry object.
                    for (int i = valuePair.length - 1; i >= 0; i--) {
                        String[] singleValue = valuePair[i].split(",");
                        j = j + 1;
                        entries.add(new Entry(j, Float.parseFloat(singleValue[1])));
                        millisecondsArray[j] = singleValue[0];
                    }
                } else {
                    for (int i = 0; i < valuePair.length; i++) {
                        String[] singleValue = valuePair[i].split(",");
                        entries.add(new Entry(i, Float.parseFloat(singleValue[1])));
                        millisecondsArray[i] = singleValue[0];
                    }
                }

                // Add entries to dataset
                LineDataSet dataSet = new LineDataSet(entries, mCurrentSymbol + " "
                        + getString(R.string.symbol_history_detail_title));

                chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

                // Hide the description of the chart(default position: right corner of the bottom)
                Description des = chart.getDescription();
                des.setEnabled(false);

                // Give offsets to left, top, right, bottom of the chart. otherwise some parts of
                // the labels are invisible.
                chart.setExtraOffsets(2f, 8f, 2f, 4f);

                int colorGreen = ContextCompat.getColor(DetailActivity.this, R.color.colorPrimaryDark);
                int colorRed = ContextCompat.getColor(DetailActivity.this, R.color.colorAccent);

                dataSet.setColor(colorGreen);
                dataSet.setValueTextColor(colorGreen);
                dataSet.setCircleColor(colorRed);

                LineData lineData = new LineData(dataSet);

                chart.setData(lineData);

                /***************
                 * xAxis Setup *
                 ***************/
                XAxis xAxis = chart.getXAxis();
                xAxis.setEnabled(true);
                xAxis.setDrawLabels(true);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                xAxis.setTextColor(colorGreen);
                xAxis.setAxisLineColor(colorGreen);
                xAxis.setTextSize(12f);
                xAxis.setAxisLineWidth(2f);
                xAxis.setValueFormatter(new MyXAxisValueFormatter(millisecondsArray));

                /***************
                 * yAxis Setup *
                 ***************/
                YAxis yAxis = chart.getAxisLeft();
                yAxis.setEnabled(true);
                yAxis.setDrawLabels(true);
                yAxis.setDrawAxisLine(true);
                yAxis.setDrawGridLines(true);
                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                yAxis.setTextColor(colorGreen);
                yAxis.setAxisLineColor(colorGreen);
                yAxis.setTextSize(12f);
                yAxis.setAxisLineWidth(2f);
                yAxis.setValueFormatter(new MyYAxisValueFormatter());

                YAxis yAxisRight = chart.getAxisRight();
                yAxisRight.setEnabled(true);
                yAxisRight.setDrawLabels(true);
                yAxisRight.setDrawAxisLine(true);
                yAxisRight.setDrawGridLines(true);
                yAxisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                yAxisRight.setTextColor(colorGreen);
                yAxisRight.setAxisLineColor(colorGreen);
                yAxisRight.setTextSize(12f);
                yAxisRight.setAxisLineWidth(2f);
                yAxisRight.setValueFormatter(new MyYAxisValueFormatter());

                //Refresh
                chart.invalidate();
            }
        }
    }

    private String getCurrentStockHistoryString(String symbol) {
        String history = "";
        String[] projection = {Quote.COLUMN_HISTORY};
        String selection = Quote.COLUMN_SYMBOL + " = ?";
        String[] selectionArgs = {symbol};
        Cursor cursor = getContentResolver().query(
                Quote.URI,
                projection,
                selection,
                selectionArgs,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            history = cursor.getString(cursor.getColumnIndex(Quote.COLUMN_HISTORY));
            cursor.close();
        }
        return history;
    }
}

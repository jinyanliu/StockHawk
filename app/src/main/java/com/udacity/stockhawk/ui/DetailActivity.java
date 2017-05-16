package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

import java.util.ArrayList;
import java.util.Calendar;
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

                // To add data to the chart, wrap each data object I have into an Entry object.
                for (int i = 0; i < valuePair.length; i++) {
                    String[] singleValue = valuePair[i].split(",");
                    entries.add(new Entry(Float.parseFloat(singleValue[0]), Float.parseFloat(singleValue[1])));
                }

                // Add entries to dataset
                LineDataSet dataSet = new LineDataSet(entries, mCurrentSymbol + " " + getString(R.string.symbol_history_detail_title));
                dataSet.setColor(Color.BLACK);
                dataSet.setValueTextColor(Color.BLACK);

                LineData lineData = new LineData(dataSet);

                XAxis xAxis = chart.getXAxis();
                xAxis.setAxisLineColor(Color.BLACK);
                xAxis.setAxisLineWidth(4f);
                xAxis.setTextSize(12f);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);

                YAxis yAxis = chart.getAxisLeft();
                yAxis.setAxisLineColor(Color.BLACK);
                yAxis.setAxisLineWidth(4f);
                yAxis.setTextColor(Color.BLACK);
                xAxis.setTextSize(12f);
                yAxis.setDrawAxisLine(true);
                yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                yAxis.setAxisLineColor(1);
                yAxis.setGranularity(1f);
                yAxis.setDrawGridLines(false);

                chart.getAxisRight().setEnabled(false);

                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        Calendar cl = Calendar.getInstance();
                        cl.setTimeInMillis((long) value);
                        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + ":" + cl.get(Calendar.MONTH) + ":" + cl.get(Calendar.YEAR);
                        return date;
                    }
                });

                chart.setData(lineData);

                //Refresh
                chart.invalidate();

            }
        }

        // Set title "history" on the detail activity menu bar as activity's title.
        setTitle(mCurrentSymbol + " " + getString(R.string.symbol_history_detail_title));


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

package com.udacity.stockhawk.utility;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jane on 17-5-17.
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter {

    private static final String TAG = MyXAxisValueFormatter.class.getSimpleName();

    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            Date date = new Date();
            date.setTime(Long.parseLong(values[i]));
            String formattedDate = new SimpleDateFormat("dd/MM/yy").format(date);
            if (formattedDate == null || formattedDate.equals("")) {
                Log.e(TAG, String.valueOf(R.string.log_date_invalid));
            } else {
                labels[i] = formattedDate;
            }
        }
        mValues = labels;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value];
    }
}

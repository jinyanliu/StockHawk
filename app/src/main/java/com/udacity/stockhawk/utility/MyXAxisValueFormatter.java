package com.udacity.stockhawk.utility;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;

import java.util.Calendar;

/**
 * Created by jane on 17-5-17.
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter {

    private static final String TAG = MyXAxisValueFormatter.class.getSimpleName();

    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        Calendar cl = Calendar.getInstance();
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cl.setTimeInMillis(Long.parseLong(values[i]));
            String date = "" + cl.get(Calendar.DAY_OF_MONTH) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR);
            if (date == null || date.equals("")) {
                Log.e(TAG, String.valueOf(R.string.log_date_invalid));
            } else {
                labels[i] = date;
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

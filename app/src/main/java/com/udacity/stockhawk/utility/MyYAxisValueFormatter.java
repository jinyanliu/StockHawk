package com.udacity.stockhawk.utility;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by jane on 17-5-17.
 */

public class MyYAxisValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        // Format values to 1 decimal digit
        mFormat = new DecimalFormat("###,###,##0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return "$" + mFormat.format(value);
    }
}

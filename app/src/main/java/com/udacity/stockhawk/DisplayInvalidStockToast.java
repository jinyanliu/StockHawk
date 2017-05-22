package com.udacity.stockhawk;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jane on 17-5-22.
 */

public class DisplayInvalidStockToast implements Runnable {
    private final Context mContext;
    String mText;

    public DisplayInvalidStockToast(Context mContext, String text) {
        this.mContext = mContext;
        mText = text;
    }

    @Override
    public void run() {
        Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
    }
}

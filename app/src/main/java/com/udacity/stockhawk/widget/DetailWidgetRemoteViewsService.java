package com.udacity.stockhawk.widget;

/**
 * Created by jane on 17-5-17.
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Contract.Quote;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;

    public final String TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    private static final String[] QUOTE_COLUMNS = {
            Quote.TABLE_NAME + "." + Quote._ID,
            Quote.COLUMN_SYMBOL,
            Quote.COLUMN_PRICE,
            Quote.COLUMN_ABSOLUTE_CHANGE,
            Quote.COLUMN_PERCENTAGE_CHANGE,
    };

    // these indices must match the projection
    static final int INDEX_QUOTE_ID = 0;
    static final int INDEX_QUOTE_SYMBOL = 1;
    static final int INDEX_QUOTE_PRICE = 2;
    static final int INDEX_QUOTE_ABSOLUTE_CHANGE = 3;
    static final int INDEX_QUOTE_PERCENTAGE_CHANGE = 4;

    public DetailWidgetRemoteViewsService() {
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                String orderBy = Quote.COLUMN_SYMBOL + " ASC";
                data = getContentResolver().query(
                        Quote.URI,
                        QUOTE_COLUMNS,
                        null,
                        null,
                        orderBy);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);
                String symbol = data.getString(INDEX_QUOTE_SYMBOL);
                views.setTextViewText(R.id.widget_symbol, symbol);

                String price = dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE));
                views.setTextViewText(R.id.widget_price, price);

                float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                int colorGreen = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
                int colorRed = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

                if (rawAbsoluteChange > 0) {
                    views.setInt(R.id.widget_change, "setBackgroundColor", colorGreen);
                } else {
                    views.setInt(R.id.widget_change, "setBackgroundColor", colorRed);
                }

                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);

                if (PrefUtils.getDisplayMode(getApplicationContext())
                        .equals(getApplicationContext().getString(R.string.pref_display_mode_absolute_key))) {
                    views.setTextViewText(R.id.widget_change, change);
                } else {
                    views.setTextViewText(R.id.widget_change, percentage);
                }

                final Intent fillIntent = new Intent();
                fillIntent.putExtra("symbol", symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(INDEX_QUOTE_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

package com.udacity.stockhawk;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.support.annotation.Nullable;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.data.StockProvider;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import yahoofinance.Utils;

import static com.udacity.stockhawk.R.id.symbol;

/**
 * Created by Maryline on 5/11/2017.
 */

public class StockWidgetIntentService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetViewFactory(getApplicationContext());
    }

    private class  StockWidgetViewFactory implements RemoteViewsFactory{
        private final Context mApplicationContext;
        private List <ContentValues> mCvList = new ArrayList<>();
        private final DecimalFormat dollarFormat;
        private final DecimalFormat dollarFormatWithPlus;
        private final DecimalFormat percentageFormat;


        public StockWidgetViewFactory (Context applicationContext){
            mApplicationContext = applicationContext;
            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");
            percentageFormat = (DecimalFormat)  NumberFormat.getCurrencyInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");

        }

        @Override
        public void onCreate(){

        }

        public void getData() {

            mCvList.clear();

            ContentResolver contentResolver = mApplicationContext.getContentResolver();
            System.out.println("bop");

               Cursor cursor = contentResolver.query(Contract.Quote.URI, null, null, null, null);
            System.out.println("bip" + cursor);

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    String symbol = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
                    Float price = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
                    Float absChange = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
                    Float percentChange = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));

                    ContentValues cv = new ContentValues();

                    cv.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                    cv.put(Contract.Quote.COLUMN_PRICE, price);
                    cv.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, absChange);
                    cv.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);

                    mCvList.add(cv);
                }
            }


            cursor.close();
        }

        @Override
        public void onDataSetChanged() {
        getData();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mCvList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            ContentValues cv = mCvList.get(i);
            RemoteViews views = new RemoteViews(mApplicationContext.getPackageName(), R.layout.list_item_quote);

            views.setTextViewText(symbol, cv.getAsString(Contract.Quote.COLUMN_SYMBOL));
            views.setTextViewText(R.id.price, dollarFormat.format(cv.getAsFloat(Contract.Quote.COLUMN_PRICE)));

            float absChange = cv.getAsFloat(Contract.Quote.COLUMN_ABSOLUTE_CHANGE);
            float perChange = cv.getAsFloat(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);

            if(absChange >0){
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            }else{
            views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);

            }

            views.setTextViewText(R.id.change, percentageFormat.format(perChange/100));
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


}


/*

public class StockWidgetIntentService extends RemoteViewsService {
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
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                // This is the same query from MyStocksActivity
                data = getContentResolver().query(
                        Contract.Quote.URI,
                        new String[] {
                                Contract.Quote._ID,
                                Contract.Quote.COLUMN_SYMBOL,
                                Contract.Quote.COLUMN_PRICE,
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                                Contract.Quote.COLUMN_HISTORY
                        },
                        Contract.Quote.QUOTE_COLUMNS + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }
            @Override
            public void onDestroy() {
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
                // Get the layout
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_stocks_small);
                // Bind data to the views
                views.setTextViewText(R.id.symbol, data.getString(data.getColumnIndex
                        ("symbol")));
                if (data.getInt(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY)) == 1) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra("symbol", data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
                views.setOnClickFillInIntent(R.id.list, fillInIntent);
                return views;
            }
            @Override
            public RemoteViews getLoadingView() {
                return null; // use the default loading view
            }
            @Override
            public int getViewTypeCount() {
                return 1;
            }
            @Override
            public long getItemId(int position) {
                // Get the row ID for the view at the specified position
                if (data != null && data.moveToPosition(position)) {
                    final int QUOTES_ID_COL = 0;
                    return data.getLong(QUOTES_ID_COL);
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
*/

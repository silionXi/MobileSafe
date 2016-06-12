package com.silion.mobilesafe.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.service.KillProcessWidgetService;
import com.silion.mobilesafe.utils.SystemInfoUtils;

/**
 * Created by silion on 2016/6/8.
 */
public class TaskManagerAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.v("slong.liang", "onReceive");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        android.util.Log.v("slong.liang", "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        android.util.Log.v("slong.liang", "onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        android.util.Log.v("slong.liang", "onEnabled");
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        android.util.Log.v("slong.liang", "onDisabled");
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        android.util.Log.v("slong.liang", "onRestored");
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}

package com.silion.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.utils.SystemInfoUtils;
import com.silion.mobilesafe.widget.TaskManagerAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

public class KillProcessWidgetService extends Service {

    private Timer mTimer;
    private AppWidgetManager mAppWidgetManager;

    public KillProcessWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mTimer = new Timer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                ComponentName componentName = new ComponentName(context, TaskManagerAppWidgetProvider.class);

                int count = SystemInfoUtils.getRunningAppProcessesCunt(context);
                long size = SystemInfoUtils.getAvailMem(context);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget_taskmanager);
                views.setTextViewText(R.id.process_count, "正在运行的软件：" + count);
                views.setTextViewText(R.id.process_memory, "可用内存：" + Formatter.formatFileSize(context, size));

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.silion.mobilesafe.service.widget");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                mAppWidgetManager.updateAppWidget(componentName, views);
            }
        }, 0, 5 * 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroy();
    }
}

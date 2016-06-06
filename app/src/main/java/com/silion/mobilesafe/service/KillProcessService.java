package com.silion.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KillProcessService extends Service {

    private SharedPreferences mPref;
    int mTime;
    private ScreenOffReceiver mReceiver;
    private Timer mClearTime;

    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("setting", MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTime = mPref.getInt("clear_time", 0);
        if (mTime == 1) {
            mReceiver = new ScreenOffReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mReceiver, filter);
            if (mClearTime != null) {
                mClearTime.cancel();
                mClearTime = null;
            }
        } else if (mTime == 2 || mTime == 3) {
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
                mReceiver = null;
            }
            mClearTime = new Timer();
            mClearTime.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    clearTask();
                }
            }, (mTime - 1) * 60 * 1000, (mTime - 1) * 60 * 60 * 1000);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mClearTime != null) {
            mClearTime.cancel();
            mClearTime = null;
        }
        super.onDestroy();
    }

    public void clearTask() {
        new Thread() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> appProcessInfos = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : appProcessInfos) {
                    am.killBackgroundProcesses(info.processName);
                }
                super.run();
            }
        }.start();
    }

    class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTime == 1) {
                clearTask();
            }
        }
    }
}

package com.silion.mobilesafe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.silion.mobilesafe.activity.EnterPwActivity;
import com.silion.mobilesafe.database.AppLockDao;

import java.util.List;

/**
 * Created by silion on 2016/6/21.
 */
public class WatchDogService extends Service {
    private boolean mFlag;
    private AppLockDao mAppLockDao;
    private AppLockReceiver mReceiver;
    private String mAllowPackageName;
    private ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            android.util.Log.v("silion", "content change");
            mAppLockList = mAppLockDao.query();
            super.onChange(selfChange);
        }
    };
    private List<String> mAppLockList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppLockDao = new AppLockDao(this);
        mReceiver = new AppLockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.silion.mobilesafe.APP_LOCK");
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
        getContentResolver().registerContentObserver(Uri.parse("content://com.silion.mobilesafe/applock"), true, observer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAppLockList = mAppLockDao.query();
        startWatchDog();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mFlag = false;
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    private void startWatchDog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFlag = true;
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                while (mFlag) {
                    String packageName = getTopActivityPackage(am);
                    android.util.Log.v("silion", "packageName = " + packageName);
                    SystemClock.sleep(30);
                    if (mAppLockList.contains(packageName)) {
                        if (packageName.equals(mAllowPackageName)) {

                        } else {
                            Intent pwIntent = new Intent(WatchDogService.this, EnterPwActivity.class);
                            pwIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            pwIntent.putExtra("packageName", packageName);
                            startActivity(pwIntent);
                        }
                    }
                }
            }
        }).start();
    }

    public String getTopActivityPackage(ActivityManager am) {
        String packageName;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<RunningAppProcessInfo> appProcessInfos = am.getRunningAppProcesses();
//            for (RunningAppProcessInfo appProcessInfo : appProcessInfos) {
//                if (appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    packageName = appProcessInfo.processName;
//                }
//            }
            RunningAppProcessInfo appProcessInfo = appProcessInfos.get(0);
            packageName = appProcessInfo.processName;
        } else {
            List<RunningTaskInfo> taskInfos = am.getRunningTasks(5);
            RunningTaskInfo taskInfo = taskInfos.get(0);
            packageName = taskInfo.topActivity.getPackageName();
        }
        return packageName;
    }

    public class AppLockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals("com.silion.mobilesafe.APP_LOCK")) {
                mAllowPackageName = intent.getStringExtra("packageName");
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                mAllowPackageName = null;
                mFlag = false;
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                if (mFlag == false) {
                    startWatchDog();
                }
            }
        }
    }

}

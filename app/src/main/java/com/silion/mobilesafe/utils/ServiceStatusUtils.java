package com.silion.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by silion on 2016/4/25.
 */
public class ServiceStatusUtils {

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
            String runningServiceName = serviceInfo.service.getClassName();
            android.util.Log.v("slong.liang", "runningServiceName = " + runningServiceName + ", serviceName" + serviceName);
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}

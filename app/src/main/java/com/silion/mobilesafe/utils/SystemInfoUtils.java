package com.silion.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by silion on 2016/4/25.
 */
public class SystemInfoUtils {

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

    public static int getRunningAppProcessesCunt(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> rapInfos = am.getRunningAppProcesses();
        return rapInfos.size();
    }

    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfor = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfor);
        long availMem = outInfor.availMem;
        /*
        低版本不能用outInfor.totalMem
        @SuppressLint("NewApi") long totalMem = outInfor.totalMem;
        */
        return availMem;
    }

    public static long getTotalMem() {
        FileInputStream fis = null;
        try {
            File file = new File("/proc/meminfo");
            fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String totalStr = reader.readLine();
            String availStr = reader.readLine();

            StringBuffer totalSB = new StringBuffer();
            for (char c : totalStr.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    totalSB.append(c);
                }
            }
            long totalMem = Long.parseLong(totalSB.toString()) * 1024;

            /* 获取到的值不对？
            StringBuffer availSB = new StringBuffer();
            for (char c : availStr.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    availSB.append(c);
                }
            }
            long availMem = Long.parseLong(availSB.toString()) * 1024;
            */
            return totalMem;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}

package com.silion.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.silion.mobilesafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/5/10.
 */
public class AppManager {
    public static List<AppInfo> getAppList(Context context) {
        List<AppInfo> appInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
            appInfo.setPackageName(packageInfo.applicationInfo.packageName);
            appInfo.setName((String) packageInfo.applicationInfo.loadLabel(pm));
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                appInfo.setIsUser(false);
            } else {
                appInfo.setIsUser(true);
            }
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                appInfo.setIsRom(false);
            } else {
                appInfo.setIsRom(true);
            }
            String path = packageInfo.applicationInfo.sourceDir;
            File file = new File(path);
            appInfo.setSize(file.length());
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}

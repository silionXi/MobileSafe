package com.silion.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        for (ApplicationInfo applicationInfo : applicationInfos) {
            AppInfo appInfo = new AppInfo();
            appInfo.setIcon(applicationInfo.loadIcon(pm));
            appInfo.setPackageName(applicationInfo.packageName);
            appInfo.setName((String) applicationInfo.loadLabel(pm));

            int flags = applicationInfo.flags;
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
            appInfo.setDataDir(applicationInfo.dataDir);
            String sourceDir = applicationInfo.sourceDir;
            appInfo.setSourceDir(sourceDir);
            File file = new File(sourceDir);
            appInfo.setSize(file.length());
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}

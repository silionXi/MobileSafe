package com.silion.mobilesafe.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/5/25.
 */
public class TaskManager {
    public static List<TaskInfo> getTaskList(Context context) {
        List<TaskInfo> taskInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> rapInfos = am.getRunningAppProcesses();
        for (RunningAppProcessInfo rapInfo : rapInfos) {
            TaskInfo taskInfo = new TaskInfo();
            String packageName = rapInfo.processName;
            taskInfo.setPackageName(packageName);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

                String name = (String) packageInfo.applicationInfo.loadLabel(pm);
                taskInfo.setAppName(name);

                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                    taskInfo.setIsUser(true);
                } else {
                    taskInfo.setIsUser(false);
                }

                //运行花费的内存
                Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{rapInfo.pid});
                int total = memoryInfos[0].getTotalPrivateDirty() * 1024;
                taskInfo.setSize(total);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setAppName(packageName);
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.launcher_ic));
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}

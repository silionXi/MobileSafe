package com.silion.mobilesafe.receiver;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.silion.mobilesafe.utils.UIUtils;

import java.util.List;

/**
 * Created by silion on 2016/6/12.
 */
public class KillProcessWidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : appProcessInfos) {
            am.killBackgroundProcesses(info.processName);
        }
        Toast.makeText(context, "清理完逼", Toast.LENGTH_SHORT).show();
    }
}

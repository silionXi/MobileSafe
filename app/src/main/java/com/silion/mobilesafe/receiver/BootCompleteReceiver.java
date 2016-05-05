package com.silion.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        boolean protect = pref.getBoolean("lostfind_open", false);
        //开启防盗保护才检测sim卡是否改变
        if (protect) {
            String saveSim = pref.getString("sim_serial", null);
            if (saveSim != null && !saveSim.isEmpty()) {
                TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String currentSim = telManager.getSimSerialNumber();
                if (!saveSim.equals(currentSim)) {
                    String phone = pref.getString("security_contact", "18928818247");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, "手机安全卫士测试", null, null);
                }
            }
        }
    }
}

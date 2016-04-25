package com.silion.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String body = smsMessage.getMessageBody();
            if ("#*alarm*#".equals(body)) {
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(0.5f, 0.5f);
                player.setLooping(false);
                player.start();

                //中断短信的传递，系统app收不到短信，4.4以上想要阻断短信通知必须成为系统默认短信应用
                abortBroadcast();
            } else if ("#*location*#".equals(body)) {
                Intent serviceIntent = new Intent(context, LocationService.class);
                context.startService(serviceIntent);

                abortBroadcast();
            } else if ("#*wipedata*#".equals(body)) {
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName deviceAdmin = new ComponentName(context, BaseDeviceAdminReceiver.class);
                if (dpm.isAdminActive(deviceAdmin)){
                    dpm.wipeData(0);
                }

                abortBroadcast();
            } else if ("#*lockscreen*#".equals(body)) {
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName deviceAdmin = new ComponentName(context, BaseDeviceAdminReceiver.class);
                if (dpm.isAdminActive(deviceAdmin)){
                    dpm.resetPassword("123456", 0);
                    dpm.lockNow();
                }

                abortBroadcast();
            }
        }
    }
}

package com.silion.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.silion.mobilesafe.bean.BlackInfo;
import com.silion.mobilesafe.database.CallSafeDao;

public class BlackService extends Service {
    BroadcastReceiver mBlackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CallSafeDao callSafeDao = new CallSafeDao(context);
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String number = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody();
                BlackInfo blackInfo = callSafeDao.querySingle(number);
                if (blackInfo != null) {
                    if (blackInfo.getMode() == 2 || blackInfo.getMode() == 3) {
                        android.util.Log.v("slong.liang", "求你把我拦下来吧~");
                        abortBroadcast();
                    }
                }

                if (body.contains("发票")) {
                    android.util.Log.v("slong.liang", "求你把我拦下来吧~");
                    abortBroadcast();
                }
            }
        }
    };

    public BlackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBlackReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBlackReceiver);
        super.onDestroy();
    }
}

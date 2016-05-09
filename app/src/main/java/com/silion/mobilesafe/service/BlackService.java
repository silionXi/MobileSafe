package com.silion.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.silion.mobilesafe.bean.BlackInfo;
import com.silion.mobilesafe.database.CallSafeDao;

import java.lang.reflect.Method;

public class BlackService extends Service {
    BroadcastReceiver mBlackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String number = smsMessage.getOriginatingAddress();
                String body = smsMessage.getMessageBody();
                BlackInfo blackInfo = mCallSafeDao.querySingle(number);
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
    private TelephonyManager mTelephonyManager;
    private Uri mUri = Uri.parse("content://call_log/calls");
    private String mIncomingNumber;
    private ContentObserver mObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(mObserver);
            if (mIncomingNumber != null && !mIncomingNumber.isEmpty()) {
                getContentResolver().delete(mUri, "number=?", new String[]{mIncomingNumber});
            }
            super.onChange(selfChange);
        }
    };
    private PhoneStateListener mListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                BlackInfo blackInfo = mCallSafeDao.querySingle(incomingNumber);
                if (blackInfo != null) {
                    int mode = blackInfo.getMode();
                    if (mode == 1 || mode == 3) {
                        endCall();
                        mIncomingNumber = incomingNumber;
                        getContentResolver().registerContentObserver(mUri, true, mObserver);
                    }
                }
            }
        }
    };

    public void endCall() {
        try {
            //通过反射getService得到IBinder对象
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            //仿照TelephonyManager endCall通过AIDL调用挂断电话
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall(); //虽然报错但可以运行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CallSafeDao mCallSafeDao;

    public BlackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCallSafeDao = new CallSafeDao(this);
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBlackReceiver, filter);

        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBlackReceiver);

        mTelephonyManager.listen(mListener, 0);
        super.onDestroy();
    }
}

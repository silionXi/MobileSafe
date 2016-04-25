package com.silion.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.database.AddressDao;

public class AddressService extends Service {
    private TelephonyManager mTelManager;
    private OutCallReceiver mReceiver;

    private PhoneStateListener mListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                String address = AddressDao.getAddress(incomingNumber);
                showAddress(address);
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTelManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mReceiver = new OutCallReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTelManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTelManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(mReceiver);
    }

    public void showAddress(String address) {
        LayoutInflater inflate = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.view_show_address, null);
        TextView tv = (TextView) v.findViewById(R.id.tvAddress);
        tv.setText(address);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(v, params);
    }

    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String outCallNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String address = AddressDao.getAddress(outCallNumber);
            showAddress(address);
        }
    }
}

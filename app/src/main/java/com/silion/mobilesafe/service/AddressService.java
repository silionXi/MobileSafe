package com.silion.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.database.AddressDao;

public class AddressService extends Service {
    private TelephonyManager mTelManager;
    private OutCallReceiver mReceiver;
    private SharedPreferences mPref;
    private WindowManager mWindowManager;
    private View mView;

    private int[] mAddressStyle = new int[]{R.drawable.setting_call_locate_white,
            R.drawable.setting_call_locate_orange, R.drawable.setting_call_locate_blue,
            R.drawable.setting_call_locate_gray, R.drawable.setting_call_locate_green};

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
    private int mStartX;
    private int mStartY;
    private View.OnTouchListener mDragListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) v.getLayoutParams();
            Display display = mWindowManager.getDefaultDisplay();
            int winW = display.getWidth();
            int winH = display.getHeight();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = (int) event.getRawX();
                    mStartY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int endX = (int) event.getRawX();
                    int endY = (int) event.getRawY();
                    int dx = endX - mStartX;
                    int dy = endY - mStartY;

                    int x = params.x + dx;
                    int y = params.y + dy;

                    if (x < 0 || x + v.getWidth() > winW) {
                        x = params.x;
                    }
                    if (y < 0 || y + v.getHeight() > winH - 80) {
                        y = params.y;
                    }

                    params.x = x;
                    params.y = y;
                    mWindowManager.updateViewLayout(v, params);
                    mStartX = endX;
                    mStartY = endY;
                    break;
                case MotionEvent.ACTION_UP:
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putInt("locate_x", params.x);
                    editor.putInt("locate_y", params.y);
                    editor.apply();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private static Handler mHandler = new Handler();

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("setting", MODE_PRIVATE);
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
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    public void showAddress(String address) {
        LayoutInflater inflate = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflate.inflate(R.layout.view_show_address, null);
        LinearLayout ll = (LinearLayout) mView.findViewById(R.id.llAdress);
        ll.setBackgroundResource(mAddressStyle[mPref.getInt("address_style", 0)]);
        TextView tv = (TextView) mView.findViewById(R.id.tvAddress);
        tv.setText(address);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        mParams.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //去掉这个会导致只能touch到TOAST
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        int dX = mPref.getInt("locate_x", 0);
        int dY = mPref.getInt("locate_y", 0);
        params.x = dX;
        params.y = dY;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mView, params);
        mView.setOnTouchListener(mDragListener);
        int duration = 3000;
        if (duration > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mView != null) {
                        mWindowManager.removeView(mView);
                        mView = null;
                    }
                }
            }, duration);
        }
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

package com.silion.mobilesafe.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.silion.mobilesafe.R;

public class BaseDeviceAdminReceiver extends DeviceAdminReceiver {
    public BaseDeviceAdminReceiver() {
    }

    public void showToast(Context context, String msg) {
        String status = context.getString(R.string.admin_receiver_status, msg);
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "disabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context, "passwordChanged");
    }
}

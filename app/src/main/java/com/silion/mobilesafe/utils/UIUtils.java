package com.silion.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by silion on 2016/5/25.
 */
public class UIUtils {
    public static void showToast(final Activity context, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

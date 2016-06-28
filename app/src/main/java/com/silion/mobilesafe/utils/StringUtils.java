package com.silion.mobilesafe.utils;

import android.content.Context;
import android.text.format.Formatter;

/**
 * Created by silion on 2016/6/24.
 */
public class StringUtils {
    public static String getSizeStr(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }
}

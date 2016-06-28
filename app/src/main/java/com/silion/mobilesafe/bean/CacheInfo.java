package com.silion.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by silion on 2016/6/24.
 */
public class CacheInfo {
    private String mPackageName;
    private Drawable mIcon;
    private String mName;
    private long mSize;

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }
}

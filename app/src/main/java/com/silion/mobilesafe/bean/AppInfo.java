package com.silion.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by silion on 2016/5/10.
 */
public class AppInfo {
    private Drawable mIcon;
    private String mPackageName;
    private String mName;
    private boolean mIsUser;
    private boolean mIsRom;
    private long mSize;

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isUser() {
        return mIsUser;
    }

    public void setIsUser(boolean isUser) {
        mIsUser = isUser;
    }

    public boolean isRom() {
        return mIsRom;
    }

    public void setIsRom(boolean isRom) {
        mIsRom = isRom;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }
}

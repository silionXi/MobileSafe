package com.silion.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by silion on 2016/5/25.
 */
public class TaskInfo {
    private Drawable mIcon;
    private String mPackageName;
    private String mAppName;
    private boolean mIsUser;
    private boolean mIsCheck;
    private long size;

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

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public boolean isUser() {
        return mIsUser;
    }

    public void setIsUser(boolean isUser) {
        mIsUser = isUser;
    }

    public boolean isCheck() {
        return mIsCheck;
    }

    public void setIsCheck(boolean isCheck) {
        mIsCheck = isCheck;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "mIcon=" + mIcon +
                ", mPackageName='" + mPackageName + '\'' +
                ", mAppName='" + mAppName + '\'' +
                ", mIsUser=" + mIsUser +
                ", mIsCheck=" + mIsCheck +
                ", size=" + size +
                '}';
    }
}

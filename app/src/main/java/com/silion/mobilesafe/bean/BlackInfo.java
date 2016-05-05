package com.silion.mobilesafe.bean;

/**
 * Created by silion on 2016/5/3.
 */
public class BlackInfo {
    private String mNumber;
    private int mMode;

    public BlackInfo() {
    }

    public BlackInfo(String number, int mode) {
        mNumber = number;
        mMode = mode;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
    }
}

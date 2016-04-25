package com.silion.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.silion.mobilesafe.utils.MD5Utils;

/**
 * Created by silion on 2016/3/30.
 */
public class MD5UtilsTest extends ApplicationTestCase<Application> {
    public MD5UtilsTest() {
        super(Application.class);
    }

    //must start of test
    public void testEncode() {
        String result = MD5Utils.encode("123456");
        android.util.Log.v("slong.liang", "result = " + result);
    }
}

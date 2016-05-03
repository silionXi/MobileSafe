package com.silion.mobilesafe;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.silion.mobilesafe.database.CallSafeDao;

import java.util.Random;

/**
 * Created by silion on 2016/5/3.
 */
public class CallSafeDaoTest extends AndroidTestCase {
    private CallSafeDao mDao;

    public CallSafeDaoTest() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mDao = new CallSafeDao(getContext());
    }

    //run Android Test
    public void testInsert() throws Exception {
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            mDao.insert(i + 13800000001l + "", random.nextInt(2) + 1);
        }
    }

    public void testDelete() throws Exception {
        mDao.delete("13800000000");
    }

    public void testUpdate() throws Exception {
        mDao.update("13800000000", 3);
    }

    public void testQuery() throws Exception {
        Cursor cursor = mDao.query("13800000000");
        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            int mode = cursor.getInt(1);
            android.util.Log.v("slong.liang", number + " : " + mode);
        }
        cursor.close();
    }
}

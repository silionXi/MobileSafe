package com.silion.mobilesafe;

import android.test.AndroidTestCase;

import com.silion.mobilesafe.bean.BlackInfo;
import com.silion.mobilesafe.database.CallSafeDao;

import java.util.List;
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

    public void testQuerySingle() throws Exception {
        BlackInfo info = mDao.querySingle("13800000001");
        android.util.Log.v("slong.liang", info.getNumber() + " : " + info.getMode());
    }

    public void testQueryAll() throws Exception {
        List<BlackInfo> blackInfos = mDao.queryAll();
        for (BlackInfo info : blackInfos) {
            android.util.Log.v("slong.liang", info.getNumber() + " : " + info.getMode());
        }
    }
}

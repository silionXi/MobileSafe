package com.silion.mobilesafe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.silion.mobilesafe.bean.BlackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/5/3.
 */
public class CallSafeDao {
    private CallSafeOpenHelper mOpenHelper;

    public CallSafeDao(Context context) {
        mOpenHelper = new CallSafeOpenHelper(context, "callsafe.db");
    }

    public long insert(String number, int mode) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long id = db.insert("black", null, values);
        db.close();
        return id;
    }

    public int delete(String number) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int num = db.delete("black", "number=?", new String[]{number});
        db.close();
        return num;
    }

    public int update(String number, int mode) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        int num = db.update("black", values, "number=?", new String[]{number});
        db.close();
        return num;
    }

    public BlackInfo querySingle(String number) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        BlackInfo blackInfo = new BlackInfo();
        Cursor cursor = db.query("black", new String[]{"number", "mode"}, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            blackInfo.setNumber(cursor.getString(0));
            blackInfo.setMode(cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return blackInfo;
    }

    public List<BlackInfo> queryAll() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        List<BlackInfo> blackInfos = new ArrayList<>();
        Cursor cursor = db.query("black", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackInfo blackInfo = new BlackInfo();
            blackInfo.setNumber(cursor.getString(0));
            blackInfo.setMode(cursor.getInt(1));
            blackInfos.add(blackInfo);
        }
        cursor.close();
        db.close();
        return blackInfos;
    }
}

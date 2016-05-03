package com.silion.mobilesafe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        return id;
    }

    public int delete(String number) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int num = db.delete("black", "number=?", new String[]{number});
        return num;
    }

    public int update(String number, int mode) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        int num = db.update("black", values, "number=?", new String[]{number});
        return num;
    }

    public Cursor query(String number) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("black", new String[]{"number", "mode"}, "number=?", new String[]{number}, null, null, null);
        return cursor;
    }
}

package com.silion.mobilesafe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/6/17.
 */
public class AppLockDao {
    private AppLockOpenHelper mAppLockOpenHelper;
    private Context mContext;

    public AppLockDao(Context context) {
        mContext = context;
        mAppLockOpenHelper = new AppLockOpenHelper(context, "applock", 1);
    }

    public void add(String packageName) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packageName", packageName);
        db.insert("locked", null, values);
        db.close();
        mContext.getContentResolver().notifyChange(Uri.parse("content://com.silion.mobilesafe/applock"), null);
    }

    public void delete(String packageName) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        db.delete("locked", "packageName = ?", new String[]{packageName});
        db.close();
        mContext.getContentResolver().notifyChange(Uri.parse("content://com.silion.mobilesafe/applock"), null);
    }

    public List<String> query() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("locked", new String[]{"packageName"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        return list;
    }
}

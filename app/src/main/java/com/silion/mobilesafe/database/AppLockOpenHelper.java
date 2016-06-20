package com.silion.mobilesafe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by silion on 2016/6/17.
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {
    public AppLockOpenHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public AppLockOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE locked(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "packageName TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS locked");
        onCreate(db);
    }
}

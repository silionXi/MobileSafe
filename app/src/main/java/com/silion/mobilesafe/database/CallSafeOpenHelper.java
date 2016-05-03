package com.silion.mobilesafe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by silion on 2016/5/3.
 */
public class CallSafeOpenHelper extends SQLiteOpenHelper {

    public CallSafeOpenHelper(Context context, String name) {
        this(context, name, 1);
    }

    public CallSafeOpenHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public CallSafeOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE black(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "number TEXT," +
                "mode INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS black");
        onCreate(db);
    }
}

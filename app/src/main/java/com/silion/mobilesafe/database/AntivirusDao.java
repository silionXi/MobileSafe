package com.silion.mobilesafe.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.silion.mobilesafe.bean.VirusInfo;

/**
 * Created by silion on 2016/6/13.
 */
public class AntivirusDao {
    private static String PATH = "/data/data/com.silion.mobilesafe/files/antivirus.db";

    public static String getAntivirus(String md5) {
        android.util.Log.v("silion", "md5 = " + md5);
        String desc = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = db.query("datable", new String[]{"desc"}, "md5 = ?", new String[]{md5}, null, null, null);
        while (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        return desc;
    }

    public static void addAntivirus(VirusInfo virus) {
        if (getAntivirus(virus.md5) == null) {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
            ContentValues values = new ContentValues();
            values.put("md5", virus.md5);
            values.put("type", 6);
            values.put("name", "Android.Troj.AirAD.a");
            values.put("desc", virus.desc);
            db.insert("datable", null, values);

            db.close();
        }
    }
}

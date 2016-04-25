package com.silion.mobilesafe.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by silion on 2016/4/19.
 */
public class AddressDao {
    private static String PATH = "/data/data/com.silion.mobilesafe/files/address.db";

    static public String getAddress(String number) {
        String address = "未知号码";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        if (number.matches("^1[3-8]\\d{9}$")) { //手机号码正则表达式
            Cursor cursor = database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)",
                    new String[]{number.substring(0, 7)});
            if (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
            cursor.close();
        } else if (number.matches("^\\d+$")) {
            switch (number.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 7:
                case 8:
                    address = "本地号码";
                    break;
                default:
                    if (number.startsWith("0") && number.length() > 10) {
                        Cursor cursor = database.rawQuery("select location from data2 where area = ?",
                                new String[]{number.substring(1, 4)});
                        //先查询4位区号
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        } else {
                            //查询3位区号
                            cursor = database.rawQuery("select location from data2 where area = ?",
                                    new String[]{number.substring(1, 3)});
                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }
                        }
                        cursor.close();
                    }
                    break;
            }
        }

        database.close(); //关闭数据库
        return address;
    }
}

package com.silion.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by silion on 2016/5/20.
 */
public class SmsUtils {
    public interface BackupCallBack {
        void before(int max);

        void onBackup(int value);

        void finish();
    }

    public static boolean backUp(Context context, BackupCallBack callBack) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/mobilesafe");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "sms.xml");
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                XmlSerializer xmlSerializer = Xml.newSerializer();
                Uri uri = Uri.parse("content://sms");
                ContentResolver cr = context.getContentResolver();
                Cursor cursor = cr.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
                int count = cursor.getCount();
                callBack.before(count);

                xmlSerializer.setOutput(os, "utf-8");
                xmlSerializer.startDocument("utf-8", true);
                xmlSerializer.startTag(null, "smss");
                xmlSerializer.attribute(null, "size", String.valueOf(count));
                int i = 1;
                while (cursor.moveToNext()) {
                    callBack.onBackup(i++);
                    xmlSerializer.startTag(null, "sms");
                    String address = cursor.getString(0);
                    long date = cursor.getLong(1);
                    int type = cursor.getInt(2);
                    String body = cursor.getString(3);
                    android.util.Log.v("silion", address + " : " + date + " : " + type + " : " + body);

                    xmlSerializer.startTag(null, "address");
                    xmlSerializer.text(address);
                    xmlSerializer.endTag(null, "address");

                    xmlSerializer.startTag(null, "date");
                    xmlSerializer.text(date + "");
                    xmlSerializer.endTag(null, "date");

                    xmlSerializer.startTag(null, "type");
                    xmlSerializer.text(String.valueOf(type));
                    xmlSerializer.endTag(null, "type");

                    xmlSerializer.startTag(null, "body");
                    String encrypted = Crypto.encrypt("body", body);
                    xmlSerializer.text(encrypted);
                    xmlSerializer.endTag(null, "body");

                    xmlSerializer.startTag(null, "body2");
                    String de = Crypto.decrypt("body", encrypted);
                    xmlSerializer.text(de);
                    xmlSerializer.endTag(null, "body2");

                    xmlSerializer.endTag(null, "sms");
                    SystemClock.sleep(500);
                }
                xmlSerializer.endTag(null, "smss");
                xmlSerializer.endDocument();
                os.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                        callBack.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}

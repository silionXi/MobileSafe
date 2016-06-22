package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.utils.SmsUtils;
import com.silion.mobilesafe.utils.UIUtils;

/**
 * Created by silion on 2016/4/19.
 */
public class AdvToolsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advtools);
    }

    public void addressQuery(View view) {
        Intent intent = new Intent();
        intent.setClass(this, AddressActivity.class);
        startActivity(intent);
    }

    public void commonQuery(View view) {
    }

    public void smsBackup(View view) {
        final ProgressDialog pd = new ProgressDialog(AdvToolsActivity.this);
        pd.setTitle("正在备份短信");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Boolean result = SmsUtils.backUp(AdvToolsActivity.this, new SmsUtils.BackupCallBack() {
                    @Override
                    public void before(int max) {
                        pd.setMax(max);
                    }

                    @Override
                    public void onBackup(int value) {
                        pd.setProgress(value);
                    }

                    @Override
                    public void finish() {
                        pd.dismiss();
                    }
                });
                if (result) {
                    UIUtils.showToast(AdvToolsActivity.this, "备份成功");
                } else {
                    UIUtils.showToast(AdvToolsActivity.this, "备份失败");
                }
            }
        }.start();
    }

    public void appLock(View view) {
        Intent intent = new Intent(this, EnterPwActivity.class);
        startActivity(intent);
    }

    public void quickDial(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            if (0 == requestCode) {
                Uri uri = data.getData();
                long rawContactId = ContentUris.parseId(uri);
                Cursor cursor = getContentResolver().query(Uri.parse("content://com.android.contacts/data"),
                        new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{rawContactId + ""}, null);
                while (cursor.moveToNext()) {
                    String data1 = cursor.getString(0);
                    String type = cursor.getString(1);

                    if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                        createDialShortcut(data1);
                    }
                }
            }
        }
    }

    public void createDialShortcut(String phone) {
//        Intent shortcutIntent = new Intent(Intent.ACTION_CALL_PRIVILEGED);//直接拨打
        Intent shortcutIntent = new Intent(Intent.ACTION_DIAL);//拨号盘
        shortcutIntent.setData(Uri.parse("tel:" + phone));
        Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, phone);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, R.drawable.launcher_ic);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra("duplicate", false);
        sendBroadcast(addIntent);
    }
}

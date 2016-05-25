package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
    }
}

package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.silion.mobilesafe.R;

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
    }

    public void appLock(View view) {
    }
}

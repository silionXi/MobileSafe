package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.service.AddressService;
import com.silion.mobilesafe.utils.ServiceStatusUtils;
import com.silion.mobilesafe.view.SettingItemView;

/**
 * Created by silion on 2016/3/29.
 */
public class SettingActivity extends Activity {
    private SharedPreferences mSharePre;
    private SettingItemView mUpdateSettingItemView;
    private SettingItemView mAddressSettingItemView;

    private View.OnClickListener mUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUpdateSettingItemView.isCheck()) {
                mUpdateSettingItemView.setChecked(false);
                mSharePre.edit().putBoolean("auto_update", false).commit();
            } else {
                mUpdateSettingItemView.setChecked(true);
                mSharePre.edit().putBoolean("auto_update", true).commit();
            }
        }
    };

    private View.OnClickListener mAddressListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAddressSettingItemView.isCheck()) {
                mAddressSettingItemView.setChecked(false);
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, AddressService.class);
                stopService(intent);
            } else {
                mAddressSettingItemView.setChecked(true);
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, AddressService.class);
                startService(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        mSharePre = getSharedPreferences("setting", MODE_PRIVATE);
        mUpdateSettingItemView = (SettingItemView) findViewById(R.id.updateSettingItemView);
        mUpdateSettingItemView.setOnClickListener(mUpdateListener);
        mUpdateSettingItemView.setChecked(mSharePre.getBoolean("auto_update", true));

        mAddressSettingItemView = (SettingItemView) findViewById(R.id.addressSettingItemView);
        mAddressSettingItemView.setOnClickListener(mAddressListener);
        if (ServiceStatusUtils.isServiceRunning(this, AddressService.class.getName())) {
            mAddressSettingItemView.setChecked(true);
        } else {
            mAddressSettingItemView.setChecked(false);
        }
    }
}

package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.service.AddressService;
import com.silion.mobilesafe.service.BlackService;
import com.silion.mobilesafe.service.KillProcessService;
import com.silion.mobilesafe.utils.SystemInfoUtils;
import com.silion.mobilesafe.view.SettingItemView;

/**
 * Created by silion on 2016/3/29.
 */
public class SettingActivity extends Activity {
    private SharedPreferences mPref;
    private SettingItemView mUpdateSettingItemView;
    private SettingItemView mAddressSettingItemView;
    private SettingItemView mAddressStyleSettingItemView;
    private SettingItemView mAddressLocateSettingItemView;
    private SettingItemView mBlackSettingItemView;

    private String[] mAddressStyle = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

    private View.OnClickListener mUpdateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUpdateSettingItemView.isCheck()) {
                mUpdateSettingItemView.setChecked(false);
                mPref.edit().putBoolean("auto_update", false).commit();
            } else {
                mUpdateSettingItemView.setChecked(true);
                mPref.edit().putBoolean("auto_update", true).commit();
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
    private View.OnClickListener mAddressStyleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setTitle("请选择归属地提示框风格")
                    .setItems(mAddressStyle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAddressStyleSettingItemView.setDesc(mAddressStyle[which]);
                            mPref.edit().putInt("address_style", which).commit();
                        }
                    });
            builder.create().show();
        }
    };
    private View.OnClickListener mAddressLocateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SettingActivity.this, DragAdressViewActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener mBlackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBlackSettingItemView.isCheck()) {
                mBlackSettingItemView.setChecked(false);
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, BlackService.class);
                stopService(intent);
            } else {
                mBlackSettingItemView.setChecked(true);
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, BlackService.class);
                startService(intent);
            }
        }
    };
    private SettingItemView mShowSysSettingItemView;
    private View.OnClickListener mShowSysListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mShowSysSettingItemView.isCheck()) {
                mShowSysSettingItemView.setChecked(false);
                mPref.edit().putBoolean("show_sys", false).commit();
            } else {
                mShowSysSettingItemView.setChecked(true);
                mPref.edit().putBoolean("show_sys", true).apply();
            }
        }
    };
    private SettingItemView mClearOnTimeSettingItemView;
    String[] mTime = new String[]{"定时清理已关闭", "锁屏时清理", "每小时清理", "每2小时清理"};
    private View.OnClickListener mSetTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            int checkedItem = mPref.getInt("clear_time", 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setTitle("请选择定时清理时间");
//            builder.setSingleChoiceItems(mTime, checkedItem, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mPref.edit().putInt("clear_time", which).commit();
//                }
//            });
            builder.setItems(mTime, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPref.edit().putInt("clear_time", which).commit();
                    mClearOnTimeSettingItemView.setDesc(mTime[which]);
                    if (which > 0) {
                        Intent intent = new Intent(SettingActivity.this, KillProcessService.class);
                        startService(intent);
                    } else {
                        Intent intent = new Intent(SettingActivity.this, KillProcessService.class);
                        stopService(intent);
                    }
                }
            });
            builder.create().show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("setting", MODE_PRIVATE);

        mUpdateSettingItemView = (SettingItemView) findViewById(R.id.updateSettingItemView);
        mUpdateSettingItemView.setOnClickListener(mUpdateListener);
        mUpdateSettingItemView.setChecked(mPref.getBoolean("auto_update", true));

        mAddressSettingItemView = (SettingItemView) findViewById(R.id.addressSettingItemView);
        mAddressSettingItemView.setOnClickListener(mAddressListener);
        if (SystemInfoUtils.isServiceRunning(this, AddressService.class.getName())) {
            mAddressSettingItemView.setChecked(true);
        } else {
            mAddressSettingItemView.setChecked(false);
        }

        mAddressStyleSettingItemView = (SettingItemView) findViewById(R.id.addressStyleSettingItemView);
        mAddressStyleSettingItemView.setOnClickListener(mAddressStyleListener);
        mAddressStyleSettingItemView.setDesc(mAddressStyle[mPref.getInt("address_style", 0)]);

        mAddressLocateSettingItemView = (SettingItemView) findViewById(R.id.addressLocateSettingItemView);
        mAddressLocateSettingItemView.setDesc("设置归属地提示框的显示位置");
        mAddressLocateSettingItemView.setOnClickListener(mAddressLocateListener);

        mBlackSettingItemView = (SettingItemView) findViewById(R.id.blackSettingItemView);
        mBlackSettingItemView.setOnClickListener(mBlackListener);
        if (SystemInfoUtils.isServiceRunning(this, BlackService.class.getName())) {
            mBlackSettingItemView.setChecked(true);
        } else {
            mBlackSettingItemView.setChecked(false);
        }

        mShowSysSettingItemView = (SettingItemView) findViewById(R.id.showSysSettingItemView);
        mShowSysSettingItemView.setOnClickListener(mShowSysListener);
        mShowSysSettingItemView.setChecked(mPref.getBoolean("show_sys", false));

        mClearOnTimeSettingItemView = (SettingItemView) findViewById(R.id.clearOnTimeSettingItemView);
        mClearOnTimeSettingItemView.setOnClickListener(mSetTimeListener);
        if (SystemInfoUtils.isServiceRunning(this, KillProcessService.class.getName())) {
            mClearOnTimeSettingItemView.setDesc(mTime[mPref.getInt("clear_time", 1)]);
        } else {
            mClearOnTimeSettingItemView.setDesc(mTime[0]);
        }
    }
}

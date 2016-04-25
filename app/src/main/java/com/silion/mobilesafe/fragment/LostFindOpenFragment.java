package com.silion.mobilesafe.fragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.receiver.BaseDeviceAdminReceiver;

/**
 * Created by silion on 2016/3/31.
 */
public class LostFindOpenFragment extends LostFindBaseFragment {
    private CheckBox cbOpen;

    private ComponentName mDeviceAdminSample;

    private CompoundButton.OnCheckedChangeListener mOpenListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            cbOpen.setText(isChecked ? "防盗保护已经开启" : "防盗保护没有开启");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lostfind_start, container, false);
        cbOpen = (CheckBox) view.findViewById(R.id.cbOpen);
        if (mPref.getBoolean("lostfind_open", false)) {
            cbOpen.setChecked(true);
            cbOpen.setText("防盗保护已经开启");
        } else {
            cbOpen.setChecked(false);
            cbOpen.setText("防盗保护没有开启");
        }
        cbOpen.setOnCheckedChangeListener(mOpenListener);
        mDeviceAdminSample = new ComponentName(mLostFindActivity, BaseDeviceAdminReceiver.class);
        return view;
    }

    @Override
    public void preStep() {
        mLostFindActivity.pushFragment(new LostFindContactFragment(), 2);
    }

    @Override
    public void nextStep() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("lostfind_open", cbOpen.isChecked());
        editor.putBoolean("lostfind_guided", true);
        editor.commit();

        if (cbOpen.isChecked()) {
            if (!isActivityAdmin()) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "安全卫士获取设备管理器权限");
                startActivity(intent);
            }
        }

        mLostFindActivity.pushFragment(new LostFindMainFragment(), 4);
    }

    public boolean isActivityAdmin() {
        DevicePolicyManager dpm = (DevicePolicyManager) mLostFindActivity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm.isAdminActive(mDeviceAdminSample);
    }
}

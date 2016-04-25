package com.silion.mobilesafe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.view.SettingItemView;

/**
 * Created by silion on 2016/3/31.
 */
public class LostFindSimFragment extends LostFindBaseFragment {
    private SettingItemView sivBoundSim;

    private View.OnClickListener mBoundSimListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sivBoundSim.isCheck()) {
                sivBoundSim.setChecked(false);
                mPref.edit().putString("sim_serial", "").commit();
            } else {
                sivBoundSim.setChecked(true);
                TelephonyManager telManager = (TelephonyManager) mLostFindActivity.getSystemService(Context.TELEPHONY_SERVICE);
                String simSerial = telManager.getSimSerialNumber();
                mPref.edit().putString("sim_serial", simSerial).commit();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lostfind_sim, container, false);

        sivBoundSim = (SettingItemView) view.findViewById(R.id.sivBoundSim);
        sivBoundSim.setOnClickListener(mBoundSimListener);

        String simSerial = mPref.getString("sim_serial", "");
        if (simSerial == null || simSerial.isEmpty()) {
            sivBoundSim.setChecked(false);
        } else {
            sivBoundSim.setChecked(true);
        }
        return view;
    }

    @Override
    public void preStep() {
        mLostFindActivity.pushFragment(new LostFindIntroduceFragment(), 0);
    }

    @Override
    public void nextStep() {
        if (sivBoundSim.isCheck()) {
            mLostFindActivity.pushFragment(new LostFindContactFragment(), 2);
        } else {
            Toast.makeText(mLostFindActivity, "请绑定SIM卡", Toast.LENGTH_SHORT).show();
        }
    }
}

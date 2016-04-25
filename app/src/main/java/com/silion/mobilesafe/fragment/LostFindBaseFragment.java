package com.silion.mobilesafe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

import com.silion.mobilesafe.activity.LostFindActivity;

/**
 * Created by silion on 2016/4/1.
 */
public abstract class LostFindBaseFragment extends Fragment {
    protected LostFindActivity mLostFindActivity;
    protected SharedPreferences mPref;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LostFindActivity) {
            mLostFindActivity = (LostFindActivity) activity;
            mPref = mLostFindActivity.getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
    }

    public abstract void preStep();

    public abstract void nextStep();
}

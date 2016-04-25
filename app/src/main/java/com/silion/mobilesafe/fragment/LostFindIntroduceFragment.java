package com.silion.mobilesafe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silion.mobilesafe.R;

/**
 * Created by silion on 2016/3/31.
 */
public class LostFindIntroduceFragment extends LostFindBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lostfind_introduce, container, false);
        return view;
    }

    @Override
    public void preStep() {

    }

    @Override
    public void nextStep() {
        mLostFindActivity.pushFragment(new LostFindSimFragment(), 1);
    }
}

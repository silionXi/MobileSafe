package com.silion.mobilesafe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silion.mobilesafe.R;

/**
 * Created by silion on 2016/3/31.
 */
public class LostFindMainFragment extends LostFindBaseFragment {
    private TextView tvPhone;
    private ImageView ivOpen;
    private TextView tvResetGuide;

    private View.OnClickListener mResetGuideListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLostFindActivity.resetGuide();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lostfind_main, container, false);
        tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        tvPhone.setText(mPref.getString("security_contact", ""));
        ivOpen = (ImageView) view.findViewById(R.id.ivOpen);
        ivOpen.setImageResource(mPref.getBoolean("lostfind_open", false) ? R.drawable.lostfind_lock : R.drawable.lostfind_unlock);
        tvResetGuide = (TextView) view.findViewById(R.id.tvResetGuide);
        tvResetGuide.setOnClickListener(mResetGuideListener);
        return view;
    }

    @Override
    public void preStep() {

    }

    @Override
    public void nextStep() {

    }
}

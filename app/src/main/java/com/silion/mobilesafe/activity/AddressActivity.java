package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.database.AddressDao;

/**
 * Created by silion on 2016/4/19.
 */
public class AddressActivity extends Activity {
    private EditText etNumber;
    private ViewGroup llQueryResult;
    private TextView tvAddress;

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                String address = AddressDao.getAddress(s.toString());
                if (address != null && !address.isEmpty()) {
                    tvAddress.setText(address);
                    llQueryResult.setVisibility(View.VISIBLE);
                }
            } else {
                llQueryResult.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        etNumber = (EditText) findViewById(R.id.etNumber);
        etNumber.addTextChangedListener(mWatcher);
        llQueryResult = (ViewGroup) findViewById(R.id.llQueryResult);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
    }

    public void query(View view) {
        String number = etNumber.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            if (address != null && !address.isEmpty()) {
                tvAddress.setText(address);
                llQueryResult.setVisibility(View.VISIBLE);
            }
        } else {
            llQueryResult.setVisibility(View.INVISIBLE);
            shake(etNumber);
            vibrator();
        }
    }

    public void shake(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

//        shake.setInterpolator(new Interpolator() {
//
//            @Override
//            public float getInterpolation(float x) {
//                //y=ax+b
//                int y = 0;
//                return y;
//            }
//        });

        view.startAnimation(shake);
    }

    public void vibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(2000); //震动2秒
        /**
         * 先等待1秒，震动1秒，等待1秒，震动3秒
         * 参2等于-1表示只执行一次，不循环
         * 参2等于0表示从头开始循环
         * 参2表示从第几个位置开始循环
         */
        vibrator.vibrate(new long[]{1000, 1000, 1000, 3000}, 0);

//        vibrator.cancel(); //取消震动
    }
}

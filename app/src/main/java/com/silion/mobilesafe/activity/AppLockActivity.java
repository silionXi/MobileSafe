package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.fragment.AppLockFragment;
import com.silion.mobilesafe.fragment.AppUnLockFragment;

/**
 * Created by silion on 2016/6/16.
 */
public class AppLockActivity extends Activity {

    private Button btUnlock;
    private Button btLock;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
        initUI();
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new AppUnLockFragment()).commit();
    }

    private void initUI() {
        btUnlock = (Button) findViewById(R.id.btUnlock);
        btLock = (Button) findViewById(R.id.btLock);
    }

    public void tabClick(View view) {
        switch (view.getId()) {
            case R.id.btUnlock: {
                btUnlock.setBackgroundResource(R.drawable.applock_tab_pressed);
                btLock.setBackgroundResource(R.drawable.applock_tab_default);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new AppUnLockFragment()).commit();
                break;
            }
            case R.id.btLock: {
                btUnlock.setBackgroundResource(R.drawable.applock_tab_default);
                btLock.setBackgroundResource(R.drawable.applock_tab_pressed);
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new AppLockFragment()).commit();
                break;
            }
            default:
                break;
        }
    }
}

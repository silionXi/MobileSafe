package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.fragment.LostFindBaseFragment;
import com.silion.mobilesafe.fragment.LostFindIntroduceFragment;
import com.silion.mobilesafe.fragment.LostFindMainFragment;

/**
 * Created by silion on 2016/3/31.
 */
public class LostFindActivity extends Activity {
    private ViewGroup llPageBullet;
    private ViewGroup rlButton;
    private Button btPre;
    private Button btNext;

    private int mPosition;
    private SharedPreferences mPref;

    private FragmentManager mFragmentManager;

    private GestureDetector mGestureDetector;

    private View.OnClickListener mPreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            preStep();
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nextStep();
        }
    };

    //创建手势监听
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        /**
         * @param e1 表示滑动的起点
         * @param e2 表示滑动的终点
         * @param velocityX 表示水平速度
         * @param velocityY 表示垂直速度
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final float rawX1 = e1.getRawX();
            final float rawX2 = e2.getRawX();

            // 判断纵向滑动幅度是否过大, 过大的话不允许切换界面
            if (Math.abs(e2.getRawY() - e1.getRawY()) > 500) {
                Toast.makeText(LostFindActivity.this, "不能这样划哦!",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            if (rawX2 - rawX1 >= 200) { //向右划，上一步
                preStep();
            } else if (rawX1 - rawX2 >= 200) { //向左划，下一步
                nextStep();
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind);
        llPageBullet = (ViewGroup) findViewById(R.id.llPageBullet);
        rlButton = (ViewGroup) findViewById(R.id.rlButton);
        btPre = (Button) findViewById(R.id.btPre);
        btPre.setOnClickListener(mPreListener);
        btNext = (Button) findViewById(R.id.btNext);
        btNext.setOnClickListener(mNextListener);

        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mPref = getSharedPreferences("setting", MODE_PRIVATE);
        boolean guided = mPref.getBoolean("lostfind_guided", false);
        if (guided) {
            fragmentTransaction.add(R.id.fragmentContainer, new LostFindMainFragment(), LostFindMainFragment.class.getSimpleName());
            updateFooterView(4);
        } else {
            fragmentTransaction.add(R.id.fragmentContainer, new LostFindIntroduceFragment(), LostFindIntroduceFragment.class.getSimpleName());
            updateFooterView(0);
        }
        fragmentTransaction.commit();

        //创建手势识别器
        mGestureDetector = new GestureDetector(this, mGestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event); //委托手势识别器处理事件
    }

    public void updateFooterView(int position) {
        mPosition = position;
        if (mPosition < 4) {
            llPageBullet.setVisibility(View.VISIBLE);
            rlButton.setVisibility(View.VISIBLE);

            for (int i = 0; i < llPageBullet.getChildCount(); i++) {
                ImageView imageView = (ImageView) llPageBullet.getChildAt(i);
                imageView.setImageResource(i == position ? android.R.drawable.presence_online : android.R.drawable.presence_invisible);
            }

            switch (position) {
                case 0:
                    btPre.setVisibility(View.GONE);
                    btNext.setText("下一步");
                    break;
                case 1:
                    btPre.setVisibility(View.VISIBLE);
                    btPre.setText("上一步");
                    btNext.setText("下一步");
                    break;
                case 2:
                    btPre.setVisibility(View.VISIBLE);
                    btPre.setText("上一步");
                    btNext.setText("下一步");
                    break;
                case 3:
                    btPre.setVisibility(View.VISIBLE);
                    btPre.setText("上一步");
                    btNext.setText("完成");
                    break;
                default:
                    break;
            }
        } else {
            llPageBullet.setVisibility(View.GONE);
            rlButton.setVisibility(View.GONE);
        }
    }

    public void preStep() {
        LostFindBaseFragment currentFragment = (LostFindBaseFragment) mFragmentManager.findFragmentById(R.id.fragmentContainer);
        currentFragment.preStep();
    }

    public void nextStep() {
        LostFindBaseFragment currentFragment = (LostFindBaseFragment) mFragmentManager.findFragmentById(R.id.fragmentContainer);
        currentFragment.nextStep();
    }

    public void pushFragment(Fragment fragment, int position) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            if (mPosition < position) {
                fragmentTransaction.setCustomAnimations(R.animator.tran_next_in, R.animator.tran_next_out);
            } else {
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            }
            Fragment currentFragment = mFragmentManager.findFragmentById(R.id.fragmentContainer);
            if (currentFragment == null) {
                fragmentTransaction.add(fragment, fragment.getClass().getSimpleName());
            } else {
                fragmentTransaction.replace(R.id.fragmentContainer, fragment, fragment.getClass().getSimpleName());
            }
            fragmentTransaction.commit();
            updateFooterView(position);
        }
    }

    public void resetGuide() {
        mPref.edit().putBoolean("lostfind_guided", false).commit();

        updateFooterView(0);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.fragmentContainer);
        if (currentFragment == null) {
            fragmentTransaction.add(new LostFindIntroduceFragment(), LostFindIntroduceFragment.class.getSimpleName());
        } else {
            fragmentTransaction.replace(R.id.fragmentContainer, new LostFindIntroduceFragment(), LostFindIntroduceFragment.class.getSimpleName());
        }
        fragmentTransaction.commit();
    }
}

package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.silion.mobilesafe.R;

/**
 * Created by silion on 2016/4/26.
 */
public class DragAdressViewActivity extends Activity {
    private SharedPreferences mPref;
    private TextView tvTopNotice;
    private TextView tvBottomNotice;
    private ImageView ivLocate;
    private int mStartX;
    private int mStartY;
    private int mDisplayW;
    private int mDisplayH;

    private View.OnTouchListener mLocateListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = (int) event.getRawX();
                    mStartY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int) event.getRawX();
                    int y = (int) event.getRawY();
                    int dx = x - mStartX;
                    int dy = y - mStartY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    if (top > mDisplayH / 2) {
                        tvTopNotice.setVisibility(View.VISIBLE);
                        tvBottomNotice.setVisibility(View.INVISIBLE);
                    } else {
                        tvTopNotice.setVisibility(View.INVISIBLE);
                        tvBottomNotice.setVisibility(View.VISIBLE);
                    }
                    if (left < 0 || right > mDisplayW) {
                        left = v.getLeft();
                        right = v.getRight();
                    }
                    if (top < 0 || bottom > mDisplayH - 80) {
                        top = v.getTop();
                        bottom = v.getBottom();
                    }
                    v.layout(left, top, right, bottom);
                    mStartX = x;
                    mStartY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putInt("locate_x", (int) v.getX());
                    editor.putInt("locate_y", (int) v.getY());
                    editor.commit();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_address_view);
        mPref = getSharedPreferences("setting", MODE_PRIVATE);
        tvTopNotice = (TextView) findViewById(R.id.tvTopNotice);
        tvBottomNotice = (TextView) findViewById(R.id.tvBottomNotice);
        ivLocate = (ImageView) findViewById(R.id.ivLocate);
        int x = mPref.getInt("locate_x", 0);
        int y = mPref.getInt("locate_y", 0);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        mDisplayW = display.getWidth();
        mDisplayH = display.getHeight();
        /**
         * onMeasure(测量view), onLayout(安放位置), onDraw(绘制) 都在onCreate执行完后才调用
         * ivLocate.layout(x, y, x + ivLocate.getWidth(), y + ivLocate.getHeight() 不能用这个方法，因为还没有测量完成，不能安放
         */
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivLocate.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
        ivLocate.setLayoutParams(params);

        ivLocate.setOnTouchListener(mLocateListener);
    }
}

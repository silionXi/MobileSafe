package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.utils.UIUtils;

/**
 * Created by silion on 2016/6/20.
 */
public class EnterPwActivity extends Activity {
    private EditText etPw;
    private Button btOk;
    private boolean mIsAppLock;
    private String mLockPackageName;
    private View.OnClickListener mOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String pw = etPw.getText().toString();
            if (pw == null || pw.isEmpty()) {
                return;
            }
            SharedPreferences pre = getSharedPreferences("setting", MODE_PRIVATE);
            String applockPw = pre.getString("applock_pw", null);
            if (applockPw == null || applockPw.isEmpty()) {
                pre.edit().putString("applock_pw", pw).apply();
            } else {
                if (!applockPw.equals(pw)) {
                    etPw.setText("");
                    UIUtils.showToast(EnterPwActivity.this, "密码不正确请重新输入");
                    return;
                }
            }
            if (mIsAppLock) {
                Intent intent = new Intent();
                intent.setAction("com.silion.mobilesafe.APP_LOCK");
                intent.putExtra("packageName", mLockPackageName);
                sendBroadcast(intent);
            } else {
                Intent intent = new Intent(EnterPwActivity.this, AppLockActivity.class);
                startActivity(intent);
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int flag = intent.getFlags();
        android.util.Log.v("silion", "flag = " + flag);
        if ((flag & Intent.FLAG_ACTIVITY_NEW_TASK) != 0) {
            mIsAppLock = true;
            mLockPackageName = intent.getStringExtra("packageName");
        } else {
            mIsAppLock = false;
        }
        setContentView(R.layout.activity_enterpw);
        etPw = (EditText) findViewById(R.id.etPw);
        etPw.setInputType(InputType.TYPE_NULL);
        btOk = (Button) findViewById(R.id.btOk);
        btOk.setOnClickListener(mOkListener);
    }

    @Override
    public void onBackPressed() {
        if (mIsAppLock) {
            // 当用户输入后退健 的时候。我们进入到桌面
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.MONKEY");
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btOne: {
                Editable text = etPw.getText();
                text.append("1");
                etPw.setText(text);
                break;
            }
            case R.id.btTwo: {
                Editable text = etPw.getText();
                text.append("2");
                etPw.setText(text);
                break;
            }
            case R.id.btThree: {
                Editable text = etPw.getText();
                text.append("3");
                etPw.setText(text);
                break;
            }
            case R.id.btFour: {
                Editable text = etPw.getText();
                text.append("4");
                etPw.setText(text);
                break;
            }
            case R.id.btFive: {
                Editable text = etPw.getText();
                text.append("5");
                etPw.setText(text);
                break;
            }
            case R.id.btSix: {
                Editable text = etPw.getText();
                text.append("6");
                etPw.setText(text);
                break;
            }
            case R.id.btSeven: {
                Editable text = etPw.getText();
                text.append("7");
                etPw.setText(text);
                break;
            }
            case R.id.btEight: {
                Editable text = etPw.getText();
                text.append("8");
                etPw.setText(text);
                break;
            }
            case R.id.btNight: {
                Editable text = etPw.getText();
                text.append("9");
                etPw.setText(text);
                break;
            }
            case R.id.btClear: {
                etPw.setText("");
                break;
            }
            case R.id.btZero: {
                Editable text = etPw.getText();
                text.append("0");
                etPw.setText(text);
                break;
            }
            case R.id.btDelete: {
                Editable text = etPw.getText();
                if (text == null || text.length() == 0) {
                    return;
                }
                etPw.setText(text.subSequence(0, text.length() - 1));
                break;
            }
            default:
                break;
        }
    }
}

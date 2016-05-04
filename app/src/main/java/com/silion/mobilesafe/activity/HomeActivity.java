package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/3/22.
 */
public class HomeActivity extends Activity {
    private GridView mGridView;
    private List<Function> mFunctionList = new ArrayList();
    private String mSavePassword;
    private AdapterView.OnItemClickListener mFunctionListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0: {
                    showPasswordDialog();
                    break;
                }
                case 1: {
                    Intent intent = new Intent(HomeActivity.this, CallSafeActivity.class);
                    startActivity(intent);
                    break;
                }
                case 7: {
                    Intent intent = new Intent(HomeActivity.this, AdvToolsActivity.class);
                    startActivity(intent);
                    break;
                }
                case 8: {
                    Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void showPasswordDialog() {
        SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
        mSavePassword = pref.getString("password", null);
        if (mSavePassword != null && !mSavePassword.isEmpty()) {
            showLoginPasswordDialog();
        } else {
            showSetPasswordDialog();
        }

    }

    private void showSetPasswordDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText etPassword = (EditText) view.findViewById(R.id.etPasssword);
        final EditText etPasswordConfirm = (EditText) view.findViewById(R.id.etPassswordConfirm);
        Button btOk = (Button) view.findViewById(R.id.btOk);
        Button btCancel = (Button) view.findViewById(R.id.btCancel);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();

                if (password != null && !password.isEmpty() && passwordConfirm != null && !password.isEmpty()) {
                    if (password.equals(passwordConfirm)) {
                        getSharedPreferences("setting", MODE_PRIVATE).edit().putString("password", MD5Utils.encode(password)).commit();
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this, "设置密码成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showLoginPasswordDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = View.inflate(this, R.layout.dialog_login_password, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText etPassword = (EditText) view.findViewById(R.id.etPasssword);
        Button btOk = (Button) view.findViewById(R.id.btOk);
        Button btCancel = (Button) view.findViewById(R.id.btCancel);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();

                if (password != null && !password.isEmpty()) {
                    if (mSavePassword.equals(MD5Utils.encode(password))) {
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initDate();
        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(new GridAdapter());

        mGridView.setOnItemClickListener(mFunctionListener);
    }

    public void initDate() {
        mFunctionList.add(new Function("手机防盗", R.drawable.home_safe));
        mFunctionList.add(new Function("通讯卫士", R.drawable.home_callmsgsafe));
        mFunctionList.add(new Function("软件管理", R.drawable.home_apps));
        mFunctionList.add(new Function("进程管理", R.drawable.home_taskmanager));
        mFunctionList.add(new Function("流量统计", R.drawable.home_netmanager));
        mFunctionList.add(new Function("手机杀毒", R.drawable.home_trojan));
        mFunctionList.add(new Function("缓存清理", R.drawable.home_sysoptimize));
        mFunctionList.add(new Function("高级工具", R.drawable.home_tools));
        mFunctionList.add(new Function("设置中心", R.drawable.home_settings));
    }

    protected class Function {
        String title;
        int icon;

        public Function(String title, int icon) {
            this.title = title;
            this.icon = icon;
        }
    }

    protected class ViewHolder {
        TextView titleTextView;
        ImageView iconImageView;
    }

    protected class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFunctionList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFunctionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(HomeActivity.this, R.layout.griditem_home_function_list, null);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = (TextView) view.findViewById(R.id.titleTextView);
                viewHolder.iconImageView = (ImageView) view.findViewById(R.id.iconImageView);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            Function function = mFunctionList.get(position);
            viewHolder.titleTextView.setText(function.title);
            viewHolder.iconImageView.setImageResource(function.icon);
            return view;
        }
    }
}

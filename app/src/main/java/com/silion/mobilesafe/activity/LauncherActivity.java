package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.silion.mobilesafe.R;
import com.silion.mobilesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LauncherActivity extends Activity {
    private static final int CODE_UPDATE_VERSION = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NETWORK_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME = 4;
    private static final String CHECK_VERSION_URL = "http://10.0.2.2:80/version.json"; //本机地址用localhost,模拟器加载本机用10.0.0.2
    private RelativeLayout rlRoot;
    private TextView mVersionTextView;
    private ProgressBar mProgressBar;
    private TextView mProgressTextView;
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mUrl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressBar.setVisibility(View.GONE);
            switch (msg.what) {
                case CODE_UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(LauncherActivity.this, R.string.url_error, Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NETWORK_ERROR:
                    Toast.makeText(LauncherActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    showCopyDialog();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(LauncherActivity.this, R.string.json_error, Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                default:
                    break;
            }
        }
    };
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        mVersionTextView = (TextView) findViewById(R.id.versionTextView);
        mVersionTextView.setText(getString(R.string.version) + " " + getVersionName());

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mProgressTextView = (TextView) findViewById(R.id.progressTextView);

        mPref = getSharedPreferences("setting", MODE_PRIVATE);
        init();

        AlphaAnimation alpha = new AlphaAnimation(0.3f, 1);
        alpha.setDuration(1000);
        rlRoot.startAnimation(alpha);
    }

    public String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAddressDB();
                copyAntivirusDB();

                long startTime = System.currentTimeMillis();
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                try {
                    //检查更新
                    if (mPref.getBoolean("auto_update", true)) {
                        URL checkVersionUrl = new URL(CHECK_VERSION_URL);
                        conn = (HttpURLConnection) checkVersionUrl.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        conn.connect();

                        if (conn.getResponseCode() == 200) {
                            InputStream is = conn.getInputStream();
                            String result = StreamUtils.inputStream2String1(is);

                            JSONObject jsonObject = new JSONObject(result);
                            mVersionName = jsonObject.getString("versionName");
                            mVersionCode = jsonObject.getInt("versionCode");
                            mDescription = jsonObject.getString("description");
                            mUrl = jsonObject.getString("url");

                            if (mVersionCode > getVersionCode()) {
                                msg.what = CODE_UPDATE_VERSION;
                            } else {
                                msg.what = CODE_ENTER_HOME;
                            }
                        }
                    } else {
                        msg.what = CODE_ENTER_HOME;
                    }
                } catch (MalformedURLException e) {
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = CODE_NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long usedTime = endTime - startTime;
                    if (usedTime < 1000) {
                        try {
                            Thread.sleep(1000 - usedTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    public void copyDB(String dbName) {
        File destFile = new File(getFilesDir(), dbName);
        if (destFile.exists() && destFile.length() > 0) {
            return;
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = getAssets().open(dbName);
            os = new FileOutputStream(destFile);
            StreamUtils.copy(is, os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyAddressDB() {
        copyDB("address.db");
    }

    private void copyAntivirusDB() {
        copyDB("antivirus.db");
        mPref.edit().putInt("antivirus_version", 1).apply();
    }

    public void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_version) + ":" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadUpdateApk();
            }
        });
        builder.setNegativeButton(R.string.not_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    public void downloadUpdateApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String targetPath = Environment.getExternalStorageDirectory().getPath() + "/MobileSafe/update.apk";
            mProgressTextView.setVisibility(View.VISIBLE);

            HttpUtils http = new HttpUtils();
            http.download(mUrl, targetPath, new RequestCallBack<File>() {
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    mProgressTextView.setText(getString(R.string.download_progress) + current * 100 / total + "%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //跳转到系统安装页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result), "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(LauncherActivity.this, R.string.download_failed, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, R.string.sdcard_not_exist, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }

    private void showCopyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.copy_2_sdcard));
        builder.setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copy2SDcard();
            }
        });
        builder.setNegativeButton(R.string.not_copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.show();
    }

    private void copy2SDcard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory(), "test.7z");
                FileOutputStream out = null;
                AssetManager am = getAssets();
                InputStream in = null;
                try {
                    in = am.open("[Test]Galaxy Care test.7z");
                    out = new FileOutputStream(file);
                    StreamUtils.copy(in, out);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LauncherActivity.this, "复制完成", Toast.LENGTH_SHORT).show();
                            enterHome();
                        }
                    });
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

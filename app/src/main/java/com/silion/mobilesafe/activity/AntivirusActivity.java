package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.silion.mobilesafe.R;
import com.silion.mobilesafe.bean.ScanInfo;
import com.silion.mobilesafe.bean.VirusInfo;
import com.silion.mobilesafe.database.AntivirusDao;
import com.silion.mobilesafe.utils.MD5Utils;
import com.silion.mobilesafe.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by silion on 2016/6/13.
 */
public class AntivirusActivity extends Activity {

    private static final String CHECK_ANTIVIRUS_URL = "http://10.0.2.2:80/antivirus.json"; //本机地址用localhost,模拟器加载本机用10.0.0.2;
    private ImageView ivScanning;
    private LinearLayout llResult;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    tvProgress.setText("初始化八核杀毒引擎...");
                    startScanningAnimation();
                    break;
                }
                case 1: {
                    tvProgress.setText("正在玩命扫描中...");
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    TextView child = new TextView(AntivirusActivity.this);
                    if (scanInfo.desc == null) {
                        child.setTextColor(Color.GREEN);
                        child.setText(scanInfo.appName + "--" + "是安全的");
                    } else {
                        child.setTextColor(Color.RED);
                        child.setText(scanInfo.appName + "--" + scanInfo.desc);
                    }
                    llResult.addView(child);
                    break;
                }
                case 2: {
                    tvProgress.setText("扫描病毒完成啦！");
                    stopScanningAnimation();
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };
    private Handler mUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    VirusInfo virusInfo = (VirusInfo) msg.obj;
                    showUpdateDialog(virusInfo);
                    break;
                }
                case 1:
                    startScanning();
                    break;
                case 2:
                    VirusInfo virusInfo = (VirusInfo) msg.obj;
                    showUpdateDialog(virusInfo);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private ProgressBar pbScanning;
    private TextView tvProgress;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        initUI();

        mPref = getSharedPreferences("setting", MODE_PRIVATE);
        checkUpdate();
    }

    private void initUI() {
        ivScanning = (ImageView) findViewById(R.id.ivScanning);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        llResult = (LinearLayout) findViewById(R.id.llResult);
        pbScanning = (ProgressBar) findViewById(R.id.pbScanning);
    }

    private void checkUpdate() {
        new Thread() {
            @Override
            public void run() {
                int currentVersion = mPref.getInt("antivirus_version", 1);
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                URL checkVersionUrl;
                try {
                    checkVersionUrl = new URL(CHECK_ANTIVIRUS_URL);
                    conn = (HttpURLConnection) checkVersionUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(1000);
                    conn.setReadTimeout(1000);
                    conn.connect();

                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamUtils.inputStream2String1(is);

                        Gson gson = new Gson();
                        VirusInfo virusInfo = gson.fromJson(result, VirusInfo.class);

                        if (virusInfo.version > currentVersion) {
                            msg.obj = virusInfo;
                            msg.what = 0;
                        } else {
                            msg.what = 1;
                        }
                    }
                } catch (IOException e) {
                    Gson gson = new Gson();
                    VirusInfo virusInfo = gson.fromJson("{version:2,md5:826e6367ec72492fba8570de60088a0e,desc:这是什么鬼？}", VirusInfo.class);
                    msg.obj = virusInfo;
                    msg.what = 2;
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    mUpdateHandler.sendMessage(msg);
                }
                super.run();
            }
        }.start();
    }

    private void startScanning() {
        mHandler.sendEmptyMessage(0);
        new Thread() {
            @Override
            public void run() {
                super.run();
                PackageManager pm = getPackageManager();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                if (packageInfos != null && packageInfos.size() > 0) {
                    pbScanning.setMax(packageInfos.size());
                    int progress = 0;
                    for (PackageInfo info : packageInfos) {
                        ScanInfo scanInfo = new ScanInfo();
                        scanInfo.appName = info.applicationInfo.loadLabel(pm).toString();
                        //dataDir = /data/data/com.silion.mobilesafe, sourceDir = /data/app/com.silion.mobilesafe-2/base.apk
                        String dataDir = info.applicationInfo.dataDir;
                        String sourceDir = info.applicationInfo.sourceDir;
                        String md5 = MD5Utils.getFileMd5(sourceDir);
                        android.util.Log.v("silion", "dataDir = " + dataDir + ", sourceDir = " + sourceDir);
                        android.util.Log.v("silion", "md5 = " + md5);
                        scanInfo.desc = AntivirusDao.getAntivirus(md5);

                        pbScanning.setProgress(++progress);
                        Message message = Message.obtain();
                        message.obj = scanInfo;
                        message.what = 1;
                        mHandler.sendMessage(message);
                        SystemClock.sleep(100);
                    }
                }
                mHandler.sendEmptyMessage(2);
            }
        }.start();
    }

    private void startScanningAnimation() {
        RotateAnimation rotate = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(4000);
        rotate.setRepeatCount(Animation.INFINITE);
        ivScanning.startAnimation(rotate);
    }

    private void stopScanningAnimation() {
        ivScanning.clearAnimation();
    }

    public void showUpdateDialog(final VirusInfo virusInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更新病毒数据库");
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AntivirusDao.addAntivirus(virusInfo);
                startScanning();
            }
        });
        builder.setNegativeButton(R.string.not_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startScanning();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startScanning();

            }
        });
        builder.show();
    }
}

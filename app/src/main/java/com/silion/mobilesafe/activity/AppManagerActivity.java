package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.bean.AppInfo;
import com.silion.mobilesafe.engine.AppManager;
import com.silion.mobilesafe.utils.FileUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by silion on 2016/5/10.
 */
public class AppManagerActivity extends Activity {

    private TextView tvRom;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<AppInfo> mListData = new ArrayList<>();
    private List<AppInfo> mSysList = new ArrayList<>();
    private List<AppInfo> mUserList = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mListAdapter.notifyDataSetChanged();
            pbLoading.setVisibility(View.GONE);
            tvHeader.setVisibility(View.VISIBLE);
        }
    };
    private ProgressBar pbLoading;
    private TextView tvHeader;
    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            popupWindowDismiss();
            if (totalItemCount > 0 && view.getLastVisiblePosition() <= mUserList.size()) {
                tvHeader.setText("用户程序");
                tvHeader.setVisibility(View.VISIBLE);
            } else if (view.getLastVisiblePosition() > mUserList.size() && firstVisibleItem < mUserList.size() + 1) {
                tvHeader.setVisibility(View.GONE);
            } else if (firstVisibleItem == mUserList.size() + 1) {
                tvHeader.setText("系统程序");
                tvHeader.setVisibility(View.VISIBLE);
            }
        }
    };
    private View.OnClickListener mPopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindowDismiss();

            switch (v.getId()) {
                case R.id.llUninstall: {
                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_DELETE);
                    intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
                    Uri uri = Uri.parse("package:" + mClickAppInfo.getPackageName());
                    intent.setData(uri);
                    startActivity(intent);
                    break;
                }
                case R.id.llRun: {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(mClickAppInfo.getPackageName());
                    startActivity(intent);
                    break;
                }
                case R.id.llShare: {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.SUBJECT", "f分享");
                    intent.putExtra("android.intent.extra.TEXT",
                            "Hi！推荐您使用软件：" + mClickAppInfo.getName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + mClickAppInfo.getPackageName());
                    startActivity(Intent.createChooser(intent, "分享"));
                    break;
                }
                case R.id.llDetail: {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + mClickAppInfo.getPackageName()));
                    startActivity(intent);
                    break;
                }
                case R.id.llBackup: {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String dst = Environment.getExternalStorageDirectory() + "/" + AppManagerActivity.this.getPackageName() + "/" + mClickAppInfo.getName();
                            FileUtils.backup(mClickAppInfo, dst);
                        }
                    }).start();
                }
                default:
                    break;
            }
        }
    };
    private AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mClickAppInfo = (AppInfo) mListAdapter.getItem(position);
            if (mClickAppInfo != null) {
                View popupView = View.inflate(AppManagerActivity.this, R.layout.view_appmanager_menu, null);
                LinearLayout llUninstall = (LinearLayout) popupView.findViewById(R.id.llUninstall);
                llUninstall.setOnClickListener(mPopListener);
                LinearLayout llRun = (LinearLayout) popupView.findViewById(R.id.llRun);
                llRun.setOnClickListener(mPopListener);
                LinearLayout llShare = (LinearLayout) popupView.findViewById(R.id.llShare);
                llShare.setOnClickListener(mPopListener);
                LinearLayout llDetail = (LinearLayout) popupView.findViewById(R.id.llDetail);
                llDetail.setOnClickListener(mPopListener);
                LinearLayout llBackup = (LinearLayout) popupView.findViewById(R.id.llBackup);
                llBackup.setOnClickListener(mPopListener);

                popupWindowDismiss();
                mPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                //获取view展示到窗体上面的位置
                view.getLocationInWindow(location);
                mPopupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, 150, location[1]);
                /**
                 * 第一第二个参数：x从50%->100%
                 * 第三第四个参数：y从50%->100%
                 * 第五个参数：第六个参数按自己比例
                 * 第六个参数：x开始位置
                 * 第七个参数：第六个参数按自己比例
                 * 第八个参数：y开始位置
                 */
                ScaleAnimation animation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
                animation.setDuration(500);
                popupView.startAnimation(animation);
            }
        }
    };
    private AppInfo mClickAppInfo;
    private BroadcastReceiver mUnInstallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getDataString().replaceFirst("package:", "");
            Iterator<AppInfo> iterator = mListData.iterator();
            while (iterator.hasNext()) {
                AppInfo appInfo = iterator.next();
                if (appInfo != null && appInfo.getPackageName().equals(packageName)) {
                    iterator.remove();
                }
            }
            mListAdapter.notifyDataSetChanged();
        }
    };

    public void popupWindowDismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        tvRom = (TextView) findViewById(R.id.tvRom);
        initFreeSpace();

        tvHeader = (TextView) findViewById(R.id.tvHeader);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

        mListView = (ListView) findViewById(R.id.listView);
        mListAdapter = new ListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(mScrollListener);
        mListView.setOnItemClickListener(mClickListener);
        initData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mUnInstallReceiver, filter);
    }

    @Override
    protected void onStop() {
        popupWindowDismiss();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUnInstallReceiver);
        super.onDestroy();
    }

    public void initData() {
        pbLoading.setVisibility(View.VISIBLE);
        tvHeader.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppInfo> appInfos;
                appInfos = AppManager.getAppList(AppManagerActivity.this);
                if (appInfos != null && appInfos.size() > 0) {
                    for (AppInfo appInfo : appInfos) {
                        if (appInfo.isUser()) {
                            mUserList.add(appInfo);
                        } else {
                            mSysList.add(appInfo);
                        }
                    }
                    mListData.addAll(mUserList);
                    mListData.add(null);
                    mListData.addAll(mSysList);
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }

    public void initFreeSpace() {
        long romFree = Environment.getDataDirectory().getFreeSpace();
        tvRom.setText("内存可用：" + Formatter.formatFileSize(this, romFree));
    }

    public class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        class ViewHolder {
            public ImageView ivIcon;
            public TextView tvName;
            public TextView tvLocat;
            public TextView tvSize;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            AppInfo appInfo = (AppInfo) getItem(position);
            if (appInfo == null) {
                TextView tv = new TextView(mContext);
                tv.setBackgroundColor(mContext.getResources().getColor(R.color.d));
                tv.setTextSize(16);
                tv.setText("系统程序");
                return tv;
            } else {
                if (view != null && view instanceof LinearLayout) {
                    viewHolder = (ViewHolder) view.getTag();
                } else {
                    view = View.inflate(mContext, R.layout.listitem_appmanager, null);
                    viewHolder = new ViewHolder();
                    viewHolder.ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                    viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
                    viewHolder.tvLocat = (TextView) view.findViewById(R.id.tvLocate);
                    viewHolder.tvSize = (TextView) view.findViewById(R.id.tvSize);
                    view.setTag(viewHolder);
                }
                viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
                viewHolder.tvName.setText(appInfo.getName());
                if (appInfo.isRom()) {
                    viewHolder.tvLocat.setText("手机内存");
                } else {
                    viewHolder.tvLocat.setText("SD卡");
                }
                viewHolder.tvSize.setText(Formatter.formatFileSize(mContext, appInfo.getSize()));
            }
            return view;
        }
    }
}

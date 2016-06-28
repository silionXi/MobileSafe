package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.bean.CacheInfo;
import com.silion.mobilesafe.utils.StringUtils;
import com.silion.mobilesafe.utils.UIUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by silion on 2016/6/24.
 */
public class CleanCacheActivity extends Activity {
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<CacheInfo> mListData;
    private PackageManager mPackageManager;
    private Handler mHandler = new Handler();
    private Semaphore mCacheSemaphore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleancache);
        mListView = (ListView) findViewById(R.id.listView);
        mListAdapter = new ListAdapter();
        mListData = new ArrayList<>();
        mListView.setAdapter(mListAdapter);
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                getCacheApp();
                super.run();
            }
        }.start();
    }

    private void getCacheApp() {
        mCacheSemaphore = new Semaphore(0);
        mListData.clear();
        mPackageManager = getPackageManager();
        try {
            Class clazz = getClassLoader().loadClass("android.content.pm.PackageManager");
//            Class clazz = PackageManager.class;
//            Class clazz = mPackageManager.getClass();
            Method method = clazz.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            List<PackageInfo> packageInfos = mPackageManager.getInstalledPackages(0);
            for (PackageInfo info : packageInfos) {
                method.invoke(mPackageManager, info.packageName, new MyIPackageStatsObserver(info));
                mCacheSemaphore.acquire();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanAll(View view) {
        final Semaphore deleteSemaphore = new Semaphore(0);
        try {
            Class clazz = getClassLoader().loadClass("android.content.pm.PackageManager");
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if ("freeStorageAndNotify".equals(methodName)) {
                    method.invoke(mPackageManager, Integer.MAX_VALUE, new MyIPackageDataOberver());
                }
                /**
                 * 系统app才有权限
                 if (methodName.equals("deleteApplicationCacheFiles")) {
                 for (CacheInfo cacheInfo : mListData) {
                 method.invoke(mPackageManager, cacheInfo.getPackageName(), new IPackageDataObserver.Stub() {
                @Override public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                deleteSemaphore.release();
                }

                @Override public IBinder asBinder() {
                return null;
                }
                });
                 deleteSemaphore.acquire();
                 }
                 mListData.clear();
                 mHandler.post(new Runnable() {
                @Override public void run() {
                mListAdapter.notifyDataSetChanged();
                }
                });
                 }
                 */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
        private PackageInfo mPackageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            mPackageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats packageStats, boolean b) throws RemoteException {
            if (packageStats.cacheSize > 0) {
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.setPackageName(mPackageInfo.packageName);
                cacheInfo.setIcon(mPackageInfo.applicationInfo.loadIcon(mPackageManager));
                cacheInfo.setName(mPackageInfo.applicationInfo.loadLabel(mPackageManager).toString());
                cacheInfo.setSize(packageStats.cacheSize);
                mListData.add(cacheInfo);
            }
            mCacheSemaphore.release();
        }
    }

    private class MyIPackageDataOberver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            if (succeeded) {
                UIUtils.showToast(CleanCacheActivity.this, "清理完成");
                mListData.clear();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    private class ListAdapter extends BaseAdapter {

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
            if (view == null) {
                view = View.inflate(CleanCacheActivity.this, R.layout.listitem_cleancache, null);
                viewHolder = new ViewHolder();
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
                viewHolder.tvSize = (TextView) view.findViewById(R.id.tvSize);
                viewHolder.ivClean = (ImageView) view.findViewById(R.id.ivClean);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CacheInfo cacheInfo = mListData.get(position);
            viewHolder.ivIcon.setImageDrawable(cacheInfo.getIcon());
            viewHolder.tvName.setText(cacheInfo.getName());
            viewHolder.tvSize.setText(StringUtils.getSizeStr(CleanCacheActivity.this, cacheInfo.getSize()));
            return view;
        }

        private class ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            TextView tvSize;
            ImageView ivClean;
        }
    }
}

package com.silion.mobilesafe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.bean.AppInfo;
import com.silion.mobilesafe.database.AppLockDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/6/16.
 */
public class AppLockFragment extends Fragment {

    private ListView mListView;
    private ListAdapter mListAdapter;
    private AppLockDao mAppLockDao;
    private Activity mActivity;
    private List<AppInfo> mLockList = new ArrayList<>();
    private TextView tvHeader;


    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_lock, null);
        mListView = (ListView) view.findViewById(R.id.listView);
        tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        mAppLockDao = new AppLockDao(mActivity);
        initData();
        return view;
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                List<String> lockList = mAppLockDao.query();
                mLockList.clear();
                PackageManager pm = mActivity.getPackageManager();
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                for (PackageInfo packageInfo : packageInfos) {
                    if (lockList.contains(packageInfo.packageName)) {
                        AppInfo appInfo = new AppInfo();
                        appInfo.setPackageName(packageInfo.packageName);
                        appInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                        appInfo.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
                        mLockList.add(appInfo);
                    }
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
                super.run();
            }
        }.start();
    }

    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLockList.size();
        }

        @Override
        public Object getItem(int position) {
            return mLockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final View view;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(getActivity(), R.layout.listitem_applock, null);
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
                viewHolder.ivLock = (ImageView) view.findViewById(R.id.ivUnLock);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            final AppInfo appInfo = (AppInfo) getItem(position);
            viewHolder.ivIcon.setBackground(appInfo.getIcon());
            viewHolder.tvName.setText(appInfo.getName());
            viewHolder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    animation.setDuration(3000);
                    view.startAnimation(animation);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            SystemClock.sleep(3000);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAppLockDao.delete(appInfo.getPackageName());
                                    mLockList.remove(appInfo);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                }
            });
            return view;
        }

        class ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            ImageView ivLock;
        }
    }
}

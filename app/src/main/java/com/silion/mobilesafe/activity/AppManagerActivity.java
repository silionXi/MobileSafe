package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.bean.AppInfo;
import com.silion.mobilesafe.engine.AppManager;

import java.util.ArrayList;
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
        initData();

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

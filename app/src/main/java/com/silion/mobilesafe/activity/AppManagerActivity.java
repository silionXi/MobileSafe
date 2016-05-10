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
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mListAdapter.notifyDataSetChanged();
            pbLoading.setVisibility(View.GONE);
        }
    };
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        tvRom = (TextView) findViewById(R.id.tvRom);
        initFreeSpace();

        mListView = (ListView) findViewById(R.id.listView);
        mListAdapter = new ListAdapter(this);
        mListView.setAdapter(mListAdapter);
        initData();

        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
    }

    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pbLoading.setVisibility(View.VISIBLE);
                mListData = AppManager.getAppInfo(AppManagerActivity.this);
                mHandler.sendEmptyMessage(0);
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
            if (view != null) {
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
            AppInfo appInfo = (AppInfo) getItem(position);
            viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
            viewHolder.tvName.setText(appInfo.getName());
            if (appInfo.isRom()) {
                viewHolder.tvLocat.setText("手机内存");
            } else {
                viewHolder.tvLocat.setText("SD卡");
            }
            viewHolder.tvSize.setText(Formatter.formatFileSize(mContext, appInfo.getSize()));
            return view;
        }
    }
}

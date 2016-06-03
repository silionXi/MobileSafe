package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.bean.TaskInfo;
import com.silion.mobilesafe.engine.TaskManager;
import com.silion.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/5/25.
 */
public class TaskManagerActivity extends Activity {

    private TextView tvTask;
    private TextView tvRam;
    private TextView tvHeader;
    private ProgressBar pbLoading;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<TaskInfo> mListData = new ArrayList<>();
    ;
    private List<TaskInfo> mUserList = new ArrayList<>();
    ;
    private List<TaskInfo> mSysList = new ArrayList<>();
    ;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mListAdapter.notifyDataSetChanged();
            pbLoading.setVisibility(View.GONE);
        }
    };
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
    private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TaskInfo info = (TaskInfo) mListAdapter.getItem(position);
            if (info != null) {
                boolean isCheck = info.isCheck();
                info.setIsCheck(!isCheck);
                ListAdapter.ViewHolder viewHolder = (ListAdapter.ViewHolder) view.getTag();
                viewHolder.cbClear.setChecked(!isCheck);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskmanager);
        tvTask = (TextView) findViewById(R.id.tvTask);
        tvRam = (TextView) findViewById(R.id.tvRam);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        initFreeSpace();
        mListView = (ListView) findViewById(R.id.listView);
        mListAdapter = new ListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(mScrollListener);
        mListView.setOnItemClickListener(mItemListener);
        initData();
    }

    public void initFreeSpace() {
        int count = SystemInfoUtils.getRunningAppProcessesCunt(this);
        tvTask.setText("进程：" + count + "个");

        long availMem = SystemInfoUtils.getAvailMem(this);
        long totalMem = SystemInfoUtils.getTotalMem();
        tvRam.setText("内存：" + Formatter.formatFileSize(this, availMem) + "/" + Formatter.formatFileSize(this, totalMem));
    }

    public void initData() {
        pbLoading.setVisibility(View.VISIBLE);
        tvHeader.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TaskInfo> taskInfos;
                taskInfos = TaskManager.getTaskList(TaskManagerActivity.this);
                if (taskInfos != null && taskInfos.size() > 0) {
                    for (TaskInfo taskInfo : taskInfos) {
                        if (taskInfo.isUser()) {
                            mUserList.add(taskInfo);
                        } else {
                            mSysList.add(taskInfo);
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

    public void selectAll(View view) {
        for (TaskInfo info : mListData) {
            if (info != null) {
                info.setIsCheck(true);
            }
            mListAdapter.notifyDataSetChanged();
        }
    }

    public void invertSelect(View view) {
        for (TaskInfo info : mListData) {
            if (info != null) {
                info.setIsCheck(!info.isCheck());
            }
            mListAdapter.notifyDataSetChanged();
        }
    }

    public class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        class ViewHolder {
            public ImageView ivIcon;
            public TextView tvName;
            public TextView tvSize;
            public CheckBox cbClear;
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
            TaskInfo taskInfo = (TaskInfo) getItem(position);
            if (taskInfo == null) {
                TextView tv = new TextView(mContext);
                tv.setBackgroundColor(mContext.getResources().getColor(R.color.d));
                tv.setTextSize(16);
                tv.setText("系统程序");
                return tv;
            } else {
                if (view != null && view instanceof LinearLayout) {
                    viewHolder = (ViewHolder) view.getTag();
                } else {
                    view = View.inflate(mContext, R.layout.listitem_taskmanager, null);
                    viewHolder = new ViewHolder();
                    viewHolder.ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                    viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
                    viewHolder.tvSize = (TextView) view.findViewById(R.id.tvSize);
                    viewHolder.cbClear = (CheckBox) view.findViewById(R.id.cbClear);
                    view.setTag(viewHolder);
                }
                viewHolder.ivIcon.setImageDrawable(taskInfo.getIcon());
                viewHolder.tvName.setText(taskInfo.getAppName());
                viewHolder.tvSize.setText(Formatter.formatFileSize(mContext, taskInfo.getSize()));
                viewHolder.cbClear.setChecked(taskInfo.isCheck());
            }
            return view;
        }
    }
}
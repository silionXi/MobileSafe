package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.silion.mobilesafe.R;
import com.silion.mobilesafe.adapter.ListAdapter;
import com.silion.mobilesafe.bean.BlackInfo;
import com.silion.mobilesafe.database.CallSafeDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silion on 2016/5/3.
 */
public class CallSafeActivity extends Activity {
    private static final int DATA_CHANGE = 0;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<BlackInfo> mListDate = new ArrayList<>();
    private ProgressBar pbLoad;
    private CallSafeDao mCallSafeDao;
    private final int LIMIT = 20;
    private int mCurrentPage;
    private int mTotalPage;
    private TextView tvPage;
    private EditText etPage;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DATA_CHANGE:
                    pbLoad.setVisibility(View.GONE);
                    mListAdapter.setList(mListDate);
                    mListAdapter.notifyDataSetChanged();
                    tvPage.setText(mCurrentPage + "/" + mTotalPage);
                    mListView.setSelection(0);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsafe);
        mListView = (ListView) findViewById(R.id.listView);
        mListAdapter = new BlackAdapter(this, mListDate);
        mListView.setAdapter(mListAdapter);
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);
        tvPage = (TextView) findViewById(R.id.tvPage);
        etPage = (EditText) findViewById(R.id.etPage);
        initData();
    }

    public void initData() {
        mCallSafeDao = new CallSafeDao(this);
        mCurrentPage = 1;
        mTotalPage = (mCallSafeDao.totalNum() - 1) / LIMIT + 1;
        getData();
    }

    public void getData() {
        pbLoad.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mListDate = mCallSafeDao.queryMulti(LIMIT, mCurrentPage - 1);
                SystemClock.sleep(500); //模拟网络延迟
                Message msg = new Message();
                msg.what = DATA_CHANGE;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    public void btJump(View view) {
        String text = etPage.getText().toString().trim();
        etPage.setText("");
        if (text != null && !text.isEmpty()) {
            int page = Integer.parseInt(text);
            if (page <= 0 || page > mTotalPage) {
                Toast.makeText(this, "不能乱跳的哟！！！", Toast.LENGTH_SHORT).show();
            } else {
                mCurrentPage = page;
                getData();
            }
        }
    }

    public void btNext(View view) {
        if (mCurrentPage >= mTotalPage) {
            Toast.makeText(this, "已经是最后一页啦！！！", Toast.LENGTH_SHORT).show();
        } else {
            mCurrentPage++;
            getData();
        }
    }

    public void btPrev(View view) {
        if (mCurrentPage <= 1) {
            Toast.makeText(this, "这就是第一页啊！！！", Toast.LENGTH_SHORT).show();
        } else {
            mCurrentPage--;
            getData();
        }
    }

    public class BlackAdapter extends ListAdapter<BlackInfo> {
        private Context mContext;

        protected BlackAdapter(Context context, List list) {
            super(list);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.listitem_callsafe, null);
                viewHolder = new ViewHolder();
                viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
                viewHolder.tvMode = (TextView) convertView.findViewById(R.id.tvMode);
                viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BlackInfo blackInfo = (BlackInfo) getItem(position);
            viewHolder.tvNumber.setText(blackInfo.getNumber());
            if (blackInfo.getMode() == 1) {
                viewHolder.tvMode.setText("拦截电话");
            } else if (blackInfo.getMode() == 2) {
                viewHolder.tvMode.setText("拦截短信");
            } else {
                viewHolder.tvMode.setText("拦截电话和短信");
            }
            return convertView;
        }

        class ViewHolder {
            public TextView tvNumber;
            public TextView tvMode;
            public ImageView ivDelete;
        }
    }
}

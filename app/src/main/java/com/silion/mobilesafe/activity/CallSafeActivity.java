package com.silion.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
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
    private int mOffset;
    private int mTotalNum;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DATA_CHANGE:
                    pbLoad.setVisibility(View.GONE);
                    mListAdapter.setList(mListDate);
                    mListAdapter.notifyDataSetChanged();
                    mListView.setSelection(mOffset);
                    break;
                default:
                    break;
            }
        }
    };
    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        /**
         *
         * @param view
         * @param scrollState  表示滚动的状态
         *
         *                     AbsListView.OnScrollListener.SCROLL_STATE_IDLE 闲置状态
         *                     AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指触摸的时候的状态
         *                     AbsListView.OnScrollListener.SCROLL_STATE_FLING 惯性
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                int position = mListView.getLastVisiblePosition() + 1;
                int count = mListView.getCount();
                if (position == count) {
                    if (position >= mTotalNum) {
                        Toast.makeText(CallSafeActivity.this, "到底了啊~", Toast.LENGTH_SHORT).show();
                    } else {
                        mOffset += LIMIT;
                        getData();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
        mListView.setOnScrollListener(mScrollListener);
        initData();
    }

    public void initData() {
        mCallSafeDao = new CallSafeDao(this);
        mOffset = 0;
        getData();
    }

    public void getData() {
        pbLoad.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTotalNum = mCallSafeDao.totalNum();
                if (mListDate == null) {
                    mListDate = mCallSafeDao.queryMulti(LIMIT, mOffset);

                } else {
                    mListDate.addAll(mCallSafeDao.queryMulti(LIMIT, mOffset));
                }
                SystemClock.sleep(500); //模拟网络延迟
                Message msg = new Message();
                msg.what = DATA_CHANGE;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    public void addBlack(View view) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View dialogView = View.inflate(this, R.layout.dialog_callsafe_addblack, null);
        dialog.setView(dialogView);
        final EditText etBlack = (EditText) dialogView.findViewById(R.id.etBlack);
        final CheckBox cbPhone = (CheckBox) dialogView.findViewById(R.id.cbPhone);
        final CheckBox cbMsg = (CheckBox) dialogView.findViewById(R.id.cbMsg);
        Button btOk = (Button) dialogView.findViewById(R.id.btOk);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) CallSafeActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                int mode;
                String phone = etBlack.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    if (cbPhone.isChecked() && cbMsg.isChecked()) {
                        mode = 3;
                    } else if (cbPhone.isChecked()) {
                        mode = 1;
                    } else if (cbMsg.isChecked()) {
                        mode = 2;
                    } else {
                        Toast.makeText(CallSafeActivity.this, "请选择要拦截的模式", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mCallSafeDao.insert(phone, mode);
                    mListDate.add(0, new BlackInfo(phone, mode));
                    if (mListAdapter == null) {
                        mListAdapter = new BlackAdapter(CallSafeActivity.this, mListDate);
                        mListView.setAdapter(mListAdapter);
                    } else {
                        mListAdapter.setList(mListDate);
                        mListAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(CallSafeActivity.this, "请输入号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
            }
        });
        Button btCancel = (Button) dialogView.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
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

            final BlackInfo blackInfo = (BlackInfo) getItem(position);
            final String number = blackInfo.getNumber();
            viewHolder.tvNumber.setText(number);
            if (blackInfo.getMode() == 1) {
                viewHolder.tvMode.setText("拦截电话");
            } else if (blackInfo.getMode() == 2) {
                viewHolder.tvMode.setText("拦截短信");
            } else {
                viewHolder.tvMode.setText("拦截电话和短信");
            }
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builer = new AlertDialog.Builder(CallSafeActivity.this);
                    builer.setTitle("移除黑名单");
                    builer.setMessage("是否确认把号码:" + number + "从黑名单移除？");
                    builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int num = mCallSafeDao.delete(number);
                            if (num > 0) {
                                mListDate.remove(blackInfo);
                                mListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builer.setCancelable(false);
                    builer.create().show();
                }
            });
            return convertView;
        }

        class ViewHolder {
            public TextView tvNumber;
            public TextView tvMode;
            public ImageView ivDelete;
        }
    }
}

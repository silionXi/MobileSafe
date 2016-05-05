package com.silion.mobilesafe.adapter;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by silion on 2016/5/3.
 */
public abstract class ListAdapter<T> extends BaseAdapter {
    private List<T> mList;

    protected ListAdapter(List<T> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        mList = list;
    }
}

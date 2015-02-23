package com.alexander.webtextsearcher.searcher.core;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends BaseAdapter{

    List<String> mResuls;

    public ResultListAdapter(List<String> results) {
        mResuls = new ArrayList<String>(results);
    }

    @Override
    public int getCount() {
        return mResuls.size();
    }

    @Override
    public Object getItem(int i) {
        return mResuls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}

package com.alexander.webtextsearcher.searcher.core;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.alexander.webtextsearcher.searcher.R;

import java.util.*;

public class UrlListAdapter extends BaseAdapter{

    private Map<String, Boolean> mUrls;
    private LayoutInflater mInflater;

    public UrlListAdapter(Activity activity, Map<String, Boolean> urls) {
        mUrls = new LinkedHashMap<String, Boolean>(urls);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return mUrls.keySet().toArray()[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        viewHolder = new ViewHolder();

        view = mInflater.inflate(R.layout.list_item_url, viewGroup, false);
        view.setOnClickListener(null);
        view.setEnabled(false);

        viewHolder.vTextUrl = (TextView) view.findViewById(R.id.text_url);
        viewHolder.vChBoxWasScanned = (CheckBox) view.findViewById(R.id.chBox_wasScanned);

        String url = (String)mUrls.keySet().toArray()[i];
        viewHolder.vTextUrl.setText(url);
        viewHolder.vChBoxWasScanned.setChecked(mUrls.get(url));

        return view;
    }

    private static class ViewHolder {
        private TextView vTextUrl;
        private CheckBox vChBoxWasScanned;
    }

    public void updateDataSet(Map<String, Boolean> newUrlList) {
        mUrls = new LinkedHashMap<String, Boolean>(newUrlList);
        notifyDataSetChanged();
    }
}

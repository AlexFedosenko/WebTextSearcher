package com.alexander.webtextsearcher.searcher.core;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.alexander.webtextsearcher.searcher.R;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends BaseAdapter{

    private List<String> mResults;
    private LayoutInflater mInflater;

    public ResultListAdapter(Activity activity, List<String> results) {
        mResults = new ArrayList<String>(results);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Object getItem(int i) {
        return mResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        viewHolder = new ViewHolder();

        view = mInflater.inflate(R.layout.list_item_result, viewGroup, false);
        view.setOnClickListener(null);
        view.setEnabled(false);

        viewHolder.vTextResult = (TextView) view.findViewById(R.id.text_result);
        if (!mResults.isEmpty() && mResults.get(i) != null) {
            viewHolder.vTextResult.setText(mResults.get(i));
        }

        return view;
    }

    public void updateDataSet(List<String> results) {
        mResults = new ArrayList<String>(results);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView vTextResult;
    }
}

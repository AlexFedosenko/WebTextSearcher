package com.alexander.webtextsearcher.searcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.core.*;
import org.w3c.dom.Text;

public class ProgressFragment extends Fragment implements UpdateProgressListener, UpdateStatusListener {

    private EditText vEdtUrl;
    private EditText vEdtTarget;
    private EditText vEdtThreads;
    private EditText vEdtUrls;
    private TextView vTextStatus;
    private TextView vTextUrlsHeader;
    private ProgressBar vProgressBar;
    private ListView vListUrls;

    private SearchController mSearchController;
    private UrlListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.progress_fragment, container, false);

        setSearchController(((MainActivity)getActivity()).getSearchController());

        vEdtUrl = (EditText)rootView.findViewById(R.id.edt_url);
        // test
        vEdtUrl.setText("http://habrahabr.ru/post/81294/");
        if (!mSearchController.getUrl().isEmpty()) {
            vEdtUrl.setText(mSearchController.getUrl());
        }
        vEdtTarget = (EditText)rootView.findViewById(R.id.edt_target);
        if (!mSearchController.getTargetText().isEmpty()) {
            vEdtTarget.setText(mSearchController.getTargetText());
        }
        vEdtThreads = (EditText)rootView.findViewById(R.id.edt_threadAmount);
        if (mSearchController.getThreadAmount() != null) {
            vEdtThreads.setText(String.valueOf(mSearchController.getThreadAmount()));
        }
        vEdtUrls = (EditText)rootView.findViewById(R.id.edt_urlsAmount);
        vProgressBar = (ProgressBar)rootView.findViewById(R.id.view_progressBar);
        if (mSearchController.getUrlAmount() != null) {
            vEdtUrls.setText(String.valueOf(mSearchController.getUrlAmount()));
            vProgressBar.setMax(mSearchController.getUrlAmount());
            vProgressBar.setProgress(mSearchController.getAlreadyScannedUrlAmount());
        }

        if (mSearchController.isInProgress()) {
            setEditFieldsEnabled(false);
        }

        vTextStatus = (TextView)rootView.findViewById(R.id.text_status);

        vTextUrlsHeader = (TextView)rootView.findViewById(R.id.text_urlsHeader);

        vListUrls = (ListView)rootView.findViewById(R.id.list_urls);
        mAdapter = new UrlListAdapter(getActivity(), mSearchController.getAllUrlMap());
        vListUrls.setAdapter(mAdapter);
        Utils.setListViewHeightBasedOnChildren(vListUrls);

        return rootView;
    }

    private void setSearchController(SearchController searchController) {
        mSearchController = searchController;
        mSearchController.setUpdateProgressListener(this);
        mSearchController.setUpdateStatusListener(this);
    }

    public String getUrl() {
        return vEdtUrl.getText().toString();
    }

    public String getTargetText() {
        return vEdtTarget.getText().toString();
    }

    private String getThreads() {
        return vEdtThreads.getText().toString();
    }

    private String getUrls() {
        return vEdtUrls.getText().toString();
    }

    public Integer getThreadAmount() {
        return getThreads().isEmpty() ? null : Integer.parseInt(getThreads());
    }

    public Integer getUrlAmount() {
        return getUrls().isEmpty() ? null : Integer.parseInt(getUrls());
    }

    public void setEditFieldsEnabled(boolean enable) {
        vEdtTarget.setEnabled(enable);
        vEdtUrl.setEnabled(enable);
        vEdtThreads.setEnabled(enable);
        vEdtUrls.setEnabled(enable);
    }

    @Override
    public void onPause() {
        mSearchController.setUrl(getUrl());
        mSearchController.setTargetText(getTargetText());
        mSearchController.setThreadAmount(getThreadAmount());
        mSearchController.setUrlAmount(getUrlAmount());
        super.onPause();
    }

    @Override
    public void incrementProgressList(String url) {
        mAdapter.updateDataSet(mSearchController.getAllUrlMap());
        Utils.setListViewHeightBasedOnChildren(vListUrls);
    }

    @Override
    public void incrementProgress(String url) {
        vProgressBar.setProgress(vProgressBar.getProgress() + 1);
        mSearchController.checkUrl(url);
        mAdapter.updateDataSet(mSearchController.getAllUrlMap());
        Utils.setListViewHeightBasedOnChildren(vListUrls);
    }

    @Override
    public void finishProgress() {
        vProgressBar.setProgress(vProgressBar.getMax());
    }

    @Override
    public void resetProgress() {
        if (mSearchController.getUrlAmount() != null) {
            vProgressBar.setMax(mSearchController.getUrlAmount());
        }
        vProgressBar.setProgress(0);
    }

    @Override
    public void updateStatus(String status) {
        updateStatus(status, getResources().getColor(android.R.color.white));
    }

    @Override
    public void updateStatus(String status, int color) {
        vTextStatus.setText(status);
        vTextStatus.setTextColor(color);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }


}

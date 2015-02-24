package com.alexander.webtextsearcher.searcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.core.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;

public class ProgressFragment extends AbstractCustomFragment implements UpdateProgressListener, UpdateStatusListener, View.OnFocusChangeListener {

    private EditText vEdtUrl;
    private EditText vEdtTarget;
    private EditText vEdtThreads;
    private EditText vEdtUrls;
    private TextView vTextStatus;
    private CheckBox vChBoxMeta;
    private ProgressBar vProgressBar;
    private ListView vListUrls;


    private UrlListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

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
        vEdtUrl.setOnFocusChangeListener(this);
        vEdtUrl.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Consts.URL_AMOUNT_LENGTH)});

        vEdtTarget = (EditText)rootView.findViewById(R.id.edt_target);
        if (!mSearchController.getTargetText().isEmpty()) {
            vEdtTarget.setText(mSearchController.getTargetText());
        }
        vEdtTarget.setOnFocusChangeListener(this);
        vEdtTarget.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Consts.TARGET_LENGTH)});

        vEdtThreads = (EditText)rootView.findViewById(R.id.edt_threadAmount);
        if (mSearchController.getThreadAmount() != null) {
            vEdtThreads.setText(String.valueOf(mSearchController.getThreadAmount()));
        }
        vEdtThreads.setOnFocusChangeListener(this);
        vEdtThreads.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Consts.THREAD_AMOUNT_LENGTH)});
        vEdtThreads.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    String threads = editable.toString();
                    if (!threads.equals(String.valueOf(Integer.parseInt(threads)))) {
                        vEdtThreads.removeTextChangedListener(this);
                        threads = String.valueOf(Integer.parseInt(threads));
                        vEdtThreads.setText(threads);
                        vEdtThreads.setSelection(threads.length());
                        vEdtThreads.addTextChangedListener(this);
                    }
                }
            }
        });

        vEdtUrls = (EditText)rootView.findViewById(R.id.edt_urlsAmount);
        vEdtUrls.setOnFocusChangeListener(this);
        vEdtUrls.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Consts.URL_AMOUNT_LENGTH)});
        vEdtUrls.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    String threads = editable.toString();
                    if (!threads.equals(String.valueOf(Integer.parseInt(threads)))) {
                        vEdtUrls.removeTextChangedListener(this);
                        threads = String.valueOf(Integer.parseInt(threads));
                        vEdtUrls.setText(threads);
                        vEdtUrls.setSelection(threads.length());
                        vEdtUrls.addTextChangedListener(this);
                    }
                }
            }
        });

        vProgressBar = (ProgressBar)rootView.findViewById(R.id.view_progressBar);
        if (mSearchController.getUrlAmount() != null) {
            vEdtUrls.setText(String.valueOf(mSearchController.getUrlAmount()));
            vProgressBar.setMax(mSearchController.getUrlAmount());
            vProgressBar.setProgress(mSearchController.getAlreadyScannedUrlAmount());
        }

        vTextStatus = (TextView)rootView.findViewById(R.id.text_status);
        mSearchController.printFoundStatus();

        vChBoxMeta = (CheckBox)rootView.findViewById(R.id.chBox_searchInMeta);
        vChBoxMeta.setChecked(mSearchController.isSearchInMeta());

        if (mSearchController.isInProgress()) {
            setEditFieldsEnabled(false);
        }

        vListUrls = (ListView)rootView.findViewById(R.id.list_urls);
        mAdapter = new UrlListAdapter(getActivity(), mSearchController.getAllUrlMap());
        vListUrls.setAdapter(mAdapter);
        Utils.setListViewHeightBasedOnChildren(vListUrls);

        return rootView;
    }

    @Override
    protected void setSearchController(SearchController searchController) {
        super.setSearchController(searchController);
        mSearchController.setUpdateProgressListener(this);
        mSearchController.setUpdateStatusListener(this);
        mSearchController.setUpdateResultListener(null);
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

    public boolean isSearchInMeta() {
        return vChBoxMeta.isChecked();
    }

    public void setEditFieldsEnabled(boolean enable) {
        vEdtTarget.setEnabled(enable);
        vEdtUrl.setEnabled(enable);
        vEdtThreads.setEnabled(enable);
        vEdtUrls.setEnabled(enable);
        vChBoxMeta.setEnabled(enable);
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
        updateStatus(status, getActivity().getResources().getColor(android.R.color.white));
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


    @Override
    public void onFocusChange(View view, boolean focus) {
        switch (view.getId()) {
            case R.id.edt_url:
                if (!focus) {
                    checkEnteredUrl();
                }
                break;
            case R.id.edt_threadAmount:
                if (!focus) {
                    checkEnteredThreadAmount();
                }
                break;
            case R.id.edt_urlsAmount:
                if (!focus) {
                    checkEnteredUrlAmount();
                }
                break;
        }
    }

    private void checkEnteredUrl() {
        try {
            new URL(vEdtUrl.getText().toString());
            vEdtUrl.setTextColor(getResources().getColorStateList(R.color.edt_text_color));
        } catch (MalformedURLException e) {
            vEdtUrl.setTextColor(getResources().getColor(R.color.edt_wrongData_textColor));
        }
    }

    private void checkEnteredUrlAmount() {
        if (!vEdtUrls.getText().toString().isEmpty()) {
            if (Integer.parseInt(vEdtUrls.getText().toString()) > 0) {
                vEdtUrls.setTextColor(getResources().getColorStateList(R.color.edt_text_color));
            } else {
                vEdtUrls.setTextColor(getResources().getColor(R.color.edt_wrongData_textColor));
            }
        }
    }

    private void checkEnteredThreadAmount() {
        if (!vEdtThreads.getText().toString().isEmpty()) {
            if (Integer.parseInt(vEdtThreads.getText().toString()) > 0) {
                vEdtThreads.setTextColor(getResources().getColorStateList(R.color.edt_text_color));
            } else {
                vEdtThreads.setTextColor(getResources().getColor(R.color.edt_wrongData_textColor));
            }
        }
    }
}

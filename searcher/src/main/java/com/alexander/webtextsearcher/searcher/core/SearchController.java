package com.alexander.webtextsearcher.searcher.core;

import com.alexander.webtextsearcher.searcher.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchController {

    private final String IDLE = "idle";
    private final String RUNNING = "running";
    private final String SUSPENDED = "suspended";

    private String mState = IDLE;
    private String mUrl = "";
    private String mTargetText = "";
    private Integer mThreadAmount;
    private Integer mUrlAmount;

    private List<String> allUrlList;
    private List<String> foundTextList;
    private List<ProcessWebPageTask> taskList;

    private MainActivity mActivity;

    private int mUrlToProcessAmount;

    public SearchController(MainActivity activity) {
        mActivity = activity;
        allUrlList = new ArrayList<String>();
        foundTextList = new ArrayList<String>();
        taskList = new ArrayList<ProcessWebPageTask>();
        mUrlToProcessAmount = 0;
    }


    public boolean isReadyForSearch() {
        return !mUrl.isEmpty() && !mTargetText.isEmpty() && mThreadAmount != null && mUrlAmount != null;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setTargetText(String text) {
        mTargetText = text;
    }

    public void setThreadAmount(Integer threads) {
        mThreadAmount = threads;
    }

    public void setUrlAmount(Integer urls) {
        mUrlAmount = urls;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTargetText() {
        return mTargetText;
    }

    public Integer getThreadAmount() {
        return mThreadAmount;
    }

    public Integer getUrlAmount() {
        return mUrlAmount;
    }

    public void addUrl(String url) {
        // try to add new URL if it was not searched before
        if (mUrlAmount != null && mUrlToProcessAmount < mUrlAmount && !allUrlList.contains(url)) {
            runNewTask(url);
        }
    }

    public void addFoundText(String text) {
        foundTextList.add(text);
    }

    public void removeTask(ProcessWebPageTask task) {
        taskList.remove(task);
        if (taskList.isEmpty()) {
            // no more URLs available or URL limit is reached
            mActivity.stopSearch();
            mState = IDLE;
        }
    }

    public void start() {
        if (mState.equals(IDLE)) {
            AsyncTask.setCorePoolSize(getThreadAmount());
            runNewTask(mUrl);
            mState = RUNNING;
        }
        if (mState.equals(SUSPENDED)) {
            resume();
        }

    }

    private void runNewTask(String url) {
        ProcessWebPageTask task = new ProcessWebPageTask(this);
        allUrlList.add(url);
        taskList.add(task);
        task.execute(url);
        mUrlToProcessAmount++;
    }

    public void stop() {
        for (ProcessWebPageTask task : taskList) {
            task.cancel(true);
        }
        mState = IDLE;
    }

    public void pause() {
        for (ProcessWebPageTask task : taskList) {
            task.pause();
        }
        mState = SUSPENDED;
    }

    public void resume() {
        for (ProcessWebPageTask task : taskList) {
            task.resume();
        }
        mState = RUNNING;
    }
}

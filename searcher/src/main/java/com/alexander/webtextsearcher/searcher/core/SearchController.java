package com.alexander.webtextsearcher.searcher.core;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.ui.MainActivity;

import java.util.*;

public class SearchController {

    private final String IDLE = "idle";
    private final String RUNNING = "running";
    private final String SUSPENDED = "suspended";

    private String mState = IDLE;
    private String mUrl = "";
    private String mTargetText = "";
    private Integer mThreadAmount;
    private Integer mUrlAmount;

    private String mError;

    private Map<String, Boolean> allUrlMap;
    private Set<String> foundTextList;
    private List<ProcessWebPageTask> taskList;

    private MainActivity mActivity;
    private UpdateProgressListener mUpdateProgressListener;
    private UpdateStatusListener mUpdateStatusListener;
    private UpdateResultListener mUpdateResultListener;

    private int mUrlToProcessAmount;
    private int mAlreadyScannedUrlAmount;
    private boolean mSearchInMeta;

    public SearchController(MainActivity activity) {
        mActivity = activity;
        allUrlMap = new LinkedHashMap<String, Boolean>();
        foundTextList = new HashSet<String>();
        taskList = new ArrayList<ProcessWebPageTask>();
        mUrlToProcessAmount = 0;
        mAlreadyScannedUrlAmount = 0;
        mSearchInMeta = false;
    }

    public void setUpdateProgressListener(UpdateProgressListener listener) {
        mUpdateProgressListener = listener;
    }

    public void setUpdateStatusListener(UpdateStatusListener listener) {
        mUpdateStatusListener = listener;
    }

    public void setUpdateResultListener(UpdateResultListener listener) {
        mUpdateResultListener = listener;
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

    public void setSearchInMeta(boolean search) {
        mSearchInMeta = search;
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

    public boolean isSearchInMeta() {
        return mSearchInMeta;
    }

    public void addUrl(String url) {
        // try to add new URL if it was not searched before
        if (mUrlAmount != null && mUrlToProcessAmount < mUrlAmount) {
            runNewTask(url);
            if (mUpdateProgressListener != null) {
                mUpdateProgressListener.incrementProgressList(url);
            }
        }
    }

    public void checkUrl(String url) {
        if (allUrlMap.get(url) != null) {
            allUrlMap.put(url, true);
        }
    }

    public Map<String, Boolean> getAllUrlMap() {
        return allUrlMap;
    }

    public void addFoundText(String text) {
        foundTextList.add(text);
        if (mUpdateResultListener != null) {
            mUpdateResultListener.updateResult();
        } else {
            mActivity.markUnseenResults();
        }
    }

    public void showErrorStatus(String error) {
        mError = error;
        if (mUpdateStatusListener != null)
        mUpdateStatusListener.updateStatus(mActivity.getString(R.string.error) + ": " + error,
                mActivity.getResources().getColor(R.color.text_status_error_textColor));
    }

    public List<String> getFoundTextList() {
        return new ArrayList<String>(foundTextList);
    }

    public int getAlreadyScannedUrlAmount() {
        return mAlreadyScannedUrlAmount;
    }

    public boolean isInProgress() {
        return !mState.equals(IDLE);
    }

    public void removeTask(ProcessWebPageTask task, String url) {
        taskList.remove(task);
        mAlreadyScannedUrlAmount++;
        if (mUpdateProgressListener != null) {
            mUpdateProgressListener.incrementProgress(url);
        }
        checkUrl(url);
        if (taskList.isEmpty()) {
            // no more URLs available or URL limit is reached
            mActivity.stopSearch();
            mState = IDLE;
            if (mUpdateProgressListener != null) {
                mUpdateProgressListener.finishProgress();
            }
            printFoundStatus();
            unlockScreenOrientation();
        }
    }

    public void start() {
        if (mState.equals(IDLE)) {
            allUrlMap.clear();
            taskList.clear();
            foundTextList.clear();
            mUrlToProcessAmount = 0;
            mAlreadyScannedUrlAmount = 0;
            mError = "";
            AsyncTask.setCorePoolSize(getThreadAmount());
            addUrl(mUrl);
            mState = RUNNING;
            if (mUpdateProgressListener != null) {
                mUpdateProgressListener.resetProgress();
            }
            lockScreenOrientation();
        }
        if (mState.equals(SUSPENDED)) {
            resume();
        }
        if (mUpdateStatusListener != null) {
            mUpdateStatusListener.updateStatus(mActivity.getString(R.string.searching));
        }

    }

    private void runNewTask(String url) {
        ProcessWebPageTask task = new ProcessWebPageTask(this);
        allUrlMap.put(url, false);
        taskList.add(task);
        task.execute(url, String.valueOf(mSearchInMeta));
        mUrlToProcessAmount++;
    }

    public void stop() {
        for (ProcessWebPageTask task : taskList) {
            task.cancel(true);
        }
        mState = IDLE;
        mAlreadyScannedUrlAmount = mUrlAmount;
        if (mUpdateProgressListener != null) {
            mUpdateProgressListener.finishProgress();
        }
        printFoundStatus();
        unlockScreenOrientation();
    }

    public void pause() {
        for (ProcessWebPageTask task : taskList) {
            task.pause();
        }
        mState = SUSPENDED;
        printFoundStatus();
    }

    public void resume() {
        for (ProcessWebPageTask task : taskList) {
            task.resume();
        }
        mState = RUNNING;
    }

    private void printFoundStatus() {
        if (mError.isEmpty() && mUpdateStatusListener != null) {
            if (foundTextList.isEmpty()) {
                mUpdateStatusListener.updateStatus(mActivity.getString(R.string.notFound), mActivity.getResources().getColor(R.color.text_status_notFound_textColor));
            } else {
                mUpdateStatusListener.updateStatus(mActivity.getString(R.string.found), mActivity.getResources().getColor(R.color.text_status_found_textColor));
            }
        } else {
            showErrorStatus(mError);
        }
    }

    private void lockScreenOrientation() {
        int currentOrientation = mActivity.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}

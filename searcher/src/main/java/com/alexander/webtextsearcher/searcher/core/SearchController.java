package com.alexander.webtextsearcher.searcher.core;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.ui.MainActivity;

import java.util.*;

public class SearchController {

    private enum State{
        IDLE, RUNNING, PENDING, STOPPED
    }

    private State mState;
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
        setActivity(activity);
        allUrlMap = new LinkedHashMap<String, Boolean>();
        foundTextList = new HashSet<String>();
        taskList = new ArrayList<ProcessWebPageTask>();
        mUrlToProcessAmount = 0;
        mAlreadyScannedUrlAmount = 0;
        mSearchInMeta = false;
        mState = State.STOPPED;
        mError = "";
    }

    public void setActivity(MainActivity activity) {
        mActivity = activity;
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
        return !mUrl.isEmpty() && !mTargetText.isEmpty() && mThreadAmount != null && mUrlAmount != null && mThreadAmount > 0 && mUrlAmount > 0;
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
        return isPaused() || isRunning();
    }

    public boolean isIdle() {
        return mState.equals(State.IDLE);
    }

    public boolean isPaused() {
        return mState.equals(State.PENDING);
    }

    public boolean isRunning() {
        return mState.equals(State.RUNNING);
    }

    public boolean isStopped() {
        return mState.equals(State.STOPPED);
    }

    public void removeTask(ProcessWebPageTask task, String url) {
        taskList.remove(task);
        mAlreadyScannedUrlAmount++;
        checkUrl(url);
        if (mUpdateProgressListener != null) {
            mUpdateProgressListener.incrementProgress(url);
        }
        if (taskList.isEmpty()) {
            // no more URLs available or URL limit is reached
            mActivity.stopSearch();
            mState = State.IDLE;
            if (mUpdateProgressListener != null) {
                mUpdateProgressListener.finishProgress();
            }
            printFoundStatus();
//            unlockScreenOrientation();
        }
    }

    public void start() {
        if (!isInProgress()) {
            allUrlMap.clear();
            taskList.clear();
            foundTextList.clear();
            mUrlToProcessAmount = 0;
            mAlreadyScannedUrlAmount = 0;
            mError = "";
            AsyncTask.setCorePoolSize(getThreadAmount());
            addUrl(mUrl);
            mState = State.RUNNING;
            if (mUpdateProgressListener != null) {
                mUpdateProgressListener.resetProgress();
            }
//            lockScreenOrientation();
        }
        if (mState.equals(State.PENDING)) {
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
        mState = State.STOPPED;
        mAlreadyScannedUrlAmount = mUrlAmount;
        if (mUpdateProgressListener != null) {
            mUpdateProgressListener.finishProgress();
        }
        printFoundStatus();
//        unlockScreenOrientation();
    }

    public void pause() {
        for (ProcessWebPageTask task : taskList) {
            task.pause();
        }
        mState = State.PENDING;
        printFoundStatus();
    }

    public void resume() {
        for (ProcessWebPageTask task : taskList) {
            task.resume();
        }
        mState = State.RUNNING;
    }

    public void printFoundStatus() {
        if (mError.isEmpty() && mUpdateStatusListener != null) {
            if (isIdle() || isPaused()) {
                if (foundTextList.isEmpty()) {
                    mUpdateStatusListener.updateStatus(mActivity.getString(R.string.notFound), mActivity.getResources().getColor(R.color.text_status_notFound_textColor));
                } else {
                    mUpdateStatusListener.updateStatus(mActivity.getString(R.string.found), mActivity.getResources().getColor(R.color.text_status_found_textColor));
                }
            }
            if (isRunning()) {
                mUpdateStatusListener.updateStatus(mActivity.getString(R.string.searching));
            }
            if (isStopped()) {
                mUpdateStatusListener.updateStatus(mActivity.getString(R.string.ready));
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

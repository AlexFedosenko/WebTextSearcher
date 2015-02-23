package com.alexander.webtextsearcher.searcher.core;

public interface UpdateProgressListener {

    public void incrementProgressList(String url);
    public void incrementProgress(String url);
    public void finishProgress();
    public void resetProgress();
}

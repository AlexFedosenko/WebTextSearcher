package com.alexander.webtextsearcher.searcher.core;

public class SearchController {

    private String mUrl = "";
    private String mTargetText = "";
    private Integer mThreadAmount;
    private Integer mUrlAmount;



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

    public void start() {
        new ProcessWebPageTask().execute(mUrl);
    }
}

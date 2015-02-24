package com.alexander.webtextsearcher.searcher.ui;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import com.alexander.webtextsearcher.searcher.core.SearchController;

public abstract class AbstractCustomFragment extends Fragment {

    protected SearchController mSearchController;
    protected ActionBar mActionBar;

    protected void setSearchController(SearchController searchController) {
        mSearchController = searchController;
    }

    public SearchController getSearchController() {
        return mSearchController;
    }

    public void setActionBar(ActionBar bar) {
        mActionBar = bar;
    }

    public ActionBar getActionBar() {
        return mActionBar;
    }

}

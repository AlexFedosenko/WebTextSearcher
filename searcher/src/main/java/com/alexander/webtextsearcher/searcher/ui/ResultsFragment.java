package com.alexander.webtextsearcher.searcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.core.ResultListAdapter;
import com.alexander.webtextsearcher.searcher.core.SearchController;
import com.alexander.webtextsearcher.searcher.core.UpdateResultListener;
import com.alexander.webtextsearcher.searcher.core.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultsFragment extends Fragment implements UpdateResultListener {

    private SearchController mSearchController;
    private ResultListAdapter mAdapter;

    private ListView vListResults;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.results_fragment, container, false);

        vListResults = (ListView) rootView.findViewById(R.id.list_results);
        setSearchController(((MainActivity)getActivity()).getSearchController());
        mAdapter = new ResultListAdapter(getActivity(), mSearchController.getFoundTextList());
        vListResults.setAdapter(mAdapter);
//        Utils.setListViewHeightBasedOnChildren(vListResults);

        return rootView;
    }

    private void setSearchController(SearchController searchController) {
        mSearchController = searchController;
        mSearchController.setUpdateResultListener(this);
    }

    @Override
    public void updateResult() {
        mAdapter.updateDataSet(mSearchController.getFoundTextList());
//        Utils.setListViewHeightBasedOnChildren(vListResults);
    }
}

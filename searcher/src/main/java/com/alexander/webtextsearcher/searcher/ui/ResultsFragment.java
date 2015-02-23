package com.alexander.webtextsearcher.searcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.core.SearchController;

public class ResultsFragment extends Fragment {

    private SearchController mSearchController;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.results_fragment, container, false);

        final ListView v = (ListView) rootView.findViewById(R.id.list_results);
        setSearchController(((MainActivity)getActivity()).getSearchController());
//        final MarkerListAdapter adapter = new MarkerListAdapter(getActivity(), DataManager.getInstance(getActivity()).getMarkerList(), vMarkerList);
//        vMarkerList.setAdapter(adapter);

        return rootView;
    }

    private void setSearchController(SearchController searchController) {
        mSearchController = searchController;
    }
}

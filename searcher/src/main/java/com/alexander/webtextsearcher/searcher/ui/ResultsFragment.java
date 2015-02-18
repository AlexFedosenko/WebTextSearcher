package com.alexander.webtextsearcher.searcher.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.alexander.webtextsearcher.searcher.R;

public class ResultsFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.results_fragment, container, false);

        final ListView vMarkerList = (ListView) rootView.findViewById(R.id.list_results);
//        final MarkerListAdapter adapter = new MarkerListAdapter(getActivity(), DataManager.getInstance(getActivity()).getMarkerList(), vMarkerList);
//        vMarkerList.setAdapter(adapter);

        return rootView;
    }
}

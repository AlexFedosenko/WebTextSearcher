package com.alexander.webtextsearcher.searcher.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.alexander.webtextsearcher.searcher.R;
import com.alexander.webtextsearcher.searcher.core.AsyncTask;
import com.alexander.webtextsearcher.searcher.core.SearchController;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = "MainActivity";

    private ActionBar mActionBar;
    private Fragment mCurrentMainActivityFragment;
    private Menu mMenu;
    private SearchController mSearchController;

    private View vActivityRootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSearchController = new SearchController(this);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                switch (tab.getPosition()) {
                    case 1:
                        final ResultsFragment resultsFragment = new ResultsFragment();
                        mCurrentMainActivityFragment = resultsFragment;
                        fragmentTransaction.replace(R.id.layout_pagesContainer, resultsFragment,
                                getString(R.string.results_fragmentTag));

//                        collectInputData();

                        break;
                    case 0:
                    default:
                        final ProgressFragment progressFragment = new ProgressFragment();
                        progressFragment.setSearchController(mSearchController);
                        mCurrentMainActivityFragment = progressFragment;
                        fragmentTransaction.replace(R.id.layout_pagesContainer, progressFragment,
                                getString(R.string.progress_fragmentTag));
                }
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };
        for (String tabName : getResources().getStringArray(R.array.navigationTabs)) {
            mActionBar.addTab(mActionBar.newTab().setText(tabName).setTabListener(tabListener));
        }

        vActivityRootView = findViewById(R.id.layout_pagesContainer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_start:
                collectInputData();
                if (mSearchController.isReadyForSearch()) {

                    AsyncTask.setCorePoolSize(mSearchController.getThreadAmount());
                    mSearchController.start();

                    item.setVisible(false);
                    mMenu.findItem(R.id.action_stop).setVisible(true);
                } else {
                    Toast.makeText(this, R.string.cannot_play_toast, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_stop:
                mSearchController.stop();
                item.setVisible(false);
                mMenu.findItem(R.id.action_start).setVisible(true);
            default:
                Log.w(LOG_TAG, "Unknown action");
        }
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void collectInputData() {
        if (mCurrentMainActivityFragment.getTag().equals(getString(R.string.progress_fragmentTag))) {
            ProgressFragment currentProgressFragment = (ProgressFragment) mCurrentMainActivityFragment;
            mSearchController.setUrl(currentProgressFragment.getUrl());
            mSearchController.setTargetText(currentProgressFragment.getTargetText());
            mSearchController.setThreadAmount(currentProgressFragment.getThreadAmount());
            mSearchController.setUrlAmount(currentProgressFragment.getUrlAmount());
        }
    }

    public void stopSearch() {
        mMenu.performIdentifierAction(R.id.action_stop, 0);
    }

}

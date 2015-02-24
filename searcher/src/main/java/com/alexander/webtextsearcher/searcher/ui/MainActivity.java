package com.alexander.webtextsearcher.searcher.ui;

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
import com.alexander.webtextsearcher.searcher.core.SearchController;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = "MainActivity";

    private ActionBar mActionBar;
    private AbstractCustomFragment mCurrentMainActivityFragment;
    private Menu mMenu;
    private SearchController mSearchController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getSupportFragmentManager().getFragments() != null && !getSupportFragmentManager().getFragments().isEmpty()) {
            mSearchController = ((AbstractCustomFragment)getSupportFragmentManager().getFragments().get(0)).getSearchController();
            mSearchController.setActivity(this);
            mActionBar = ((AbstractCustomFragment) getSupportFragmentManager().getFragments().get(0)).getActionBar();
            mCurrentMainActivityFragment = (AbstractCustomFragment)getSupportFragmentManager().getFragments().get(0);
        } else {
            mActionBar = getSupportActionBar();
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mSearchController = new SearchController(this);
        }

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                switch (tab.getPosition()) {
                    case 1:
                        final ResultsFragment resultsFragment = new ResultsFragment();
                        mCurrentMainActivityFragment = resultsFragment;
                        fragmentTransaction.replace(R.id.layout_pagesContainer, resultsFragment,
                                getString(R.string.results_fragmentTag));
                        mActionBar.getTabAt(1).setText(getResources().getStringArray(R.array.navigationTabs)[1]);
                        mCurrentMainActivityFragment.setActionBar(mActionBar);

                        collectInputData();
                        break;
                    case 0:
                    default:
                        final ProgressFragment progressFragment = new ProgressFragment();
                        mCurrentMainActivityFragment = progressFragment;
                        fragmentTransaction.replace(R.id.layout_pagesContainer, progressFragment,
                                getString(R.string.progress_fragmentTag));
                        mCurrentMainActivityFragment.setActionBar(mActionBar);
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        if (mSearchController.isRunning()) {
            mMenu.findItem(R.id.action_start).setVisible(false);
            mMenu.findItem(R.id.action_pause).setVisible(true);
            mMenu.findItem(R.id.action_stop).setVisible(true);
        }
        if (mSearchController.isIdle()) {
            mMenu.findItem(R.id.action_start).setVisible(true);
            mMenu.findItem(R.id.action_pause).setVisible(false);
            mMenu.findItem(R.id.action_stop).setVisible(false);
        }
        if (mSearchController.isPaused()) {
            mMenu.findItem(R.id.action_start).setVisible(true);
            mMenu.findItem(R.id.action_pause).setVisible(false);
            mMenu.findItem(R.id.action_stop).setVisible(true);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_start:
                collectInputData();
                if (mSearchController.isReadyForSearch()) {

                    mSearchController.start();

                    item.setVisible(false);
                    mMenu.findItem(R.id.action_pause).setVisible(true);
                    mMenu.findItem(R.id.action_stop).setVisible(true);

                    setEditFieldsEnabled(false);

                } else {
                    Toast.makeText(this, R.string.cannot_play_toast, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_pause:
                mSearchController.pause();
                item.setVisible(false);
                mMenu.findItem(R.id.action_start).setVisible(true);
                break;
            case R.id.action_stop:
                mSearchController.stop();
                item.setVisible(false);
                mMenu.findItem(R.id.action_start).setVisible(true);
                mMenu.findItem(R.id.action_pause).setVisible(false);

                setEditFieldsEnabled(true);
                break;
            default:
                Log.w(LOG_TAG, "Unknown action");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void collectInputData() {
        if (mCurrentMainActivityFragment.getTag().equals(getString(R.string.progress_fragmentTag))) {
            ProgressFragment currentProgressFragment = (ProgressFragment) mCurrentMainActivityFragment;
            mSearchController.setUrl(currentProgressFragment.getUrl());
            mSearchController.setTargetText(currentProgressFragment.getTargetText());
            mSearchController.setThreadAmount(currentProgressFragment.getThreadAmount());
            mSearchController.setUrlAmount(currentProgressFragment.getUrlAmount());
            mSearchController.setSearchInMeta(currentProgressFragment.isSearchInMeta());
        }
    }

    public void stopSearch() {
        mMenu.performIdentifierAction(R.id.action_stop, 0);
    }

    private void setEditFieldsEnabled(boolean enable) {
        if (mCurrentMainActivityFragment.getTag().equals(getString(R.string.progress_fragmentTag))) {
            ProgressFragment currentProgressFragment = (ProgressFragment) mCurrentMainActivityFragment;
            currentProgressFragment.setEditFieldsEnabled(enable);
        }
    }

    public SearchController getSearchController() {
        return mSearchController;
    }

    public void markUnseenResults() {
        mActionBar.getTabAt(1).setText(getResources().getStringArray(R.array.navigationTabs)[1] + "*");
    }

}

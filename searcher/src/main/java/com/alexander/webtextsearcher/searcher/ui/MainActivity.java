package com.alexander.webtextsearcher.searcher.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alexander.webtextsearcher.searcher.R;


public class MainActivity extends ActionBarActivity {

    private ActionBar mActionBar;
    private Fragment mCurrentMainActivityFragment;
    private View vActivityRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                switch (tab.getPosition()) {
                    case 1:
                        final ResultsFragment resultsFragment = new ResultsFragment();
                        mCurrentMainActivityFragment = resultsFragment;
                        fragmentTransaction.replace(R.id.layout_pagesContainer, resultsFragment,
                                getString(R.string.results_fragmentTag));
                        break;
                    case 0:
                    default:
                        final ProgressFragment progressFragment = new ProgressFragment();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}

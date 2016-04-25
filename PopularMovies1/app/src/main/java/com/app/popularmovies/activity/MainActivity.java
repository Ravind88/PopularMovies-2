package com.app.popularmovies.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;

import com.app.popularmovies.AppController;
import com.app.popularmovies.R;
import com.app.popularmovies.database.MoviesHelper;
import com.app.popularmovies.fragment.MoviesListFragment;
import com.app.popularmovies.model.MoviesListFilterEvent;
import com.app.popularmovies.utils.AppConstants;

import de.greenrobot.event.EventBus;

/**
 * This class contains all the list of movies . Also provide filter for most popular and highest rated movies.
 */
public class MainActivity extends BaseActivity  {
    private static final String TAG_MY_FRAGMENT = "mylistfragment";
    public boolean mTwoPane;
    MoviesListFragment moviesListFragment;
    @Override
    public int getLayoutById() {
        return R.layout.activity_main;
    }

    @Override
    public void initUi(Bundle savedInstanceState) {
        MoviesHelper.getDatabaseHelperInstance(AppController.getApplicationInstance());
        if (findViewById(R.id.item_detail_container) != null) {
         // for tab
            mTwoPane = true;
        }
        if(savedInstanceState==null){
            moviesListFragment = new MoviesListFragment();
            moviesListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_list_fragment, moviesListFragment,TAG_MY_FRAGMENT).commit();
        }else{
            moviesListFragment = (MoviesListFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_MY_FRAGMENT);

        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                EventBus.getDefault().post(new MoviesListFilterEvent(AppConstants.POPULARITY_DESC));
                return true;
            case R.id.highest_rated:
                EventBus.getDefault().post(new MoviesListFilterEvent(AppConstants.HIGHEST_RATED));
                return true;
            case R.id.my_favorites:
                EventBus.getDefault().post(new MoviesListFilterEvent(AppConstants.MY_FAVORITES));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getSupportFragmentManager().putFragment(outState, "TAG_MY_FRAGMENT", moviesListFragment);

    }

}

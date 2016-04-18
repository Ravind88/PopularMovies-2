package com.app.popularmovies.activity;

import android.view.Menu;
import android.view.MenuItem;

import com.app.popularmovies.AppController;
import com.app.popularmovies.R;
import com.app.popularmovies.database.MoviesHelper;
import com.app.popularmovies.model.MoviesListFilterEvent;
import com.app.popularmovies.utils.AppConstants;

import de.greenrobot.event.EventBus;

/**
 * This class contains all the list of movies . Also provide filter for most popular and highest rated movies.
 */
public class MainActivity extends BaseActivity  {

    public boolean mTwoPane;

    @Override
    public int getLayoutById() {
        return R.layout.activity_main;
    }

    @Override
    public void initUi() {
        MoviesHelper.getDatabaseHelperInstance(AppController.getApplicationInstance());
        if (findViewById(R.id.item_detail_container) != null) {
         // for tab
            mTwoPane = true;
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
}

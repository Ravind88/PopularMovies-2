package com.app.popularmovies.activity;

import android.view.MenuItem;

import com.app.popularmovies.R;
import com.app.popularmovies.fragment.MoviesDetailFragment;
import com.app.popularmovies.model.MoviesResponseBean;

/**
 * Created by Ravind maurya on 2/21/2016.
 * <p/>
 * This class provide full detail of the movies including rating, timing, description etc.
 * In this : using images thickness color for title, movie name and their background color.They render at run time on the basic
 * Poster's thickness of color
 */
public class MovieDetailActivity extends BaseActivity  {
    private MoviesResponseBean.MoviesResult moviesResult;

    @Override
    public int getLayoutById() {
        return R.layout.activity_movies_detail;
    }

    @Override
    public void initUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        MoviesDetailFragment moviesDetailFragment = new MoviesDetailFragment();
        moviesDetailFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, moviesDetailFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}

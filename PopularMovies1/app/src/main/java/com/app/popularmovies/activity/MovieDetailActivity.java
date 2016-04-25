package com.app.popularmovies.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
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
    private static final String TAG_MY_FRAGMENT = "mydetailsFragment";
    private MoviesResponseBean.MoviesResult moviesResult;
    MoviesDetailFragment moviesDetailFragment;
    @Override
    public int getLayoutById() {
        return R.layout.activity_movies_detail;
    }

    @Override
    public void initUi(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(savedInstanceState==null){
            moviesDetailFragment = new MoviesDetailFragment();
            moviesDetailFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, moviesDetailFragment,TAG_MY_FRAGMENT).commit();
        }else{
            moviesDetailFragment = (MoviesDetailFragment)getSupportFragmentManager().getFragment(savedInstanceState, TAG_MY_FRAGMENT);

        }

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


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getSupportFragmentManager().putFragment(outState, "TAG_MY_FRAGMENT", moviesDetailFragment);

    }
}

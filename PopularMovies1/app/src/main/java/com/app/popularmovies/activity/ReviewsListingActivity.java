package com.app.popularmovies.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.app.popularmovies.AppController;
import com.app.popularmovies.R;
import com.app.popularmovies.adapter.ReviewsListAdapter;
import com.app.popularmovies.model.ReviewsListingResponse;
import com.app.popularmovies.utils.AppConstants;
import com.app.popularmovies.utils.EndlessScrollListener;
import com.app.popularmovies.utils.Lg;
import com.app.popularmovies.utils.SnackBarBuilder;
import com.app.popularmovies.utils.Utility;
import com.app.popularmovies.webServices.ServerInteractor;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ravind maurya on 2/21/2016.
 */
public class ReviewsListingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mReviewsListView;
    private int mPagination = 1;
    private ProgressBar progressBar;
    EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            if (AppController.getApplicationInstance().isNetworkConnected()) {
                mPagination = mPagination + 1;
                getMovieReviews();
            } else
                mSnackBar = SnackBarBuilder.make(getWindow().getDecorView(), getString(R.string.no_internet_connction)).build();

        }
    };
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long movieId;
    private ArrayList<ReviewsListingResponse.ReviewsEntity> moviesReviewsList = new ArrayList<>();
    private ReviewsListAdapter mAdapter;

    @Override
    public int getLayoutById() {
        return R.layout.activity_reviews_listing;
    }

    @Override
    public void initUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        movieId = getIntent().getLongExtra(AppConstants.EXTRA_INTENT_PARCEL, 0);
        mReviewsListView = (ListView) findViewById(R.id.reviews_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        progressBar = Utility.getProgressBarInstance(this, R.id.circular_progress_bar);
        getMovieReviews();
    }

    private void getMovieReviews() {
        if (AppController.getApplicationInstance().isNetworkConnected()) {
            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put(AppConstants.PARAM_API_KEY, AppConstants.API_KEY);
            stringHashMap.put(AppConstants.PARAM_PAGE, mPagination + "");

            Call<ReviewsListingResponse> beanCall = ServerInteractor.getInstance().getApiServices().apiMovieReviews(movieId, stringHashMap);
            beanCall.enqueue(new Callback<ReviewsListingResponse>() {
                @Override
                public void onResponse(Response<ReviewsListingResponse> response1, Retrofit retrofit) {
                    progressBar.setVisibility(View.GONE);
                    ReviewsListingResponse responseBean = response1.body();
                    if (responseBean != null) {
                        moviesReviewsList.addAll(responseBean.getResults());

                        if (mPagination == 1) {
                            mReviewsListView.setOnScrollListener(mEndlessScrollListener);
                        }

                        if (mAdapter == null) {
                            mAdapter = new ReviewsListAdapter(ReviewsListingActivity.this, moviesReviewsList);
                            mReviewsListView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        if (responseBean.getResults().size() == 0) {
                            mReviewsListView.setOnScrollListener(null);
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Lg.i("Retro", t.toString());
                }
            });
        } else {
            mSnackBar = SnackBarBuilder.make(mParent, getString(R.string.no_internet_connction))
                    .setActionText(getString(R.string.retry))
                    .onSnackBarClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMovieReviews();
                        }
                    })
                    .build();
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
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (AppController.getApplicationInstance().isNetworkConnected()) {
            mPagination = 1;
            mReviewsListView.setOnScrollListener(null);
            moviesReviewsList.clear();
            try {
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
            } catch (Exception ignored) {
            }

            getMovieReviews();
        } else {
            mSnackBar = SnackBarBuilder.make(getWindow().getDecorView(), getString(R.string.no_internet_connction)).build();
        }
    }
}

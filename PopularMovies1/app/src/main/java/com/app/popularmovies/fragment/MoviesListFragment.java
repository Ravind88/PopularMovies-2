package com.app.popularmovies.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.popularmovies.AppController;
import com.app.popularmovies.R;
import com.app.popularmovies.activity.MainActivity;
import com.app.popularmovies.activity.MovieDetailActivity;
import com.app.popularmovies.adapter.MovieListAdapter;
import com.app.popularmovies.database.MoviesListingDao;
import com.app.popularmovies.model.MoviesListFilterEvent;
import com.app.popularmovies.model.MoviesResponseBean;
import com.app.popularmovies.utils.AppConstants;
import com.app.popularmovies.utils.EndlessScrollListener;
import com.app.popularmovies.utils.Lg;
import com.app.popularmovies.utils.SnackBarBuilder;
import com.app.popularmovies.webServices.ServerInteractor;

import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ravind maurya on 2/21/2016.
 */
public class MoviesListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private GridView mGridView;
    static final String STATE_SCROLL_POSITION = "scrollPosition";
    int savedPosition = 0;
    private ArrayList<MoviesResponseBean.MoviesResult> moviesResultsList = new ArrayList<>();
    private int mPagination = 1;
    MoviesDetailFragment moviesDetailFragment;
    MoviesResponseBean responseBean;
    EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            if (AppController.getApplicationInstance().isNetworkConnected()) {
                mPagination = mPagination + 1;
                getMoviesList(View.GONE);
            } else
                mSnackBar = SnackBarBuilder.make(mGridView, getString(R.string.no_internet_connction)).build();

        }
    };
    private MovieListAdapter mAdapter;
    private String mSortByParam = AppConstants.POPULARITY_DESC;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isSortApplied;
    private Bundle savedState = null;

    @Override
    public int getLayoutById() {
        return R.layout.fragment_movies_list;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initUi(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mGridView = (GridView) findViewById(R.id.popular_movies_gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity moviesListActivity = (MainActivity) getActivity();
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.EXTRA_INTENT_PARCEL, moviesResultsList.get(position));

                if (moviesListActivity.mTwoPane) {
                    moviesDetailFragment = new MoviesDetailFragment();
                    moviesDetailFragment.setArguments(bundle);
                    moviesListActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, moviesDetailFragment).commit();

                } else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle("listmovie");
        }
        if (savedState == null) {

        }

        synchronized (this) {
            getMoviesList(View.VISIBLE);
        }

        savedState = null;
    }


    private void getMoviesList(int progressBarVisibility) {
        if (mSortByParam.equals(AppConstants.MY_FAVORITES)) {
            MoviesListingDao moviesListingDao = new MoviesListingDao(mContext);
            mGridView.setOnScrollListener(null);
            mAdapter = new MovieListAdapter(mContext, moviesListingDao.getFavouriteMovieList());
            mGridView.setAdapter(mAdapter);


            showProgressBar(false);
        } else if (savedState == null) {

            if (AppController.getApplicationInstance().isNetworkConnected()) {
                showProgressBar(true);

                HashMap<String, String> stringHashMap = new HashMap<>();
                stringHashMap.put(AppConstants.PARAM_SORT_BY, mSortByParam);
                stringHashMap.put(AppConstants.PARAM_API_KEY, AppConstants.API_KEY);
                stringHashMap.put(AppConstants.PARAM_PAGE, mPagination + "");

                Call<MoviesResponseBean> beanCall = ServerInteractor.getInstance().getApiServices().apiMoviesList(stringHashMap);
                beanCall.enqueue(new Callback<MoviesResponseBean>() {
                    @Override
                    public void onResponse(Response<MoviesResponseBean> response, Retrofit retrofit) {
                        showProgressBar(false);
                        responseBean = response.body();
                        if (responseBean != null) {

                            moviesResultsList.addAll(responseBean.getResults());

                            if (mPagination == 1) {
                                mGridView.setOnScrollListener(mEndlessScrollListener);
                            }

                            if (mAdapter == null) {
                                mAdapter = new MovieListAdapter(mContext, moviesResultsList);
                                mGridView.setAdapter(mAdapter);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }
                            if (responseBean.getResults().size() == 0) {
                                mGridView.setOnScrollListener(null);
                            }

//            if (responseBean.getResults().isEmpty())
//                Lg.i("Retro", responseBean.toString());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Lg.i("Retro", t.toString());
                    }
                });
            } else {
                showProgressBar(false);
                mSnackBar = SnackBarBuilder.make(mGridView, getString(R.string.no_internet_connction))
                        .setActionText(getString(R.string.retry))
                        .onSnackBarClicked(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getMoviesList(View.VISIBLE);
                            }
                        })
                        .build();
            }

        } else {
            setMovieList((MoviesResponseBean) savedState.getParcelable("list"));

        }


    }

    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putParcelable("list", responseBean);
        //
        return state;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* If onDestroyView() is called first, we can use the previously savedState but we can't call saveState() anymore */
        /* If onSaveInstanceState() is called first, we don't have savedState, so we need to call saveState() */
        /* => (?:) operator inevitable! */
        int currentPosition = mGridView.getFirstVisiblePosition();
        outState.putInt(STATE_SCROLL_POSITION,
                currentPosition);
        outState.putBundle("listmovie", (savedState != null) ? savedState : saveState());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = saveState(); /* vstup defined here for sure */
    }

    private void setMovieList(MoviesResponseBean responseBean) {

        showProgressBar(false);

        if (responseBean != null) {
            moviesResultsList.clear();
            moviesResultsList.addAll(responseBean.getResults());

            if (mPagination == 1) {
                mGridView.setOnScrollListener(mEndlessScrollListener);
            }
            savedPosition = savedState
                    .getInt(STATE_SCROLL_POSITION);

            mAdapter = new MovieListAdapter(mContext, moviesResultsList);
            mGridView.setAdapter(mAdapter);
            mGridView.setSelection(savedPosition);
            if (responseBean.getResults().size() == 0) {
                mGridView.setOnScrollListener(null);
            }

//            if (responseBean.getResults().isEmpty())
//                Lg.i("Retro", responseBean.toString());
        }

    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (AppController.getApplicationInstance().isNetworkConnected()) {
            mPagination = 1;
            mGridView.setOnScrollListener(null);
            moviesResultsList.clear();
            try {
                mAdapter.notifyDataSetChanged();
                mAdapter = null;
            } catch (Exception ignored) {
            }

            if (isSortApplied) {
                isSortApplied = false;
            } else {
                mSortByParam = AppConstants.POPULARITY_DESC;
            }
            getMoviesList(View.VISIBLE);
        } else {
            showProgressBar(false);
            mSnackBar = SnackBarBuilder.make(mGridView, getString(R.string.no_internet_connction)).build();
        }
    }

    public void onEvent(MoviesListFilterEvent filterEvent) {
        isSortApplied = true;
        mSortByParam = filterEvent.getFilter();

        onRefresh();
        removeYourFragment();
    }

    public void removeYourFragment() {
        if (((MainActivity) getActivity()).mTwoPane) {
            FragmentTransaction transaction = ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction();
            if (moviesDetailFragment != null) {
                transaction.remove(moviesDetailFragment);
                transaction.commit();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                moviesDetailFragment = null;
            }
        }

    }

}

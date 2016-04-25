package com.app.popularmovies.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.popularmovies.AppController;
import com.app.popularmovies.R;
import com.app.popularmovies.activity.ReviewsListingActivity;
import com.app.popularmovies.adapter.TrailersAdapter;
import com.app.popularmovies.database.MoviesListingDao;
import com.app.popularmovies.model.MoviesResponseBean;
import com.app.popularmovies.model.ReviewsListingResponse;
import com.app.popularmovies.model.TrailersResponseBean;
import com.app.popularmovies.utils.AppConstants;
import com.app.popularmovies.utils.Lg;
import com.app.popularmovies.utils.SnackBarBuilder;
import com.app.popularmovies.utils.SquareImageView;
import com.app.popularmovies.utils.Utility;
import com.app.popularmovies.webServices.ServerInteractor;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by ravind maurya on 2/21/2016.
 */
public class MoviesDetailFragment extends BaseFragment implements View.OnClickListener {
    private MoviesResponseBean.MoviesResult moviesResult;
    private ProgressBar mTrailersProgressBar;
    private RecyclerView mTrailersRecyclerView;
    private LinearLayout reviewsContainer;
    private TextView seeMoreReviews;
    private Bundle savedState = null;
    MoviesResponseBean.MoviesResult moviesResult2;
    private TrailersResponseBean responseBean;
    private ReviewsListingResponse reviewResponseBean;
    private TextView movieName, movieDescTv;
    private  SquareImageView movieImageView;

    @Override
    public int getLayoutById() {
        return R.layout.fragment_movies_detail;
    }


    @Override
    public void initUi(Bundle savedInstanceState) {


        movieName = (TextView) findViewById(R.id.movie_name);
        movieDescTv = (TextView) findViewById(R.id.movie_desc);
        movieImageView = (SquareImageView) findViewById(R.id.movie_image);
        mTrailersProgressBar = Utility.getProgressBarInstance(mContext, R.id.trailer_progress_bar);
        mTrailersProgressBar = (ProgressBar) findViewById(R.id.trailer_progress_bar);
        mTrailersProgressBar.setIndeterminateDrawable(new IndeterminateProgressDrawable(mContext));
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.movie_detail_trailers_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTrailersRecyclerView.setLayoutManager(linearLayoutManager);
        reviewsContainer = (LinearLayout) findViewById(R.id.reviews_listing_parent);
        TextView markFavoriteTv = (TextView) findViewById(R.id.mark_favorite);
        markFavoriteTv.setOnClickListener(this);
        seeMoreReviews = (TextView) findViewById(R.id.see_more_reviews);
        seeMoreReviews.setOnClickListener(this);
        seeMoreReviews.setVisibility(View.GONE);


        if (savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle("detail");
        }
        if (savedState != null) {
            mTrailersProgressBar.setVisibility(View.GONE);
            setHeaderData((MoviesResponseBean.MoviesResult) savedState.getParcelable("headerdata"));
            setTagLine((MoviesResponseBean.MoviesResult) savedState.getParcelable("tagline"));
            setTrailer((TrailersResponseBean) savedState.getParcelable("trailer"));
            setreview((ReviewsListingResponse) savedState.getParcelable("review"));
            setFavoriteText();
        } else {
            moviesResult = getArguments().getParcelable(AppConstants.EXTRA_INTENT_PARCEL);
            setHeaderData(moviesResult);
            getMoviesDetail();
            getMovieTrailers();
            getMovieReviews();
            setFavoriteText();
        }
        savedState = null;


    }

    private void setHeaderData(MoviesResponseBean.MoviesResult moviesResult) {


        Utility.setText(movieName, moviesResult.getTitle());
        if (TextUtils.isEmpty(moviesResult.getOverview())) {
            movieDescTv.setVisibility(View.GONE);
        }
        Utility.setText(movieDescTv, moviesResult.getOverview());

        String formattedDate = Utility.parseDateTime(moviesResult.getReleaseDate(), AppConstants.DATE_FORMAT1
                , AppConstants.DATE_FORMAT2);
        Utility.setText((TextView) findViewById(R.id.movie_release_year), formattedDate);
        Utility.setText((TextView) findViewById(R.id.movie_rating), moviesResult.getVoteAverage() + " /10");



        if (!TextUtils.isEmpty(moviesResult.getPosterPath()))
            Picasso.with(mContext)
                    .load(AppConstants.BASE_THUMB_IMAGE_URL + moviesResult.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            movieImageView.setImageBitmap(bitmap);

                            // Asynchronous
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette p) {
                                    // Use generated instance
                                    Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
                                    if (vibrantSwatch != null) {
                                        movieName.setTextColor(vibrantSwatch.getTitleTextColor());
                                        ((LinearLayout) movieName.getParent()).setBackgroundColor(vibrantSwatch.getRgb());
                                    }

                                    int defaultColor = getResources().getColor(R.color.colorPrimary);
                                    setToolBarColor(p.getLightVibrantColor(defaultColor));
                                    setToolBarTextColor(p.getDarkMutedColor(defaultColor));


                                    Lg.i("Palette", p.toString());
                                }
                            });


                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        else {
            movieImageView.setImageResource(R.drawable.placeholder);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = saveState(); /* vstup defined here for sure */
    }


    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putParcelable("tagline", moviesResult2);
        state.putParcelable("trailer", responseBean);
        state.putParcelable("review", reviewResponseBean);
        state.putParcelable("headerdata", moviesResult);
        //
        return state;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* If onDestroyView() is called first, we can use the previously savedState but we can't call saveState() anymore */
        /* If onSaveInstanceState() is called first, we don't have savedState, so we need to call saveState() */
        /* => (?:) operator inevitable! */
        outState.putBundle("detail", (savedState != null) ? savedState : saveState());
    }


    private void setFavoriteText() {
        MoviesListingDao moviesListingDao = new MoviesListingDao(mContext);
        if (moviesListingDao.isMovieFavourite(moviesResult)) {
            Utility.setText((TextView) findViewById(R.id.mark_favorite), getString(R.string.mark_unfavorite));
        } else {
            Utility.setText((TextView) findViewById(R.id.mark_favorite), getString(R.string.mark_favorite));
        }
    }

    private void getMoviesDetail() {
        if (AppController.getApplicationInstance().isNetworkConnected()) {
            showProgressDialog(false);
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put(AppConstants.PARAM_API_KEY, AppConstants.API_KEY);

            Call<MoviesResponseBean.MoviesResult> beanCall = ServerInteractor.getInstance().getApiServices().apiMoviesDetail(moviesResult.getId(), stringHashMap);
            beanCall.enqueue(new Callback<MoviesResponseBean.MoviesResult>() {
                @Override
                public void onResponse(Response<MoviesResponseBean.MoviesResult> response, Retrofit retrofit) {
                    showProgressDialog(false);
                    moviesResult2 = response.body();
                    if (moviesResult2 != null) {
                        setTagLine(moviesResult2);

                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    showProgressDialog(false);
                    Lg.i("Retro", t.toString());
                }
            });
        } else {
            mSnackBar = SnackBarBuilder.make(mParent, getString(R.string.no_internet_connction))
                    .setActionText(getString(R.string.retry))
                    .onSnackBarClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMoviesDetail();
                        }
                    })
                    .build();
        }
    }

    private void setTagLine(MoviesResponseBean.MoviesResult moviesResult2) {

        Utility.setText((TextView) findViewById(R.id.movie_runtime), moviesResult2.getRuntime() + " min");
        Utility.setText((TextView) findViewById(R.id.movie_tagline), moviesResult2.getTagLine());


    }

    private void getMovieTrailers() {
        findViewById(R.id.trailer_list_parent).setVisibility(View.GONE);
        if (AppController.getApplicationInstance().isNetworkConnected() && isAdded()) {
            mTrailersProgressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put(AppConstants.PARAM_API_KEY, AppConstants.API_KEY);
            Call<TrailersResponseBean> beanCall = ServerInteractor.getInstance().getApiServices().apiMovieTrailers(moviesResult.getId(), stringHashMap);
            beanCall.enqueue(new Callback<TrailersResponseBean>() {
                @Override
                public void onResponse(Response<TrailersResponseBean> response1, Retrofit retrofit) {
                    mTrailersProgressBar.setVisibility(View.GONE);
                    responseBean = response1.body();
                    setTrailer(responseBean);
                }

                @Override
                public void onFailure(Throwable t) {
                    mTrailersProgressBar.setVisibility(View.GONE);
                    Lg.i("Retro", t.toString());
                }
            });
        } else {
            mSnackBar = SnackBarBuilder.make(mParent, getString(R.string.no_internet_connction))
                    .setActionText(getString(R.string.retry))
                    .onSnackBarClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMoviesDetail();
                        }
                    })
                    .build();
        }
    }

    private void setTrailer(TrailersResponseBean responseBean) {

        if (responseBean != null && responseBean.getResults() != null && !responseBean.getResults().isEmpty()) {
            TrailersAdapter trailersAdapter = new TrailersAdapter(mContext, responseBean.getResults());
            mTrailersRecyclerView.setAdapter(trailersAdapter);
            findViewById(R.id.trailer_list_parent).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.trailer_list_parent).setVisibility(View.GONE);
        }


    }

    private void getMovieReviews() {
        if (AppController.getApplicationInstance().isNetworkConnected() && isAdded()) {
            mTrailersProgressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put(AppConstants.PARAM_API_KEY, AppConstants.API_KEY);
            Call<ReviewsListingResponse> beanCall = ServerInteractor.getInstance().getApiServices().apiMovieReviews(moviesResult.getId(), stringHashMap);
            beanCall.enqueue(new Callback<ReviewsListingResponse>() {
                @Override
                public void onResponse(Response<ReviewsListingResponse> response1, Retrofit retrofit) {
                    reviewResponseBean = response1.body();
                    setreview(reviewResponseBean);
                }

                @Override
                public void onFailure(Throwable t) {
                    mTrailersProgressBar.setVisibility(View.GONE);
                    Lg.i("Retro", t.toString());
                }
            });
        } else {
            mSnackBar = SnackBarBuilder.make(mParent, getString(R.string.no_internet_connction))
                    .setActionText(getString(R.string.retry))
                    .onSnackBarClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMoviesDetail();
                        }
                    })
                    .build();
        }
    }

    private void setreview(ReviewsListingResponse reviewResponseBean) {

        if (reviewResponseBean != null) {
            ArrayList<ReviewsListingResponse.ReviewsEntity> reviewsEntities = reviewResponseBean.getResults();
            if (reviewsEntities != null && !reviewsEntities.isEmpty()) {
                addReviews(reviewResponseBean.getResults());
            } else {
                reviewsContainer.setVisibility(View.GONE);
            }
        }


    }

    private void addReviews(ArrayList<ReviewsListingResponse.ReviewsEntity> resultsEntityArrayList) {
        ArrayList<ReviewsListingResponse.ReviewsEntity> results;
        if (resultsEntityArrayList.size() > 3) {
            seeMoreReviews.setVisibility(View.VISIBLE);
            results = new ArrayList<>(resultsEntityArrayList.subList(0, 3));
        } else {
            results = resultsEntityArrayList;
        }
        for (int i = 0; i < results.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_reviews_row, null);
            TextView reviewContentTv = (TextView) view.findViewById(R.id.review_content_tv);
            TextView reviewAuthorTv = (TextView) view.findViewById(R.id.review_author_tv);
            reviewContentTv.setText(results.get(i).getContent());
            reviewAuthorTv.setText(results.get(i).getAuthor());
            reviewsContainer.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mark_favorite:
                MoviesListingDao moviesListingDao = new MoviesListingDao(mContext);
                boolean isMovieFavorited = moviesListingDao.toggleFavouriteMovie(moviesResult);
                if (isMovieFavorited)
                    mSnackBar = SnackBarBuilder.make(mParent, moviesResult.getTitle() +
                            mContext.getString(R.string.add_to_fav)).build();
                else {
                    mSnackBar = SnackBarBuilder.make(mParent, moviesResult.getTitle() +
                            mContext.getString(R.string.removed_from_favourites)).build();
                }
                setFavoriteText();
                break;
            case R.id.see_more_reviews:
                Intent intent = new Intent(mContext, ReviewsListingActivity.class);
                intent.putExtra(AppConstants.EXTRA_INTENT_PARCEL, moviesResult.getId());
                startActivity(intent);
                break;
        }
    }
}

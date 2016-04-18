package com.app.popularmovies.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

    @Override
    public int getLayoutById() {
        return R.layout.fragment_movies_detail;
    }

    @Override
    public void initUi() {
        moviesResult = getArguments().getParcelable(AppConstants.EXTRA_INTENT_PARCEL);
        final TextView movieName = (TextView) findViewById(R.id.movie_name);
        Utility.setText(movieName, moviesResult.getTitle());
        TextView movieDescTv = (TextView) findViewById(R.id.movie_desc);
        Utility.setText(movieDescTv, moviesResult.getOverview());
        if (TextUtils.isEmpty(moviesResult.getOverview())) {
            movieDescTv.setVisibility(View.GONE);
        }
        String formattedDate = Utility.parseDateTime(moviesResult.getReleaseDate(), AppConstants.DATE_FORMAT1
                , AppConstants.DATE_FORMAT2);
        Utility.setText((TextView) findViewById(R.id.movie_release_year), formattedDate);
        Utility.setText((TextView) findViewById(R.id.movie_rating), moviesResult.getVoteAverage() + " /10");
        final SquareImageView movieImageView = (SquareImageView) findViewById(R.id.movie_image);
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
        getMoviesDetail();
        getMovieTrailers();
        getMovieReviews();

        setFavoriteText();
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
                    MoviesResponseBean.MoviesResult moviesResult2 = response.body();
                    if (moviesResult2 != null) {
                        Utility.setText((TextView) findViewById(R.id.movie_runtime), moviesResult2.getRuntime() + " min");
                        Utility.setText((TextView) findViewById(R.id.movie_tagline), moviesResult2.getTagLine());
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
                    TrailersResponseBean responseBean = response1.body();
                    if (responseBean != null && responseBean.getResults() != null && !responseBean.getResults().isEmpty()) {
                        TrailersAdapter trailersAdapter = new TrailersAdapter(mContext, responseBean.getResults());
                        mTrailersRecyclerView.setAdapter(trailersAdapter);
                        findViewById(R.id.trailer_list_parent).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.trailer_list_parent).setVisibility(View.GONE);
                    }
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

    private void getMovieReviews() {
        if (AppController.getApplicationInstance().isNetworkConnected() && isAdded()) {
            mTrailersProgressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> stringHashMap = new HashMap<>();
            stringHashMap.put(AppConstants.PARAM_API_KEY, AppConstants.API_KEY);
            Call<ReviewsListingResponse> beanCall = ServerInteractor.getInstance().getApiServices().apiMovieReviews(moviesResult.getId(), stringHashMap);
            beanCall.enqueue(new Callback<ReviewsListingResponse>() {
                @Override
                public void onResponse(Response<ReviewsListingResponse> response1, Retrofit retrofit) {
                    ReviewsListingResponse responseBean = response1.body();
                    if (responseBean != null) {
                        ArrayList<ReviewsListingResponse.ReviewsEntity> reviewsEntities = responseBean.getResults();
                        if (reviewsEntities != null && !reviewsEntities.isEmpty()) {
                            addReviews(responseBean.getResults());
                        } else {
                            reviewsContainer.setVisibility(View.GONE);
                        }
                    }
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

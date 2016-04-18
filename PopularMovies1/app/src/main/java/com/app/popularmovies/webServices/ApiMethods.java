package com.app.popularmovies.webServices;


import com.app.popularmovies.model.MoviesResponseBean;
import com.app.popularmovies.model.ReviewsListingResponse;
import com.app.popularmovies.model.TrailersResponseBean;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by ravind maurya on 9/9/15.
 */
public interface ApiMethods {

    @GET("discover/movie")
    Call<MoviesResponseBean> apiMoviesList(@QueryMap Map<String, String> stringMap);


    @GET("movie/{movie_id}?")
    Call<MoviesResponseBean.MoviesResult> apiMoviesDetail(@Path("movie_id") long taskId, @QueryMap Map<String, String> stringMap);

    @GET("movie/{movie_id}/videos?")
    Call<TrailersResponseBean> apiMovieTrailers(@Path("movie_id") long movieId, @QueryMap Map<String, String> stringMap);

    @GET("movie/{movie_id}/reviews?")
    Call<ReviewsListingResponse> apiMovieReviews(@Path("movie_id") long movieId, @QueryMap Map<String, String> stringMap);


}

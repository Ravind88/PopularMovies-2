package com.app.popularmovies.model;

/**
 * Created by ravind maurya on 12/3/16.
 */
public class MoviesListFilterEvent {

    private String filter;

    public MoviesListFilterEvent(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }
}

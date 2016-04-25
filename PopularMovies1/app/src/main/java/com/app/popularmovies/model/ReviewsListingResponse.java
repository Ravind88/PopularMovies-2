package com.app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ravind maurya on 3/25/2016.
 */
public class ReviewsListingResponse implements Parcelable {
    long id, page, total_pages, total_results;

    ArrayList<ReviewsEntity> results;

    protected ReviewsListingResponse(Parcel in) {
        id = in.readLong();
        page = in.readLong();
        total_pages = in.readLong();
        total_results = in.readLong();
    }

    public static final Creator<ReviewsListingResponse> CREATOR = new Creator<ReviewsListingResponse>() {
        @Override
        public ReviewsListingResponse createFromParcel(Parcel in) {
            return new ReviewsListingResponse(in);
        }

        @Override
        public ReviewsListingResponse[] newArray(int size) {
            return new ReviewsListingResponse[size];
        }
    };

    public long getId() {
        return id;
    }

    public long getPage() {
        return page;
    }

    public long getTotal_pages() {
        return total_pages;
    }

    public long getTotal_results() {
        return total_results;
    }

    public ArrayList<ReviewsEntity> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(page);
        dest.writeLong(total_pages);
        dest.writeLong(total_results);
    }

    public static class ReviewsEntity {
        String id, author, content, url;

        public String getId() {
            return id;
        }

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            return url;
        }
    }
}

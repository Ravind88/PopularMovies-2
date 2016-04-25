package com.app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ravind maurya on 9/2/16.
 */
public class MoviesResponseBean implements Parcelable {

    private int page;

    @SerializedName("total_results")
    private long totalResults;

    @SerializedName("total_pages")
    private long totalPages;

    ArrayList<MoviesResult> results;

    protected MoviesResponseBean(Parcel in) {
        page = in.readInt();
        totalResults = in.readLong();
        totalPages = in.readLong();
        results = in.createTypedArrayList(MoviesResult.CREATOR);
    }

    public static final Creator<MoviesResponseBean> CREATOR = new Creator<MoviesResponseBean>() {
        @Override
        public MoviesResponseBean createFromParcel(Parcel in) {
            return new MoviesResponseBean(in);
        }

        @Override
        public MoviesResponseBean[] newArray(int size) {
            return new MoviesResponseBean[size];
        }
    };

    public int getPage() {
        return page;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public ArrayList<MoviesResult> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeLong(totalResults);
        dest.writeLong(totalPages);
        dest.writeTypedList(results);
    }

    public static class MoviesResult implements Parcelable {

        @SerializedName("poster_path")
        private String posterPath;

        private String overview;

        @SerializedName("release_date")
        private String releaseDate;

        private long id;
        private String isFavorite;
        private String title;

        @SerializedName("vote_average")
        private double voteAverage;

        private double runtime;

        @SerializedName("tagline")
        private String tagLine;

        public static final Creator<MoviesResult> CREATOR = new Creator<MoviesResult>() {
            @Override
            public MoviesResult createFromParcel(Parcel in) {
                return new MoviesResult(in);
            }

            @Override
            public MoviesResult[] newArray(int size) {
                return new MoviesResult[size];
            }
        };

        public MoviesResult() {

        }

        public String getPosterPath() {
            if (TextUtils.isEmpty(posterPath))
                return "";
            return posterPath;
        }

        public String getOverview() {
            return overview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public double getVoteAverage() {
            return voteAverage;
        }

        public String getIsFavorite() {
            if (TextUtils.isEmpty(isFavorite))
                return "0";
            return isFavorite;
        }

        public void setIsFavorite(String isFavorite) {
            this.isFavorite = isFavorite;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setVoteAverage(double voteAverage) {
            this.voteAverage = voteAverage;
        }

        public String getTagLine() {
            if (TextUtils.isEmpty(tagLine))
                return "";
            return tagLine;
        }

        public double getRuntime() {
            return runtime;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(posterPath);
            parcel.writeString(overview);
            parcel.writeString(releaseDate);
            parcel.writeLong(id);
            parcel.writeString(title);
            parcel.writeDouble(voteAverage);
            parcel.writeDouble(runtime);
            parcel.writeString(tagLine);
            parcel.writeString(isFavorite);
        }

        public MoviesResult(Parcel in) {
            posterPath = in.readString();
            overview = in.readString();
            releaseDate = in.readString();
            id = in.readLong();
            title = in.readString();
            voteAverage = in.readDouble();
            runtime = in.readDouble();
            tagLine = in.readString();
            isFavorite = in.readString();
        }

    }
}

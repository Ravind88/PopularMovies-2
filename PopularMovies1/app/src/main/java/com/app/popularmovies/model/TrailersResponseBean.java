package com.app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ravind maurya on 3/25/2016.
 */
public class TrailersResponseBean implements Parcelable {

    long id;
    ArrayList<ResultEntity> results;

    protected TrailersResponseBean(Parcel in) {
        id = in.readLong();
    }

    public static final Creator<TrailersResponseBean> CREATOR = new Creator<TrailersResponseBean>() {
        @Override
        public TrailersResponseBean createFromParcel(Parcel in) {
            return new TrailersResponseBean(in);
        }

        @Override
        public TrailersResponseBean[] newArray(int size) {
            return new TrailersResponseBean[size];
        }
    };

    public ArrayList<ResultEntity> getResults() {
        return results;
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
    }

    public static class ResultEntity {
        String id, iso_639_1, iso_3166_1, key, name, site, size, type;

        public String getId() {
            return id;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public String getIso_3166_1() {
            return iso_3166_1;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getSite() {
            return site;
        }

        public String getSize() {
            return size;
        }

        public String getType() {
            return type;
        }
    }
}

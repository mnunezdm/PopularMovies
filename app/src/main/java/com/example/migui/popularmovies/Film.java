package com.example.migui.popularmovies;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


class Film implements Parcelable {
    private String title;
    private String origTitle;
    private String overview;
    private double voteAverage;
    private int voteNumber;
    private String imageURL;
    private String releaseDate;

    Film(JSONObject json) throws JSONException {
        title = json.getString("title");
        origTitle = json.getString("original_title");
        overview = json.getString("overview");
        releaseDate = json.getString("release_date");
        voteNumber = json.getInt("vote_count");
        voteAverage = json.getDouble("vote_average");
        imageURL = NetworkUtils.IMAGE_BASE_URL + json.getString("poster_path");
    }

    // ************ Getters ************

    String getTitle() {
        return title;
    }

    String getOrigTitle() {
        if (title.equals(origTitle))
            return null;
        return origTitle;
    }

    String getOverview() {
        return overview;
    }

    String getImageURL() {
        return imageURL;
    }

    String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return title;
    }

    // Parcelable Methods

    private Film(Parcel in) {
        title = in.readString();
        origTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        voteNumber = in.readInt();
        imageURL = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(origTitle);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeInt(voteNumber);
        dest.writeString(imageURL);
        dest.writeString(releaseDate);
    }

    public static final Creator<Film> CREATOR = new Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };

    String getRating() {
        return voteAverage + "/10 (" + voteNumber +")";
    }
}

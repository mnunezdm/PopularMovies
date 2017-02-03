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
    private double vote_average;
    private String image;
    private String release_date;

    Film(JSONObject json) throws JSONException {
        title = json.getString("title");
        origTitle = json.getString("original_title");
        overview = json.getString("overview");
        release_date = json.getString("release_date");
        vote_average = json.getDouble("vote_average");
        image = NetworkUtils.IMAGE_BASE_URL + json.getString("poster_path");
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

    ImageView getImageView(Context context) {
        ImageView imageView = new ImageView(context);
        Picasso.with(context).load(image).into(imageView);
        return imageView;
    }

    String getRelease_date() {
        return release_date;
    }

    double getVote_average() {
        return vote_average;
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
        vote_average = in.readDouble();
        image = in.readString();
        release_date = in.readString();
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
        dest.writeDouble(vote_average);
        dest.writeString(image);
        dest.writeString(release_date);
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
}

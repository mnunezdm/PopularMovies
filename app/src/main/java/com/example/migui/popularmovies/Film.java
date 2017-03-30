package com.example.migui.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.migui.popularmovies.data.MovieContract.MovieEntry;

class Film {

    private long id;
    private String title;
    private String origTitle;
    private String overview;
    private double voteAverage;
    private int voteNumber;
    private String posterPath;
    private String releaseDate;
    private boolean favourite;

    Film(JSONObject json) throws JSONException {
        id = json.getLong("id");
        title = json.getString("title");
        origTitle = json.getString("original_title");
        overview = json.getString("overview");
        releaseDate = json.getString("release_date");
        voteNumber = json.getInt("vote_count");
        voteAverage = json.getDouble("vote_average");
        posterPath = json.getString("poster_path");
    }

    Film(Cursor cursor) {
        cursor.moveToFirst();
        id = cursor.getLong(cursor.getColumnIndex(MovieEntry._ID));
        title =  cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
        origTitle =  cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE));
        overview =  cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
        voteAverage =  cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
        voteNumber =  cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_NUMBER));
        posterPath =  cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_FRONT_POSTER));
        releaseDate =  cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
        favourite = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FAVOURITE)) != 0;
    }

    // *************** Getters ***************


    String getTitle() {
        return title;
    }

    String getOrigTitle() {
        return origTitle;
    }

    String getOverview() {
        return overview;
    }

    String getPosterPath() {
        return posterPath;
    }

    String getReleaseDate() {
        return releaseDate;
    }

    long getId() {

        return id;
    }

    public boolean isFavourite() {
        return favourite;
    }

    ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry._ID, id);
        cv.put(MovieEntry.COLUMN_TITLE, title);
        cv.put(MovieEntry.COLUMN_ORIGINAL_TITLE, origTitle);
        cv.put(MovieEntry.COLUMN_OVERVIEW, overview);
        cv.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        cv.put(MovieEntry.COLUMN_VOTE_NUMBER, voteNumber);
        cv.put(MovieEntry.COLUMN_FRONT_POSTER, posterPath);
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        return cv;
    }

    String getRating(String votes) {
        return String.valueOf(voteAverage) + "/10 (" + String.valueOf(voteNumber) +" " + votes +  ")";
    }


}

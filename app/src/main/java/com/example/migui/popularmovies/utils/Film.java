package com.example.migui.popularmovies.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Film {
    private String title;
    private String origTitle;
    private String overview;
    private double rate;
    private boolean onlyForAdults;
    private String imageURL;

    Film(JSONObject json) throws JSONException {
        title = json.getString("title");
        origTitle = json.getString("original_title");
        overview = json.getString("original_title");
        rate = json.getDouble("vote_average");
        onlyForAdults = json.getBoolean("adult");
        imageURL = json.getString("poster_path");
    }

    public String getTitle() {
        return title;
    }

    public String getOrigTitle() {
        if (title.equals(origTitle))
            return null;
        return origTitle;
    }

    public String getOverview() {
        return overview;
    }

    public double getRate() {
        return rate;
    }

    public boolean isOnlyForAdults() {
        return onlyForAdults;
    }

    public String getImageURL() {
        return imageURL;
    }
}

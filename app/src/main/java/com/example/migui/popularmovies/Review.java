package com.example.migui.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

class Review {
    private String author;
    private String content;

    Review(JSONObject json) throws JSONException {
        author = json.getString("author");
        content = json.getString("content");
    }

}

package com.example.migui.popularmovies.auxiliar;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {
    private String author;
    private String content;

    public Review(JSONObject json) throws JSONException {
        author = json.getString("author");
        content = json.getString("content");
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}

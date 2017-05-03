package com.example.migui.popularmovies.auxiliar;

import org.json.JSONException;
import org.json.JSONObject;

class Review {
    private String author;
    private String content;

    Review(JSONObject json) throws JSONException {
        author = json.getString("author");
        content = json.getString("content");
    }

    String getAuthor() {
        return author;
    }

    String getContent() {
        return content;
    }
}

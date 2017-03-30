package com.example.migui.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

class Trailer {
    private final static String URL_YOUTUBE_THUMBAIL = "https://img.youtube.com/vi/%1$s/0.jpg";
    private String key;
    private String name;
    private String site;

    Trailer(JSONObject json) {
        try {
            key = json.getString("key");
            name = json.getString("name");
            site = json.getString("site");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    String getSite() {
        return site;
    }

    String getThumbailString() {
        return String.format(URL_YOUTUBE_THUMBAIL, key);
    }

}

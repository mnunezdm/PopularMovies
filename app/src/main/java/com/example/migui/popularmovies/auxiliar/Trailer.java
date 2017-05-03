package com.example.migui.popularmovies.auxiliar;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer {
    private final static String URL_YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/%1$s/0.jpg";
    private String key;
    private String name;
    private String site;

    public Trailer(JSONObject json) {
        try {
            key = json.getString("key");
            name = json.getString("name");
            site = json.getString("site");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getThumbnailString() {
        return String.format(URL_YOUTUBE_THUMBNAIL, key);
    }
}

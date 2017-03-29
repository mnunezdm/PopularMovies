package com.example.migui.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

class Trailer {
    private String key;
    private String name;
    private String site;

    Trailer (JSONObject json) {
        try {
            key = json.getString("key");
            name = json.getString("name");
            site = json.getString("site");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

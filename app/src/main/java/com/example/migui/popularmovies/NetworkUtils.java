package com.example.migui.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class NetworkUtils {
    private final static String FILM_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    final static String IMAGE_BASE_URL =
            "https://image.tmdb.org/t/p/w500";

    private final static String KEY = "YOUR_API_KEY_GOES_HERE"; // TODO api key!!

    private static OkHttpClient client = new OkHttpClient();

    static String queryFilms(String sort) throws IOException {
        Uri.Builder uriBuilder = Uri.parse(FILM_BASE_URL).buildUpon()
                .appendPath(sort).appendQueryParameter("api_key", KEY);
        URL url = new URL(uriBuilder.build().toString());

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        return (checkJson(responseString))? responseString : null;
    }

    private static boolean checkJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            return json.has("results");
        } catch (JSONException e) {
            return false;
        }
    }

    static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
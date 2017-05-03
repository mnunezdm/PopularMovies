package com.example.migui.popularmovies.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.migui.popularmovies.ui.ActivityBillboard;

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

    private final static String PATH_POPULAR = "popular";
    private final static String PATH_TOP_RATED = "top_rated";
    private final static String PATH_REVIEW = "reviews";
    private final static String PATH_VIDEOS = "videos";

    private final static String API_KEY = "YOUR API_KEY_GOES_HERE"; // TODO api key!!

    private static OkHttpClient client = new OkHttpClient();

    static String queryFilms(ActivityBillboard.SORT_TYPE sort_type) throws IOException {
        Uri.Builder uriBuilder = Uri.parse(FILM_BASE_URL).buildUpon();
        switch (sort_type) {
            case POPULAR:
                uriBuilder.appendPath(PATH_POPULAR);
                break;
            case TOP_RATED:
                uriBuilder.appendPath(PATH_TOP_RATED);
                break;
        }

        URL url = new URL(uriBuilder.appendQueryParameter("api_key", API_KEY).build().toString());

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        return (checkJson(responseString)) ? responseString : null;
    }

    static String queryDetails(String movieId) {
        return queryDetail(movieId, true) + ":#:#:" + queryDetail(movieId, false);
    }

    private static String queryDetail(String movieId, boolean videos) {
        try {
            Uri.Builder uriBuilder = Uri.parse(FILM_BASE_URL).buildUpon();
            uriBuilder.appendPath(movieId).appendPath((videos) ? PATH_VIDEOS : PATH_REVIEW);
            URL url = new URL(uriBuilder.appendQueryParameter("api_key", API_KEY).build().toString());

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            return (checkJson(responseString)) ? responseString : null;
        } catch (Exception e ) {
            return null;
        }
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
package com.example.migui.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class NetworkUtils {
    private final static String FILM_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    final static String IMAGE_BASE_URL =
            "https://image.tmdb.org/t/p/w500";

    private final static String KEY = "YOUR_API_KEY_GOES_HERE"; // TODO api key!!

    // TODO use https://github.com/square/okhttp
    static String queryFilms(String sort) throws IOException {
        Uri.Builder uriBuilder = Uri.parse(FILM_BASE_URL).buildUpon()
                .appendPath(sort).appendQueryParameter("api_key", KEY);
        URL url = new URL(uriBuilder.build().toString());
        System.out.println(url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
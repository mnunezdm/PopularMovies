/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.migui.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

class NetworkUtils {
    private final static String FILM_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    final static String IMAGE_BASE_URL =
            "https://image.tmdb.org/t/p/w500";

    private final static String KEY = "YOUR_API_GOES_HERE"; // TODO api key!!

    static String queryFilms(String sort) throws IOException {
        Uri.Builder uriBuilder = Uri.parse(FILM_BASE_URL).buildUpon()
                .appendPath(sort).appendQueryParameter("api_key", KEY);
        URL url = new URL(uriBuilder.build().toString());
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

    static boolean checkJSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.getInt("status_code") != 0) {
                System.out.println("error");
                throw new JSONException("");
            }
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
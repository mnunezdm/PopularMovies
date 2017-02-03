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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class NetworkUtils {
    private final static String FILM_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    private final static String IMAGE_BASE_URL =
            "https://image.tmdb.org/t/p/w500";

    private final static String KEY = "API_KEY_IN_HERE"; // TODO

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

    static Bitmap queryImage(String path) {
        Bitmap mIcon11 = null;
        Uri.Builder uriBuilder = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(path).appendQueryParameter("api_key", KEY);
        try {
            URL url = new URL(uriBuilder.build().toString());
            InputStream in = url.openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }
}
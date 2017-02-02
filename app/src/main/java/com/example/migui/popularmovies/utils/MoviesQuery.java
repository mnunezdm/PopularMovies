package com.example.migui.popularmovies.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

class MoviesQuery extends AsyncTask<URL, Void, String>{

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(URL... params) {
        URL searchUrl = params[0];
        String githubSearchResults = null;
        try {
            githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return githubSearchResults;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
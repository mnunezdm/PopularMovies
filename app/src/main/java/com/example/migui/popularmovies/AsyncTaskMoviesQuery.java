package com.example.migui.popularmovies;

import android.os.AsyncTask;

import java.io.IOException;

class AsyncTaskMoviesQuery extends AsyncTask<String, Void, String> {
    private AsyncTaskCompleteListener<String> listener;

    AsyncTaskMoviesQuery(AsyncTaskCompleteListener<String> listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String searchUrl = params[0];
        String githubSearchResults = null;
        try {
            githubSearchResults = NetworkUtils.queryFilms(searchUrl);
        } catch (IOException ignored) {
        }
        return githubSearchResults;
    }

    @Override
    protected void onPostExecute(String s) {
        listener.taskCompleted(s);
    }

    interface AsyncTaskCompleteListener<E> {
        void taskCompleted(E s);
    }
}
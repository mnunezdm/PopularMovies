package com.example.migui.popularmovies;

import android.os.AsyncTask;


import java.io.IOException;

class AsyncTaskMoviesQuery extends AsyncTask<String, Void, String> {
    private AsyncTaskCompleteListener<String> listener;
    private ActivityBillboard.SORT_TYPE sortType;

    AsyncTaskMoviesQuery(AsyncTaskCompleteListener<String> listener, ActivityBillboard.SORT_TYPE sortType) {
        this.listener = listener;
        this.sortType = sortType;
    }

    @Override
    protected String doInBackground(String... params) {
        String githubSearchResults = null;
        try {
            switch (sortType) {
                case POPULAR:
                    githubSearchResults = NetworkUtils.queryFilms("popular");
                    break;
                case TOP_RATED:
                    githubSearchResults = NetworkUtils.queryFilms("top_rated");
                    break;
                case UNIQUE:
                    // TODO
                    break;
            }
        } catch (IOException ignored) {}
        return githubSearchResults;
    }

    @Override
    protected void onPostExecute(String s) {
        listener.taskCompleted(sortType, s);
    }

    interface AsyncTaskCompleteListener<E> {
        void taskCompleted(ActivityBillboard.SORT_TYPE sort_type, E s);
    }
}
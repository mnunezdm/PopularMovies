package com.example.migui.popularmovies.sync;

import android.os.AsyncTask;


import com.example.migui.popularmovies.ui.ActivityBillboard;

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
        String result = null;
        try {
            switch (sortType) {
                case POPULAR:
                    result = NetworkUtils.queryFilms(sortType);
                    break;
                case TOP_RATED:
                    result = NetworkUtils.queryFilms(sortType);
                    break;
                case UNIQUE:
                    result = NetworkUtils.queryDetails(params[0]);
                    break;
            }
        } catch (IOException ignored) {}
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        listener.taskCompleted(sortType, s);
    }

    interface AsyncTaskCompleteListener<E> {
        void taskCompleted(ActivityBillboard.SORT_TYPE sort_type, E s);
    }
}
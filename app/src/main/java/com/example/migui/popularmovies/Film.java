package com.example.migui.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import org.json.JSONException;
import org.json.JSONObject;


class Film {
    private String title;
    private String origTitle;
    private String overview;
//    private double rate;
//    private boolean onlyForAdults;
    private ImageView image;

    Film(JSONObject json, Context context) throws JSONException {
        title = json.getString("title");
        origTitle = json.getString("original_title");
        overview = json.getString("overview");
//        rate = json.getDouble("vote_average");
//        onlyForAdults = json.getBoolean("adult");
        image = new ImageView(context);
        new DownloadImageTask().execute(json.getString("poster_path").substring(1));
    }

    String getTitle() {
        return title;
    }

    String getOrigTitle() {
        if (title.equals(origTitle))
            return null;
        return origTitle;
    }

    String getOverview() {
        return overview;
    }

//    double getRate() {
//        return rate;
//    }
//
//    boolean isOnlyForAdults() {
//        return onlyForAdults;
//    }

    @Override
    public String toString() {
        return title;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String path = urls[0];
            return NetworkUtils.queryImage(path);
        }

        protected void onPostExecute(Bitmap result) {
            image.setImageBitmap(result);
        }
    }
}

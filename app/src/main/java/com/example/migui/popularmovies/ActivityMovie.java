package com.example.migui.popularmovies;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.migui.popularmovies.data.MovieContract;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ActivityMovie extends ActivityBase
        implements AsyncTaskMoviesQuery.AsyncTaskCompleteListener<String>,
        LoaderManager.LoaderCallbacks<Cursor>, OnLikeListener {

    @BindView(R.id.text_original_title)
    TextView textOriginalTitle;
    @BindView(R.id.text_overview)
    TextView textOverview;
    @BindView(R.id.text_release_date)
    TextView textDate;
    @BindView(R.id.text_vote_average)
    TextView textRating;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.imageView3)
    ImageView imageView;
    @BindView(R.id.button_like)
    LikeButton likeButton;

    private static final int MOVIE_LOADER_ID = 2;
    private static final String[] MOVIE_PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_FRONT_POSTER,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_VOTE_NUMBER,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_FAVOURITE,
    };

    private List<Trailer> trailers;
    private List<Review> reviews;
    private Uri uriFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareMovieInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_LOADER_ID:
                return new CursorLoader(this,
                        getIntent().getData(),
                        MOVIE_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Film film = new Film(data);

        textTitle.setText(film.getTitle());

        if (film.getOrigTitle() != null) {
            textOriginalTitle.setVisibility(View.VISIBLE);
            textOriginalTitle.setText(film.getOrigTitle());
        }

        textDate.setText(film.getReleaseDate());
        textRating.setText(film.getRating(getResources().getString(R.string.votes)));
        textOverview.setText(film.getOverview());

        Picasso.with(this).load(NetworkUtils.IMAGE_BASE_URL + film.getPosterPath())
                .placeholder(R.drawable.ic_unknown).error(R.drawable.ic_error).into(imageView);

        Log.d(this.toString(), String.valueOf(film.isFavourite()));
        likeButton.setLiked(film.isFavourite());
        likeButton.setOnLikeListener(this);

        long id = film.getId();

        uriFavourite = MovieContract.MovieEntry.getUri(ActivityBillboard.SORT_TYPE.UNIQUE).buildUpon().
                appendPath(String.valueOf(id)).appendPath(MovieContract.PATH_FAVOURITES).build();

        if (NetworkUtils.isOnline(this))
            new AsyncTaskMoviesQuery(this, ActivityBillboard.SORT_TYPE.UNIQUE)
                    .execute(String.valueOf(film.getId()));
        else
            Toast.makeText(this, "Offline couldnt fetch reviews and videos", Toast.LENGTH_LONG).show();
    }

    @Override
    public void liked(LikeButton likeButton) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 1);
        getContentResolver().update(uriFavourite, cv, null, null);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 0);
        getContentResolver().update(uriFavourite, cv, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void taskCompleted(ActivityBillboard.SORT_TYPE sort_type, String s) {
        if (sort_type == ActivityBillboard.SORT_TYPE.UNIQUE) {
            parseJson(s);
        }

    }

    void parseJson(String jsonConcat) {
        try {
            JSONArray arrayJson;
            String jsonString;
            String[] splitedResponse = jsonConcat.split(":#:#:");
            jsonString = splitedResponse[0];
            if (jsonString != null) {
                trailers = new ArrayList<>();
                arrayJson = new JSONObject(jsonString).getJSONArray("results");
                for (int i = 0; i < arrayJson.length(); i++)
                    trailers.add(new Trailer(arrayJson.getJSONObject(i)));
                Toast.makeText(this, trailers.size() + " TRAILERS", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "EMPTY TRAILERS RESPONSE", Toast.LENGTH_SHORT).show();

            jsonString = splitedResponse[1];
            if (jsonString != null) {
                reviews = new ArrayList<>();
                arrayJson = new JSONObject(jsonString).getJSONArray("results");
                for (int i = 0; i < arrayJson.length(); i++)
                    reviews.add(new Review(arrayJson.getJSONObject(i)));
                Toast.makeText(this, reviews.size() + " REVIEWS", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "EMPTY REVIEWS RESPONSE", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            Toast.makeText(this, "EXCEPTION SUU", Toast.LENGTH_SHORT).show();
//            errorConnection();
        }
    }

    private void shareMovieInfo() {
        String shareMessage = getResources().getString(R.string.share_string);
        shareMessage = String.format(shareMessage, textTitle.getText());
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain").setText(shareMessage).startChooser();
    }
}

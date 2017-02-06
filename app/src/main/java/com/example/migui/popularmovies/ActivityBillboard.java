package com.example.migui.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.migui.popularmovies.NetworkUtils.isOnline;

public class ActivityBillboard extends AppCompatActivity
        implements PosterAdapter.PosterAdapterOnClickHandler {
    private RecyclerView rvMoviesList;
    private PosterAdapter posterAdapter;
    private ProgressBar pbFetchingMovies;
    private LinearLayout linearLayout;
    private boolean byTopRated = true;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);

        Intent intent = getIntent();
        pbFetchingMovies = (ProgressBar) findViewById(R.id.pb_fetching_indicator);

        rvMoviesList = (RecyclerView) findViewById(R.id.view_movies);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, 2);
        rvMoviesList.setLayoutManager(layoutManager);
        rvMoviesList.setHasFixedSize(true);

        posterAdapter = new PosterAdapter(this);
        rvMoviesList.setAdapter(posterAdapter);

        linearLayout = (LinearLayout) findViewById(R.id.layout_error);

        parseJSON(intent.getStringExtra("json"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_billboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.menu_sort:
                    sortMovies();
                    if (!byTopRated) {
                        item.setTitle(R.string.top_rated);
                        byTopRated = true;
                    } else {
                        item.setTitle(R.string.popular);
                        byTopRated = false;
                    }
                    return true;
            }
        } catch (Exception e) {
            errorConnection();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * <B>FUNCTION:</B> checks if there is connectivity and launches the query
     */
    private void sortMovies() throws Exception {
        if (!isOnline(this)) {
            errorConnection();
            throw new Exception();
        } else if (!byTopRated)
            new MoviesQuery().execute("top_rated");
        else
            new MoviesQuery().execute("popular");
    }

    /**
     * <B>FUNCTION:</B> creates the list of all the movies in the json passed
     *
     * @param json JSON array with all the movies
     */
    private void parseJSON(String json) {
        List<Film> films = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                films.add(new Film(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            errorConnection();
        }

        posterAdapter.setFilmList(films);
        changeViews(false);
    }

    /**
     * <B>FUNCTION:</B> hides or shows the ProgressBar and the RecyclerView
     *
     * @param fetching if true fetching is in progress show PB hide RV
     */
    private void changeViews(boolean fetching) {
        linearLayout.setVisibility(View.INVISIBLE);
        if (!fetching) {
            pbFetchingMovies.setVisibility(View.INVISIBLE);
            rvMoviesList.setVisibility(View.VISIBLE);
        } else {
            rvMoviesList.setVisibility(View.INVISIBLE);
            pbFetchingMovies.setVisibility(View.VISIBLE);
        }
    }

    /**
     * <B>FUNCTION:</B> shows an error message
     */
    private void errorConnection() {
        if (toast == null){
            toast = Toast.makeText(this, R.string.connectivity_issues, Toast.LENGTH_LONG);
        }
        toast.show();
        pbFetchingMovies.setVisibility(View.INVISIBLE);
        rvMoviesList.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * <B>FUNCTION:</B> starts a ActivityMovie with filmToExpand
     *
     * @param film film to expand
     */
    @Override
    public void onClick(Film film) {
        Intent intent = new Intent(this, ActivityMovie.class);
        intent.putExtra("film", film);
        startActivity(intent);
    }

    private class MoviesQuery extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            changeViews(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String searchUrl = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.queryFilms(searchUrl);
            } catch (IOException e) {
                errorConnection();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            parseJSON(s);
            changeViews(false);
        }
    }
}

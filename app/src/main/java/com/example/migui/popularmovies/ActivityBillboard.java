package com.example.migui.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.migui.popularmovies.NetworkUtils.isOnline;

// TODO create the recyclerview
// TODO implement sort button, change at click popular to top_rated to popular...
public class ActivityBillboard extends AppCompatActivity {

    private RecyclerView rvMoviesList;
    private ProgressBar pbFetchingMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);

        Intent intent = getIntent();

        if (intent != null) {
            rvMoviesList = (RecyclerView) findViewById(R.id.view_movies);
            pbFetchingMovies = (ProgressBar) findViewById(R.id.pb_fetching_indicator);

            parseJSON(intent.getStringExtra("json"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_billboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                sortMovies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortMovies() {
        if (!isOnline(this))
            Toast.makeText(this, "No connection Detected", Toast.LENGTH_LONG).show();
        else
            new MoviesQuery().execute("top_rated");
        // TODO -> implement sort movies
    }

    private void parseJSON(String s) {
        List<Film> films = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(s).getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                films.add(new Film(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO DEMO!
        System.out.println(films);
        Intent intent = new Intent(this, ActivityMovie.class);
        intent.putExtra("film", films.get(0));
        startActivity(intent);
    }

    private class MoviesQuery extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String searchUrl = params[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.queryFilms(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            parseJSON(s);
            showFilms();
        }
    }

    private void showFilms() {
        pbFetchingMovies.setVisibility(View.INVISIBLE);
        rvMoviesList.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        rvMoviesList.setVisibility(View.INVISIBLE);
        pbFetchingMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
    }
}

package com.example.migui.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;


public class Billboard extends AppCompatActivity {

    private RecyclerView rvMoviesList;
    private ProgressBar pbFetchingMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);

        rvMoviesList = (RecyclerView) findViewById(R.id.view_movies);
        pbFetchingMovies = (ProgressBar) findViewById(R.id.pb_fetching_indicator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.billboard_menu, menu);
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
        new MoviesQuery().execute("top_rated");
        // TODO -> implement sort movies
    }

    private void parseJSON(String s) {
        List<Film> films = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(s).getJSONArray("results");
            for (int i = 0; i < array.length(); i++) {
                films.add(new Film(array.getJSONObject(i), this));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO DEMO!
        System.out.println(films);
        Intent intent = new Intent(this, MovieDetails.class);
        Film film = films.get(0);
        intent.putExtra("title", film.getTitle());
        if (film.getOrigTitle() != null)
            intent.putExtra("original_title", film.getOrigTitle());
        intent.putExtra("overview", film.getOverview());
        startActivity(intent);
    }

    class MoviesQuery extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
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
        }
    }
}

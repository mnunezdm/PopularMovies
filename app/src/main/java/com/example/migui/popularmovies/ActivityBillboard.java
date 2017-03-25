package com.example.migui.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.example.migui.popularmovies.NetworkUtils.isOnline;

public class ActivityBillboard extends ActivityBase
        implements PosterAdapter.PosterAdapterOnClickHandler,
        AsyncTaskMoviesQuery.AsyncTaskCompleteListener<String> {

    @BindView(R.id.view_movies) RecyclerView rvMoviesList;
    @BindView(R.id.layout_fetching_indicator) LinearLayout pbFetchingMovies;
    @BindView(R.id.layout_error) LinearLayout layoutErrors;

    private PosterAdapter posterAdapter;
    private static boolean byTopRated = true;
    private Toast toast;
    private static List<Film> films;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);

        GridLayoutManager layoutManager =
                new GridLayoutManager(this, calculateNoOfColumns());
        rvMoviesList.setLayoutManager(layoutManager);
        rvMoviesList.setHasFixedSize(true);

        posterAdapter = new PosterAdapter(this);
        rvMoviesList.setAdapter(posterAdapter);

        if (films == null) {
            Intent intent = getIntent();
            parseJSON(intent.getStringExtra("json"));
        }
        posterAdapter.setFilmList(films);
        changeViews(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_billboard, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_sort);
        if (byTopRated) {
            menuItem.setTitle(R.string.top_rated);
            byTopRated = true;
        } else {
            menuItem.setTitle(R.string.popular);
            byTopRated = false;
        }
        return true;
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

    @Override
    public void taskCompleted(String json) {
        if (json == null)
            errorConnection();
        else {
            parseJSON(json);
            posterAdapter.setFilmList(films);
            changeViews(false);
        }
    }

    /**
     * <B>FUNCTION:</B> checks if there is connectivity and launches the query
     */
    private void sortMovies() throws Exception {
        changeViews(true);
        posterAdapter.setFilmList(null);
        if (!isOnline(this)) {
            errorConnection();
            throw new Exception();
        }
        if (!byTopRated)
            new AsyncTaskMoviesQuery(this).execute("top_rated");
        else
            new AsyncTaskMoviesQuery(this).execute("popular");
    }

    /**
     * <B>FUNCTION:</B> creates the list of all the movies in the json passed
     *
     * @param json JSON array with all the movies
     */
    private void parseJSON(String json) {
        films = new ArrayList<>();
        try {
            JSONArray array = new JSONObject(json).getJSONArray("results");
            for (int i = 0; i < array.length(); i++)
                films.add(new Film(array.getJSONObject(i)));
        } catch (JSONException e) {
            errorConnection();
        }
    }

    /**
     * <B>FUNCTION:</B> hides or shows the ProgressBar and the RecyclerView
     *
     * @param fetching if true fetching is in progress show PB hide RV
     */
    private void changeViews(boolean fetching) {
        if (!fetching) {
            pbFetchingMovies.setVisibility(View.INVISIBLE);
            rvMoviesList.setVisibility(View.VISIBLE);
        } else {
            rvMoviesList.setVisibility(View.INVISIBLE);
            pbFetchingMovies.setVisibility(View.VISIBLE);
        }
        layoutErrors.setVisibility(View.INVISIBLE);
    }

    /**
     * <B>FUNCTION:</B> shows an error message
     */
    private void errorConnection() {
        if (toast == null) {
            toast = Toast.makeText(this, R.string.connectivity_issues, Toast.LENGTH_LONG);
        }
        toast.show();
        pbFetchingMovies.setVisibility(View.INVISIBLE);
        rvMoviesList.setVisibility(View.INVISIBLE);
        layoutErrors.setVisibility(View.VISIBLE);
    }

    /**
     * <B>FUNCTION:</B> returns the number of columns that fit in the screen
     *
     * @return the number of columns that fit in the actual screen
     * from http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     */
    private int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 130);
    }
}

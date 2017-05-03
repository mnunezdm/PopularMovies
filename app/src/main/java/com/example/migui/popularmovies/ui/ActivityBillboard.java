package com.example.migui.popularmovies.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.content.CursorLoader;
import android.app.LoaderManager;
import android.os.Parcelable;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.migui.popularmovies.AsyncTaskMoviesQuery;
import com.example.migui.popularmovies.Film;
import com.example.migui.popularmovies.R;
import com.example.migui.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.example.migui.popularmovies.NetworkUtils.isOnline;

public class ActivityBillboard extends ActivityBase
        implements PosterAdapter.PosterAdapterOnClickHandler,
        AsyncTaskMoviesQuery.AsyncTaskCompleteListener<String>,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    public enum SORT_TYPE {
        POPULAR, TOP_RATED, ALL, FAVOURITES, UNIQUE
    }

    private static final int MOVIES_LOADER_ID = 1;
    private static final String[] MAIN_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_FRONT_POSTER,
            MovieContract.MovieEntry._ID
    };
    private final String SORT_CRITERIA = "sort_criteria";
    private final String POSITION = "position";

    @BindView(R.id.view_movies)
    RecyclerView rvMoviesList;
    @BindView(R.id.layout_fetching_indicator)
    LinearLayout pbFetchingMovies;
    @BindView(R.id.layout_error)
    LinearLayout layoutErrors;


    private Parcelable layoutStateSaved;
    private PosterAdapter posterAdapter;
    private Toast toast;
    private Menu menu;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);

        if (savedInstanceState != null)
            layoutStateSaved = savedInstanceState.getParcelable(POSITION);

        layoutManager =
                new GridLayoutManager(this, calculateNoOfColumns());

        rvMoviesList.setLayoutManager(layoutManager);
        rvMoviesList.setHasFixedSize(true);

        posterAdapter = new PosterAdapter(this);
        rvMoviesList.setAdapter(posterAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sortMovies(SORT_TYPE.values()
                [sharedPreferences.getInt(SORT_CRITERIA, SORT_TYPE.POPULAR.ordinal())]);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        LoaderManager manager = getLoaderManager();
        if (manager.getLoader(MOVIES_LOADER_ID) == null)
            manager.initLoader(MOVIES_LOADER_ID, null, this);
        else {
            manager.restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_billboard, menu);
        this.menu = menu;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (SORT_TYPE.values()
                [sharedPreferences.getInt(SORT_CRITERIA, SORT_TYPE.POPULAR.ordinal())]) {
            case POPULAR:
                menu.findItem(R.id.menu_sort).setTitle(getString(R.string.popular));
                menu.findItem(R.id.menu_sort_pop).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.menu_sort).setTitle(getString(R.string.top_rated));
                menu.findItem(R.id.menu_sort_top).setChecked(true);
                break;
            case ALL:
                menu.findItem(R.id.menu_sort).setTitle(getString(R.string.all));
                menu.findItem(R.id.menu_all).setChecked(true);
                break;
            case FAVOURITES:
                menu.findItem(R.id.menu_sort).setTitle(getString(R.string.favourites));
                menu.findItem(R.id.menu_favourites).setChecked(true);
                break;
        }
        return true;
    }

    public boolean onGroupMenuSelected(MenuItem item) {
        menu.findItem(R.id.menu_sort).setTitle(item.toString());
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.menu_sort_pop:
                sortMovies(SORT_TYPE.POPULAR);
                return true;
            case R.id.menu_sort_top:
                sortMovies(SORT_TYPE.TOP_RATED);
                return true;
            case R.id.menu_all:
                sortMovies(SORT_TYPE.ALL);
                return true;
            case R.id.menu_favourites:
                sortMovies(SORT_TYPE.FAVOURITES);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * <B>FUNCTION:</B> starts a ActivityMovie with filmToExpand
     */
    @Override
    public void onClick(String uri) {
        Intent intent = new Intent(this, ActivityMovie.class);
        intent.setData(MovieContract.MovieEntry.getUri(SORT_TYPE.UNIQUE)
                .buildUpon().appendPath(uri).build());
        startActivity(intent);
    }

    /**
     * <B>FUNCTION:</B> checks if there is connectivity and launches the query
     */
    private void sortMovies(SORT_TYPE sort) {
        changeViews(true);
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(SORT_CRITERIA, sort.ordinal()).apply();
        if (!isOnline(this) || sort == SORT_TYPE.ALL || sort == SORT_TYPE.FAVOURITES) {
            LoaderManager manager = getLoaderManager();
            if (manager.getLoader(MOVIES_LOADER_ID) == null)
                manager.initLoader(MOVIES_LOADER_ID, null, this);
            else {
                manager.restartLoader(MOVIES_LOADER_ID, null, this);
            }
        } else
            new AsyncTaskMoviesQuery(this, sort).execute();
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
        return (int) (dpWidth / 180);
    }

    @Override
    public void taskCompleted(SORT_TYPE sort_type, String json) {
        try {
            JSONArray array = new JSONObject(json).getJSONArray("results");
            List<ContentValues> cvs = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                cvs.add(new Film(array.getJSONObject(i)).getContentValues());
            }
            getContentResolver().bulkInsert(MovieContract.MovieEntry.getUri(sort_type),
                    cvs.toArray(new ContentValues[cvs.size()]));
            LoaderManager manager = getLoaderManager();
            if (manager.getLoader(MOVIES_LOADER_ID) == null)
                manager.initLoader(MOVIES_LOADER_ID, null, this);
            else {
                manager.restartLoader(MOVIES_LOADER_ID, null, this);
            }
        } catch (JSONException e) {
            errorConnection();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case MOVIES_LOADER_ID:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SORT_TYPE sort = SORT_TYPE.values()
                        [sharedPreferences.getInt(SORT_CRITERIA, SORT_TYPE.POPULAR.ordinal())];
                String selection = MovieContract.MovieEntry.getSelection(sort);
                String sortOrder = MovieContract.MovieEntry.getOrderBy(sort);
                Uri uri = MovieContract.MovieEntry.getUri(sort);
                return new CursorLoader(this,
                        uri,
                        MAIN_PROJECTION,
                        selection,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        posterAdapter.swapCursor(data);
        posterAdapter.notifyDataSetChanged();
        if (layoutStateSaved != null) {
            layoutManager.onRestoreInstanceState(layoutStateSaved);
            layoutStateSaved = null;
        }
        changeViews(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(POSITION, layoutManager.onSaveInstanceState());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        posterAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SORT_CRITERIA:
                getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }
}

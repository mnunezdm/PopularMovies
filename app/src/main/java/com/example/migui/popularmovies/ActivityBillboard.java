package com.example.migui.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.content.CursorLoader;
import android.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        LoaderManager.LoaderCallbacks<Cursor> {

    private MenuItem menuItem;

    public enum SORT_TYPE {
        POPULAR, TOP_RATED, ALL, FAVOURITES, UNIQUE
    }

    private static final int MOVIES_LOADER_ID = 1;
    private static final String[] MAIN_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_FRONT_POSTER,
            MovieContract.MovieEntry._ID
    };

    @BindView(R.id.view_movies)
    RecyclerView rvMoviesList;
    @BindView(R.id.layout_fetching_indicator)
    LinearLayout pbFetchingMovies;
    @BindView(R.id.layout_error)
    LinearLayout layoutErrors;

    private PosterAdapter posterAdapter;
    private Toast toast;
    private SORT_TYPE sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);

        rvMoviesList.setLayoutManager(new GridLayoutManager(this, calculateNoOfColumns()));
        rvMoviesList.setHasFixedSize(true);

        posterAdapter = new PosterAdapter(this); // TODO
        rvMoviesList.setAdapter(posterAdapter); // TODO

        sortMovies(SORT_TYPE.POPULAR);

        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, ActivityBillboard.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_billboard, menu);
        menuItem = menu.findItem(R.id.menu_sort);
        return true;
    }

    public boolean onGroupMenuSelected(MenuItem item) {
        menuItem.setTitle(item.toString());
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
        this.sort = sort;
        if (!isOnline(this) || sort == SORT_TYPE.ALL || sort == SORT_TYPE.FAVOURITES)
            getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, ActivityBillboard.this);
        else
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
        return (int) (dpWidth / 160);
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
            getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        } catch (JSONException e) {
            errorConnection();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case MOVIES_LOADER_ID:
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
        rvMoviesList.smoothScrollToPosition(0);
        posterAdapter.swapCursor(data);
        changeViews(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        posterAdapter.swapCursor(null);
    }
}

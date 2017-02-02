package com.example.migui.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        switch (item.getItemId()){
            case R.id.menu_sort:
                sortMovies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortMovies() {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
        // TODO -> implement sort movies
    }
}

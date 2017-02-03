package com.example.migui.popularmovies;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

// TODO implement the rest of the fields
// TODO organize everything
public class ActivityMovie extends AppCompatActivity {
    private Film film;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        film = bundle.getParcelable("film");

        if (film != null) {
            TextView textOriginalTitle = (TextView) findViewById(R.id.text_original_title);
            TextView textOverview = (TextView) findViewById(R.id.text_overview);
            TextView textTitle = (TextView) findViewById(R.id.text_title);

            textTitle.setText(film.getTitle());

            if (film.getOrigTitle() != null) {
                textOriginalTitle.setVisibility(View.VISIBLE);
                textOriginalTitle.setText(film.getOrigTitle());
            }

            textOverview.setText(film.getOverview());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                shareMovieInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareMovieInfo() {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain").setText(film.toString()).startChooser();
    }
}

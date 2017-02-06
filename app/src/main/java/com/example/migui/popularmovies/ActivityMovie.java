package com.example.migui.popularmovies;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
            TextView textDate = (TextView) findViewById(R.id.text_release_date);
            TextView textRating = (TextView) findViewById(R.id.text_vote_average);
            TextView textTitle = (TextView) findViewById(R.id.text_title);
            ImageView imageView = (ImageView) findViewById(R.id.imageView3);

            textTitle.setText(film.getTitle());

            if (film.getOrigTitle() != null) {
                textOriginalTitle.setVisibility(View.VISIBLE);
                textOriginalTitle.setText(film.getOrigTitle());
            }

            textDate.setText(film.getReleaseDate());

            textRating.setText(film.getRating());

            textOverview.setText(film.getOverview());

            Picasso.with(this).load(film.getImageURL()).into(imageView);
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

    /**
     * <B>FUNCTION:</B> creates an Explicit Intent to share information about the film
     */
    private void shareMovieInfo() {
        String shareMessage = getResources().getString(R.string.share_string);
        shareMessage = String.format(shareMessage, film.getTitle());
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain").setText(shareMessage).startChooser();
    }
}

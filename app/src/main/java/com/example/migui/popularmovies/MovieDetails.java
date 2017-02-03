package com.example.migui.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intent = this.getIntent();
        if (intent == null || !intent.hasExtra("title"))
            this.finishActivity(0);
        else {
            TextView textTitle = (TextView) findViewById(R.id.text_title);
            textTitle.setText(intent.getStringExtra("title"));
            if (intent.hasExtra("original_title")) {
                TextView textOriginalTitle = (TextView) findViewById(R.id.text_original_title);
                textOriginalTitle.setVisibility(View.VISIBLE);
                textOriginalTitle.setText(intent.getStringExtra("original_title"));
            }
            TextView textOverview = (TextView) findViewById(R.id.text_overview);
            textOverview.setText(intent.getStringExtra("overview"));
        }
    }
}

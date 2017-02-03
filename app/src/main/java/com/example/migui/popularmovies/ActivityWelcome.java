package com.example.migui.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.example.migui.popularmovies.NetworkUtils.isOnline;

public class ActivityWelcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (!isOnline(this)) {
            TextView tvFetching = (TextView) findViewById(R.id.tv_fetching);
            tvFetching.setText(R.string.error_connectivity);
            tvFetching.setTextColor(ContextCompat.getColor(this, R.color.colorError));

            TextView tvConnectivity = (TextView) findViewById(R.id.tv_connectivity);
            tvConnectivity.setVisibility(View.VISIBLE);

            ProgressBar pbFetching = (ProgressBar) findViewById(R.id.pb_fetching_welcome);
            pbFetching.setVisibility(View.INVISIBLE);

            Toast.makeText(this, R.string.connectivity_issues, Toast.LENGTH_LONG).show();
        } else {
            new WelcomeTask().execute();
        }
    }

    private class WelcomeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            try {
                result = NetworkUtils.queryFilms("top_rated");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String json) {
            finishQuery(json);
        }
    }

    private void finishQuery(String json) {
        Intent intent = new Intent(this, ActivityBillboard.class);
        intent.putExtra("json", json);
        startActivity(intent);
        finish();
    }
}

package com.example.migui.popularmovies;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.migui.popularmovies.NetworkUtils.isOnline;

public class ActivityWelcome extends AppCompatActivity
        implements AsyncTaskMoviesQuery.AsyncTaskCompleteListener<String> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        connect();
    }

    /**
     * <B>FUNCTION:</B> shows an error message
     */
    private void showError(int error) {
        TextView tvFetching = (TextView) findViewById(R.id.tv_fetching);
        tvFetching.setText(R.string.error_connectivity);
        tvFetching.setTextColor(ContextCompat.getColor(this, R.color.colorError));

        TextView tvConnectivity = (TextView) findViewById(R.id.tv_connectivity);
        tvConnectivity.setVisibility(View.VISIBLE);
        switch (error){
            case 0:
                tvConnectivity.setText(getResources().getString(R.string.connectivity_issues));
                break;
            case 1:
                tvConnectivity.setText(getResources().getString(R.string.api_error));
        }
        ProgressBar pbFetching = (ProgressBar) findViewById(R.id.pb_fetching_welcome);
        pbFetching.setVisibility(View.INVISIBLE);

        Toast.makeText(this, R.string.connectivity_issues, Toast.LENGTH_LONG).show();
    }

    void connect() {
        if (!isOnline(this)) {
            showError(0);
        }
        else {
            new AsyncTaskMoviesQuery(this).execute("top_rated");
        }
    }

    /**
     * <B>FUNCTION:</B> calls ActivityBillboard with the info of the parameter
     *
     * @param json JSON string with all the data from the query
     */
    @Override
    public void taskCompleted(String json) {
        if (json == null)
            showError(1);
        else {
            Intent intent = new Intent(this, ActivityBillboard.class);
            intent.putExtra("json", json);
            startActivity(intent);
            finish();
        }
    }

    // TODO http://stackoverflow.com/questions/11123621/running-code-in-main-thread-from-another-thread
}
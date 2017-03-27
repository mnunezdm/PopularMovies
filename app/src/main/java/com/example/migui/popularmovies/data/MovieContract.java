package com.example.migui.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +  CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_FRONT_POSTER      = "front_poster";
        public static final String COLUMN_NAME              = "name";
        public static final String COLUMN_ORIGINAL_NAME     = "original_name";
        public static final String COLUMN_RELEASE_DATE      = "release_date";
        public static final String COLUMN_VOTE_AVERAGE      = "vote_average";
        public static final String COLUMN_POPULARITY        = "popularity";
        public static final String COLUMN_OVERVIEW          = "overview";
        public static final String COLUMN_REVIEWS           = "reviews";
        public static final String COLUMN_TRAILERS          = "trailers";
        public static final String COLUMN_FAVOURITE        = "favourite";
    }
}

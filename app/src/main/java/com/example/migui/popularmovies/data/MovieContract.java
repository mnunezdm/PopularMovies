package com.example.migui.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.migui.popularmovies.ui.ActivityBillboard;

public class MovieContract {

    static final String  CONTENT_AUTHORITY       = "com.example.migui.popularmovies";
    private static final Uri BASE_CONTENT_URI    = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String  PATH_MOVIES             = "movies";
    public static final String  PATH_FAVOURITES         = "favourites";
    static final String  PATH_TOP_RATED          = "top_rated";
    static final String  PATH_POPULAR            = "popular";

    public static final class MovieEntry implements BaseColumns {

        static final String TABLE_NAME           = "movies";

        public static final String COLUMN_FRONT_POSTER  = "front_poster";
        public static final String COLUMN_TITLE         = "name";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_RELEASE_DATE  = "release_date";
        public static final String COLUMN_VOTE_AVERAGE  = "vote_average";
        public static final String COLUMN_VOTE_NUMBER = "popularity";
        public static final String COLUMN_OVERVIEW      = "overview";
//        public static final String COLUMN_REVIEWS       = "reviews";
//        public static final String COLUMN_TRAILERS      = "trailers";
        public static final String COLUMN_FAVOURITE     = "favourite";
        static final String COLUMN_POSITION_TOP  = "position_top";
        static final String COLUMN_POSITION_POP  = "position_pop";


        public static String getSelection(ActivityBillboard.SORT_TYPE sort) {
            switch (sort) {
                case POPULAR:
                    return COLUMN_POSITION_POP + " >= 0";
                case TOP_RATED:
                    return COLUMN_POSITION_TOP + " >= 0";
                case FAVOURITES:
                    return COLUMN_FAVOURITE + " == 1";
                default:
                    return null;
            }
        }

        public static String getOrderBy(ActivityBillboard.SORT_TYPE sort) {
            switch (sort) {
                case POPULAR:
                    return COLUMN_POSITION_POP + " ASC";
                case TOP_RATED:
                    return COLUMN_POSITION_TOP + " ASC";
                default:
                    return null;
            }
        }

        public static Uri getUri(ActivityBillboard.SORT_TYPE sort) {
            switch (sort) {
                case POPULAR:
                    return BASE_CONTENT_URI
                            .buildUpon().appendPath(PATH_MOVIES).appendPath(PATH_POPULAR).build();
                case TOP_RATED:
                    return BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES)
                            .appendPath(PATH_TOP_RATED).build();
                default:
                    return BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
            }
        }
    }
}

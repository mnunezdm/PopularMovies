package com.example.migui.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.migui.popularmovies.data.MovieContract.MovieEntry;

class MovieDBHelper extends SQLiteOpenHelper{

    private static  final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE         = "CREATE TABLE " + MovieEntry.TABLE_NAME +
                " (" +
                MovieEntry._ID                      + " STRING PRIMARY KEY, "   +
                MovieEntry.COLUMN_FRONT_POSTER      + " STRING NOT NULL, "   +
                MovieEntry.COLUMN_TITLE             + " STRING NOT NULL, "   +
                MovieEntry.COLUMN_ORIGINAL_TITLE    + " STRING NOT NULL, "   +
                MovieEntry.COLUMN_RELEASE_DATE      + " STRING NOT NULL, "   +
                MovieEntry.COLUMN_VOTE_AVERAGE      + " DECIMAL NOT NULL, "  +
                MovieEntry.COLUMN_VOTE_NUMBER       + " DECIMAL NOT NULL, "  +
                MovieEntry.COLUMN_OVERVIEW          + " STRING NOT NULL, "   +
                MovieEntry.COLUMN_FAVOURITE         + " INTEGER DEFAULT -1, " +
                MovieEntry.COLUMN_POSITION_TOP      + " INTEGER DEFAULT -1, " +
                MovieEntry.COLUMN_POSITION_POP      + " INTEGER DEFAULT -1"   +
//                MovieEntry.COLUMN_REVIEWS           + " STRING, "   +
//                MovieEntry.COLUMN_TRAILERS          + " STRING"   +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
    }
}

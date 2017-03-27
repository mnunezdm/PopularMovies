package com.example.migui.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.migui.popularmovies.data.MovieContract.MovieEntry;

public class MovieDBHelper extends SQLiteOpenHelper{

    public static  final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 0;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_FRONT_POSTER      + " STRING NOT NULL, " +
                MovieEntry.COLUMN_NAME              + " STRING NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_NAME     + " STRING NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE      + " STRING NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE      + " DECIMAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY        + " DECIMAL NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW          + " STRING NOT NULL, " +
                MovieEntry.COLUMN_REVIEWS           + " STRING NOT NULL, " +
                MovieEntry.COLUMN_TRAILERS          + " STRING NOT NULL, " +
                MovieEntry.COLUMN_FAVOURITE         + " STRING NOT NULL, " +

                " UNIQUE (" + MovieEntry._ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
    }
}

package com.example.migui.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_POPULAR = 101;
    public static final int CODE_MOVIES_TOP_RATED = 102;
    public static final int CODE_MOVIES_FAVOURITE = 103;
    public static final int CODE_MOVIES_UNIQUE = 104;
    public static final int CODE_MOVIES_UNIQUE_FAV = 105;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, CODE_MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#",
                CODE_MOVIES_UNIQUE);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#/"
                + MovieContract.PATH_FAVOURITES, CODE_MOVIES_UNIQUE_FAV);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/" +
                MovieContract.PATH_POPULAR, CODE_MOVIES_POPULAR);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/" +
                MovieContract.PATH_TOP_RATED, CODE_MOVIES_TOP_RATED);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/" +
                MovieContract.PATH_FAVOURITES, CODE_MOVIES_FAVOURITE);
        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int uriType = sUriMatcher.match(uri);
        if (uriType >= 100 && uriType < 104)
            return db.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, sortOrder);
        else if (uriType == 104) {
            String id = uri.getPathSegments().get(1);
            return db.query(MovieContract.MovieEntry.TABLE_NAME, projection, "_id=?",
                    new String[]{id}, null, null, null, "1");
        } else
            throw new UnsupportedOperationException("You can't query " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (sUriMatcher.match(uri) == CODE_MOVIES_UNIQUE_FAV) {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            String id = uri.getPathSegments().get(1);
            values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 1);
            return db.update(MovieContract.MovieEntry.TABLE_NAME, values, "_id=?",
                    new String[]{id});
        } else
            throw new UnsupportedOperationException("You can't update " + uri);
        // TODO implement update
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int uriType = sUriMatcher.match(uri);
        if (uriType == CODE_MOVIES_POPULAR || uriType == CODE_MOVIES_TOP_RATED) {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();
            int rowsInserted = 0;
            try {
                String column = (uriType == CODE_MOVIES_POPULAR) ?
                        MovieContract.MovieEntry.COLUMN_POSITION_POP :
                        MovieContract.MovieEntry.COLUMN_POSITION_TOP;
                resetPosition(column, db);
                bulkInsert(column, db, values);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return rowsInserted;
        } else
            return super.bulkInsert(uri, values);
    }

    private void resetPosition(String column, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(column, -1);
        db.update(MovieContract.MovieEntry.TABLE_NAME, cv, null, null);
    }

    private int bulkInsert(String column, SQLiteDatabase db, ContentValues[] values) {
        int rowsInserted = 0;
        for (ContentValues value : values) {
            value.put(column, rowsInserted);
            try {
                db.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, value);
            } catch (SQLiteConstraintException e) {
                db.update(MovieContract.MovieEntry.TABLE_NAME, value, "_id=?",
                        new String[]{value.getAsString(MovieContract.MovieEntry._ID)});
            }
        }
        return rowsInserted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

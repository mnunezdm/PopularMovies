package com.example.migui.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_POPULAR = 101;
    public static final int CODE_MOVIES_TOP_RATED = 102;
    public static final int CODE_MOVIES_FAVOURITE = 103;
    public static final int CODE_MOVIES_UNIQUE = 104;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, CODE_MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#",
                CODE_MOVIES_UNIQUE);
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
        }
        else
            throw new UnsupportedOperationException("You can query " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // TODO implement update
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int uriType = sUriMatcher.match(uri);
        if (uriType == CODE_MOVIES_POPULAR || uriType == CODE_MOVIES_TOP_RATED) {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();
            int rowsInserted = 0;
            try {
                if (uriType == CODE_MOVIES_POPULAR)
                    for (ContentValues value : values) {
                        value.put(MovieContract.MovieEntry.COLUMN_POSITION_POP, rowsInserted);
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                else
                    for (ContentValues value : values) {
                        value.put(MovieContract.MovieEntry.COLUMN_POSITION_TOP, rowsInserted);
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            if (rowsInserted > 0)
                getContext().getContentResolver().notifyChange(uri, null);
            return rowsInserted;
        } else
            return super.bulkInsert(uri, values);
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

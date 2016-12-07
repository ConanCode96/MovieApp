package com.conan.app.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Conan on 11/25/2016.
 */

public class MovieProvider extends ContentProvider {

    MovieDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        retCursor =
                mDbHelper.getReadableDatabase().query(MovieContract.Movie.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id;
        Uri returnUri;

        id = db.insert(MovieContract.Movie.TABLE_NAME, null, values);

        if(id > 0)
            returnUri = MovieContract.Movie.buildMovieUri(id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);

        getContext().getContentResolver().notifyChange(uri, null);

        if(db.isDbLockedByCurrentThread())
            db.close();

        return returnUri;

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();

        int rowsInserted = 0;

        try {

            for (ContentValues value : values) {

                long id = db.insert(MovieContract.Movie.TABLE_NAME, null, value);

                if (id > 0)
                    rowsInserted++;
            }

            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);

        if(db.isDbLockedByCurrentThread())
            db.close();

        return rowsInserted;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = 0;

        rowsUpdated = db.update(MovieContract.Movie.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        if(db.isDbLockedByCurrentThread())
            db.close(); //fetching trailers and reviews could intersect with the CursorLoader, this avoids Threads intersections!

        return rowsUpdated;
    }
}

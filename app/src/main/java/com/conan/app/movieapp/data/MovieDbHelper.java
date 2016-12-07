package com.conan.app.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Conan on 11/25/2016.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE " +
                MovieContract.Movie.TABLE_NAME + " ( " +
                MovieContract.Movie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.Movie.TITLE + " TEXT NOT NULL, " +
                MovieContract.Movie.POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.Movie.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Movie.VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieContract.Movie.SYNPOSIS + " TEXT NOT NULL, " +
                MovieContract.Movie.TRAILERS + " TEXT , " +
                MovieContract.Movie.REVIEWS + " TEXT , " +
                MovieContract.Movie.IS_POPULAR + " INTEGER NOT NULL , " +
                MovieContract.Movie.IS_TOP_RATED + " INTEGER NOT NULL , " +
                MovieContract.Movie.IS_FAVORABLE + " INTEGER NOT NULL , " +
                MovieContract.Movie.Movie_ID + " INTEGER NOT NULL, " +
                MovieContract.Movie.POPULARITY + " REAL NOT NULL" + " )";

        Log.v("INSIDE CREATE TABLE", CREATE_TABLE + "\n" + MovieContract.Movie.CONTENT_URI + "\n" + MovieContract.Movie.CONTENT_TYPE + "\n" + MovieContract.Movie.CONTENT_ITEM_TYPE);

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
        onCreate(db);
    }
}

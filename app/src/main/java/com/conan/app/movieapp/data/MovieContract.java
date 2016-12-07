package com.conan.app.movieapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Conan on 11/25/2016.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.conan.app.movieapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String MOVIE_PATH = "movie";

    public static final class Movie implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;


        public static final String TABLE_NAME = "movie";

        public static final String TITLE = "title";

        public static final String Movie_ID = "movie_id";

        public static final String POSTER_PATH = "poster_path";

        public static final String RELEASE_DATE = "release_date";

        public static final String VOTE_AVERAGE = "vote_average";

        public static final String SYNPOSIS = "synposis";

        public static final String TRAILERS = "trailers";

        public static final String REVIEWS = "reviews";

        public static final String IS_TOP_RATED = "is_top_rated";

        public static final String IS_POPULAR = "is_popular";

        public static final String IS_FAVORABLE = "is_favorable";

        public static final String POPULARITY = "popularity";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}

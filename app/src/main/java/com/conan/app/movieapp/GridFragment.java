package com.conan.app.movieapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.conan.app.movieapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;


/**
 * Created by Conan on 11/25/2016.
 */

public class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = GridFragment.class.getSimpleName();

    private static final int Movie_LOADER_ID = 0;

    public boolean NO_INTERNET_CONNECTION = false;

    private final String POSITION = "POSITION";
    private final String ORDER = "SORT_ORDER";

    private int mPosition = GridView.INVALID_POSITION;
    private String SORT_ORDER = "popular";
    private MovieAdapter mAdapter;
    private GridView mGrid;
    private int PAGE_MAX = 1;
    private int curPAGE = 1;


    public String getTitle() {

        switch (SORT_ORDER) {
            case "popular":
                return getString(R.string.popular);
            case "top_rated":
                return getString(R.string.top_rated);
            case "favorite":
                return getString(R.string.favorite);

            default:
                Log.v(LOG_TAG, "Error while editing ActionBar Title!");
                return "";
        }
    }

    public static interface Callback {
        public void onItemSelected(long id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ORDER, SORT_ORDER);
        outState.putInt(POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.grid_fragment, container, false);

        mAdapter = new MovieAdapter(getActivity(), null, 0);

        mGrid = (GridView) rootView.findViewById(R.id.grid_view);

        mGrid.setAdapter(mAdapter);

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cur = (Cursor) parent.getItemAtPosition(position);

                if (cur != null) {
                    ((Callback) getActivity()).onItemSelected(id);
                    mPosition = position;
                }

            }
        });

/*
        mGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (!SORT_ORDER.contentEquals("favorite") && totalItemCount != 0 && (firstVisibleItem + visibleItemCount >= totalItemCount)) {

                    Log.v(LOG_TAG, firstVisibleItem + " " + visibleItemCount + " >= " + totalItemCount);

                    ++curPAGE;
                    updateGrid();
                }
            }
        });

*/


        if (savedInstanceState != null && savedInstanceState.containsKey(ORDER) && savedInstanceState.containsKey(POSITION)){
            SORT_ORDER = savedInstanceState.getString(ORDER);
            mPosition = savedInstanceState.getInt(POSITION);
            //Log.v(LOG_TAG, "retrieved data bacccccccccccccck !!! " + SORT_ORDER + " " + mPosition);
        }
        else
            updateGrid(); //only at first start ^_^

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getTitle());

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(Movie_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.pop_item) {
            SORT_ORDER = "popular";
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.popular);
            updateGrid(); // fetch Popular movies
            return true;
        }
        else if (item.getItemId() == R.id.topR_item) {
            SORT_ORDER = "top_rated";
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.top_rated);
            updateGrid(); // fetch Top-Rated
            return true;
        }
        else if (item.getItemId() == R.id.favorite_item) {
            SORT_ORDER = "favorite";
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.favorite);
            restartLoader();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    void restartLoader() {
        getLoaderManager().restartLoader(Movie_LOADER_ID, null, this); //restart The Loader.
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                MovieContract.Movie.CONTENT_URI,
                null, // we'll need all the fields
                ((SORT_ORDER.contentEquals("popular")) ? MovieContract.Movie.IS_POPULAR : ((SORT_ORDER.contentEquals("top_rated")) ? MovieContract.Movie.IS_TOP_RATED : MovieContract.Movie.IS_FAVORABLE)) + " = 1", // SORTING Property
                null,
                ((SORT_ORDER.contentEquals("popular")) ? (MovieContract.Movie.POPULARITY + " DESC") : ((SORT_ORDER.contentEquals("top_rated")) ? (MovieContract.Movie.VOTE_AVERAGE + " DESC") : null)) // SORTING Per type
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if ((data == null || data.getCount() == 0) && !NO_INTERNET_CONNECTION && !SORT_ORDER.contentEquals("favorite")) {
            updateGrid();
        }

        mAdapter.swapCursor(data);

/*
        if (mPosition != GridView.INVALID_POSITION)
            mGrid.smoothScrollToPosition(mPosition);
*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    void updateGrid() {
        if (!NO_INTERNET_CONNECTION && !SORT_ORDER.contentEquals("favorite")) updateImages();
        restartLoader();
    }

    void updateImages() {

        /*
        final Cursor cursor = getContext().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                null,
                MovieContract.Movie.IS_POPULAR + "=1",
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            curPAGE = 1 + (int) Math.floor(cursor.getCount() / 20.0);
            Log.v(LOG_TAG, "dataBase has " + cursor.getCount() + " " + SORT_ORDER);
        }

        cursor.close();
        */

        if (PAGE_MAX == 1 || curPAGE <= PAGE_MAX) {

            FetchMoviesTask<Boolean> fetch_task;

            fetch_task = new FetchMoviesTask<Boolean>() {

                @Override
                Boolean fetchDataFromJson(String json) {

                    if (json == null)
                        return false;

                    JSONObject obj;

                    try {

                        obj = new JSONObject(json);
                        JSONArray results = obj.getJSONArray("results");

                        if (PAGE_MAX == 1)
                            PAGE_MAX = obj.getInt("total_pages"); //avoid unnecessary parsing...

                        int length = results.length();

                        Vector<ContentValues> cvv = new Vector<ContentValues>();

                        int inserted_count = 0;

                        for (int i = 0; i < length; ++i) {

                            JSONObject movie_object = results.getJSONObject(i); //movie_object

                            ContentValues value = new ContentValues();

                            int id = movie_object.getInt("id");
                            String title = movie_object.getString("title");
                            String overView = movie_object.getString("overview");
                            double popularity = movie_object.getDouble("popularity");
                            String poster_path = movie_object.getString("poster_path");
                            String release_date = movie_object.getString("release_date");
                            double vote_average = movie_object.getDouble("vote_average");

                            value.put(MovieContract.Movie.Movie_ID, id);
                            value.put(MovieContract.Movie.POSTER_PATH, Image_fetch_prefix + poster_path);
                            value.put(MovieContract.Movie.TITLE, title);
                            value.put(MovieContract.Movie.RELEASE_DATE, release_date);
                            value.put(MovieContract.Movie.SYNPOSIS, overView);
                            value.put(MovieContract.Movie.VOTE_AVERAGE, vote_average);
                            value.put(MovieContract.Movie.IS_POPULAR, SORT_ORDER.contentEquals("popular") ? 1 : 0);
                            value.put(MovieContract.Movie.IS_TOP_RATED, SORT_ORDER.contentEquals("top_rated") ? 1 : 0);
                            value.put(MovieContract.Movie.IS_FAVORABLE, 0);
                            value.put(MovieContract.Movie.POPULARITY, popularity);

                            Cursor cur = getContext().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                                    new String[]{MovieContract.Movie.IS_POPULAR, MovieContract.Movie.IS_TOP_RATED},
                                    MovieContract.Movie.Movie_ID + "=" + id,
                                    null,
                                    null
                            );

                            //Log.v(LOG_TAG, "cur -> " + cur.getCount() + " duplicates");

                            if (cur == null || cur.getCount() == 0) //make sure there is no duplicate ^_^
                                cvv.add(value);

                            else if (cur != null && cur.getCount() == 1) { //if there's a duplicate with another attribute update the requested sortOrder column if not checked(i.e. equals 0)

                                cur.moveToFirst();

                                int is_pop = cur.getInt(cur.getColumnIndex(MovieContract.Movie.IS_POPULAR));
                                int is_topR = cur.getInt(cur.getColumnIndex(MovieContract.Movie.IS_TOP_RATED));

                                ContentValues update = new ContentValues();

                                if (SORT_ORDER.equals("popular") && is_pop == 0) {
                                    update.put(MovieContract.Movie.IS_POPULAR, 1);
                                    inserted_count += getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, update, MovieContract.Movie.Movie_ID + "=" + id, null);
                                } else if (SORT_ORDER.equals("top_rated") && is_topR == 0) {
                                    update.put(MovieContract.Movie.IS_TOP_RATED, 1);
                                    inserted_count += getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, update, MovieContract.Movie.Movie_ID + "=" + id, null);
                                }

                            }

                            cur.close();
                        }

                        if (cvv.size() > 0) {
                            ContentValues[] values_array = new ContentValues[cvv.size()];
                            cvv.toArray(values_array);
                            inserted_count += getContext().getContentResolver().bulkInsert(MovieContract.Movie.CONTENT_URI, values_array);
                        }

                        //Log.v("Count:", "inserted " + inserted_count);

                    } catch (JSONException e) {
                        Log.v(LOG_TAG, e.getMessage());
                    }

                    return true;
                }

                @Override
                void onTaskCompleted(Boolean state) {
                    if (state == false)
                        NO_INTERNET_CONNECTION = true;
                    else
                        NO_INTERNET_CONNECTION = false;
                }

            };

            fetch_task.execute(SORT_ORDER, String.valueOf(curPAGE));

        }

    }

}

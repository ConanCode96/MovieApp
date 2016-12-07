package com.conan.app.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.conan.app.movieapp.Adapters.Review;
import com.conan.app.movieapp.Adapters.ReviewAdapter;
import com.conan.app.movieapp.Adapters.ReviewsDialog;
import com.conan.app.movieapp.Adapters.Trailer;
import com.conan.app.movieapp.Adapters.TrailersAdapter;
import com.conan.app.movieapp.data.MovieContract;
import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.drawable.btn_star_big_off;

/**
 * Created by Conan on 11/28/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private static long Cursor_ID;

    private boolean NO_INTERNET_CONNECTION = false;

    private View rootView;
    private TextView detail_title;
    private ImageView poster;
    private TextView releaseDate;
    private TextView rating;
    private ImageView fav_icon;
    private TextView overview;
    private LinearListView trailers;
    private LinearListView reviews;
    private CardView trailers_card;
    private CardView reviews_card;

    private TrailersAdapter TA;
    private ReviewAdapter RA;
    private ShareActionProvider mShareActionProvider;

    private final int _id_col = 0;
    private final int title_col = 1;
    private final int poster_path_col = 2;
    private final int release_date_col = 3;
    private final int vote_average_col = 4;
    private final int overview_col = 5;
    private final int trailers_col = 6;
    private final int reviews_col = 7;
    private final int is_pop_col = 8;
    private final int is_topR_col = 9;
    private final int is_fav_col = 10;
    private final int movie_id_col = 11;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detail_menu, menu);

        MenuItem share_item = menu.findItem(R.id.share_item);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share_item);

        if (TA != null)
            mShareActionProvider.setShareIntent(createShareIntent());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle b = getArguments();

        Cursor_ID = b.getLong("ID", 0);

        if (Cursor_ID != 0) {

            /*
            TA = new TrailersAdapter(getContext()); //initializing the adapter
            RA = new ReviewAdapter(getContext()); //same :)
            */

            fetchTrailersAndReviews();

            rootView = inflater.inflate(R.layout.detail_fragment, container, false);

            //inflate our views...
            detail_title = (TextView) rootView.findViewById(R.id.detail_title);
            poster = (ImageView) rootView.findViewById(R.id.detail_poster);
            releaseDate = (TextView) rootView.findViewById(R.id.release_date);
            rating = (TextView) rootView.findViewById(R.id.vote_average);
            fav_icon = (ImageView) rootView.findViewById(R.id.favorite_icon);
            overview = (TextView) rootView.findViewById(R.id.overview);
            trailers = (LinearListView) rootView.findViewById(R.id.trailer_list);
            reviews = (LinearListView) rootView.findViewById(R.id.reviews_list);
            trailers_card = (CardView) rootView.findViewById(R.id.trailers_card);
            reviews_card = (CardView) rootView.findViewById(R.id.reviews_card);

            return rootView;
        }

        return null;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getContext(),
                MovieContract.Movie.CONTENT_URI,
                null,
                MovieContract.Movie._ID + "=" + Cursor_ID,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {

        if (data != null && data.moveToFirst()) {

            //set data...
            overview.setText(data.getString(overview_col));
            detail_title.setText(data.getString(title_col));
            releaseDate.setText((data.getString(release_date_col)).substring(0, 4));
            rating.setText(String.valueOf(data.getDouble(vote_average_col)) + "/10");
            Picasso.with(getContext()).load(data.getString(poster_path_col)).resize(400, 600).into(poster);

            //handling videos
            String T_json;
            if ((T_json = data.getString(trailers_col)) != null && !T_json.equals("NOT FOUND")) {

                try {

                    JSONObject obj = new JSONObject(T_json);

                    JSONArray results = obj.getJSONArray("results");

                    int size = results.length();

                    List<Trailer> list;

                    if (size > 0) {

                        trailers_card.setVisibility(View.VISIBLE); //set visible

                        list = new ArrayList<Trailer>();

                        for (int i = 0; i < size; ++i) {
                            JSONObject trailer = results.getJSONObject(i); //trailer_object

                            String key = trailer.getString("key");
                            String name = trailer.getString("name");
                            String site = trailer.getString("site");

                            Trailer t = new Trailer(key, name, site);

                            list.add(t);
                        }

                        TA = new TrailersAdapter(getContext(), list);
                        trailers.setAdapter(TA);
                        TA.notifyDataSetChanged();

                        //Log.v(LOG_TAG, "Trailers count " + TA.getCount());

                        //apply the share intent ^_^
                        if (mShareActionProvider != null)
                            mShareActionProvider.setShareIntent(createShareIntent());

                        trailers.setOnItemClickListener(new LinearListView.OnItemClickListener() {
                            @Override
                            public void onItemClick(LinearListView parent, View view, int position, long id) {

                                Trailer trailer = TA.getItem(position);

                                //Log.v(LOG_TAG, "site " + trailer.getSite());

                                if (!trailer.getSite().equals("YouTube")) return;

                                String video_key = trailer.getUrl_path();
                                Uri uri = Uri.parse(Trailer.YOUTUBE_URL).buildUpon().appendQueryParameter("v", video_key).build();
                                createViewIntent(uri);
                            }
                        });

                        //Log.v(LOG_TAG, size + " trailers, " + list.size() + " parsed");

                    } else {
                        ContentValues v = new ContentValues();
                        v.put(MovieContract.Movie.TRAILERS, "NOT FOUND");
                        getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, v, MovieContract.Movie._ID + "=" + Cursor_ID, null);
                    }
                } catch (Exception e) {
                    Log.v(LOG_TAG, e.getMessage());
                }

            }

            //handling reviews
            String R_json;
            if ((R_json = data.getString(reviews_col)) != null && !R_json.equals("NOT FOUND")) {

                try {

                    JSONObject obj = new JSONObject(R_json);

                    final JSONArray results = obj.getJSONArray("results");

                    int size = results.length();

                    List<Review> list;

                    if (size > 0) {

                        reviews_card.setVisibility(View.VISIBLE);//set visible

                        list = new ArrayList<Review>();

                        for (int i = 0; i < size; ++i) {
                            JSONObject review = results.getJSONObject(i); //review_object

                            String author = review.getString("author");
                            String content = review.getString("content");
                            String url = review.getString("url");

                            Review r = new Review(author, content, url);

                            list.add(r);
                        }

                        RA = new ReviewAdapter(getContext(), list);
                        reviews.setAdapter(RA);
                        RA.notifyDataSetChanged();

                        reviews.setOnItemClickListener(new LinearListView.OnItemClickListener() {

                            @Override
                            public void onItemClick(LinearListView parent, View view, int position, long id) {

                                Review review = RA.getItem(position);

                                String url = review.getUrl();
                                Uri uri = Uri.parse(url).buildUpon().build();

                                ReviewsDialog dialog = new ReviewsDialog();

                                Bundle bundle = new Bundle();
                                bundle.putString("AUTHOR", review.getAuthor());
                                bundle.putString("CONTENT", review.getContent());
                                bundle.putParcelable("URI", uri);
                                dialog.setArguments(bundle);

                                dialog.show(getActivity().getSupportFragmentManager(), "ReviewsDialog");

                            }
                        });

                        //Log.v(LOG_TAG, size + " reviews, " + list.size() + " parsed");

                    } else {
                        ContentValues v = new ContentValues();
                        v.put(MovieContract.Movie.REVIEWS, "NOT FOUND");
                        getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, v, MovieContract.Movie._ID + "=" + Cursor_ID, null);
                    }

                } catch (Exception e) {
                    Log.v(LOG_TAG, e.getMessage());
                }

            }


            /////////favourite icon

            if (data.getInt(is_fav_col) == 0)
                fav_icon.setImageResource(btn_star_big_off);
            else
                fav_icon.setImageResource(android.R.drawable.btn_star_big_on);

            fav_icon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (data.getInt(is_fav_col) == 0) {
                        ContentValues update = new ContentValues();
                        update.put(MovieContract.Movie.IS_FAVORABLE, 1);
                        getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, update, MovieContract.Movie._ID + "=" + Cursor_ID, null);
                        fav_icon.setImageResource(android.R.drawable.btn_star_big_on);
                        Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();

                    } else {
                        ContentValues update = new ContentValues();
                        update.put(MovieContract.Movie.IS_FAVORABLE, 0);
                        getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, update, MovieContract.Movie._ID + "=" + Cursor_ID, null);
                        fav_icon.setImageResource(btn_star_big_off);
                        Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}


    void createViewIntent(Uri uri) {

        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setData(uri);

        if (viewIntent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(viewIntent);
    }

    Intent createShareIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Trailer.YOUTUBE_URL + "&v=" + TA.getItem(0).getUrl_path());
        return shareIntent;

    }


    void fetchTrailersAndReviews() {

        if (NO_INTERNET_CONNECTION) return; //shortcut ^_^

        Cursor cur = getContext().getContentResolver().query(MovieContract.Movie.CONTENT_URI, null, MovieContract.Movie._ID + "=" + Cursor_ID, null, null);

        if (cur != null)
            cur.moveToFirst();

        if (cur.getString(trailers_col) == null) {

            FetchMoviesTask<Boolean> task = new FetchMoviesTask<Boolean>() {

                @Override
                Boolean fetchDataFromJson(String json) {

                    if (json == null) return false;

                    try {
                        ContentValues trailers = new ContentValues();
                        trailers.put(MovieContract.Movie.TRAILERS, json);

                        getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, trailers, MovieContract.Movie._ID + "=" + Cursor_ID, null);

                    } catch (Exception e) {
                        Log.v(LOG_TAG, e.getMessage());
                    }

                    return true;
                }

                @Override
                void onTaskCompleted(Boolean data) {
                    NO_INTERNET_CONNECTION = !data;
                }
            };

            task.execute(cur.getInt(movie_id_col) + "/videos");

        }

        if (cur.getString(reviews_col) == null) {

            FetchMoviesTask<Boolean> task = new FetchMoviesTask<Boolean>() {

                @Override
                Boolean fetchDataFromJson(String json) {

                    if (json == null) return false;

                    try {

                        ContentValues reviews = new ContentValues();
                        reviews.put(MovieContract.Movie.REVIEWS, json);

                        getContext().getContentResolver().update(MovieContract.Movie.CONTENT_URI, reviews, MovieContract.Movie._ID + "=" + Cursor_ID, null);

                    } catch (Exception e) {
                        Log.v(LOG_TAG, e.getMessage());
                    }

                    return true;
                }

                @Override
                void onTaskCompleted(Boolean data) {
                    NO_INTERNET_CONNECTION = !data;
                }
            };

            task.execute(cur.getInt(movie_id_col) + "/reviews");

        }

        cur.close();

    }

}
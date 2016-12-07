package com.conan.app.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.conan.app.movieapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Conan on 10/21/2016.
 */

public class MovieAdapter extends CursorAdapter {

    MovieAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_image, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView poster;

        poster = (ImageView) view;

        String url = cursor.getString(cursor.getColumnIndex("poster_path"));

        Picasso.with(context).load(url).resize(600, 500).into(poster);

    }
}

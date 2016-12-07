package com.conan.app.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.conan.app.movieapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Conan on 12/1/2016.
 */

public class TrailersAdapter extends BaseAdapter {

    private Context context;
    private List<Trailer> trailers;


    public TrailersAdapter(Context context){
        this.context = context;
        trailers = new ArrayList<Trailer>();
    }

    public TrailersAdapter(Context context, List<Trailer> trailers){
        this.context = context;
        this.trailers = trailers;
    }

    void addAll(List<Trailer> list){
        this.trailers.addAll(list);
    }

    @Override
    public int getCount() {
        return this.trailers.size();
    }

    @Override
    public Trailer getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = null;

        if(convertView == null){

            rootView = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);

            rootView.setTag(new ViewHolder(rootView));
        }

        else
            rootView = convertView;

        ViewHolder vHolder = (ViewHolder) rootView.getTag();

        if(vHolder == null) return null; // just in case the world ends! ^_^

        vHolder.trailer_name.setText(trailers.get(position).getName());
        vHolder.youtube_icon.setImageResource(android.R.drawable.ic_media_play);


        return rootView;
    }

    private static class ViewHolder{

        public ImageView youtube_icon;
        public TextView trailer_name;

        public ViewHolder(View view){
            youtube_icon = (ImageView) view.findViewById(R.id.trailer_image);
            trailer_name = (TextView) view.findViewById(R.id.trailer_name);
        }
    }

}

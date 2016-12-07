package com.conan.app.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.conan.app.movieapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Conan on 12/1/2016.
 */

public class ReviewAdapter extends BaseAdapter {

    private Context context;
    private List<Review> reviews;


    public ReviewAdapter(Context context){
        this.context = context;
        reviews = new ArrayList<Review>();
    }

    public ReviewAdapter(Context context, List<Review> reviews){
        this.context = context;
        this.reviews = reviews;
    }

    void addAll(List<Review> list){
        this.reviews.addAll(list);
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Review getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = null;

        if(convertView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
            rootView.setTag(new ViewHolder(rootView));
        }

        else
            rootView = convertView;

        if(rootView == null) return null; // just in case the world ends at this moment! ^_^

        ViewHolder view = (ViewHolder) rootView.getTag();

        view.author.setText(reviews.get(position).getAuthor());
        view.content.setText(reviews.get(position).getContent());

        return rootView;

    }

    private static class ViewHolder{

        public TextView author;
        public TextView content;

        public ViewHolder(View view){
            author = (TextView) view.findViewById(R.id.review_author);
            content = (TextView) view.findViewById(R.id.review_content);
        }
    }

}

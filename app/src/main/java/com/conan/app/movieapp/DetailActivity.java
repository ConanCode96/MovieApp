package com.conan.app.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Conan on 10/21/2016.
 */

public class DetailActivity extends AppCompatActivity {


    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();

        long id = intent.getLongExtra("ID", 0);

        DetailFragment fragment = new DetailFragment();

        Bundle bundle = new Bundle();

        bundle.putLong("ID", id);

        fragment.setArguments(bundle);

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.detail_frag, fragment).commit();

    }
}

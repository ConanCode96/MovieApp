package com.conan.app.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GridFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    boolean is_Two_Pane_UI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.main_frag, new GridFragment(), "GridFragment").commit();

        if (findViewById(R.id.detail_frag) != null) {
            is_Two_Pane_UI = true;
        }
    }


    @Override
    public void onItemSelected(long id) {

        if (is_Two_Pane_UI) {

            DetailFragment fragment = new DetailFragment();

            Bundle bundle = new Bundle();

            bundle.putLong("ID", id);

            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_frag, fragment).commit();

        } else {

            Intent intent = new Intent(this, DetailActivity.class);

            intent.putExtra("ID", id);

            startActivity(intent);
        }

    }
}




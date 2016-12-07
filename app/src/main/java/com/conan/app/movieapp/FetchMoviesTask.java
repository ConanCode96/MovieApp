package com.conan.app.movieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

abstract class FetchMoviesTask <T> extends AsyncTask<String, Void, T> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    final String base_url = "http://api.themoviedb.org/3/movie/";
    private final String API_key = "8592608b51526e6605053eb6fc793dbb";
    final String Image_fetch_prefix = "http://image.tmdb.org/t/p/w342/";

    @Override
    protected T doInBackground(String... params) {

        Uri.Builder uri_builder = Uri.parse(base_url + params[0] + "?").buildUpon().appendQueryParameter("api_key", API_key);

        if(params.length > 1){
            uri_builder.appendQueryParameter("page", params[1]);
        }

        Uri uri = uri_builder.build();

        Log.v(LOG_TAG, uri.toString());

        URL url;

        try {

            url = new URL(uri.toString());

            String Json = retrieveJson(url);

            if(Json == null)
                return null;

            return fetchDataFromJson(Json);

        }catch (Exception e) {
            Log.v(LOG_TAG + " parsing", e.getMessage());
        }

        return null;
    }

    String retrieveJson(URL url){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String Json = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuffer sb = new StringBuffer("");

            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line + "\n");

            Json = sb.toString();

            if (Json == null)
                return null;


        } catch (Exception e) {
            Log.v(LOG_TAG + " retrieval", e.getMessage());
        }

        finally {

            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return Json;

        }

    }

    abstract T fetchDataFromJson(String json);

    abstract void onTaskCompleted(T data);

    @Override
    protected void onPostExecute(T data) {
        if (data == null)
            return;

        onTaskCompleted(data);
    }

}
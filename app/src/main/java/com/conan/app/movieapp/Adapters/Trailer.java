package com.conan.app.movieapp.Adapters;

/**
 * Created by Conan on 12/1/2016.
 */

public class Trailer {

    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?";
    private String url_path;
    private String name;
    private String site;

    public Trailer(String url_path, String name, String site){
        this.url_path = url_path;
        this.name = name;
        this.site = site;
    }

    public String getUrl_path() {
        return url_path;
    }

    public void setUrl_path(String url_path) {
        this.url_path = url_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {return site;}

    public void setSite(String site) {this.site = site;}
}

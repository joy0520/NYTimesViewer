package com.joy.nytimesviewer.item;

import java.util.ArrayList;

/**
 * Created by joy0520 on 2017/2/22.
 */

public class Article {
    public static final String THUMBNAIL_BASE_URL = "http://www.nytimes.com/";

    class Headline {
        String main;
    }

    class Multimedia {
        String url;
    }

    String web_url;
    Headline headline;
    ArrayList<Multimedia> multimedia;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\narticle {")
                .append("\nheadline : ").append(getHeadline())
                .append("\nweb_url : ").append(getWebUrl())
                .append("\nthumbnailFullUrl : ").append(getThumbnailFullUrl())
                .append(" }");
        return builder.toString();
    }

    public String getHeadline() {
        return headline.main;
    }

    public String getWebUrl() {
        return web_url;
    }

    public String getThumbnailRawUrl() {
        if (multimedia.size() == 0) {
            return "";
        } else {
            return multimedia.get(0).url;
        }
    }

    public String getThumbnailFullUrl() {
        if (getThumbnailRawUrl().isEmpty()) return "";
        return THUMBNAIL_BASE_URL + getThumbnailRawUrl();
    }
}

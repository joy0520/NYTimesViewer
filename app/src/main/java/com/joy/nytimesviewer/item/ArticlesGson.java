package com.joy.nytimesviewer.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy0520 on 2017/2/22.
 */

public class ArticlesGson {
    class Response {
        Meta meta;
        ArrayList<Article> docs;
    }

    class Meta {
        int hits;
        int time;
        int offset;
    }

    public Response response;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("meta : { ")
                .append("\nhits : " + response.meta.hits)
                .append("\ntime : " + response.meta.time)
                .append("\noffset : " + response.meta.offset)
                .append("}\ndocs: [\n");

        for (Article article : response.docs) {
            builder.append(article.toString());
        }
        builder.append("]}");

        return builder.toString();
    }

    public List<Article> getArticles() {
        return response.docs;
    }

    public int getHits() {
        return response.meta.hits;
    }

    public int getOffset() {
        return response.meta.offset;
    }
}

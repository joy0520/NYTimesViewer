package com.joy.nytimesviewer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.joy.nytimesviewer.R;
import com.joy.nytimesviewer.activity.ArticleWebviewActivity;
import com.joy.nytimesviewer.item.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joy0520 on 2017/2/23.
 */

public class ArticlesAdapter extends RecyclerView.Adapter {
    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_NO_THUMBNAIL = 1;

    private Context mContext;
    private List<Article> mArticles;

    public ArticlesAdapter(Context context) {
        mContext = context;
        mArticles = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (mArticles != null && mArticles.get(position) != null) {
            if (mArticles.get(position).getThumbnailRawUrl() != null
                    && !mArticles.get(position).getThumbnailRawUrl().isEmpty()) {
                return ITEM_TYPE_NORMAL;
            } else {
                return ITEM_TYPE_NO_THUMBNAIL;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("onCreateViewHolder", "viewType="+viewType);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View listItem;
        if (viewType == ITEM_TYPE_NORMAL) {
            listItem = inflater.inflate(R.layout.item_article, parent, false);
        } else {
            listItem = inflater.inflate(R.layout.item_article_no_thumbnail, parent, false);
        }

        return new ArticleHolder(listItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArticleHolder) {
            final ArticleHolder articleHolder = (ArticleHolder) holder;
            final Article article = mArticles.get(position);
            // Set up a ArticleHolder
            if (article.getThumbnailRawUrl() != null && !article.getThumbnailRawUrl().isEmpty()) {
                Glide.with(mContext)
                        .load(article.getThumbnailFullUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_error_loading_thumbnail)
                        .into(articleHolder.image/*, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.i("Picasso.load into", "onSuccess()");
                                articleHolder.progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Log.i("Picasso.load into", "onError()");
                                articleHolder.progressBar.setVisibility(View.INVISIBLE);
                            }
                        }*/);
            } else {
                // no thumbnail, do nothing
            }
            if (articleHolder.headline != null) {
                articleHolder.headline.setText(article.getHeadline());
            }
            if (articleHolder.snippet != null) {
                articleHolder.snippet.setText(article.getSnippet());
            }

            // Set up item click listener
            if (article.getWebUrl() != null && !article.getWebUrl().isEmpty()) {
                articleHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ArticleWebviewActivity.class);
                        intent.putExtra(ArticleWebviewActivity.EXTRA_KEY_URL, article.getWebUrl());
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public void addAllArticles(List<Article> articles) {
        mArticles.clear();
        mArticles.addAll(articles);
        notifyDataSetChanged();
    }

    public void addArticles(List<Article> articles) {
        mArticles.addAll(articles);
        notifyDataSetChanged();
    }

    static class ArticleHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.image)
        ImageView image;
        @Nullable
        @BindView(R.id.progress)
        ProgressBar progressBar;
        @Nullable
        @BindView(R.id.headline)
        TextView headline;
        @BindView(R.id.snippet)
        TextView snippet;
        @BindView(R.id.item_container)
        View container;

        public ArticleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

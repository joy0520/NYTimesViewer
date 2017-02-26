package com.joy.nytimesviewer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.joy.nytimesviewer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleWebviewActivity extends AppCompatActivity {
    public static final String EXTRA_KEY_URL = "extra_key_url";
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_webview);
        ButterKnife.bind(this);

        String url = getIntent().getExtras().getString(EXTRA_KEY_URL);
        if (url != null && !url.isEmpty()) {
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            webview.loadUrl(url);
        }
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgress.setVisibility(View.GONE);
            }
        });

        setupToolbar();
    }

    protected void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setLogo(R.drawable.ic_main_viewer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_article_viewer, menu);

        MenuItem item = menu.findItem(R.id.share);
        ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, webview.getUrl());

        miShare.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }
}

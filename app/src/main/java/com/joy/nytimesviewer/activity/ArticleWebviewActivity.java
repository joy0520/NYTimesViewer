package com.joy.nytimesviewer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joy.nytimesviewer.R;

public class ArticleWebviewActivity extends AppCompatActivity {
    public static final String EXTRA_KEY_URL = "extra_key_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_webview);

        WebView webView = (WebView) findViewById(R.id.webview);
        String url = getIntent().getExtras().getString(EXTRA_KEY_URL);
        if (url != null && !url.isEmpty()) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            webView.loadUrl(url);
        }
    }
}

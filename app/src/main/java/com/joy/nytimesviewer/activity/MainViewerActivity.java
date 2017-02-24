package com.joy.nytimesviewer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joy.nytimesviewer.R;
import com.joy.nytimesviewer.adapter.ArticlesAdapter;
import com.joy.nytimesviewer.item.Article;
import com.joy.nytimesviewer.item.ArticlesGson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainViewerActivity extends AppCompatActivity {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.activity_main_viewer)
    RelativeLayout activityMainViewer;

    private ArticlesGson mArticlesGson;
    private List<Article> mArticles;
    private ArticlesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewer);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);
        setupList();

    }

    private void setupList() {
        mArticles = new ArrayList<>();
        mList.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.articles_columns),
                StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new ArticlesAdapter(this);
        mAdapter.setArtices(mArticles);
        mList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_viewer, menu);
        return true;
    }

    public void onArticleSearch(View view) {
        String query = etSearch.getText().toString();

        Toast.makeText(this, "Searching " + query, Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        String baseUrl = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", getString(R.string.nyt_api_key));
        params.put("page", 0);
        params.put("q", query);

        client.get(baseUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("onFailure", "" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("onSuccess", "" + responseString);
                Gson gson = new Gson();
                mArticlesGson = gson.fromJson(responseString, ArticlesGson.class);
                mArticles = mArticlesGson.getArticles();
                mAdapter.setArtices(mArticles);

                Log.i("onSuccess", "mArticles=" + mArticles);
            }
        });
        // Close input manager (virtual keyboard)
        InputMethodManager ipm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ipm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

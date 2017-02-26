package com.joy.nytimesviewer.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joy.nytimesviewer.R;
import com.joy.nytimesviewer.adapter.ArticlesAdapter;
import com.joy.nytimesviewer.item.Article;
import com.joy.nytimesviewer.item.ArticlesGson;
import com.joy.nytimesviewer.setting.SettingDialog;
import com.joy.nytimesviewer.setting.SettingModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.joy.nytimesviewer.setting.SettingModel.SORTED_BY_OLDEST;

public class MainViewerActivity extends AppCompatActivity implements SettingDialog.Callback {
    private static final long DELAY_LOADING_NEXT_PAGE_MS = 2000;
    private static final int NUM_ARTICLES_PER_PAGE = 10;
    private static final int NUM_PAGES_LIMIT = 100;

    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.activity_main_viewer)
    RelativeLayout activityMainViewer;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private ArticlesGson mArticlesGson;
    private List<Article> mArticles;
    private ArticlesAdapter mAdapter;
    private SettingModel mSettingModel;
    private int mCurrentLoadingPage;
    private String mCurrentSearchQuery;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewer);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);
        setupList();
        setupToolbar();
        mSettingModel = new SettingModel();
        mHandler = new Handler();
    }

    private void setupList() {
        mArticles = new ArrayList<>();
        mList.setLayoutManager(new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.articles_columns),
                StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new ArticlesAdapter(this);
        mAdapter.addAllArticles(mArticles);
        mList.setAdapter(mAdapter);
    }

    protected void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setLogo(R.drawable.ic_main_viewer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_viewer, menu);
        // Setup search item
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mCurrentSearchQuery = query;
                // perform a new query
                searchView.clearFocus();
                onArticleSearch(query);

                // Close input manager (virtual keyboard)
                InputMethodManager ipm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus().getWindowToken() != null) {
                    ipm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_setting: // Setup setting item
                showSettingDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onArticleSearch(final String query, final int page) {
        Log.i("onArticleSearch()", query + ", page=" + page);
        AsyncHttpClient client = new AsyncHttpClient();
        String baseUrl = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", getString(R.string.nyt_api_key));
        params.put("page", page);
        params.put("q", query);
        mCurrentLoadingPage = page;
        // Include SettingModel begin date
        params.put("begin_date", SettingModel.toQueryString(mSettingModel.date));
        // Include sort order
        params.put("sort", mSettingModel.sorted_by == SORTED_BY_OLDEST
                ? getString(R.string.setting_query_sort_arg_oldest)
                : getString(R.string.setting_query_sort_arg_newest));
        // Include news desk
        if (mSettingModel.news_desk.size() != 0) {
            params.put("fq", SettingModel.newsDeskToQueryString(mSettingModel.news_desk));
        }

        client.get(baseUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("onFailure", "" + responseString);
                // Hide the progress bar
                mProgress.setVisibility(View.GONE);

                Toast.makeText(MainViewerActivity.this,
                        "Loading error:\n" + responseString, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                mArticlesGson = gson.fromJson(responseString, ArticlesGson.class);
                Log.i("onArticleSearch().onSuccess()", "mArticlesGson=" + mArticlesGson);

                mArticles.addAll(mArticlesGson.getArticles());
                mAdapter.addArticles(mArticlesGson.getArticles());
                mAdapter.notifyDataSetChanged();

                // Hide the progress bar
                mProgress.setVisibility(View.GONE);

                // If there are more articles, load them!
                if (mArticlesGson.getHits() / NUM_ARTICLES_PER_PAGE > mCurrentLoadingPage + 1
                         && mCurrentLoadingPage +1 < NUM_PAGES_LIMIT) {
                    // But with a delay, to reduce query times
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onArticleSearch(query, page + 1);
                        }
                    }, DELAY_LOADING_NEXT_PAGE_MS);
                }
            }
        });
    }

    private void onArticleSearch(String query) {
        Toast.makeText(MainViewerActivity.this, "Searching " + query, Toast.LENGTH_SHORT)
                .show();
        // Show the progress bar
        mProgress.setVisibility(View.VISIBLE);
        // Clear old date set
        mArticles.clear();
        mAdapter.addAllArticles(mArticles); // Set an empty array to clear the array in adapter
        mAdapter.notifyDataSetChanged();
        onArticleSearch(query, 0);
    }


    private void showSettingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingDialog dialog = SettingDialog.newInstance(getString(R.string.setting_dialog_title));
        dialog.setCallback(this);
        dialog.setSettingModel(mSettingModel);
        dialog.show(fm, "fragment_setting_dialog");
    }

    @Override
    public void onSettingDialogFinished(SettingModel model) {
        mSettingModel = model;
        Log.i("MainViewer.onSettingDialogFinished()", "mSettingModel=" + mSettingModel);
        // Query a search again after filter changed
        onArticleSearch(mCurrentSearchQuery);
    }
}

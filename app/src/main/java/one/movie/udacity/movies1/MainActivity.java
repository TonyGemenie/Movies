package one.movie.udacity.movies1;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import one.movie.udacity.movies1.Adapter.PosterRecycler;


public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<String>,
        PosterRecycler.vHClickListener{

    public static final String RESULTS = "results";
    public static final String JSON_STRING = "json_string";
    private static final String API_KEY = "e95fd204f344fdf4253d4a02a51ca31f" ;
    private static final String POSITION = "position";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final int SEARCH_LOADER = 1000;
    public static final String SEARCH_EXTRA_TOP_RATED = "search_extra_top_rated";
    public static final String SEARCH_EXTRA_POPULAR = "search_extra_popular";
    public static final String MOVIE_DB_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    public static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    public static final String API = "?api_key=";
    public static final String SAVED_STRING = "saved_string";
    public static final String IMAGE_SIZE = "w780";
    private static final String FAVORITES = "favorites";
    private static final String SEARCH_EXTRA_FAVORITES = "search_extra_favorites";
    public ArrayList<JSONObject> movies = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public boolean preferenceChanged;

    @BindView(R.id.poster_list) RecyclerView posterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        movies = new ArrayList<>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        getWindow().setExitTransition(new Explode());

        if(savedInstanceState != null){
            getLoaderManager().restartLoader(SEARCH_LOADER, createBundle(), this);
        }else {
            getMovieDB();
        }
    }

    public void getMovieDB(){
        getLoaderManager().initLoader(SEARCH_LOADER, createBundle(), this);
    }

    public Bundle createBundle(){
        Boolean popularSearch = sharedPreferences.getBoolean(POPULAR, true);
        Boolean topRatedSearch = sharedPreferences.getBoolean(TOP_RATED, true);
        Boolean favoritesSearch = sharedPreferences.getBoolean(FAVORITES, false);
        Bundle searchBundle = new Bundle();
        searchBundle.putBoolean(SEARCH_EXTRA_TOP_RATED, topRatedSearch);
        searchBundle.putBoolean(SEARCH_EXTRA_POPULAR, popularSearch);
        searchBundle.putBoolean(SEARCH_EXTRA_FAVORITES, favoritesSearch);
        return searchBundle;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        preferenceChanged = true;
        getLoaderManager().restartLoader(SEARCH_LOADER, createBundle(), this);
    }

    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (sharedPreferences.getString(SAVED_STRING , "").contains(RESULTS) && !preferenceChanged) {
                    deliverResult(sharedPreferences.getString(SAVED_STRING, ""));
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String responseString = "";
                try {
                    String topRatedString = null;
                    String popularString = null;
                    String favoritesString = null;
                    if (args.getBoolean(SEARCH_EXTRA_POPULAR)) {
                        OkHttpClient client = new OkHttpClient();

                        MediaType mediaType = MediaType.parse("application/octet-stream");
                        RequestBody body = RequestBody.create(mediaType, "{}");
                        Request request = new Request.Builder()
                                .url(MOVIE_DB_BASE + getString(R.string.popular_key) + API + API_KEY)
                                .get()
                                .build();

                        Response response = client.newCall(request).execute();
                        popularString = response.body().string();
                    }
                    if (args.getBoolean(SEARCH_EXTRA_TOP_RATED)) {
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(MOVIE_DB_BASE + getString(R.string.top_rated_key) + API + API_KEY)
                                .get()
                                .build();

                        Response response = client.newCall(request).execute();
                        topRatedString = response.body().string();
                    }
                    if (args.getBoolean(SEARCH_EXTRA_FAVORITES)){
                        int size = sharedPreferences.getInt(DetailsActivity.SIZE, 0);
                        for (int i = 0; i < size ; i++) {
                            favoritesString += sharedPreferences.getString(DetailsActivity.FAVORITE_STRING, "");
                        }
                    }


                    if(topRatedString != null && popularString != null){
                        popularString = popularString.substring(popularString.lastIndexOf("results\":[")).substring(10);
                        topRatedString = topRatedString.substring(0,topRatedString.length() - 2) + ",";
                        responseString = topRatedString + popularString + favoritesString;
                    }else {
                        if(topRatedString != null) {
                            responseString = topRatedString + favoritesString;
                        }
                        if(popularString != null) {
                            responseString = popularString + favoritesString;
                        }
                        if(topRatedString == null && popularString == null){
                            responseString = "{results:\"[" + favoritesString + "}";
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                return responseString;
            }

            @Override
            public void deliverResult(String response) {
                super.deliverResult(response);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (null == data) {
            showErrorMessage();
        } else {
            try {
                JSONObject obj = new JSONObject(data);
                JSONArray arr = obj.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                movies.add((JSONObject) arr.get(i));
            }
            sharedPreferences.edit().putString(SAVED_STRING, data).apply();
            preferenceChanged = false;
            loadPosters();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    public void showErrorMessage(){

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_STRING, RESULTS);
    }

    public void loadPosters() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        posterList.setLayoutManager(staggeredGridLayoutManager);

        String[] posters = new String[movies.size()];
        for (int i = 0; i < movies.size(); i++) {
            try {
                posters[i] = movies.get(i).getString("poster_path");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        PosterRecycler posterRecycler = new PosterRecycler(posters, this, this);
        posterList.setAdapter(posterRecycler);
        posterList.scrollToPosition(sharedPreferences.getInt(POSITION, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPosterClicked(int clickedPosition, View v) {
        if(clickedPosition >= movies.size()){
            clickedPosition = clickedPosition % movies.size();
        }
        sharedPreferences.edit().putInt(POSITION, clickedPosition).apply();
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(JSON_STRING, movies.get(clickedPosition).toString());
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, v, "poster");
        startActivity(intent, options.toBundle());
    }
}

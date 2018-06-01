package one.movie.udacity.movies1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import one.movie.udacity.movies1.Adapter.PosterRecycler;


public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        PosterRecycler.vHClickListener{

    public static final String RESULTS = "results";
    public static final String JSON_STRING = "json_string";
    private static final String POSITION = "position";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String MOVIE_DB_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    public static final String SAVED_STRING = "saved_string";
    public static final String IMAGE_SIZE = "w185";
    public ArrayList<JSONObject> movies = new ArrayList<>();
    String[] posters = null;
    private LiveDataMovieModel mLiveDataMovieModel;
    SharedPreferences sharedPreferences;
    private PosterRecycler posterRecycler;

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

        mLiveDataMovieModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataMovieModel.class);

        if(savedInstanceState != null){
            createPosterList(sharedPreferences.getString(SAVED_STRING, ""));
            createRecycler();
        }
        subscribe();
    }

    private void subscribe() {
        final Observer<String> movieObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                createPosterList(s);
                if(posterRecycler != null) {
                    posterRecycler.notifyDataSetChanged();
                }
            }
        };
        mLiveDataMovieModel.getMovies().observe(this, movieObserver);
    }

    private void createPosterList(String s){
        if (s != null) {
            try {
                JSONObject obj = new JSONObject(s);
                JSONArray arr = obj.getJSONArray("results");
                posters = new String[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    movies.add((JSONObject) arr.get(i));
                    posters[i] = movies.get(i).getString("poster_path");
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            sharedPreferences.edit().putString(SAVED_STRING, s).apply();
        }
    }

    public void createRecycler() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        posterList.setLayoutManager(staggeredGridLayoutManager);
        posterRecycler = new PosterRecycler(posters, this, this);
        posterList.setAdapter(posterRecycler);
        posterList.scrollToPosition(sharedPreferences.getInt(POSITION, 0));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences s, String key) {

        //SharedPreferences not being set Correctly
        sharedPreferences.edit().putBoolean(key, true).apply();
        mLiveDataMovieModel.setPreferences(sharedPreferences.getBoolean(key, true), key);
        mLiveDataMovieModel.getMovies();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_STRING, RESULTS);
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

        //Unable to Test DetailsActivity
        sharedPreferences.edit().putInt(POSITION, clickedPosition).apply();
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(JSON_STRING, movies.get(clickedPosition).toString());
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, v, "poster");
        startActivity(intent, options.toBundle());
    }

    public void setFavorites(){
        int size = sharedPreferences.getInt(DetailsActivity.SIZE, 0);
        String favorites = null;
        for (int i = 0; i < size; i++) {
            favorites = sharedPreferences.getString(DetailsActivity.FAVORITE_STRING, "");
        }
        mLiveDataMovieModel.setFavorites(favorites);
    }
}

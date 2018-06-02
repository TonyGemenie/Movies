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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Adapter.PosterRecycler;
import one.movie.udacity.movies1.Database.MovieDetails;


public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        PosterRecycler.vHClickListener{

    public static final String RESULTS = "results";
    public static final String MOVIE_ID = "json_string";
    private static final String POSITION = "position";
    public static final String MOVIE_DB_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    public static final String SAVED_STRING = "saved_string";
    public static final String IMAGE_SIZE = "w185";
    private LiveDataMovieModel mLiveDataMovieModel;
    SharedPreferences sharedPreferences;
    private PosterRecycler posterRecycler;

    @BindView(R.id.poster_list) RecyclerView posterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        getWindow().setExitTransition(new Explode());

        //LiveData not set correctly
        mLiveDataMovieModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataMovieModel.class);
        createRecycler();
        subscribe();
    }

    private void subscribe() {
        final Observer<List<MovieDetails>> movieObserver = new Observer<List<MovieDetails>>() {
            @Override
            public void onChanged(@Nullable List<MovieDetails> movieDetails) {
                posterRecycler.setList(movieDetails);
            }
        };
        mLiveDataMovieModel.getMovies().observe(this, movieObserver);
    }

    public void createRecycler() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        posterList.setLayoutManager(staggeredGridLayoutManager);
        posterRecycler = new PosterRecycler(this, this);
        posterList.setAdapter(posterRecycler);
        posterList.scrollToPosition(sharedPreferences.getInt(POSITION, 0));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences s, String key) {
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
    public void onPosterClicked(int id, View v) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(MOVIE_ID, id);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "poster");
        startActivity(intent, options.toBundle());
    }

}

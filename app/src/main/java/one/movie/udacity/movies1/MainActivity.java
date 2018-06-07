package one.movie.udacity.movies1;

import android.arch.lifecycle.MediatorLiveData;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Adapter.PosterRecycler;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        PosterRecycler.vHClickListener{

    public static final String RESULTS = "results";
    public static final String MOVIE_ID = "json_string";
    private static final String POSITION = "position";
    public static final String MOVIE_DB_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    public static final String SAVED_STRING = "saved_string";
    public static final String IMAGE_SIZE = "w185";
    public static final String KEY = "key";
    private static final String TAG = "tag";
    private LiveDataPopularModel mLiveDataPopularModel;
    private LiveDataTopRatedModel mLiveDataTopRatedModel;
    private LiveDataFavoriteModel mLiveDataFavoriteModel;
    SharedPreferences s;
    private PosterRecycler posterRecycler;
    MovieDatabase movieDatabase;

    @BindView(R.id.poster_list) RecyclerView posterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        Timber.i("MainActivity: Start");

        ButterKnife.bind(this);

        s = PreferenceManager.getDefaultSharedPreferences(this);
        s.registerOnSharedPreferenceChangeListener(this);

        getWindow().setExitTransition(new Explode());
        mLiveDataPopularModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataPopularModel.class);
        mLiveDataTopRatedModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataTopRatedModel.class);
        mLiveDataFavoriteModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataFavoriteModel.class);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                movieDatabase = MovieDatabase.getInstance(getApplication());
                mLiveDataPopularModel.getPopular().postValue(movieDatabase.movieDao().loadPopular());
                mLiveDataTopRatedModel.getTopRated().postValue(movieDatabase.movieDao().loadTopRated());
                mLiveDataFavoriteModel.getFavorites().postValue(movieDatabase.movieDao().loadFavorites());
                createMediator();
            }
        });
        startMovieService();
        createRecycler();
        Timber.i("MainActivity: Stop");
    }


    public void createRecycler() {
        Timber.i("createRecycler: start");
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        posterList.setLayoutManager(staggeredGridLayoutManager);
        posterRecycler = new PosterRecycler(this, this);
        posterList.setAdapter(posterRecycler);
        posterList.scrollToPosition(s.getInt(POSITION, 0));
        Timber.i("createRecycler: stop");
    }

    public void startMovieService(){
        Intent intent = new Intent(MainActivity.this, RetrieveWebDataService.class);
        intent.putExtra(KEY, getString(R.string.moviedb_api_key)).putExtra(DetailsActivity.MOVIE_ID, 0);
        startService(intent);
    }

    Observer<List<MovieDetails>> posterObserver = new Observer<List<MovieDetails>>() {
        @Override
        public void onChanged(@Nullable List<MovieDetails> movieDetails) {
            Timber.i("posterObserver: Called");
            posterRecycler.setList(movieDetails);
        }
    };

    public void createMediator() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Timber.i("createMediator: Runnable: Start");
                List<MovieDetails> list = null;
                if (s.getBoolean(getString(R.string.popular_key), true)) {
                    list = movieDatabase.movieDao().loadPopular();
                }
                if (s.getBoolean(getString(R.string.top_rated_key), true)) {
                    List<MovieDetails> movies = movieDatabase.movieDao().loadTopRated();
                    for (int i = 0; i < movies.size(); i++) {
                        list.add(movies.get(i));
                    }
                }
                if (s.getBoolean(getString(R.string.favorites_key), true)) {
                    for (MovieDetails movie : movieDatabase.movieDao().loadFavorites()) {
                        if (!list.contains(movie)) {
                            list.add(movie);
                        }
                    }
                }
                final List<MovieDetails> slist = list;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(slist != null) {
                            posterRecycler.setList(slist);
                        }
                    }
                });
                Timber.i("createMediator: Runnable: Stop");
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences s, String key) {
        createMediator();
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

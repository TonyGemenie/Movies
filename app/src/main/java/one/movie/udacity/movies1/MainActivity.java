package one.movie.udacity.movies1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
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
    public static final String SEARCH_ID = "search_id";
    public static final String BROADCAST_ACTION = "broadcast_action";
    private LiveDataMovieModel mLiveDataMovieModel;
    int searchId;
    SharedPreferences s;
    private PosterRecycler posterRecycler;
    private ArrayList<Integer> movieId = new ArrayList<>();
    private ArrayList<String> moviePosterPath = new ArrayList<>();

    @BindView(R.id.poster_list) RecyclerView posterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new myTree());
        Timber.i("MainActivity: Start");

        ButterKnife.bind(this);

        s = PreferenceManager.getDefaultSharedPreferences(this);
        s.registerOnSharedPreferenceChangeListener(this);

        //UnNecessary Code Added {Android Architecture Components Learning Protocol}
        DownloadMovieReciever downloadStockReciever = new DownloadMovieReciever();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadStockReciever, intentFilter);
        setSearchId();

        //Android Arch Components {Will Work Code}
        /*mLiveDataMovieModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataMovieModel.class);
        mLiveDataMovieModel.getMovies().observe(this, posterObserver);
        setPosterList();*/

        startMovieService();
        createRecycler();
        getWindow().setExitTransition(new Explode());
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
        intent.putExtra(KEY, getString(R.string.moviedb_api_key)).putExtra(DetailsActivity.MOVIE_ID, 0).putExtra(SEARCH_ID, searchId);
        startService(intent);
    }

    //Android Arch Components {Will Work Code}
    /*Observer<List<MovieDetails>> posterObserver = new Observer<List<MovieDetails>>() {
        @Override
        public void onChanged(@Nullable List<MovieDetails> movieDetails) {
            Timber.i("posterObserver: Called");
            posterRecycler.setList(movieDetails);
        }
    };*/

    //UnNecessary Code Added {Android Architecture Components Learning Protocol}
    public void setSearchId(){
        if(s.getBoolean(getString(R.string.popular_key), true) && s.getBoolean(getString(R.string.top_rated_key), true)
        && s.getBoolean(getString(R.string.favorites_key), true)){
            searchId = 1000;
        }
        if(s.getBoolean(getString(R.string.popular_key), true) && s.getBoolean(getString(R.string.top_rated_key), true)) {
            searchId = 2000;
        }
        if(s.getBoolean(getString(R.string.popular_key), true)) {
            searchId = 3000;
        }
        if(s.getBoolean(getString(R.string.top_rated_key), true)) {
            searchId = 4000;
        }
        if(s.getBoolean(getString(R.string.popular_key), true) && s.getBoolean(getString(R.string.favorites_key), true)) {
            searchId = 5000;
        }
        if(s.getBoolean(getString(R.string.top_rated_key), true) && s.getBoolean(getString(R.string.favorites_key), true)) {
            searchId = 6000;
        }
        if(s.getBoolean(getString(R.string.favorites_key), true)) {
            searchId = 7000;
        }
    }


    //Android Arch Components {Will Work Code}
    public void setPosterList(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
            Timber.i("createMediator: Runnable: Start");
            MovieDatabase movieDatabase = MovieDatabase.getInstance(getApplication());
            final List<MovieDetails> list = movieDatabase.movieDao().loadFavorites();
            if (s.getBoolean(getString(R.string.popular_key), true) && movieDatabase.movieDao().loadPopular().size() > 0) {
                list.addAll(movieDatabase.movieDao().loadPopular());
            }
            if (s.getBoolean(getString(R.string.top_rated_key), true) && movieDatabase.movieDao().loadTopRated().size() > 0) {
                MovieDetails movie = movieDatabase.movieDao().loadMovieID(268);
                list.addAll(movieDatabase.movieDao().loadTopRated());
            }
            if (s.getBoolean(getString(R.string.favorites_key), true) && movieDatabase.movieDao().loadFavorites().size() > 0) {
                list.addAll(movieDatabase.movieDao().loadFavorites());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLiveDataMovieModel.getMovies().setValue(list);
                }
            });
            Timber.i("createMediator: Runnable: Stop");
            }
        });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences s, String key) {
        setSearchId();
        startMovieService();
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
        intent.putExtra(MOVIE_ID, movieId.get(id));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "poster");
        startActivity(intent, options.toBundle());
    }

    //UnNecessary Code Added {Android Architecture Components Learning Protocol}
    public class DownloadMovieReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            moviePosterPath = getIntent().getStringArrayListExtra(KEY);
            movieId = getIntent().getIntegerArrayListExtra(IMAGE_SIZE);

            posterRecycler.setList(moviePosterPath);
        }
    }
}

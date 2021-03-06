package one.movie.udacity.movies1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
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

    public static final String MOVIE_ID = "json_string";
    private static final String POSITION = "position";
    public static final String MOVIE_DB_IMAGE_BASE = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185";
    private LiveDataMovieModel mLiveDataMovieModel;
    SharedPreferences s;
    private PosterRecycler posterRecycler;
    Bundle bundled;

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
        if(savedInstanceState != null){
            bundled = savedInstanceState;
        }

        mLiveDataMovieModel =  new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataMovieModel.class);
        mLiveDataMovieModel.getMovies().observe(this, posterObserver);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                MovieDatabase movieDatabase = MovieDatabase.getInstance(getApplication());
                List<MovieDetails> popularList = movieDatabase.movieDao().loadPopular();
                if(popularList.isEmpty()) {
                    GetWebData getWebData = new GetWebData(getApplication());
                    final List<MovieDetails> initialList = getWebData.getMovieDetails(getString(R.string.moviedb_api_key));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLiveDataMovieModel.getMovies().setValue(initialList);
                        }
                    });
                }
            }
        });

        setPosterList();
        createRecycler();
        getWindow().setExitTransition(new Explode());
        Timber.i("MainActivity: Stop");
    }

    public void createRecycler() {
        Timber.i("createRecycler: start");
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        posterList.setLayoutManager(staggeredGridLayoutManager);
        posterRecycler = new PosterRecycler(this);
        posterList.setAdapter(posterRecycler);
        Timber.i("createRecycler: stop");
    }

    Observer<List<MovieDetails>> posterObserver = new Observer<List<MovieDetails>>() {
        @Override
        public void onChanged(@Nullable List<MovieDetails> movieDetails) {
            Timber.i("posterObserver: Called");
            posterRecycler.setList(movieDetails);
        }
    };

    public void setPosterList(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
            Timber.i("createMediator: Runnable: Start");
            MovieDatabase movieDatabase = MovieDatabase.getInstance(getApplication());
            final List<MovieDetails> list = movieDatabase.movieDao().loadFavorites();
            list.clear();
            if (s.getBoolean(getString(R.string.popular_key), true)) {
                Timber.i("Popular");
                list.addAll(movieDatabase.movieDao().loadPopular());
            }
            if (s.getBoolean(getString(R.string.top_rated_key), true)) {
                Timber.i("Top_Rated");
                list.addAll(movieDatabase.movieDao().loadTopRated());
            }
            if (s.getBoolean(getString(R.string.favorites_key), true) && movieDatabase.movieDao().loadFavorites().size() > 0) {
                list.addAll(movieDatabase.movieDao().loadFavorites());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Timber.i("list: " + list);
                    mLiveDataMovieModel.getMovies().setValue(list);
                    if(bundled != null) {
                        posterList.getLayoutManager().onRestoreInstanceState(bundled.getParcelable(POSITION));
                    }
                }
            });
            Timber.i("createMediator: Runnable: Stop");
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences s, String key) {
        setPosterList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable recyclerViewState = posterList.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(POSITION, recyclerViewState);
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

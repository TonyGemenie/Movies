package one.movie.udacity.movies1;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Adapter.DetailRecycler;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;
import one.movie.udacity.movies1.Database.VideoReviewDetails;
import timber.log.Timber;


public class DetailsActivity extends AppCompatActivity implements
        DetailRecycler.onListClickListener{

    public static final String TRAILER = "trailer";
    @BindView(R.id.plot_text) TextView plotTX;
    @BindView(R.id.rating_text) TextView ratingTX;
    @BindView(R.id.date_text) TextView dateTX;
    @BindView(R.id.poster_image) ImageView imageView;
    @BindView(R.id.trailer_list) RecyclerView trailerList;
    @BindView(R.id.review_list) RecyclerView reviewList;
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.favorite_button) Button favoriteButton;
    @BindViews({R.id.plot_text, R.id.rating_text, R.id.date_text})List<TextView> textViews;

    private MovieDatabase movieDatabase;
    private VideoReviewDatabase videoReviewDatabase;
    private MovieDetails movieDetails;
    private DetailRecycler reviewDetailRecycler;
    private DetailRecycler trailerDetailRecycler;

    private LiveDataVideoReviewModel mLiveDataVideoReviewModel;
    private int movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);
        movieID = getIntent().getIntExtra(MainActivity.MOVIE_ID, 0);
        mLiveDataVideoReviewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataVideoReviewModel.class);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                videoReviewDatabase = VideoReviewDatabase.getInstance(getApplicationContext());
                List<VideoReviewDetails> videoReviewDetails = videoReviewDatabase.detailsDao().getMovieReviewsTrailers(String.valueOf(movieID));
                if(videoReviewDatabase.detailsDao().getMovieReviewsTrailers(String.valueOf(movieID)).size() < 1){
                    GetWebData getWebData = new GetWebData(getApplication());
                     videoReviewDetails = getWebData.getVideoReviewDetails(getString(R.string.moviedb_api_key),
                            getString(R.string.google_youtube_api_key), movieID);
                }
                final List<VideoReviewDetails> videoValue = videoReviewDetails;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLiveDataVideoReviewModel.getVideoReviews().setValue(videoValue);
                    }
                });
            }
        });

        reviewDetailRecycler = new DetailRecycler(null, this);
        trailerDetailRecycler = new DetailRecycler(this, this);
        createRecycler(reviewList, reviewDetailRecycler);
        createRecycler(trailerList, trailerDetailRecycler);
        setLiveData();
        populateUI();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void populateUI(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                movieDatabase = MovieDatabase.getInstance(getApplication());
                movieDetails = movieDatabase.movieDao().loadMovieID(movieID);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(imageView.getContext())
                                .load(movieDetails.getPosterPath())
                                .noFade()
                                .noPlaceholder()
                                .into(imageView);

                        plotTX.setText(movieDetails.getPlot());
                        ratingTX.setText(movieDetails.getRating());
                        dateTX.setText(movieDetails.getDate());
                        movieTitle.setText(movieDetails.getTitle());
                        if(movieDetails.isFavorite()){
                            favoriteButton.setActivated(true);
                        }
                    }
                });
            }
        });
    }

    public void createRecycler(RecyclerView recyclerView, DetailRecycler adapter){
        Timber.i("START");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Timber.i("STOP");
    }

    public void addToFavorites(View v){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                MovieDetails movieDetails = movieDatabase.movieDao().loadMovieID(movieID);
                if(movieDetails.isFavorite()){
                    movieDetails.setFavorite(false);
                }else {
                    movieDetails.setFavorite(true);
                }
                movieDatabase.movieDao().updateMovie(movieDetails);
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrailerClicked(final int clickedPosition, View v) {
        if(v.getTag().toString().equals(TRAILER)){
            final Activity activity = this;
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final String key = videoReviewDatabase.detailsDao().loadVideo(String.valueOf(movieID)).get(clickedPosition).getVideoKey();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity , getString(R.string.google_youtube_api_key), key);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }

    public void setLiveData(){
        final Observer<List<VideoReviewDetails>> videoObserver = new Observer<List<VideoReviewDetails>>() {
            @Override
            public void onChanged(@Nullable List<VideoReviewDetails> videoReviewDetails) {
                List<VideoReviewDetails> trailerList = videoReviewDetails;
                List<VideoReviewDetails> reviewList = videoReviewDetails;
                trailerList.clear();
                reviewList.clear();
                for (int i = 0; i < videoReviewDetails.size(); i++) {
                    if(videoReviewDetails.get(i).getAuthor().isEmpty()){
                        trailerList.add(videoReviewDetails.get(i));
                    }else {
                        reviewList.add(videoReviewDetails.get(i));
                    }
                }
                trailerDetailRecycler.setDetails(trailerList);
                reviewDetailRecycler.setDetails(reviewList);
            }
        };

        mLiveDataVideoReviewModel.getVideoReviews().observe(this, videoObserver);
    }
}

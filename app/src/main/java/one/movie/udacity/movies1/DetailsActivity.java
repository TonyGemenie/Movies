package one.movie.udacity.movies1;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Adapter.DetailRecycler;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;
import one.movie.udacity.movies1.Database.VideoReviewDetails;


public class DetailsActivity extends AppCompatActivity implements
        DetailRecycler.onListClickListener{

    public static final String TRAILER = "size";
    @BindView(R.id.plot_text) TextView plotTX;
    @BindView(R.id.rating_text) TextView ratingTX;
    @BindView(R.id.date_text) TextView dateTX;
    @BindView(R.id.poster_image) ImageView imageView;
    @BindView(R.id.trailer_list) RecyclerView trailerList;
    @BindView(R.id.review_list) RecyclerView reviewList;
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindViews({R.id.plot_text, R.id.rating_text, R.id.date_text})List<TextView> textViews;

    MovieDatabase movieDatabase;
    VideoReviewDatabase videoReviewDatabase;
    MovieDetails movieDetails;
    DetailRecycler detailRecycler;
    private LiveDataVideoReviewModel mLiveDataVideoReviewModel;
    int movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        movieID = getIntent().getIntExtra(MainActivity.MOVIE_ID, 0);

        movieDatabase = MovieDatabase.getInstance(getApplicationContext());
        videoReviewDatabase = VideoReviewDatabase.getInstance(getApplicationContext());

        populateUI();

        if(videoReviewDatabase.detailsDao().dataCheck(movieID) == null) {
            mLiveDataVideoReviewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataVideoReviewModel.class);
            mLiveDataVideoReviewModel.getmVideoReviews().observe(this, new Observer<List<VideoReviewDetails>>() {
                @Override
                public void onChanged(@Nullable List<VideoReviewDetails> videoReviewDetails) {
                    getRecyclerData();
                }
            });
            movieDetails = movieDatabase.movieDao().loadMovieID(movieID);
            MovieExecutors.getsInstance().getNetwork().execute(new Runnable() {
                @Override
                public void run() {
                    RetrieveWebData.getData(movieID, null, videoReviewDatabase, getString(R.string.moviedb_api_key), getString(R.string.google_youtube_api_key));
                    finish();
                }
            });
        }
        getRecyclerData();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void getRecyclerData(){
        createRecycler(reviewList);
        createRecycler(trailerList);

        detailRecycler.setList(videoReviewDatabase.detailsDao().loadVideos(movieID));
        detailRecycler.setList(videoReviewDatabase.detailsDao().loadReviews(movieID));
    }

    public void populateUI(){
        Picasso.with(imageView.getContext())
                .load(movieDetails.getPosterPath())
                .noFade()
                .noPlaceholder()
                .into(imageView);

        plotTX.setText(movieDetails.getPlot());
        ratingTX.setText(movieDetails.getRating());
        dateTX.setText(movieDetails.getDate());
        movieTitle.setText(movieDetails.getTitle());
    }

    public void createRecycler(RecyclerView recyclerView){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DetailRecycler detailRecycler = new DetailRecycler(this, this);
        recyclerView.setAdapter(detailRecycler);
    }

    public void addToFavorites(View v){
        MovieDetails movieDetails = movieDatabase.movieDao().loadMovieID(movieID);
        movieDetails.setFavorite(true);
        movieDatabase.movieDao().updateMovie(movieDetails);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrailerClicked(int clickedPosition, View v) {
        if(!v.getTag().toString().equals(TRAILER)){
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, getString(R.string.moviedb_api_key),
                    videoReviewDatabase.detailsDao().loadVideos(movieID).get(clickedPosition).getVideoKey());
            startActivity(intent);
        }
    }
}

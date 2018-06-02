package one.movie.udacity.movies1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.net.Uri;
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

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Adapter.DetailRecycler;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;

import static one.movie.udacity.movies1.RetrieveWebData.*;

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
    int movieID;
    SimpleExoPlayer player;

    public LiveDataMovieDetailsModel mLiveDataMovieDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        movieID = getIntent().getIntExtra(MainActivity.MOVIE_ID, 0);

        movieDatabase = MovieDatabase.getInstance(getApplicationContext());
        videoReviewDatabase = VideoReviewDatabase.getInstance(getApplicationContext());

        movieDetails = movieDatabase.movieDao().loadMovieID(movieID);

        mLiveDataMovieDetailsModel = new ViewModelProvider.from(getApplication()).create(LiveDataMovieDetailsModel.class);
        mLiveDataMovieDetailsModel.getVR();

        populateUI();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        subscribe();
    }

    public void subscribe(){
        Observer<String[]> reviewObs = new Observer<String[]>() {
            @Override
            public void onChanged(@Nullable String[] s) {
                createRecycler(reviewList);
                detailRecycler.setList(videoReviewDatabase.detailsDao().loadReviews(movieID));
            }
        };
        Observer<String[]> trailerObs = new Observer<String[]>() {
            @Override
            public void onChanged(@Nullable String[] s) {
                createRecycler(trailerList);
                detailRecycler.setList(videoReviewDatabase.detailsDao().loadVideos(movieID));
            }
        };
        mLiveDataMovieDetailsModel.reviews.observe(this, reviewObs);
        mLiveDataMovieDetailsModel.trailer.observe(this, trailerObs);
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

        DetailRecycler detailRecycler = new DetailRecycler(this);
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
            String url = v.getTag().toString();
            //Uri incorrect
            initializePlayer(Uri.parse(videoReviewDatabase.detailsDao().loadVideos(movieID).get(clickedPosition).getVideoId()));
        }
    }

    public void initializePlayer(Uri uri){
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        PlayerView playerView = findViewById(R.id.exo_player_view);
        MediaSource mediaSource = buildMediaSource(uri);
        playerView.setPlayer(player);
        player.prepare(mediaSource, true, false);
    }

    public MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("trailer");
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory
                ("trailer", new DefaultBandwidthMeter()));
        return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            player.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            player.release();
        }
    }
}

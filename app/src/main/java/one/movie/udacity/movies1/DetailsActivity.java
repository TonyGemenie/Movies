package one.movie.udacity.movies1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
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
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Adapter.DetailRecycler;
import one.movie.udacity.movies1.Utils.ParseJson;
import one.movie.udacity.movies1.movieDetails.MovieDetails;

import static one.movie.udacity.movies1.RetrieveWebData.*;

public class DetailsActivity extends AppCompatActivity implements
        DetailRecycler.onListClickListener{

    public static final String FAVORITE_STRING = "favorite_string";
    public static final String SIZE = "size";
    @BindView(R.id.plot_text) TextView plotTX;
    @BindView(R.id.rating_text) TextView ratingTX;
    @BindView(R.id.date_text) TextView dateTX;
    @BindView(R.id.poster_image) ImageView imageView;
    @BindView(R.id.trailer_list) RecyclerView trailerList;
    @BindView(R.id.review_list) RecyclerView reviewList;
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindViews({R.id.plot_text, R.id.rating_text, R.id.date_text})List<TextView> textViews;

    MovieDetails movieDetails;

    public LiveDataMovieDetailsModel mLiveDataMovieDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        String movieString = getIntent().getStringExtra(MainActivity.JSON_STRING);
        movieDetails = ParseJson.parseMovieJson(movieString);

        mLiveDataMovieDetailsModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LiveDataMovieDetailsModel.class);
        mLiveDataMovieDetailsModel.setMovieID(movieDetails.getmImage());

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
                createRecycler(s, reviewList, false);
            }
        };
        Observer<String[]> trailerObs = new Observer<String[]>() {
            @Override
            public void onChanged(@Nullable String[] s) {
                createRecycler(s, trailerList, true);
            }
        };
        mLiveDataMovieDetailsModel.reviews.observe(this, reviewObs);
        mLiveDataMovieDetailsModel.trailer.observe(this, trailerObs);
    }

    public void populateUI(){
        Picasso.with(imageView.getContext())
                .load(movieDetails.getmImage())
                .noFade()
                .noPlaceholder()
                .into(imageView);

        plotTX.setText(movieDetails.getmPlot());
        ratingTX.setText(movieDetails.getmRating());
        dateTX.setText(movieDetails.getmDate());
        movieTitle.setText(movieDetails.getmTitle());
    }

    public void createRecycler(String[] list, RecyclerView recyclerView, boolean trailer){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DetailRecycler detailRecycler = new DetailRecycler(list, this, trailer);
        recyclerView.setAdapter(detailRecycler);
    }

    public void addToFavorites(View v){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int size = sharedPreferences.getInt(SIZE, 0);
        sharedPreferences.edit().putString(FAVORITE_STRING + size, String.valueOf(movieDetails)).putInt(SIZE, size + 1).apply();
        MainActivity mainActivity  = new MainActivity();
        mainActivity.setFavorites();
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
        if(!v.getTag().toString().equals(REVIEWS)){
            String url = v.getTag().toString();

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            PlayerView exoView = v.findViewById(R.id.exo_player_view);
            exoView.setPlayer(player);

            DefaultSsChunkSource.Factory chunkFactory = new DefaultSsChunkSource.Factory(
                    new DefaultDataSourceFactory(this, "app"));

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "movie");

            MediaSource videoSource = new SsMediaSource.Factory(chunkFactory, dataSourceFactory)
                    .createMediaSource(Uri.parse(MOVIE_DB_BASE + movieDetails.getmImage() + VIDEOS + "/" + url));

            player.prepare(videoSource);
        }
    }
}

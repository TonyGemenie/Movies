package one.movie.udacity.movies1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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
    @BindView(R.id.review_bar) ImageView reviewBar;
    @BindView(R.id.trailer_bar) ImageView trailerBar;
    @BindViews({R.id.plot_text, R.id.rating_text, R.id.date_text})List<TextView> textViews;

    private MovieDatabase movieDatabase;
    private VideoReviewDatabase videoReviewDatabase;
    private MovieDetails movieDetails;
    private DetailRecycler reviewDetailRecycler;
    private DetailRecycler trailerDetailRecycler;

    public static int bottomBarColor;

    private int movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);
        movieID = getIntent().getIntExtra(MainActivity.MOVIE_ID, 0);
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
                setRecyclerLists(videoReviewDetails);
            }
        });

        reviewDetailRecycler = new DetailRecycler(null, this);
        trailerDetailRecycler = new DetailRecycler(this, this);
        createRecycler(reviewList, reviewDetailRecycler);
        createRecycler(trailerList, trailerDetailRecycler);
        populateUI();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.app_name);
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
                        if(movieDetails.isFavorite()){
                            favoriteButton.setBackground(getDrawable(R.drawable.ic_baseline_star_pressed_24px));
                        }
                        ByteArrayInputStream is = new ByteArrayInputStream(movieDetails.getByteData());
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        imageView.setImageBitmap(bitmap);
                        Palette palette = Palette.from(bitmap).generate();

                        int defaultPanelColor = 0xFF808080;
                        bottomBarColor = palette.getDarkMutedColor(defaultPanelColor);
                        reviewBar.setColorFilter(bottomBarColor);
                        trailerBar.setColorFilter(bottomBarColor);
                        movieTitle.setBackgroundColor(bottomBarColor);
                        movieTitle.setTextColor(palette.getLightVibrantColor(defaultPanelColor));

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
                int drawableID;
                if(movieDetails.isFavorite()){
                    movieDetails.setFavorite(false);
                    drawableID = R.drawable.ic_baseline_star_24px;
                }else {
                    movieDetails.setFavorite(true);
                    drawableID = R.drawable.ic_baseline_star_pressed_24px;
                }
                movieDatabase.movieDao().updateMovie(movieDetails);
                final int finalDrawableID = drawableID;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        favoriteButton.setBackground(getDrawable(finalDrawableID));
                    }
                });
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
    public void onTrailerClicked(final int clickedPosition) {
        final Activity activity = this;
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final String key = videoReviewDatabase.detailsDao().loadVideo(String.valueOf(movieID)).get(clickedPosition).getKey();
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

    private void setRecyclerLists(List<VideoReviewDetails> videoReviewDetails){
            ArrayList<VideoReviewDetails> trailerList = new ArrayList<>();
            ArrayList<VideoReviewDetails> reviewList = new ArrayList<>();
            for (int i = 0; i < videoReviewDetails.size(); i++) {
                if(videoReviewDetails.get(i).getAuthor() == null){
                    trailerList.add(videoReviewDetails.get(i));
                }else {
                    reviewList.add(videoReviewDetails.get(i));
                }
            }
            trailerDetailRecycler.setDetails(trailerList);
            reviewDetailRecycler.setDetails(reviewList);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reviewDetailRecycler.notifyDataSetChanged();
                    trailerDetailRecycler.notifyDataSetChanged();
                }
            });

    }
}


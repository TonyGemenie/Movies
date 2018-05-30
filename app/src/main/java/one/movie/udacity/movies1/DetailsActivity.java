package one.movie.udacity.movies1;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.preference.PreferenceManager;
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

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import one.movie.udacity.movies1.Adapter.DetailRecycler;
import one.movie.udacity.movies1.Utils.ParseJson;
import one.movie.udacity.movies1.movieDetails.MovieDetails;

public class DetailsActivity extends AppCompatActivity implements
        DetailRecycler.onListClickListener, LoaderManager.LoaderCallbacks<String>{

    private static final String MOVIE_ID = "movie_id";
    private static final String TRAILER = "trailer";
    private static final String API_KEY = "e95fd204f344fdf4253d4a02a51ca31f";
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

    Bitmap bitmap;

    public static final int SEARCH_LOADER = 2000;

    MovieDetails movieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        String movieString = getIntent().getStringExtra(MainActivity.JSON_STRING);
        movieDetails = ParseJson.parseMovieJson(movieString);
        /*Gson gson = new GsonBuilder().create();
        movieDetails = gson.fromJson(movieString, MovieDetails.class);*/
        populateUI();

        getDetailLoader(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void getDetailLoader(boolean trailer){
        Bundle movieData = new Bundle();
        movieData.putString(MOVIE_ID, movieDetails.getmImage());
        movieData.putBoolean(TRAILER, trailer);
        getLoaderManager().initLoader(SEARCH_LOADER, movieData , this);
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

        DetailRecycler detailRecycler = new DetailRecycler(list, this, this, trailer);
        recyclerView.setAdapter(detailRecycler);
    }

    public void getReviews(View v){
        reviewList.setVisibility(View.VISIBLE);
        //createRecycler(reviewList, false, );
    }

    public void addToFavorites(View v){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int size = sharedPreferences.getInt(SIZE, 0);
        sharedPreferences.edit().putString(FAVORITE_STRING + size, String.valueOf(movieDetails)).putInt(SIZE, size + 1).apply();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMovieTitleColors() {
        Palette palette = Palette.from(bitmap).generate();

        int defaultPanelColor = 0xFF808080;

        movieTitle.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        movieTitle.setTextColor(palette.getLightMutedColor(defaultPanelColor));
    }

    @Override
    public void onTrailerClicked(int clickedPosition, Object tag) {
        if(tag.toString().equals("trailer")){
            //ExoPlayer
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            public String loadInBackground() {
                String responseString = "";
                try{
                    OkHttpClient client = new OkHttpClient();
                    String searchFor = "/trailers";
                    for (int i = 0; i < 2; i++) {
                        Request request = new Request.Builder()
                                .url(MainActivity.MOVIE_DB_BASE + args.getString(MOVIE_ID) + searchFor + MainActivity.API + API_KEY)
                                .get()
                                .build();

                        Response response = client.newCall(request).execute();
                        responseString += response.body().string();
                        searchFor = "/reviews";
                    }
                if(!searchFor.equals("/trailers")) {
                    Request bitmapRequest = new Request.Builder()
                            .url(MainActivity.MOVIE_DB_BASE + args.getString(MOVIE_ID) + searchFor + MainActivity.API + API_KEY)
                            .get()
                            .build();
                    Response bitmapResponse = client.newCall(bitmapRequest).execute();
                    InputStream inputStream = bitmapResponse.body().byteStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);

                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return responseString;
            }

            @Override
            public void deliverResult(String data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        //createRecycler(trailerList, true);
        setMovieTitleColors();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

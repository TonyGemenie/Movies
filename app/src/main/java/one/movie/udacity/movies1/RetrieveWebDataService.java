package one.movie.udacity.movies1;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;
import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;
import timber.log.Timber;

public class RetrieveWebDataService extends IntentService {

    private static final String API_KEY = "?api_key=";
    public  static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_BASE = "https://www.googleapis.com/youtube/v3/videos?id=";
    public static final String IMAGE_END = "&part=snippet,contentDetails,statistics,status";
    public static final String[] searchTerms = {"popular", "top_rated"};
    public static final String[] detailTerms = {"/reviews", "/trailers"};

    public RetrieveWebDataService() {
        super("RetrieveWebDataService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent.hasExtra(MainActivity.KEY)) {
            getData(intent.getStringExtra(MainActivity.KEY), intent.getIntExtra(DetailsActivity.MOVIE_ID, 0));
        }
    }

    public void getData(String key, int id) {
        Timber.i("getData: Start");
        OkHttpClient client = new OkHttpClient();
        String responseString;
        try {
            if (id == 0) {
                MovieDatabase movieDatabase = MovieDatabase.getInstance(getApplication());
                if (movieDatabase.movieDao().dataCheck().size() < 1) {
                    for (int j = 0; j < 2; j++) {
                        String url = MOVIE_DB_BASE + searchTerms[j] + API_KEY + key;
                        Request request = new Request.Builder()
                                .url(url)
                                .get()
                                .build();
                        Response response = client.newCall(request).execute();
                        responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray arr = jsonObject.getJSONArray("results");
                        Gson gson = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                .create();
                        int arrSize = arr.length();
                        movieDatabase.beginTransaction();
                        try {
                            for (int i = 0; i < arrSize; i++) {
                                MovieDetails movie = (gson.fromJson(arr.get(i).toString(), MovieDetails.class));
                                if(j == 0) {
                                    movie.setPopular(true);
                                }else {
                                    movie.setToprated(true);
                                }
                                Timber.i("getData: Insert");
                                movieDatabase.movieDao().insertMovie(movie);
                            }
                            movieDatabase.setTransactionSuccessful();
                        }finally {
                            movieDatabase.endTransaction();
                        }
                    }
                }
            }
            else {
                if(VideoReviewDatabase.getInstance(getApplication()).detailsDao().dataCheck(id).size() < 1) {
                    for (String term : detailTerms) {
                        String url = MOVIE_DB_BASE + String.valueOf(id) + term + API_KEY + key;
                        Request request = new Request.Builder()
                                .url(url)
                                .get()
                                .build();
                        Response response = client.newCall(request).execute();
                        responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        String stringID = "\"movieID\":\"" + jsonObject.getString("id") + "\",";
                        JSONArray arr = jsonObject.getJSONArray("results");
                        Gson gson = new GsonBuilder().create();
                        for (int i = 0; i < arr.length(); i++) {
                            if (arr.get(i).toString().contains("Trailer")) {
                                String imageUrl = IMAGE_BASE + arr.getJSONObject(i).getString("id") + "&key=" +
                                        getResources().getString(R.string.google_youtube_api_key) + IMAGE_END;
                                Request imageRequest = new Request.Builder()
                                        .url(imageUrl)
                                        .get()
                                        .build();
                                Response imageResponse = client.newCall(imageRequest).execute();
                                responseString = imageResponse.body().string();
                                JSONObject obj = new JSONObject(responseString);
                                JSONArray imageArr = obj.getJSONArray("items");
                                String image = imageArr.getJSONObject(0).getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                                stringID = stringID + "\"imageURL\":\"" + image + "\",";
                            }
                            VideoReviewDetails videoReviewDetail = gson.fromJson(stringID + arr.get(i).toString(), VideoReviewDetails.class);
                            VideoReviewDatabase.getInstance(getApplicationContext()).detailsDao().insertVideoReview(videoReviewDetail);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timber.i("getData: Stop");
    }
}


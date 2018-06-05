package one.movie.udacity.movies1;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;
import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;

public class RetrieveWebData extends Activity {

    private static final String API_KEY = "?api_key=";
    public  static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_BASE = "https://www.googleapis.com/youtube/v3/videos?id=";
    public static final String IMAGE_END = "&part=snippet,contentDetails,statistics,status";
    public static final String[] searchTerms = {"popular", "top_rated"};
    public static final String[] detailTerms = {"/reviews", "/trailers"};
    MovieDatabase movieDatabase;
    VideoReviewDatabase videoReviewDatabase;
    String key;
    String imageKey;
    int id;
    MovieExecutors executor;

    public RetrieveWebData(MovieDatabase movieDatabase, VideoReviewDatabase videoReviewDatabase,
                           String key, String imageKey, int id, MovieExecutors executor) {
        this.movieDatabase = movieDatabase;
        this.videoReviewDatabase = videoReviewDatabase;
        this.key = key;
        this.imageKey = imageKey;
        this.id = id;
        this.executor = executor;
    }


    public void getData() {
        executor.getNetwork().execute(new Runnable() {
            @Override
            public void run() {


        OkHttpClient client = new OkHttpClient();
        String responseString;
        try {
            if (movieDatabase != null) {
                for (String term : searchTerms) {
                    String url = MOVIE_DB_BASE + term + API_KEY + key;
            /*MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");*/
                    Request request = new Request.Builder()
                            .url(url)
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONArray arr = jsonObject.getJSONArray("results");
                    Gson gson = new GsonBuilder().create();
                    for (int i = 0; i < arr.length(); i++) {
                        MovieDetails movie = gson.fromJson(arr.get(i).toString(), MovieDetails.class);
                        movieDatabase.movieDao().insertMovie(movie);
                    }
                }
            } else {
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
                            String imageUrl = IMAGE_BASE + arr.getJSONObject(i).getString("id") + "&key=" + imageKey + IMAGE_END;
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
                        videoReviewDatabase.detailsDao().insertVideoReview(videoReviewDetail);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            }
        });
    }
}


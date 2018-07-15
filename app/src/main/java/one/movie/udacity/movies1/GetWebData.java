package one.movie.udacity.movies1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;
import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;

public class GetWebData {
    private  static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String IMAGE_BASE = "https://www.googleapis.com/youtube/v3/videos?id=";
    private static final String IMAGE_END = "&part=snippet,contentDetails,statistics,status";
    private String[] searchTerms = {"videos", "reviews"};
    private String[] terms = {"popular", "top_rated"};
    private Context context;

    public GetWebData(Context context) {
        this.context = context;
    }

    public List<MovieDetails> getMovieDetails(String key) {
        MovieDatabase movieDatabase = MovieDatabase.getInstance(context);
        for (int i = 0; i < 2; i++) {
            try {
                String url = MOVIE_DB_BASE + terms[i] +"?api_key=" + key;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray arr = jsonObject.getJSONArray("results");
                for (int j = 0; j < arr.length(); j++) {
                    MovieDetails movieDetails = gson.fromJson(arr.get(j).toString(), MovieDetails.class);
                    if (i == 0) {
                        movieDetails.setPopular(true);
                    } else {
                        movieDetails.setToprated(true);
                    }
                    if(movieDatabase.movieDao().loadMovieID(movieDetails.getId()) == null) {
                        URL imageurl = new URL(MainActivity.MOVIE_DB_IMAGE_BASE + MainActivity.IMAGE_SIZE + movieDetails.getPosterPath());
                        HttpURLConnection connection = (HttpURLConnection) imageurl.openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
                        byte[] bitmapdata = os.toByteArray();
                        movieDetails.setByteData(bitmapdata);
                        movieDatabase.movieDao().insertMovie(movieDetails);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return movieDatabase.movieDao().dataCheck();
    }

    public List<VideoReviewDetails> getVideoReviewDetails(String movieKey, String youtubeKey, int id) {
        VideoReviewDatabase videoReviewDatabase = VideoReviewDatabase.getInstance(context);
        OkHttpClient client = new OkHttpClient();
        for (int i = 0; i < 2; i++) {
            try {
                String url = MOVIE_DB_BASE + id + "/" + searchTerms[i] + "?api_key=" + movieKey;
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray arr = jsonObject.getJSONArray("results");

                for (int j = 0; j < arr.length(); j++) {
                    VideoReviewDetails videoReviewDetails = gson.fromJson(arr.get(j).toString(), VideoReviewDetails.class);
                    if (videoReviewDetails.getType() != null) {
                        if(videoReviewDetails.getType().equals("Trailer")) {
                            String youtubeUrl = IMAGE_BASE + videoReviewDetails.getVideoKey() + "&key=" + youtubeKey + IMAGE_END;
                            Request youTubeRequest = new Request.Builder()
                                    .url(youtubeUrl)
                                    .get()
                                    .build();
                            Response youTubeResponse = client.newCall(youTubeRequest).execute();
                            JSONObject obj = new JSONObject(Objects.requireNonNull(youTubeResponse.body()).string());
                            JSONArray imageArr = obj.getJSONArray("items");
                            String image = imageArr.getJSONObject(0).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                            videoReviewDetails.setImageURL(image);
                            URL trailerImage = new URL(image);
                            HttpURLConnection connection = (HttpURLConnection) trailerImage.openConnection();
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
                            byte[] bitmapdata = os.toByteArray();
                            videoReviewDetails.setByteData(bitmapdata);
                            videoReviewDetails.setId(String.valueOf(id));
                            videoReviewDatabase.detailsDao().insertVideoReview(videoReviewDetails);
                        }
                    }
                    if(videoReviewDetails.getAuthor() != null){
                        videoReviewDetails.setId(String.valueOf(id));
                        videoReviewDatabase.detailsDao().insertVideoReview(videoReviewDetails);
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return videoReviewDatabase.detailsDao().getMovieReviewsTrailers(String.valueOf(id));
    }
}


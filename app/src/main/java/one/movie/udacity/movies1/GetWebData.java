package one.movie.udacity.movies1;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;
import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class GetWebData {
    private  static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String IMAGE_BASE = "https://www.googleapis.com/youtube/v3/videos?id=";
    private static final String IMAGE_END = "&part=snippet,contentDetails,statistics,status";
    private Context context;

    public GetWebData(Context context) {
        this.context = context;
    }

    public interface CallMovieDB {
        //Retrofit Builder not Accepting Strings//
        @GET("{term}")
        Call<ResponseBody> movieList(@Path("term") String searchTerm, @Query("api_key=") String key);

        @GET("{term}" + IMAGE_END )
        Call<ResponseBody> youTubeImage(@Path("term") String searchTerm, @Query("api_key=") String key);

        @GET("{id}/{term}")
        Call<ResponseBody> videoReviewList(@Path("id") String id, @Path("term") String searchTerm, @Query("api_key=") String key);
    }

    public void getMovieDetails(String term, String key) {
        MovieDatabase movieDatabase = MovieDatabase.getInstance(context);
        if (movieDatabase.movieDao().loadTopRated().size() < 25) {
            try {
                String url = MOVIE_DB_BASE + term +"?api_key=" + key;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray arr = jsonObject.getJSONArray("results");
                movieDatabase.beginTransaction();
                for (int i = 0; i < arr.length(); i++) {
                    MovieDetails movieDetails = gson.fromJson(arr.get(i).toString(), MovieDetails.class);
                    if (term.equals(context.getString(R.string.popular_key))) {
                        movieDetails.setPopular(true);
                    } else {
                        movieDetails.setToprated(true);
                    }
                    movieDatabase.movieDao().insertMovie(movieDetails);
                }
                movieDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                movieDatabase.endTransaction();
            }
        }
    }

    public void getVideoReviewDetails(String movieKey, String youtubeKey, int id, String term) {
        VideoReviewDatabase videoReviewDatabase = VideoReviewDatabase.getInstance(context);
        if (videoReviewDatabase.detailsDao().dataCheck(id).size() < 1) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MOVIE_DB_BASE)
                        .build();
                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                CallMovieDB service = retrofit.create(CallMovieDB.class);
                Call<ResponseBody> call = service.videoReviewList(String.valueOf(id), term, movieKey);
                JSONObject jsonObject = new JSONObject(String.valueOf(call.execute()));
                JSONArray arr = jsonObject.getJSONArray("results");
                videoReviewDatabase.beginTransaction();
                for (int i = 0; i < arr.length(); i++) {
                    VideoReviewDetails videoReviewDetails = gson.fromJson(arr.get(i).toString(), VideoReviewDetails.class);
                    if (videoReviewDetails.getType().equals("Trailer")) {
                        Retrofit youTubeImage = new Retrofit.Builder()
                                .baseUrl(IMAGE_BASE)
                                .build();
                        CallMovieDB imageService = youTubeImage.create(CallMovieDB.class);
                        Call<ResponseBody> youtubeCall = imageService.youTubeImage(arr.getJSONObject(i).getString("id"), youtubeKey);
                        JSONObject obj = new JSONObject(String.valueOf(youtubeCall.execute()));
                        JSONArray imageArr = obj.getJSONArray("items");
                        String image = imageArr.getJSONObject(0).getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                        videoReviewDetails.setImageURL(image);
                    }
                    videoReviewDetails.setId(id);
                    videoReviewDatabase.detailsDao().insertVideoReview(videoReviewDetails);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


package one.movie.udacity.movies1;

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

public class RetrieveWebData {

    private static final String API_KEY = "?api_key=e95fd204f344fdf4253d4a02a51ca31f";
    public  static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    public static final String[] searchTerms = {"popular", "top_rated"};
    public static final String[] detailTerms = {"/reviews", "/trailers"};

    public static void getData(int id, MovieDatabase movieDatabase, VideoReviewDatabase videoReviewDatabase) {
        OkHttpClient client = new OkHttpClient();
        String responseString;

        try {
        if(movieDatabase != null) {
            for (String term : searchTerms) {
                String url = MOVIE_DB_BASE + term + API_KEY;
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
        }else {
            for(String term : detailTerms) {
                String url = MOVIE_DB_BASE + String.valueOf(id) + term + API_KEY;
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
                JSONObject jsonObject = new JSONObject(responseString);
                String stringID = "\"movieID\":\"" + jsonObject.getString("id") + "\", ";
                JSONArray arr = jsonObject.getJSONArray("results");
                Gson gson = new GsonBuilder().create();
                for (int i = 0; i < arr.length(); i++) {
                    VideoReviewDetails videoReviewDetail = gson.fromJson(stringID + arr.get(i).toString(), VideoReviewDetails.class);
                    videoReviewDatabase.detailsDao().insertVideoReview(videoReviewDetail);
                }
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

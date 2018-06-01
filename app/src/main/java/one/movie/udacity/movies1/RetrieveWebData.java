package one.movie.udacity.movies1;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RetrieveWebData {

    private static final String API_KEY = "e95fd204f344fdf4253d4a02a51ca31f";
    public  static final String MOVIE_DB_BASE = "https://api.themoviedb.org/3/movie/";
    private static final String API = "?api_key=";
    public  static final String REVIEWS = "/reviews";
    public static final String VIDEOS = "/videos";

    public static String getData(String search, String id) {
        OkHttpClient client = new OkHttpClient();
        String responseString = null;
        String url = null;
        if (id == null) {
            url = MOVIE_DB_BASE + search + API + API_KEY;
        }else {
            if(id.equals(VIDEOS)) {
                url = MOVIE_DB_BASE + search + "/" + id + VIDEOS + API + API_KEY;
            }
            if(id.equals(REVIEWS)){
                url = MOVIE_DB_BASE + search + "/" + id + REVIEWS + API + API_KEY;
            }
        }

        //Implmentation Working For MainActivity Unable to Test DetailsActivity
        try {
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, "{}");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
            } catch (IOException e1) {
            e1.printStackTrace();
        }
        return responseString;
    }
}

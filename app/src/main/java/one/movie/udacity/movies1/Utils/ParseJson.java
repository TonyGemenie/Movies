package one.movie.udacity.movies1.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import one.movie.udacity.movies1.movieDetails.MovieDetails;

public class ParseJson {
    public static final String TITLE = "title";
    public static final String OVERVIEW = "overview";
    public static final String VOTE = "vote_average";
    public static final String IMAGEPATH = "poster_path";
    public static final String DATE = "release_date";


    public static MovieDetails parseMovieJson(String json) {
        MovieDetails movieDetails = null;
        try{
            JSONObject obj = new JSONObject(json);
            String title = obj.optString(TITLE);
            String plot = obj.optString(OVERVIEW);
            String vote_average = obj.optString(VOTE);
            String image = obj.optString(IMAGEPATH);
            String date = obj.optString(DATE);

            movieDetails = new MovieDetails(title, plot, vote_average, date, image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieDetails;
    }
}

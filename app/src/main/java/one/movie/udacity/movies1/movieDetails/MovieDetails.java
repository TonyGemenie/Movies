package one.movie.udacity.movies1.movieDetails;

import one.movie.udacity.movies1.MainActivity;

public class MovieDetails {

    String mTitle;
    String mPlot;
    String mRating;
    String mDate;
    String mImage;

    public MovieDetails(String mTitle, String mPlot, String mRating, String mDate, String mImage) {
        this.mTitle = mTitle;
        this.mPlot = mPlot;
        this.mRating = mRating;
        this.mDate = mDate;
        this.mImage = mImage;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmPlot() {
        return mPlot;
    }

    public String getmRating() {
        return mRating;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmImage() {
        return MainActivity.MOVIE_DB_IMAGE_BASE + MainActivity.IMAGE_SIZE + mImage;
    }

}


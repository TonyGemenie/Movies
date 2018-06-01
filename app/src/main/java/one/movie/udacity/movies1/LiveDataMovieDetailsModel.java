package one.movie.udacity.movies1;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class LiveDataMovieDetailsModel extends ViewModel {

    private String movieID;

    private static final String TRAILER = "trailer";
    private static final String REVIEWS = "reviews";

    public MutableLiveData<String[]> trailer = new MutableLiveData<>();
    public MutableLiveData<String[]> reviews = new MutableLiveData<>();

    public LiveDataMovieDetailsModel() {
        String trailerString = RetrieveWebData.getData(TRAILER, movieID);
        String reviewsString = RetrieveWebData.getData(REVIEWS, movieID);

        String[] trailerArray = trailerString.split(",");
        String[] reviewsArray = reviewsString.split(",");

        trailer.postValue(trailerArray);
        reviews.postValue(reviewsArray);
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

}

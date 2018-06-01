package one.movie.udacity.movies1;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class LiveDataMovieModel extends ViewModel {

    private MutableLiveData<String> mMovies = new MutableLiveData<>();

    //PlaceHolder true
    private boolean mPopular = true;
    private boolean mTop_rated = true;
    private boolean mFavorites;
    private String mFavoritesString;

    public LiveDataMovieModel() {
        String popularString = null;
        String responseString = null;
        String topRatedString;
        if (mPopular) {
            popularString = RetrieveWebData.getData(MainActivity.POPULAR, null);
        }
        if (mTop_rated) {
            topRatedString = RetrieveWebData.getData(MainActivity.TOP_RATED, null);
            if (popularString != null) {
                popularString = popularString.substring(popularString.lastIndexOf("results\":[")).substring(10);
                responseString = topRatedString.substring(0, topRatedString.length() - 2) + "," + popularString + "]";
            }
        }
        if (mFavorites) {
            if (responseString != null) {
                responseString = responseString.substring(0, responseString.length() - 2) + "," + mFavoritesString + "]";
            } else {
                responseString = "results:\"[" + mFavoritesString + "}";
            }
        }
        mMovies.postValue(responseString);
    }

    public void setPreferences(boolean preference, String key){
        switch(key){
            case "popular":
                mPopular = preference;
            case "top_rates":
                mTop_rated = preference;
            case "favorites":
                mFavorites = preference;
        }
    }

    public void setFavorites(String favorites){
        mFavoritesString = favorites;
    }

    public LiveData<String> getMovies(){
        return mMovies;
    }
}

package one.movie.udacity.movies1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;

public class LiveDataMovieModel extends AndroidViewModel {

    private LiveData<List<MovieDetails>> mMovies;

    //PlaceHolder true
    private boolean mPopular = true;
    private boolean mTop_rated = true;
    private boolean mFavorites;

    public LiveDataMovieModel(Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(this.getApplication());
        mMovies = database.movieDao().loadAllMovies();
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

    public LiveData<List<MovieDetails>> getMovies(){
        return mMovies;
    }
}

package one.movie.udacity.movies1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import one.movie.udacity.movies1.Database.MovieDatabase;
import one.movie.udacity.movies1.Database.MovieDetails;

public class LiveDataMovieModel extends AndroidViewModel {

    private LiveData<List<MovieDetails>> mMovies;

    public LiveDataMovieModel(Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(this.getApplication());
        mMovies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<MovieDetails>> getMovies(){
        return mMovies;
    }

    public LiveData<List<MovieDetails>> setMovies(LiveData<List<MovieDetails>> list) {
        mMovies = list;
        return mMovies;
    }
}

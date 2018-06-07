package one.movie.udacity.movies1;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import one.movie.udacity.movies1.Database.MovieDetails;

public class LiveDataTopRatedModel extends ViewModel{

    private MutableLiveData<List<MovieDetails>> mMovies;


    public MutableLiveData<List<MovieDetails>> getTopRated() {
        if(mMovies == null){
            mMovies = new MutableLiveData<>();
        }
        return mMovies;
    }
}

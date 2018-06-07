package one.movie.udacity.movies1.Database;

import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface MovieDao {

    @Query("SELECT * FROM moviedatabase ORDER BY voteAverage")
    List<MovieDetails> loadAllMovies();

    @Insert
    void insertMovie(MovieDetails movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieDetails movieDetails);

    @Delete
    void deleteMovie(MovieDetails movieEntry);

    @Query("SELECT * FROM moviedatabase WHERE id = :id")
    MovieDetails loadMovieID(int id);

    @Query("SELECT * FROM moviedatabase WHERE favorite = 1")
    List<MovieDetails> loadFavorites();

    @Query("SELECT * FROM moviedatabase WHERE popular = 1")
    List<MovieDetails> loadPopular();

    @Query("SELECT * FROM moviedatabase WHERE toprated = 1")
    List<MovieDetails> loadTopRated();

    @Query("SELECT * FROM moviedatabase WHERE toprated = 1 OR popular = 1" )
    List<MovieDetails> loadTopRatedPopular();

    @Query("SELECT * FROM moviedatabase WHERE toprated = 1 OR favorite = 1")
    List<MovieDetails> loadTopRatedFavorite();

    @Query("SELECT * FROM moviedatabase WHERE popular = 1 OR favorite = 1")
    List<MovieDetails> loadPopularFavorite();

    @Query("SELECT * FROM moviedatabase WHERE popular = 1 OR favorite = 1 OR toprated = 1")
    List<MovieDetails> dataCheck();

}

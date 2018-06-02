package one.movie.udacity.movies1.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface MovieDao {

    @Query("SELECT * FROM moviedatabase ORDER BY rating")
    LiveData<List<MovieDetails>> loadAllMovies();

    @Insert
    void insertMovie(MovieDetails movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieDetails movieDetails);

    @Delete
    void deleteMovie(MovieDetails movieEntry);

    @Query("SELECT * FROM moviedatabase WHERE type = :id")
    MovieDetails loadMovieID(int id);

    @Query("SELECT * FROM moviedatabase WHERE favorite = true")
    LiveData<List<MovieDetails>> loadFavorites();

    @Query("SELECT * FROM moviedatabase WHERE popular = true")
    LiveData<List<MovieDetails>> loadPopular();

    @Query("SELECT * FROM moviedatabase WHERE toprated = true")
    LiveData<List<MovieDetails>> loadTopRated();

}

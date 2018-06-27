package one.movie.udacity.movies1.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface VideoReviewDao {

    @Insert
    void insertVideoReview(VideoReviewDetails movieEntry);

    @Query("SELECT * FROM detailsdatabase WHERE movieID = :id")
    List<VideoReviewDetails> getMovieReviewsTrailers(int id);

    @Query("SELECT * FROM detailsdatabase WHERE type = 'Trailer'  AND movieID = :id")
    List<VideoReviewDetails> loadVideo(int id);

}

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

    @Query("SELECT * FROM detailsdatabase WHERE id = :id")
    List<VideoReviewDetails> getMovieReviewsTrailers(String id);

    @Query("SELECT * FROM detailsdatabase WHERE type = 'Trailer'  AND id = :id")
    List<VideoReviewDetails> loadVideo(String id);

}

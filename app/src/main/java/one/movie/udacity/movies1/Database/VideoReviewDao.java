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

    @Query("SELECT * FROM detailsdatabase ORDER BY movieID ")
    LiveData<List<VideoReviewDetails>> loadAll();

    @Query("SELECT * FROM detailsdatabase WHERE author NOT null AND movieID = :id")
    List<VideoReviewDetails> loadReviews(int id);

    @Query("SELECT * FROM detailsdatabase WHERE movieID = :id")
    List<VideoReviewDetails> dataCheck(int id);

    @Query("SELECT * FROM detailsdatabase WHERE type = 'Trailer'  AND movieID = :id")
    List<VideoReviewDetails> loadVideos(int id);

}

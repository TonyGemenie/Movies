package one.movie.udacity.movies1.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface VideoReviewDao {

    @Query("SELECT * FROM detailsdatabase ORDER BY rating")
    LiveData<List<VideoReviewDetails>> loadAllTasks();

    @Insert
    void insertVideoReview(VideoReviewDetails movieEntry);

    @Delete
    void deleteVideoReview(VideoReviewDetails movieEntry);

    @Query("SELECT * FROM detailsdatabase WHERE author NOT null AND id = :id")
    List<VideoReviewDetails> loadReviews(int id);

    @Query("SELECT * FROM detailsdatabase WHERE type = trailer AND id = :id ")
    List<VideoReviewDetails> loadVideos(int id);

}

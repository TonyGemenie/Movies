package one.movie.udacity.movies1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;

public class LiveDataVideoReviewModel extends AndroidViewModel {

        VideoReviewDatabase database;
        private LiveData<List<VideoReviewDetails>> mVideoReviews;

        public LiveDataVideoReviewModel(Application application) {
            super(application);
            database = VideoReviewDatabase.getInstance(this.getApplication());
            mVideoReviews = database.detailsDao().loadAll();
        }

        public LiveData<List<VideoReviewDetails>> getReviews(int id){
            return database.detailsDao().loadReviews(id);
        }

        public LiveData<List<VideoReviewDetails>> getVideos(int id){
            return database.detailsDao().loadVideos(id);
        }

        public List<VideoReviewDetails> getDataCheck(int id) {
            return database.detailsDao().dataCheck(id);
        }
}

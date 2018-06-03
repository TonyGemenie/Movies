package one.movie.udacity.movies1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;

public class LiveDataVideoReviewModel extends AndroidViewModel {


        private LiveData<List<VideoReviewDetails>> mVideoReviews;

        public LiveDataVideoReviewModel(Application application) {
            super(application);
            VideoReviewDatabase database = VideoReviewDatabase.getInstance(this.getApplication());
            mVideoReviews = database.detailsDao().loadAll();
        }

        public LiveData<List<VideoReviewDetails>> getmVideoReviews(){ return mVideoReviews;}
}

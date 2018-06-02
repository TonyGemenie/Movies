package one.movie.udacity.movies1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;

public class LiveDataMovieDetailsModel extends AndroidViewModel {

    private String movieID;

    private static final String TRAILER = "trailer";
    private static final String REVIEWS = "reviews";

    public LiveData<List<VideoReviewDetails>> videoReviewDetailsLiveData;

    public LiveDataMovieDetailsModel(Application application) {
        super(application);
        VideoReviewDatabase database = VideoReviewDatabase.getInstance(this.getApplication());
        videoReviewDetailsLiveData = database.detailsDao().loadAllTasks();
    }

    public LiveData<List<VideoReviewDetails>> getVR() {
        return videoReviewDetailsLiveData;
    }

}

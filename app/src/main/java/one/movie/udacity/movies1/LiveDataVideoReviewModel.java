package one.movie.udacity.movies1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import one.movie.udacity.movies1.Database.VideoReviewDatabase;
import one.movie.udacity.movies1.Database.VideoReviewDetails;

public class LiveDataVideoReviewModel extends ViewModel {


        private MutableLiveData<List<VideoReviewDetails>> mVideoReviews;


        public MutableLiveData<List<VideoReviewDetails>> getVideoReviews(){
            if(mVideoReviews == null){
                mVideoReviews = new MutableLiveData<>();
            }
            return mVideoReviews;
        }

}

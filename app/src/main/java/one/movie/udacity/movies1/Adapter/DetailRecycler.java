package one.movie.udacity.movies1.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.R;

import static one.movie.udacity.movies1.RetrieveWebData.REVIEWS;

public class DetailRecycler extends RecyclerView.Adapter<DetailRecycler.TrailerReviewVH> {

    private String[] mMovieArray;
    private DetailRecycler.onListClickListener mOnListClickListener;
    private boolean mTrailer;

    public DetailRecycler(String[] listarray, DetailRecycler.onListClickListener listener, Boolean trailer) {
        mMovieArray = listarray;
        mOnListClickListener = listener;
        mTrailer = trailer;
    }

    @NonNull
    @Override
    public DetailRecycler.TrailerReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_review, parent, false);
        return new DetailRecycler.TrailerReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailRecycler.TrailerReviewVH holder, int position) {
        if(mTrailer) {
            holder.review.setTextSize(30);
            String trailerNumber = "Trailer " + (position + 1);
            holder.review.setText(trailerNumber);
            holder.review.setTag(mMovieArray[position]);
        }else {
            holder.review.setText(mMovieArray[position]);
            holder.review.setTag(REVIEWS);
        }
    }

    @Override
    public int getItemCount() {
        return mMovieArray.length;
    }

    public interface onListClickListener{
        void onTrailerClicked(int clickedPosition, View v);
    }

    class TrailerReviewVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.review_text) TextView review;

        public TrailerReviewVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnListClickListener.onTrailerClicked(getAdapterPosition(), v);
        }
    }
}

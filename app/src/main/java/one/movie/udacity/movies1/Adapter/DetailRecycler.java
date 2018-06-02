package one.movie.udacity.movies1.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Database.VideoReviewDetails;
import one.movie.udacity.movies1.DetailsActivity;
import one.movie.udacity.movies1.R;

public class DetailRecycler extends RecyclerView.Adapter<DetailRecycler.TrailerReviewVH> {

    private List<VideoReviewDetails> mList;
    private DetailRecycler.onListClickListener mOnListClickListener;

    public DetailRecycler(DetailRecycler.onListClickListener listener) {
        mOnListClickListener = listener;
    }

    @NonNull
    @Override
    public DetailRecycler.TrailerReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_review, parent, false);
        return new DetailRecycler.TrailerReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailRecycler.TrailerReviewVH holder, int position) {
        VideoReviewDetails videoReviewDetails = mList.get(position);
        if(videoReviewDetails.getVideoId() != null) {
            holder.review.setTextSize(30);
            String trailerNumber = "Trailer " + (position + 1);
            holder.review.setText(trailerNumber);
            holder.review.setTag(trailerNumber);
        }else {
            holder.review.setText(videoReviewDetails.getContent());
            holder.review.setTag(DetailsActivity.TRAILER);
        }
    }

    public void setList(List<VideoReviewDetails> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
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

package one.movie.udacity.movies1.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Database.VideoReviewDetails;
import one.movie.udacity.movies1.DetailsActivity;
import one.movie.udacity.movies1.R;

public class DetailRecycler extends RecyclerView.Adapter<DetailRecycler.TrailerReviewVH> {

    private List<VideoReviewDetails> mList;
    private Context mContext;
    private DetailRecycler.onListClickListener mOnListClickListener;

    public DetailRecycler(DetailRecycler.onListClickListener listener, Context context) {
        mOnListClickListener = listener;
        mContext = context;
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
        if(videoReviewDetails.getType() != null) {
            holder.review.setVisibility(View.GONE);
            Picasso.with(mContext).load(videoReviewDetails.getImageURL()).into(holder.image);
        }
        if(videoReviewDetails.getAuthor() != null) {
            holder.image.setVisibility(View.GONE);
            holder.review.setText(videoReviewDetails.getContent());
            holder.review.setTag(DetailsActivity.TRAILER);
        }
    }

    public void setDetails(List<VideoReviewDetails> list){
        if(list != null) {
            mList = list;
            notifyDataSetChanged();
        }
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
        @BindView(R.id.trailer_image) ImageView image;

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

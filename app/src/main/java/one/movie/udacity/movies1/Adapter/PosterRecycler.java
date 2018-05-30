package one.movie.udacity.movies1.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.MainActivity;
import one.movie.udacity.movies1.R;


public class PosterRecycler extends RecyclerView.Adapter<PosterRecycler.PosterVH> {

    public String[] mPosterArray;
    public vHClickListener mVHClickListener;
    public Context mContext;

    public PosterRecycler(String[] posterArray, vHClickListener listener, Context context) {
        mPosterArray = posterArray;
        mVHClickListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public PosterRecycler.PosterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_view, parent, false);
        PosterVH viewHolder = new PosterVH(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull PosterRecycler.PosterVH holder, int position) {
        String imageUri = MainActivity.MOVIE_DB_IMAGE_BASE + MainActivity.IMAGE_SIZE + mPosterArray[position % mPosterArray.length];
        Picasso.with(mContext).load(imageUri).into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return mPosterArray.length * 4;
    }

    public interface vHClickListener{
        void onPosterClicked(int clickedPosition, View v);
    }

    class PosterVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.poster_view) ImageView posterImage;

        public PosterVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mVHClickListener.onPosterClicked(getAdapterPosition(), posterImage);
        }
    }
}




package one.movie.udacity.movies1.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.movie.udacity.movies1.Database.MovieDetails;
import one.movie.udacity.movies1.MainActivity;
import one.movie.udacity.movies1.R;
import timber.log.Timber;


public class PosterRecycler extends RecyclerView.Adapter<PosterRecycler.PosterVH> {

    private List<MovieDetails> mList;
    private vHClickListener mVHClickListener;
    private Context mContext;

    public PosterRecycler(vHClickListener listener, Context context) {
        mVHClickListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public PosterRecycler.PosterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_view, parent, false);
        return new PosterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterRecycler.PosterVH holder, int position) {
        MovieDetails movieDetails = mList.get(position);
        ByteArrayInputStream is = new ByteArrayInputStream(movieDetails.getByteData());
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        holder.posterImage.setImageBitmap(bitmap);
    }

    public void setList(List<MovieDetails> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mList == null){
            return 0;
        }
        return mList.size();
    }

    public interface vHClickListener{
        void onPosterClicked(int Id, View v);
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
            mVHClickListener.onPosterClicked(mList.get(getAdapterPosition()).getId(), posterImage);
        }
    }
}




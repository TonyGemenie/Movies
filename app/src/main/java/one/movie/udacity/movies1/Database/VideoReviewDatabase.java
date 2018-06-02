package one.movie.udacity.movies1.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {VideoReviewDetails.class}, version = 1, exportSchema = false)
public abstract class VideoReviewDatabase extends RoomDatabase {

        private static final String DATABASE = "detailsdatabase";
        public static VideoReviewDatabase detailsdatabase;

        public static VideoReviewDatabase getInstance(Context context) {
            if(detailsdatabase == null){
                synchronized (new Object()) {
                    detailsdatabase = Room.databaseBuilder(context.getApplicationContext(),
                            VideoReviewDatabase.class, DATABASE)
                            .build();
                }
            }
            return detailsdatabase;
        }

        public abstract VideoReviewDao detailsDao();
}


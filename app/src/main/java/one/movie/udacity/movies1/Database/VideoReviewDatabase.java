package one.movie.udacity.movies1.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {VideoReviewDetails.class}, version = 3, exportSchema = false)
public abstract class VideoReviewDatabase extends RoomDatabase {

    static final Migration MIGRATION = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE detailsdatabase "
                    + " ADD COLUMN identity INTEGER");
        }
    };

    private static final String DATABASE = "detailsdatabase";
    public static VideoReviewDatabase detailsdatabase;

    public static VideoReviewDatabase getInstance(Context context) {
        if(detailsdatabase == null){
            synchronized (new Object()) {
                detailsdatabase = Room.databaseBuilder(context.getApplicationContext(),
                        VideoReviewDatabase.class, VideoReviewDatabase.DATABASE)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return detailsdatabase;
    }

    public abstract VideoReviewDao detailsDao();
}


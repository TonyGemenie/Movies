package one.movie.udacity.movies1.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MovieDetails.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String DATABASE = "moviedatabase";
    public static MovieDatabase movieDatabase;

    public static MovieDatabase getInstance(Context context) {
        if(movieDatabase == null){
            synchronized (new Object()) {
                movieDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, MovieDatabase.DATABASE)
                        .build();
            }
        }
        return movieDatabase;
    }

    public abstract MovieDao movieDao();

}

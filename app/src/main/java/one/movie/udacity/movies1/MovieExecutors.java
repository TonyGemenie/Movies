package one.movie.udacity.movies1;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MovieExecutors {

    private static MovieExecutors sInstance;
    private final Executor disk;
    private final Executor main;
    private final Executor network;

    private MovieExecutors(Executor disk, Executor main, Executor network) {
        this.disk = disk;
        this.main = main;
        this.network = network;
    }

    public static MovieExecutors getsInstance() {
        if(sInstance == null){
            synchronized (new Object()) {
                sInstance = new MovieExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor getDisk() {
        return disk;
    }

    public Executor getMain() {
        return main;
    }

    public Executor getNetwork() {
        return network;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

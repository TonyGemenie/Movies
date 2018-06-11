package one.movie.udacity.movies1;

import android.util.Log;

import timber.log.Timber;

public class myTree extends Timber.DebugTree {

    @Override
    public String createStackElementTag(StackTraceElement ignored) {
        StackTraceElement[] element = newStackTraceElement();
        return getMethodName(element);
    }

    public StackTraceElement[] newStackTraceElement() {
        StackTraceElement [] elements = new Throwable()
                .getStackTrace();
        return elements;
    }

    public String getMethodName(StackTraceElement[] elements) {
        return elements[8].toString();
    }

}

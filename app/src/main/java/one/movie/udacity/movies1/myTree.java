package one.movie.udacity.movies1;

import timber.log.Timber;

public class myTree extends Timber.DebugTree {

    @Override
    public String createStackElementTag(StackTraceElement ignored) {
        StackTraceElement element = newStackTraceElement();
        return getCleanClassName(element);
    }

    public StackTraceElement newStackTraceElement() {
        StackTraceElement [] element = new Throwable()
                .getStackTrace();
        return element[8];
    }

    public String getCleanClassName(StackTraceElement elements) {
        /*String returnString = null;
        for(StackTraceElement element: elements){
            returnString += element;
            Log.i("TAG", "getCleanClassName: " + returnString);
        }*/
        return elements.toString();
    }




}

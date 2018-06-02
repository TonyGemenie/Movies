package one.movie.udacity.movies1.Database;


public class MovieDetails {
    boolean favorite;
    boolean popular;
    boolean toprated;
    int id;
    int voteCount;
    int popularity;
    String title;
    String plot;
    String rating;
    String date;
    String posterPath;
    String originalLanguage;

    public MovieDetails(boolean favorite, boolean popular, boolean toprated, int id, int voteCount, int popularity, String title, String plot, String rating, String date, String posterPath, String originalLanguage) {
        this.favorite = favorite;
        this.popular = popular;
        this.toprated = toprated;
        this.id = id;
        this.voteCount = voteCount;
        this.popularity = popularity;
        this.title = title;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
        this.posterPath = posterPath;
        this.originalLanguage = originalLanguage;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public boolean isToprated() {
        return toprated;
    }

    public void setToprated(boolean toprated) {
        this.toprated = toprated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }




}


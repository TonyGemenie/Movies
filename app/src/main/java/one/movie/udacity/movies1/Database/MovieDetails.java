package one.movie.udacity.movies1.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "moviedatabase")
public class MovieDetails {
    boolean favorite;
    boolean popular;
    boolean toprated;
    @PrimaryKey
    int id;
    int voteCount;
    float popularity;
    String title;
    String overview;
    String voteAverage;
    String releaseDate;
    String posterPath;
    String originalLanguage;

    public MovieDetails(boolean favorite, boolean popular, boolean toprated, int id, int voteCount, float popularity, String title, String overview, String voteAverage, String releaseDate, String posterPath, String originalLanguage) {
        this.favorite = favorite;
        this.popular = popular;
        this.toprated = toprated;
        this.id = id;
        this.voteCount = voteCount;
        this.popularity = popularity;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
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

    public float getPopularity() {
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
        return overview;
    }

    public void setPlot(String plot) {
        this.overview = plot;
    }

    public String getRating() {
        return voteAverage;
    }

    public void setRating(String rating) {
        this.voteAverage = rating;
    }

    public String getDate() {
        return releaseDate;
    }

    public void setDate(String date) {
        this.releaseDate = date;
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


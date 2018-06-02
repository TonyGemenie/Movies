package one.movie.udacity.movies1.Database;

public class VideoReviewDetails {

    int movieID;
    String author;
    String content;
    String reviewid;
    String reviewurl;
    String videoId;
    String iso_639_1;
    String iso_3166_1;
    String videoKey;
    String videoName;
    String site;
    String size;
    String type;

    public VideoReviewDetails(int movieID, String author, String content, String reviewid, String reviewurl, String videoId, String iso_639_1,
                              String iso_3166_1, String videoKey, String videoName, String site, String size, String type) {
        this.movieID = movieID;
        this.author = author;
        this.content = content;
        this.reviewid = reviewid;
        this.reviewurl = reviewurl;
        this.videoId = videoId;
        this.iso_639_1 = iso_639_1;
        this.iso_3166_1 = iso_3166_1;
        this.videoKey = videoKey;
        this.videoName = videoName;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public int getId() {
        return movieID;
    }

    public void setId(int id) {
        this.movieID = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReviewid() {
        return reviewid;
    }

    public void setReviewid(String reviewid) {
        this.reviewid = reviewid;
    }

    public String getReviewurl() {
        return reviewurl;
    }

    public void setReviewurl(String reviewurl) {
        this.reviewurl = reviewurl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

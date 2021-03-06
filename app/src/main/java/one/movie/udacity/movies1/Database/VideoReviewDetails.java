package one.movie.udacity.movies1.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "detailsdatabase")
public class VideoReviewDetails {

    @PrimaryKey(autoGenerate = true)
    int identity;
    private String id;
    private String imageURL;
    private String author;
    private String content;
    private String iso_639_1;
    private String iso_3166_1;
    private String key;
    private String site;
    private String size;
    private String type;
    private byte[] byteData;

    public VideoReviewDetails(String id, String author, String content, String iso_639_1,
                              String iso_3166_1, String key, String site, String size, String type, byte[] byteData) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.iso_639_1 = iso_639_1;
        this.iso_3166_1 = iso_3166_1;
        this.key = key;
        this.site = site;
        this.size = size;
        this.type = type;
        this.byteData = byteData;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getIso_3166_1() {
        return iso_3166_1;
    }

    public String getVideoKey() {
        return key;
    }

    public String getSite() {
        return site;
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

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getByteData() {
        return byteData;
    }

    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }
}

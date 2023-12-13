package generation.model;

import jakarta.persistence.*;

@Entity
public class Image {

    @Id
    private String id;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }
}
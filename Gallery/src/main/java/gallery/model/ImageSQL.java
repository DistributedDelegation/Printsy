package gallery.model;

import jakarta.persistence.*;

@Entity
public class ImageSQL {
    
    @Id
    @Column(unique = true, columnDefinition = "VARCHAR(250)")
    private String imageId;

    @Column(columnDefinition = "TEXT")
    private String usrId;

    @Column(columnDefinition = "BOOLEAN")
    private Boolean isImagePublishedYN;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public Boolean getIsImagePublishedYN() {
        return isImagePublishedYN;
    }

    public void setIsImagePublishedYN(Boolean isImagePublishedYN) {
        this.isImagePublishedYN = isImagePublishedYN;
    }
}

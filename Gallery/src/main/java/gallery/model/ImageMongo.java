package gallery.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ImageMongo {

    @Id
    private String imageId;

    private String imageUrl;

    private String imageName;

    private String imageDescription;

    private Boolean isImagePublishedYN;

    private String imageTimestamp;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public Boolean getIsImagePublishedYN() {
        return isImagePublishedYN;
    }

    public void setIsImagePublishedYN(Boolean isImagePublishedYN) {
        this.isImagePublishedYN = isImagePublishedYN;
    }

    public String getImageTimestamp() {
        return imageTimestamp;
    }

    public void setImageTimestamp(String imageTimestamp) {
        this.imageTimestamp = imageTimestamp;
    }
}

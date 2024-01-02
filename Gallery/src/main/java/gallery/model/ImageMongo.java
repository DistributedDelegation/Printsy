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

    private Integer likeCount;

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

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public String toString() {
        return "ImageMongo{" +
                "imageId='" + imageId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageName='" + imageName + '\'' +
                ", imageDescription='" + imageDescription + '\'' +
                ", isImagePublishedYN=" + isImagePublishedYN +
                ", imageTimestamp='" + imageTimestamp + '\'' +
                ", likeCount=" + likeCount +
                '}';
    }
}

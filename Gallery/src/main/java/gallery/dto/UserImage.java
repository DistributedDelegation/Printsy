package gallery.dto;

public class UserImage {
    private String imageId;
    private String imageUrl;

    public UserImage() {
    }

    public UserImage(String imageid, String imageUrl) {
        this.imageId = imageid;
        this.imageUrl = imageUrl;
    }

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

    @Override
    public String toString() {
        return "UserImage{" +
                "imageId='" + imageId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}

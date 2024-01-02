package gallery.dto;

public class PublishedImage {
    private String imageId;
    private String imageUrl;
    private Integer likeCount;

    public PublishedImage() {
    }

    public PublishedImage(String imageid, String imageUrl, Integer likeCount) {
        this.imageId = imageid;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
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

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public String toString() {
        return "PublishedImage{" +
                "imageId='" + imageId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", likeCount=" + likeCount +
                '}';
    }
}

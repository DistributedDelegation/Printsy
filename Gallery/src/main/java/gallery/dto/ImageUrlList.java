package gallery.dto;

import java.util.List;

public class ImageUrlList {

    private List<String> imageUrls;

    public ImageUrlList() {
    }

    public ImageUrlList(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}

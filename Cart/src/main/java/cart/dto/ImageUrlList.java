package cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ImageUrlList {

    private List<String> imageUrls;

    public ImageUrlList(List<String> imageIds) {
        this.imageUrls = imageIds;
    }

    public ImageUrlList() {
    }

    @JsonProperty("imageUrls")
    public List<String> getImageUrls() {
        return imageUrls;
    }

    @JsonProperty("imageUrls")
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}

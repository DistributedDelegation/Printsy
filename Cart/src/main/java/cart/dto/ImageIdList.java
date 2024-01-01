package cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ImageIdList {

    private List<String> imageIds;

    public ImageIdList(List<String> imageIds) {
        this.imageIds = imageIds;
    }

    public ImageIdList() {
    }

    @JsonProperty("imageIds")
    public List<String> getImageIds() {
        return imageIds;
    }

    @JsonProperty("imageIds")
    public void setImageIds(List<String> imageIds) {
        this.imageIds = imageIds;
    }
}

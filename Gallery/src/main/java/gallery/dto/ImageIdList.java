package gallery.dto;

import java.util.List;

public class ImageIdList {

    private List<String> imageIds;

    public ImageIdList() {
    }

    public ImageIdList(List<String> imageIds) {
        this.imageIds = imageIds;
    }

    public List<String> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<String> imageIds) {
        this.imageIds = imageIds;
    }
}

package cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductResult {

    private String imageId;
    private String imageUrl;
    private Long stockId;
    private Integer price;

    public ProductResult() {
    }

    public ProductResult(String imageId, String imageUrl, Long stockId, Integer price) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.stockId = stockId;
        this.price = price;
    }

    @JsonProperty("imageId")
    public String getImageId() {
        return imageId;
    }

    @JsonProperty("imageId")
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @JsonProperty("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("stockId")
    public Long getStockId() {
        return stockId;
    }

    @JsonProperty("stockId")
    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    @JsonProperty("price")
    public Integer getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(Integer price) {
        this.price = price;
    }
}

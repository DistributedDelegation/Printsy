package worker.model;

import java.util.Date;

public class Transaction {

    private Long userId;
    private String imageId;
    private Date timestamp;

    public Transaction(Long userId, String imageId, Date timestamp) {
        this.userId = userId;
        this.imageId = imageId;
        this.timestamp = timestamp;
    }

    public Transaction() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}

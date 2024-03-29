package gateway.dto;

import gateway.model.Transaction;

import java.util.Date;

public class TransactionInput {

    private Long userId;
    private String imageId;

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

    public Transaction toTransaction(){
        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setImageId(imageId);
        t.setTimestamp(new Date());

        return t;
    }

    @Override
    public String toString() {
        return "TransactionInput{" +
                "userId=" + userId +
                ", imageId='" + imageId + '\'' +
                '}';
    }
}

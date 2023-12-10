package TransactionGateway.model;

public class Transaction {
    private String sender;
    private String receiver;
    private String imageId;

    public Transaction() {
    }

    public Transaction(String sender, String receiver, String imageId) {
        this.sender = sender;
        this.receiver = receiver;
        this.imageId = imageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}


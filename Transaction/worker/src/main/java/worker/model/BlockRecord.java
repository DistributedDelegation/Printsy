package worker.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class BlockRecord {

    @Id
    private ObjectId id;
    private Block block;
    private Long userId;
    private Long imageId;
    private Date timestamp;

    public BlockRecord(Block block) {
        this.block = block;
        this.userId = block.getTransaction().getUserId();
        this.imageId = block.getTransaction().getImageId();
        this.timestamp = new Date();
    }

    public ObjectId getId() {
        return id;
    }

    public Block getBlock() {
        return block;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getImageId() {
        return imageId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}


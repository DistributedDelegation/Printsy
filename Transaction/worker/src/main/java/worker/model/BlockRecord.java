package worker.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Document
public class BlockRecord {

    @Id
    private ObjectId id;
    private Block block;
    private Long userId;
    private Map<String, Integer> imageIdCounts;
    private Date timestamp;

    public BlockRecord(Block block) {
        this.block = block;
        this.userId = block.getTransactions().get(0).getUserId();
        this.imageIdCounts = calculateImageIdCounts(block);
        this.timestamp = new Date();
    }

    public BlockRecord() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Map<String, Integer> getImageIdCounts() {
        return imageIdCounts;
    }

    public void setImageIdCounts(Map<String, Integer> imageIdCounts) {
        this.imageIdCounts = imageIdCounts;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private Map<String, Integer> calculateImageIdCounts(Block block) {
        Map<String, Integer> counts = new HashMap<>();
        for (Transaction transaction : block.getTransactions()) {
            String imageId = transaction.getImageId();
            counts.put(imageId, counts.getOrDefault(imageId, 0) + 1);
        }
        return counts;
    }

}


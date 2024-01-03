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
    private final Block block;
    private final Long userId;
    private final Map<String, Integer> imageIdCounts;
    private final Date timestamp;

    public BlockRecord(Block block) {
        this.block = block;
        this.userId = block.getTransactions().get(0).getUserId();
        this.imageIdCounts = calculateImageIdCounts(block);
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

    public Map<String, Integer> getImageIdCounts() {
        return imageIdCounts;
    }

    public Date getTimestamp() {
        return timestamp;
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


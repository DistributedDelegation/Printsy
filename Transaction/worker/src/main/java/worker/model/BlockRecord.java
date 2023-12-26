package worker.model;

import java.util.Date;
import java.util.UUID;

public class BlockRecord {

    private UUID blockId;
    private Block block;
    private Integer userId;
    private Integer imageId;
    private Date timestamp;

    public BlockRecord(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

package worker.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import worker.model.BlockRecord;

import java.util.List;
import java.util.UUID;

public interface BlockchainRepository extends MongoRepository<BlockRecord, ObjectId> {

    List<BlockRecord> findAllByUserId(long userId);

    @Query("{'block.imageIdCounts.?0': {$exists: true}}")
    List<BlockRecord> findAllByImageId(String imageId);

    BlockRecord findTopByOrderByTimestampDesc();

    default int countByImageId(String imageId) {
        List<BlockRecord> records = findAllByImageId(imageId);
        int totalCount = 0;
        for (BlockRecord record : records) {
            Integer count = record.getImageIdCounts().get(imageId);
            if (count != null) {
                totalCount += count;
            }
        }
        return totalCount;
    }
}

package worker.repository;

import org.apache.juli.logging.Log;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import worker.WorkerNodeServer;
import worker.model.BlockRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public interface BlockchainRepository extends MongoRepository<BlockRecord, ObjectId> {

    Logger LOGGER = Logger.getLogger(BlockchainRepository.class.getName());

    List<BlockRecord> findAllByUserId(long userId);

    @Query("{'imageIdCounts.?0': {$exists: true}}")
    List<BlockRecord> findAllByImageId(String imageId);

    BlockRecord findTopByOrderByTimestampDesc();

    default int countByImageId(String imageId) {
        List<BlockRecord> records = findAllByImageId(imageId);
        int totalCount = 0;
        for (BlockRecord record : records) {

            Map<String, Integer> imageCounts = record.getImageIdCounts();
            LOGGER.info("Image counts for record: " + imageCounts);
            Integer count = imageCounts.get(imageId);
            LOGGER.info("Image count for imageId: " + count);

            if (count != null) {
                totalCount += count;
            }
        }
        return totalCount;
    }
}

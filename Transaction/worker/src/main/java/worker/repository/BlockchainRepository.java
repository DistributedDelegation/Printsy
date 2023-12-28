package worker.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import worker.model.BlockRecord;

import java.util.List;
import java.util.UUID;

public interface BlockchainRepository extends MongoRepository<BlockRecord, ObjectId> {

    public List<BlockRecord> findAllByUserId(long userId);

    public List<BlockRecord> findAllByImageId(String imageId);

    public BlockRecord findTopByOrderByTimestampDesc();
    public int countByImageId(String imageId);
}

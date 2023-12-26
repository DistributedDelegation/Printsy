package worker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import worker.model.BlockRecord;

import java.util.List;
import java.util.UUID;

public interface BlockchainRepository extends MongoRepository<BlockRecord, UUID> {

    public List<BlockRecord> findAllByUserId(long userId);

    public List<BlockRecord> findAllByImageId(long imageId);

    public BlockRecord findTopByOrderByTimestampDesc();
    public int countByImageId(long imageId);
}

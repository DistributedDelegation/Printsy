package worker.service;

import org.springframework.stereotype.Service;
import worker.model.Block;
import worker.model.Transaction;
import worker.repository.BlockchainRepository;

@Service
public class BlockchainValidator {

    private BlockchainRepository blockchainRepository;

	public BlockchainValidator(BlockchainRepository blockchainRepository) {
            this.blockchainRepository = blockchainRepository;
    }

    public void validateTransaction(Transaction transaction, Block lastBlock) throws Exception {

        if (transaction.getUserId() == null || transaction.getImageId() == null) {
            throw new Exception("Transaction validation failed: User ID and Image ID are required.");
        }

        if (blockchainRepository.countByImageId(transaction.getImageId()) >= 10) {
            throw new Exception("Transaction validation failed: Image count exceeds the limit.");
        }

        if (transaction.getTimestamp().before(lastBlock.getTransaction().getTimestamp())) {
            throw new Exception("Transaction validation failed: Timestamp is earlier than the last block.");
        }

    }


    public void validateBlock(Block block, Block lastBlock) {
        // de hash

        //check pow

        //check seq #
    }

}

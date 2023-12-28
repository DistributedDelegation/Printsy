package worker.blockchainNetwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import worker.State;
import worker.model.Block;
import worker.model.BlockRecord;
import worker.model.Transaction;
import worker.repository.BlockchainRepository;
import worker.service.BlockchainMiner;
import worker.service.BlockchainValidator;

import java.util.ArrayList;
import java.util.List;

@Component
public class BlockchainNode {
    private BlockchainNodeRegistry registry;
    private TransactionPool transactionPool;
    private State state;
    private BlockchainValidator validator;
    private BlockchainMiner blockchainMiner;
    private BlockchainRepository blockchainRepository;
    // vote storage for validation
    // vote storage for blocks

    @Autowired
    public BlockchainNode(BlockchainValidator validator, BlockchainMiner blockchainMiner, BlockchainRepository blockchainRepository){
        this.validator = validator;
        this.blockchainMiner = blockchainMiner;
        this.blockchainRepository = blockchainRepository;
    }

    private Block getLastBlock(){
        return blockchainRepository.findTopByOrderByTimestampDesc().getBlock();
    }

    public void writeTransaction(Transaction t) throws Exception {
        Block lastBlock = getLastBlock();
        validator.validateTransaction(t, lastBlock);
        Block block = blockchainMiner.mineBlock(t, lastBlock);
        BlockRecord blockRecord = new BlockRecord(block);
        blockchainRepository.save(blockRecord);
    }

    public List<Transaction> getTransactionsByUserId(long userId) {
        List<BlockRecord> blockRecords = blockchainRepository.findAllByUserId(userId);

        List<Transaction> transactions = new ArrayList<Transaction>();
        for (BlockRecord blockRecord : blockRecords) {
            transactions.add(blockRecord.getBlock().getTransaction());
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByImageId(long imageId) {
        List<BlockRecord> blockRecords = blockchainRepository.findAllByImageId(imageId);

        List<Transaction> transactions = new ArrayList<Transaction>();
        for (BlockRecord blockRecord : blockRecords) {
            transactions.add(blockRecord.getBlock().getTransaction());
        }
        return transactions;
    }

    public int getImageTransactionCount(long imageId){
        return blockchainRepository.countByImageId(imageId);
    }

}

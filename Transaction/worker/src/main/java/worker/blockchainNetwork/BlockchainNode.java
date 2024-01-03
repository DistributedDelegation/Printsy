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

    public void writeTransaction(List<Transaction> transactions) throws Exception {
        Block lastBlock = getLastBlock();
        for (Transaction transaction : transactions){
            validator.validateTransaction(transaction, lastBlock);
        }
        Block block = blockchainMiner.mineBlock(transactions, lastBlock);
        BlockRecord blockRecord = new BlockRecord(block);
        blockchainRepository.save(blockRecord);
    }

    public List<List<Transaction>> getTransactionsByUserId(long userId) {
        List<BlockRecord> blockRecords = blockchainRepository.findAllByUserId(userId);

        List<List<Transaction>> transactions = new ArrayList<>();
        for (BlockRecord blockRecord : blockRecords) {
            transactions.add(blockRecord.getBlock().getTransactions());
        }
        return transactions;
    }

    public List<List<Transaction>> getTransactionsByImageId(String imageId) {
        List<BlockRecord> blockRecords = blockchainRepository.findAllByImageId(imageId);

        List<List<Transaction>> transactions = new ArrayList<>();
        for (BlockRecord blockRecord : blockRecords) {
            transactions.add(blockRecord.getBlock().getTransactions());
        }
        return transactions;
    }

    public int getImageTransactionCount(String imageId){
        return blockchainRepository.countByImageId(imageId);
    }

}

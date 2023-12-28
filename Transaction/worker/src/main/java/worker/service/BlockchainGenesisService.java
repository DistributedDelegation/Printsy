package worker.service;

import org.springframework.stereotype.Service;
import worker.model.Transaction;
import worker.model.Block;
import worker.model.BlockRecord;
import worker.repository.BlockchainRepository;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class BlockchainGenesisService {

    private final BlockchainRepository blockchainRepository;
    private final BlockchainMiner blockchainMiner;

    public BlockchainGenesisService(BlockchainRepository blockchainRepository, BlockchainMiner blockchainMiner) {
        this.blockchainRepository = blockchainRepository;
        this.blockchainMiner = blockchainMiner;
    }

    public void initializeBlockchain() {
        if (blockchainRepository.count() == 0) {
            try {
                Block genesisBlock = createGenesisBlock();
                System.out.println("Initialized blockchain with genesis block" + genesisBlock);
            } catch (Exception e){
                System.out.println("Failed to initialize blockchain: " + e);
                for (StackTraceElement element: e.getStackTrace()){
                    System.out.println("\t " + element.toString());
                }
            }
        }
    }

    private Block createGenesisBlock() throws NoSuchAlgorithmException {
        // Initializing the transaction for the genesis block
        Transaction genesisTransaction = new Transaction();
        genesisTransaction.setUserId(0L);
        genesisTransaction.setImageId("0");
        genesisTransaction.setTimestamp(new Date());

        // Initializing the genesis block
        Block genesisBlock = new Block();
        genesisBlock.setSequenceNo(0); // Genesis block is the first block so sequence number is 0
        genesisBlock.setPreviousHash("0"); // No preceding block, so set to 0
        genesisBlock.setNonce(0);
        genesisBlock.setTransaction(genesisTransaction);
        genesisBlock.setHash(blockchainMiner.calculateHash(genesisBlock, 0));

        BlockRecord genesisRecord = new BlockRecord(genesisBlock);
        blockchainRepository.save(genesisRecord);

        return genesisBlock;
    }


}

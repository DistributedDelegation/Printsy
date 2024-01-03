package worker.service;

import org.springframework.stereotype.Service;
import worker.model.Block;
import worker.model.Transaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class BlockchainMiner {
    public Block mineBlock(List<Transaction> t, Block lastBlock) throws NoSuchAlgorithmException {
        Block newBlock = new Block();
        newBlock.setTransactions(t);
        newBlock.setPreviousHash(lastBlock.getHash());
        newBlock.setSequenceNo(lastBlock.getSequenceNo());

        // Proof of Work
        int nonce = 0;
        String hash;
        do {
            nonce++;
            hash = calculateHash(newBlock, nonce);
        } while(!hash.startsWith("0"));

        newBlock.setNonce(nonce);
        newBlock.setHash(hash);

        return newBlock;
    }


    public String calculateHash(Block block, int nonce) throws NoSuchAlgorithmException {
        String input = block.getPreviousHash()
                + block.getTransactions().toString()
                + nonce;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b: hash){
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}

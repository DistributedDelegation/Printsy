package worker;

import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.blockchainNetwork.BlockchainNode;
import worker.TransactionServiceGrpc.TransactionServiceImplBase;
import worker.WorkerMessages.ImageCountRequest;
import worker.WorkerMessages.ImageCountResponse;
import worker.WorkerMessages.TransactionRequest;
import worker.WorkerMessages.TransactionResponse;
import worker.model.Transaction;
import worker.util.TimestampConverter;

import java.util.List;
import java.util.logging.Logger;

@Service
public class TransactionServiceImpl extends TransactionServiceImplBase {

    public Logger logger;
    public BlockchainNode node;

    @Autowired
    public TransactionServiceImpl(BlockchainNode node) {
        this.logger = Logger.getLogger(WorkerNodeServer.class.getName());
        this.node = node;
    }

    @Override
    public void checkImageCount(ImageCountRequest request, StreamObserver<ImageCountResponse> responseObserver){
        logger.info("Got request from client:" + request);

        int imageCount = node.getImageTransactionCount(request.getImageId());
        ImageCountResponse response = ImageCountResponse.newBuilder().setImageId(request.getImageId()).setImageCount(imageCount).build();

        logger.info("Response is "+ response.toString());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void submitTransactions(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver){
        logger.info("Got request from client: " + request);

        List<Transaction> transactions = request.getTransactionsList().stream().map(protoTransaction -> new Transaction(
                        protoTransaction.getUserId(),
                        protoTransaction.getImageId(),
                        TimestampConverter.fromProtoTimestamp(protoTransaction.getTimestamp())
                )).toList();

        for (Transaction transaction: transactions) {
            logger.info(transaction.toString());
        }

        TransactionResponse response = null;
        try {
            node.writeTransaction(transactions);
            response = TransactionResponse.newBuilder().setStatus(true).setDetail("Success!").build();
        } catch (Exception e) {
            response = TransactionResponse.newBuilder().setStatus(false).setDetail("Failed to execute transaction" + e).build();
        }

        logger.info("Response is "+ response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

}
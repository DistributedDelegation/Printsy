package gateway.service;

import gateway.model.Transaction;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import worker.TransactionServiceGrpc;
import worker.TransactionServiceGrpc.TransactionServiceBlockingStub;
import worker.WorkerMessages;
import worker.WorkerMessages.ImageCountRequest;
import worker.WorkerMessages.ImageCountResponse;
import worker.WorkerMessages.ValidateTransactionRequest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class ClientNodeService {

    private static final Logger logger = Logger.getLogger(ClientNodeService.class.getName());

    private TransactionServiceBlockingStub blockingStub;
    private Channel channel1;

    @Value("${grpc.server.address}")
    private String serverAddress;

    @PostConstruct
    private void init() {
        channel1 = ManagedChannelBuilder.forTarget(serverAddress).usePlaintext().build();
        blockingStub = TransactionServiceGrpc.newBlockingStub(channel1);
    }

    public int checkImageCount(Long imageId) {
        logger.info("Creating request to check image count on the blockchain for " + imageId);
        ImageCountRequest request = ImageCountRequest.newBuilder().setImageId(imageId).build();
        logger.info("Sending request to server at port " + serverAddress + ": " + request);

        int response;
        ImageCountResponse grpcResponse;

        try {
            grpcResponse = blockingStub.checkImageCount(request);
            logger.info("Received the following from the server: ImageId is " + grpcResponse.getImageId() + " and ImageCount is " + grpcResponse.getImageCount());
            response = grpcResponse.getImageCount();
        } catch (StatusRuntimeException e) {
            logger.warning("RPC Failed:" +  e.getStatus());
            response = -1;
        }

        return response;
    }

    public boolean validateTransaction(Transaction transaction) {
        logger.info("Creating request to check transaction is valid addition to the blockchain");
        ValidateTransactionRequest request = ValidateTransactionRequest.newBuilder().setImageId(transaction.getImageId()).setUserId(transaction.getUserId()).build();
        logger.info("Sending validation request to server at port" + serverAddress + ":" + request);

        boolean response;
        WorkerMessages.ValidateTransactionResponse grpcResponse;

        try {
            grpcResponse = blockingStub.validateTransaction(request);
            logger.info("Received the following from the server: " + grpcResponse.toString());
            response = grpcResponse.getIsValid();
        } catch (StatusRuntimeException e) {
            logger.warning("RPC Failed:" +  e.getStatus());
            response = false;
        }

        return response;
    }

    @PreDestroy
    private void destroy() {
        try {
            ((ManagedChannel) channel1).shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warning("Error during channel shutdown");
        }
    }

}

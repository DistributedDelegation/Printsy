package TransactionGateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import TransactionGateway.dto.TransactionResult;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import worker.Message;
import worker.Message.TestRequest;
import worker.Message.TestResponse;
import worker.WorkerServiceGrpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class ClientNodeService {

    private static final Logger logger = Logger.getLogger(ClientNodeService.class.getName());

    private WorkerServiceGrpc.WorkerServiceBlockingStub blockingStub1;
    private WorkerServiceGrpc.WorkerServiceBlockingStub blockingStub2;
    private Channel channel1;
    private Channel channel2;

    @Value("${grpc.server.address1}")
    private String serverAddress1;

    @Value("${grpc.server.address2}")
    private String serverAddress2;

    @PostConstruct
    private void init() {
        channel1 = ManagedChannelBuilder.forTarget(serverAddress1).usePlaintext().build();
        blockingStub1 = WorkerServiceGrpc.newBlockingStub(channel1);

        channel2 = ManagedChannelBuilder.forTarget(serverAddress2).usePlaintext().build();
        blockingStub2 = WorkerServiceGrpc.newBlockingStub(channel2);
    }

    public List<TransactionResult> makeTestRequest(String sender, String receiver, String imageId) {
        logger.info("Creating test request: transaction between " + sender + " and " + receiver + " for " + imageId);
        TestRequest request1 = TestRequest.newBuilder().setSender(sender).setReceiver(receiver).setImageId(imageId).build();
        TestRequest request2 = TestRequest.newBuilder().setSender(sender).setReceiver(receiver).setImageId(imageId).build();
        logger.info("Sending request to server at port" + serverAddress1 + ":" + request1);
        logger.info("Sending request to server at port" + serverAddress2 + ":" + request1);
        TestResponse response1;
        TestResponse response2;

        List<TransactionResult> transactionResult = new ArrayList<>();

        try {
            response1 = blockingStub1.processRequest(request1);
            response2 = blockingStub2.processRequest(request2);
            logger.info("Received the following from the server: " + response1.getResult());
            logger.info("Received the following from the server: " + response2.getResult());
            transactionResult.add(new TransactionResult(response1.getSuccess(), response1.getResult()));
            transactionResult.add(new TransactionResult(response2.getSuccess(), response2.getResult()));
        } catch (StatusRuntimeException e) {
            logger.warning("RPC Failed:" +  e.getStatus());
            transactionResult.add(new TransactionResult(false, 0));
        }

        return transactionResult;
    }

    @PreDestroy
    private void destroy() {
        try {
            ((ManagedChannel) channel1).shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            ((ManagedChannel) channel2).shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warning("Error during channel shutdown");
        }
    }

}

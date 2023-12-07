package TransactionGateway.service;

import java.awt.*;
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

    private WorkerServiceGrpc.WorkerServiceBlockingStub blockingStub;
    private Channel channel;

    @Value("${grpc.server.address}")
    private String serverAddress;

    @PostConstruct
    private void init() {
        channel = ManagedChannelBuilder.forTarget(serverAddress).usePlaintext().build();
        blockingStub = WorkerServiceGrpc.newBlockingStub(channel);
    }

    public TransactionResult makeTestRequest(String sender, String receiver, String imageId) {
        logger.info("Creating test request: transaction between " + sender + " and " + receiver + " for " + imageId);
        TestRequest request = TestRequest.newBuilder().setSender(sender).setReceiver(receiver).setImageId(imageId).build();
        logger.info("Sending request to server" + request);
        TestResponse response;
        try {
            response = blockingStub.processRequest(request);
        } catch (StatusRuntimeException e) {
            logger.warning("RPC Failed:" +  e.getStatus());
            return new TransactionResult(false, 0);
        }

        logger.info("Received the following from the server: " + response.getResult());
        return new TransactionResult(response.getSuccess(), response.getResult());
    }

    @PreDestroy
    private void destroy() {
        try {
            ((ManagedChannel) channel).shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warning("Error during channel shutdown");
        }
    }

}

package worker;

import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.blockchainNetwork.BlockchainNode;
import worker.TransactionServiceGrpc.TransactionServiceImplBase;
import worker.WorkerMessages.ImageCountRequest;
import worker.WorkerMessages.ImageCountResponse;

import java.util.logging.Logger;

@Service
public class WorkerImpl extends TransactionServiceImplBase {

    public Logger logger;
    public BlockchainNode node;

    @Autowired
    public WorkerImpl(BlockchainNode node) {
        this.logger = Logger.getLogger(WorkerNodeServer.class.getName());
        this.node = node;
    }

    @Override
    public void checkImageCount(ImageCountRequest request, StreamObserver<ImageCountResponse> responseObserver){
        logger.info("Got request from client:" + request);

        int imageCount = node.getImageTransactionCount(request.getImageId());
        ImageCountResponse response = ImageCountResponse.newBuilder().setImageId(request.getImageId()).setImageCount(imageCount).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
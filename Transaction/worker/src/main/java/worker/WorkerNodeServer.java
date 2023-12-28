package worker;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import worker.service.BlockchainGenesisService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class WorkerNodeServer {


    private final TransactionServiceImpl worker;
    private final BlockchainGenesisService blockchainGenesisService;
    private Server server;

    @Autowired
    public WorkerNodeServer(TransactionServiceImpl worker, BlockchainGenesisService blockchainGenesisService){
        this.worker = worker;
        this.blockchainGenesisService = blockchainGenesisService;
    }

    @PostConstruct
    public void start() throws IOException {
        int port = 50051;

        worker.logger.info("Starting server on " + port);

        blockchainGenesisService.initializeBlockchain();

        worker.logger.info("Initialized blockchain");

        server = ServerBuilder.forPort(port).
                addService(worker).
                build().start();

        worker.logger.info("Server started, listening on " + port);
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
            worker.logger.info("Server shut down");
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}

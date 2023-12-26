package worker;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class WorkerNodeServer {


    private WorkerImpl worker;
    private Server server;

    @Autowired
    public WorkerNodeServer(WorkerImpl worker){
        this.worker = worker;
    }

    @PostConstruct
    public void start() throws IOException {
        int port = 50051;
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

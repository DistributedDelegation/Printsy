package TransactionWorker;

import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import io.grpc.ServerBuilder;

import worker.Message.TestRequest;
import worker.Message.TestResponse;
import worker.WorkerServiceGrpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WorkerNodeServer {
    private static final Logger logger = Logger.getLogger(WorkerNodeServer.class.getName());
    private Server server;

    private void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port).addService(new WorkerImpl()).build().start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.err.println("Shutting down gRPC server");
                try {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });

    }


    static class WorkerImpl extends WorkerServiceGrpc.WorkerServiceImplBase {
        @Override
        public void processRequest(TestRequest request, StreamObserver<TestResponse> responseObserver){
            logger.info("Got request from client:" + request);

            // LOGIC FOR VALIDATING TRANSACTIONS GOES HERE!

            TestResponse response = TestResponse.newBuilder().setSuccess(true).setResult(123456).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    public static void main (String[] args) throws IOException, InterruptedException {
        final WorkerNodeServer workerNodeServer = new WorkerNodeServer();
        workerNodeServer.start();
        workerNodeServer.server.awaitTermination();
    }
}

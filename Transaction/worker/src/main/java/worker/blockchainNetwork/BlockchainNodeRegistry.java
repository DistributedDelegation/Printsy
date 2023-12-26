package worker.blockchainNetwork;

import io.grpc.Server;

import java.util.List;

public class BlockchainNodeRegistry {

    private List<Server> workers;

    public BlockchainNodeRegistry(List<Server> workers) {
    }

    public List<Server> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Server> workers) {
        this.workers = workers;
    }


}

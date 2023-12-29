package cart.service;

import cart.model.Product;
import cart.queue.CartItemTask;
import cart.queue.CartQueue;
import cart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.logging.Logger;

@Service
public class CartQueueService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    // Use ConcurrentHashMap for thread-safe operations
    private final Map<String, CartQueue> queueMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ProductRepository productRepository;

    @Autowired
    public CartQueueService(ProductRepository productRepository){
        this.productRepository = productRepository;
        startProcessing();
    }

    // Adds a new CartQueue to the dictionary with the given imageId
    public Boolean addToQueue(String imageId, CartItemTask cartItemTask) {
        // Compute if absent will run the lambda function is the key is not in the Queue Map
        CartQueue cartQueue = queueMap.computeIfAbsent(imageId, k -> new CartQueue());
        System.out.println("Added to queue: " + imageId);
        if (cartQueue.enqueue(cartItemTask)){
            LOGGER.info("success");
            return true;
        }
        else {
            LOGGER.severe("failed to enqueue task");
            return false;
        }
    }


    private void startProcessing(){
        executorService.submit(this::processAllQueues);
    }

    // Processes all CartQueues in order
    public void processAllQueues() {
        // running is set to true, so this will run continuously
        while (running.get()) {
            try {
                queueMap.forEach((imageId, cartQueue) -> {
                    CartItemTask task = cartQueue.dequeue();
                    if (task != null){
                        processTask(task);
                    }
                });
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        running.set(false);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }


    private void processTask(CartItemTask task){
        // Create product and add to Products table
        Product newProduct = new Product();
        newProduct.setImageId(task.getImageId());
        newProduct.setStockId(task.getStockId());
        newProduct.setPrice(task.getPrice());
        Product savedProduct = productRepository.save(newProduct);

        if (savedProduct.getProductId() == null) {
            String errorMessage = "Failed to save product in the database for Image ID: " + task.getImageId();
            LOGGER.severe(errorMessage);
            return;
        }

        // Add product to cart queue
        LOGGER.info("Product created with Product ID: " + savedProduct.getProductId() + ". Enqueuing to add to cart.");

    }

}

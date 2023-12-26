package cart.service;

import cart.model.Cart;
import cart.model.Product;
import cart.queue.CartItemTask;
import cart.queue.CartQueue;
import cart.repository.CartRepository;
import cart.repository.ProductRepository;
import cart.service.TaskSchedulerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.logging.Logger;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class CartService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final TaskSchedulerService taskSchedulerService;
    private final CartQueue cartQueue;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, TaskSchedulerService taskSchedulerService, CartQueue cartQueue) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.taskSchedulerService = taskSchedulerService;
        this.cartQueue = cartQueue;
    }

    // ----------------- Transaction Service -----------------
    // checked with: {"query": "query FindImageAvailability($imageId: ID!) { findImageAvailability(imageId: $imageId) }","variables": { "imageId": "1" } }
    WebClient webClient = WebClient.builder()
        .baseUrl("http://transaction-gateway")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    // Needs a Query in TransactionGateway to get the count of transactions for an image
    public boolean getTransactionImageAvailability(Long imageId) {
        //Integer response = webClient.post()
            // .uri("/graphql")
            // .bodyValue("{ \"query\": \"query GetTransactionsForImage($imageId: String!) { getTransactionsForImage(imageId: $imageId) { sender receiver imageId } }\", \"variables\": { \"imageId\": \"" + imageId + "\" } }")
            // .retrieve()
            // .bodyToMono(Integer.class)
            // .block();
        
        Integer transactionCount = 0;
    
        return isImageAvailable(imageId, transactionCount);
    }

    public boolean isImageAvailable(Long imageId, Integer transactionCount) {
        // Check the total count of the image in carts and transactions
        Integer countInCarts = cartRepository.checkImagesInCart(imageId);
        Integer total = countInCarts + transactionCount;
    
        // Determine availability (assuming a threshold of 10)
        return total < 10;
    }

    // ----------------- Cart Service -----------------
    // checked with: {"query": "query findImageByImageId($imageId: ID!) { findImageByImageId(imageId: $imageId) }","variables": { "imageId": "1" }}
    public Integer getImageByImageId(Long imageId) {
        if (cartRepository.checkImagesInCart(imageId) != null) {
            return cartRepository.checkImagesInCart(imageId);
        } else {
            throw new RuntimeException("Image with ID " + imageId + " not found");
        }
    }

    // checked with: {"query": "query findCartItemsByProductId($productId: ID!) { findCartItemsByProductId(productId: $productId) }","variables": { "productId": "1" }}
    public Integer getCartItemsByProductId(Long productId) {
        if(cartRepository.checkImagesInCart(productId) != null) {
            return cartRepository.checkImagesInCart(productId);
        } else {
            throw new RuntimeException("Product with ID " + productId + " not found");
        }
    }

    // checked with: {"query": "query findCartItemsByUserId($userId: ID!) { findCartItemsByUserId(userId: $userId) { productId userId expirationTime } }","variables": { "userId": "1" }}
    public List<Cart> getCartItemsByUserId(Long userId) {
        if (cartRepository.checkCartItemsByUserId(userId).isEmpty()) {
           throw new RuntimeException("No cart items found for user with ID " + userId);
        }
        return cartRepository.checkCartItemsByUserId(userId);
    }

    // checked with: {"query": "query findProductById($productId: ID!) { findProductById(productId: $productId) { productId imageId stockId price } }","variables": { "productId": "1" }}
    public Optional<Product> getProductById(Long productId) {
        if (productRepository.existsById(productId)) {
            return productRepository.checkByProductId(productId);
        } else {
            throw new RuntimeException("Product with ID " + productId + " not found");
        }
    }

    // checked with: {"query": "query findAllProducts { findAllProducts { productId imageId stockId price } }"}
    public List<Product> getAllProducts() {
        if (productRepository.checkAllProducts().isEmpty()) {
            throw new RuntimeException("No products found");
        }
        return productRepository.checkAllProducts();
    }

    // added a cart item with user_id = 333 for the check
    // checked with: {"query": "mutation deleteCartItemsByUserId($userId: ID!) { deleteCartItemsByUserId(userId: $userId) }","variables": { "userId": "333" }}
    public void deleteCartItemsByUserId(Long userId) {
        if (cartRepository.checkCartItemsByUserId(userId).isEmpty()) {
            throw new RuntimeException("No cart items found for user with ID " + userId);
        }
        cartRepository.deleteByUserId(userId);
    }

    // ----------------- Product Creation & Enqueuing -----------------
    // checked with: {"query": "mutation AddProductToProducts($imageId: ID!, $stockId: ID!, $price: Int!, $userId: ID!) { addProductToProducts(imageId: $imageId, stockId: $stockId, price: $price, userId: $userId) }","variables": {"imageId": "1", "stockId": "1", "price": 100, "userId": "1" } }
    public Boolean addProductToProducts(Long imageId, Long stockId, Integer price, Long userId) {
        LOGGER.info("Attempting to create product with Image ID: " + imageId + ", Stock ID: " + stockId + ", Price: " + price);
        // Check if image is available
        if (!getTransactionImageAvailability(imageId)) {
            String errorMessage = "Image with ID " + imageId + " is not available";
            LOGGER.warning(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        // Create product and add to Products table
        Product newProduct = new Product();
        newProduct.setImageId(imageId);
        newProduct.setStockId(stockId);
        newProduct.setPrice(price);
        Product savedProduct = productRepository.save(newProduct);
        if (savedProduct == null || savedProduct.getProductId() == null) {
            String errorMessage = "Failed to save product in the database for Image ID: " + imageId;
            LOGGER.severe(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        // Add product to cart queue
        LOGGER.info("Product created with Product ID: " + savedProduct.getProductId() + ". Enqueuing to add to cart.");
        CartItemTask task = new CartItemTask(userId, savedProduct.getProductId(), imageId);
        cartQueue.enqueue(task);
        return true;
    }    

    // ----------------- Scheduled Task -----------------
    public void scheduleCartCleanup(Long userId) {
        Runnable cleanupTask = () -> {
            System.out.println("Running scheduled cart cleanup for user ID: " + userId);
            deleteCartItemsByUserId(userId);
        };

        taskSchedulerService.scheduleTask(cleanupTask);
    }

    // checked with: {"query": "{ getRemainingCleanupTime }"}
    public Duration getRemainingCleanupTime() {
        return taskSchedulerService.getRemainingTime();
    }

    // ----------------- Queue Tasks -----------------
    // checked with: {"query": "{ peekQueue { userId productId imageId } }"}
    public CartItemTask peekQueue() {
        return cartQueue.peekQueue();
    }

    private void addCartItem(CartItemTask task) {
        LOGGER.info("Adding new cart item for User ID: " + task.getUserId() + ", Product ID: " + task.getProductId());
        cartRepository.insertCartItem(task.getUserId(), task.getProductId(), new Date());  // date logic?
    }
    
    // Update logic exists but due to time constraints, not implemented
    private void updateCartItem(CartItemTask task) {
        LOGGER.info("Updating existing cart item for User ID: " + task.getUserId() + ", Product ID: " + task.getProductId());
        cartRepository.updateCartItem(task.getUserId(), task.getProductId(), new Date());  // date logic?
    }  

    @Scheduled(fixedDelay = 10000) // 10 seconds
    public void processQueue() {
        CartItemTask task;
        while ((task = cartQueue.dequeue()) != null) {
            if (!cartRepository.existsByUserIdAndProductId(task.getUserId(), task.getProductId())) {
                // Add new cart item
                addCartItem(task);
            } else {
                // Update existing cart item
                updateCartItem(task);
            }
            scheduleCartCleanup(task.getUserId());  // Schedule delete task
        }
    }

}
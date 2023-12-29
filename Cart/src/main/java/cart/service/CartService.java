package cart.service;

import cart.model.Cart;
import cart.model.Product;
import cart.dto.TransactionInput;
import cart.queue.CartItemTask;
import cart.queue.CartQueue;
import cart.repository.CartRepository;
import cart.repository.ProductRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.logging.Logger;
import java.time.Duration;

@Service
public class CartService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final TaskSchedulerService taskSchedulerService;
    private final CartQueue cartQueue;
    private final TransactionGatewayService transactionGatewayService;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, TaskSchedulerService taskSchedulerService, CartQueue cartQueue, TransactionGatewayService transactionGatewayService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.taskSchedulerService = taskSchedulerService;
        this.cartQueue = cartQueue;
        this.transactionGatewayService = transactionGatewayService;
    }

    // ----------------- Transaction Service -----------------
    public boolean isImageAvailable(String imageId) {
        // Check the total count of the image in carts and transactions
        Integer countInCarts = cartRepository.checkImagesInCart(imageId);
        Integer countInTransactions = transactionGatewayService.getTransactionImageAvailability(imageId);
        Integer total = countInCarts + countInTransactions;
        // Determine availability (assuming a threshold of 10)
        return total < 10;
    }

    // checked with: {"query": "mutation CompletePurchase($userId: ID!) { completePurchase(userId: $userId) }","variables": { "userId": "1" }}
    public Boolean completePurchase(Long userId) throws JsonProcessingException {
        // Cancel the scheduled task
        taskSchedulerService.cancelScheduledTask();
        List<Cart> cartItems = cartRepository.findAllByUserId(userId);
        List<TransactionInput> transactionInputs = cartItems.stream() // Convert list to stream
                .map(Cart::toTransactionInput) // Map each cart item to a TransactionInput
                .toList(); // Convert the stream back into a list

        if (transactionInputs.isEmpty()) {
            LOGGER.info("No items in the cart for user ID " + userId);
            return false;
        }
        boolean success = transactionGatewayService.completeTransaction(transactionInputs);
        if (success) {
            cartRepository.deleteAll(cartItems);
        }
        return success;
    }


    // ----------------- Cart Service -----------------
    // checked with: {"query": "query findImageByImageId($imageId: ID!) { findImageByImageId(imageId: $imageId) }","variables": { "imageId": "1" }}
    public Integer getImageByImageId(String imageId) {
        if (cartRepository.checkImagesInCart(imageId) != null) {
            return cartRepository.checkImagesInCart(imageId);
        } else {
            throw new RuntimeException("Image with ID " + imageId + " not found");
        }
    }

    // checked with: {"query": "query findCartItemsByProductId($productId: ID!) { findCartItemsByProductId(productId: $productId) }","variables": { "productId": "1" }}
    // public Integer getCartItemsByProductId(Long productId) {
    //     if(cartRepository.checkImagesInCart(productId) != null) {
    //         return cartRepository.checkImagesInCart(productId);
    //     } else {
    //         throw new RuntimeException("Product with ID " + productId + " not found");
    //     }
    // }

    // checked with: {"query": "query findCartItemsByUserId($userId: ID!) { findCartItemsByUserId(userId: $userId) { productId userId expirationTime } }","variables": { "userId": "1" }}
    public List<Cart> getCartItemsByUserId(Long userId) {
        List<Cart> cartItems = cartRepository.findAllByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("No cart items found for user with ID " + userId);
        }
        return cartItems;
    }

    // checked with: {"query": "query checkCartProductsByUserId($userId: ID!) { checkCartProductsByUserId(userId: $userId) { productId imageId stockId price } }","variables": {"userId": "1"}}
    public List<Product> checkCartProductsByUserId(Long userId) {
        if (productRepository.checkCartProductsByUserId(userId).isEmpty()) {
           throw new RuntimeException("No cart items found for user with ID " + userId);
        }
        return productRepository.checkCartProductsByUserId(userId);
    }

    // checked with: {"query": "query findProductById($productId: ID!) { findProductById(productId: $productId) { productId imageId stockId price } }","variables": { "productId": "1" }}
    public Product getProductById(Long productId) {

        Optional<Product> product = productRepository.findByProductId(productId);

        if (product.isPresent()) {
            return product.get();
        }
        else {
            throw new RuntimeException("Product with ID " + productId + " not found");
        }
    }

    // checked with: {"query": "query findAllProducts { findAllProducts { productId imageId stockId price } }"}
    public List<Product> getAllProducts() {
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            throw new RuntimeException("No products found");
        }
        return allProducts;
    }

    // added a cart item with user_id = 333 for the check
    // checked with: {"query": "mutation deleteCartItemsByUserId($userId: ID!) { deleteCartItemsByUserId(userId: $userId) }","variables": { "userId": "333" }}
    public void deleteCartItemsByUserId(Long userId) {

        List<Cart> cartItems = cartRepository.findAllByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("No cart items found for user with ID " + userId);
        }
        cartRepository.deleteAll(cartItems);
    }

    // ----------------- Product Creation & Enqueuing -----------------
    // checked with: {"query": "mutation addItemtoCart($imageId: ID!, $stockId: ID!, $price: Int!, $userId: ID!) { addItemtoCart(imageId: $imageId, stockId: $stockId, price: $price, userId: $userId) }","variables": {"imageId": "1", "stockId": "1", "price": 100, "userId": "1" } }
    public String addItemtoCart(String imageId, Long stockId, Integer price, Long userId) {
        LOGGER.info("Attempting to create product with Image ID: " + imageId + ", Stock ID: " + stockId + ", Price: " + price);
        // Check if image is available
        if (!isImageAvailable(imageId)) {
            String errorMessage = "Image with ID " + imageId + " is not available";
            LOGGER.warning(errorMessage);
            return "limit exceeded";
        }
        // Create product and add to Products table
        Product newProduct = new Product();
        newProduct.setImageId(imageId);
        newProduct.setStockId(stockId);
        newProduct.setPrice(price);
        Product savedProduct = productRepository.save(newProduct);

        if (savedProduct.getProductId() == null) {
            String errorMessage = "Failed to save product in the database for Image ID: " + imageId;
            LOGGER.severe(errorMessage);
            return "error occurred";
        }
        // Add product to cart queue
        LOGGER.info("Product created with Product ID: " + savedProduct.getProductId() + ". Enqueuing to add to cart.");

        Runnable cleanupTask = cleanupTasksByUser.get()
        CartItemTask task = new CartItemTask(userId, savedProduct.getProductId(), imageId);
        cartQueue.enqueue(task);
        return "successfully added";
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

//    private void addCartItem(CartItemTask task) {
//        LOGGER.info("Adding new cart item for User ID: " + task.getUserId() + ", Product ID: " + task.getProductId());
//        cartRepository.insertCartItem(task.getUserId(), task.getProductId(), new Date());  // date logic?
//    }
//
//    // Update logic exists but due to time constraints, not implemented
//    private void updateCartItem(CartItemTask task) {
//        LOGGER.info("Updating existing cart item for User ID: " + task.getUserId() + ", Product ID: " + task.getProductId());
//        cartRepository.updateCartItem(task.getUserId(), task.getProductId(), new Date());  // date logic?
//    }

    @Scheduled(fixedDelay = 100) // 0.1 seconds
    public void processQueue() {
        CartItemTask task;
        while ((task = cartQueue.dequeue()) != null) {

            Optional<Cart> cartItem = cartRepository.findByUserIdAndProductId(task.getUserId(), task.getProductId());
            Optional<Product> product = productRepository.findByProductId(task.getProductId());

            if (cartItem.isEmpty() && product.isPresent()) {
                Cart newCartItem = new Cart(task.getUserId(), product.get(), new Date());
                cartRepository.save(newCartItem);
            } else if (cartItem.isPresent()) {
                Cart updatedCartItem = cartItem.get();
                updatedCartItem.setExpirationTime(new Date());
                cartRepository.save(updatedCartItem);
            }

            scheduleCartCleanup(task.getUserId());  // Schedule delete task
        }
    }

}
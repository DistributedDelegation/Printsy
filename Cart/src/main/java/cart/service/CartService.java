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
    private final CartQueueService cartQueueService;
    private final CartQueue cartQueue;
    private final TransactionGatewayService transactionGatewayService;


    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, TaskSchedulerService taskSchedulerService, CartQueue cartQueue, TransactionGatewayService transactionGatewayService, CartQueueService cartQueueService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.taskSchedulerService = taskSchedulerService;
        this.cartQueue = cartQueue;
        this.transactionGatewayService = transactionGatewayService;
        this.cartQueueService = cartQueueService;
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
    public Boolean completePurchase(Long userId) {
        // Cancel the scheduled task
        taskSchedulerService.cancelScheduledTask(userId);
        List<Cart> cartItems = cartRepository.findAllByUserId(userId);
        List<TransactionInput> transactionInputs = cartItems.stream() // Convert list to stream
                .map(Cart::toTransactionInput) // Map each cart item to a TransactionInput
                .toList(); // Convert the stream back into a list

        if (transactionInputs.isEmpty()) {
            LOGGER.info("No items in the cart for user ID " + userId);
            return false;
        }
        try {
            boolean success = transactionGatewayService.completeTransaction(transactionInputs);
            if (success) {
                cartRepository.deleteAll(cartItems);
            }
            return success;
        } catch (Exception e) {
            LOGGER.severe("Failed to execute transaction");
            return false;
        }
    }


    // ----------------- Cart Service -----------------
    // checked with: {"query": "query findImageByImageId($imageId: ID!) { findImageByImageId(imageId: $imageId) }","variables": { "imageId": "1" }}
    public Integer getImageCountByImageId(String imageId) {

        Integer imageCount = cartRepository.checkImagesInCart(imageId);

        if (imageCount != null) {
            return imageCount;
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
        List<Cart> cart = getCartItemsByUserId(userId);

        List<Product> products = cart.stream() // Convert list to stream
                .map(Cart::getProduct) // get product for each cart item
                .toList(); // Convert the stream back into a list

        if (products.isEmpty()) {
            throw new RuntimeException("No products found for user with ID " + userId);
        }
        return products;
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
    public String addItemToCart(String imageId, Long stockId, Integer price, Long userId) {
        LOGGER.info("Attempting to create product with Image ID: " + imageId + ", Stock ID: " + stockId + ", Price: " + price);

        // Check if image is available
        if (!isImageAvailable(imageId)) {
            String errorMessage = "Image with ID " + imageId + " is not available";
            LOGGER.warning(errorMessage);
            return "limit exceeded";
        }
        CartItemTask task = new CartItemTask(imageId, stockId, price, userId);
        Boolean success = cartQueueService.addToQueue(imageId, task);
        if (success) {
            scheduleCartCleanup(userId);
        }
        return "successfully added";
    }    

    // ----------------- Scheduled Task -----------------
    public void scheduleCartCleanup(Long userId) {
        Runnable cleanupTask = () -> {
            System.out.println("Running scheduled cart cleanup for user ID: " + userId);
            deleteCartItemsByUserId(userId);
        };
        taskSchedulerService.scheduleTask(userId, cleanupTask);
    }

    // checked with: {"query": "{ getRemainingCleanupTime }"}
    public Duration getRemainingCleanupTime(Long userId) {
        return taskSchedulerService.getRemainingTime(userId);
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

}
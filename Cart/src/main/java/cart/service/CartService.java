package cart.service;

import cart.dto.CartResult;
import cart.dto.ProductResult;
import cart.model.Cart;
import cart.model.Product;
import cart.dto.TransactionInput;
import cart.queue.CartItemTask;
import cart.queue.CartQueue;
import cart.repository.CartRepository;
import cart.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.time.Duration;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CleanUpService cleanUpService;
    private final TaskSchedulerService taskSchedulerService;
    private final CartQueueService cartQueueService;
    private final CartQueue cartQueue;
    private final TransactionGatewayService transactionGatewayService;
    private final GalleryService galleryService;


    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, ProductService productService, CleanUpService  cleanUpService,
                       TaskSchedulerService taskSchedulerService, CartQueue cartQueue,
                       TransactionGatewayService transactionGatewayService, CartQueueService cartQueueService, GalleryService galleryService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.cleanUpService = cleanUpService;
        this.taskSchedulerService = taskSchedulerService;
        this.cartQueue = cartQueue;
        this.transactionGatewayService = transactionGatewayService;
        this.cartQueueService = cartQueueService;
        this.galleryService = galleryService;
    }

    // ----------------- Transaction Service -----------------
    public boolean isImageAvailable(String imageId) {
        // Check the total count of the image in carts and transactions
        int countInCarts = cartRepository.countByProductImageId(imageId);
        int countInTransactions = transactionGatewayService.getTransactionImageAvailability(imageId);
        int total = countInCarts + countInTransactions;
        // Determine availability (assuming a threshold of 10)
        return total < 10;
    }

    // checked with: {"query": "mutation CompletePurchase($userId: ID!) { completePurchase(userId: $userId) }","variables": { "userId": "1" }}
    public boolean completePurchase(Long userId) {
        // Cancel the scheduled task
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
                cleanUpService.deleteCartAndProductEntitiesByUser(userId);
                taskSchedulerService.cancelScheduledTask(userId);
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

        Integer imageCount = cartRepository.countByProductImageId(imageId);

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

    // checked with: {"query": "query getCartItemsByUserId($userId: ID!) { getCartItemsByUserId(userId: $userId) { product userId expirationTime } }","variables": { "userId": "1" }}
    public List<CartResult> getCartItemsByUserId(Long userId) {
        List<Cart> cartItems = cartRepository.findAllByUserId(userId);
        return convertToCartResults(cartItems);
    }

    // checked with: {"query": "query checkCartProductsByUserId($userId: ID!) { checkCartProductsByUserId(userId: $userId) { productId imageId stockId price } }","variables": {"userId": "1"}}
    public List<ProductResult> checkCartProductsByUserId(Long userId) {
        List<Cart> cartItems = cartRepository.findAllByUserId(userId);

        List<Product> products = cartItems.stream() // Convert list to stream
                .map(Cart::getProduct) // get product for each cart item
                .toList(); // Convert the stream back into a list

        if (products.isEmpty()) {
            throw new RuntimeException("No products found for user with ID " + userId);
        }
        return productService.convertToProductResults(products);
    }

    // checked with: {"query": "query findProductById($productId: ID!) { findProductById(productId: $productId) { productId imageId stockId price } }","variables": { "productId": "1" }}
    public ProductResult getProductById(Long productId) {

        Optional<Product> product = productRepository.findByProductId(productId);

        if (product.isPresent()) {
            return productService.convertToProductResult(product.get());
        }
        else {
            throw new RuntimeException("Product with ID " + productId + " not found");
        }
    }

    // checked with: {"query": "query findAllProducts { findAllProducts { productId imageId stockId price } }"}
    public List<ProductResult> getAllProducts() {
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            throw new RuntimeException("No products found");
        }
        return productService.convertToProductResults(allProducts);
    }

    // added a cart item with user_id = 333 for the check
    // checked with: {"query": "mutation deleteCartItemsByUserId($userId: ID!) { deleteCartItemsByUserId(userId: $userId) }","variables": { "userId": "333" }}
    public void deleteCartItemsByUserId(Long userId) {
        cleanUpService.deleteCartAndProductEntitiesByUser(userId);
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
        cartQueueService.addToQueue(imageId, task);
        return "successfully added";
    }    

    // ----------------- Scheduled Task -----------------


    // checked with: {"query": "{ getRemainingCleanupTime }"}
    public Long getRemainingCleanupTime(Long userId) {
        Long remainingTime = taskSchedulerService.getRemainingTime(userId).getSeconds();
        LOGGER.info("Remaining clean up time for userID " + userId + ": " + remainingTime + "seconds");
        return remainingTime;
    }

    // checked with: {"query": "{ peekQueue { userId productId imageId } }"}
//    public CartItemTask peekQueue() {
//        return cartQueueService..peekQueue();
//    }


    public List<CartResult> convertToCartResults(List<Cart> cartItems) {
        // Fetch all imageUrls at once if possible to minimize calls
        List<Product> products = cartItems.stream()
                .map(Cart::getProduct)
                .toList();
        List<ProductResult> productResults = productService.convertToProductResults(products);

        List<CartResult> cartResults = new ArrayList<>();
        // Convert each product to ProductResult and collect into a list
        for (int i = 0; i < cartItems.size(); i++) {
            CartResult cartResult = new CartResult(cartItems.get(i));
            cartResult.setProductResult(productResults.get(i));
            cartResults.add(cartResult);
        }
        return cartResults;
    }

}
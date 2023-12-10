package cart.service;

import cart.model.Cart;
import cart.model.Product;
import cart.repository.CartRepository;
import cart.repository.ProductRepository;
import cart.service.TaskSchedulerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.logging.Logger;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final TaskSchedulerService taskSchedulerService;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, TaskSchedulerService taskSchedulerService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.taskSchedulerService = taskSchedulerService;
    }

    // ----------------- Transaction Service -----------------
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

    // ----------------- Scheduled Task -----------------

    public void scheduleCartCleanup(Long userId) {
        Runnable cleanupTask = () -> {
            System.out.println("Running scheduled cart cleanup for user ID: " + userId);
            deleteCartItemsByUserId(userId);
        };

        taskSchedulerService.scheduleTask(cleanupTask);
    }

}

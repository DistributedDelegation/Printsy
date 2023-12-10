package cart.service;

import cart.model.Cart;
import cart.model.Product;
import cart.repository.CartRepository;
import cart.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.logging.Logger;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final WebClient client; // Client to interact with the Transaction Service?

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository,  WebClient client) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.client = client;
    }

    public boolean isImageAvailable(Long imageId, Integer transactionCount) {
        // Check the total count of the image in carts and transactions
        Integer countInCarts = cartRepository.checkImagesInCart(imageId);
        Integer total = countInCarts + transactionCount;
    
        // Determine availability (assuming a threshold of 10)
        return total < 10;
    }
    
    public Integer getCartItemsByProductId(Long productId) {
        return cartRepository.checkImagesInCart(productId);
    }

    public List<Cart> getCartItemsByUserId(Long userId) {
        if (cartRepository.findCartItemsByUserId(userId).isEmpty()) {
            throw new RuntimeException("No cart items found for user with ID " + userId);
        }
        return cartRepository.findCartItemsByUserId(userId);
    }

    public Optional<Product> getProductById(Long productId) {
        if (productRepository.existsById(productId)) {
            return productRepository.findByProductId(productId);
        } else {
            throw new RuntimeException("Product with ID " + productId + " not found");
        }
    }

    public List<Product> getAllProducts() {
        if (productRepository.findAllProducts().isEmpty()) {
            throw new RuntimeException("No products found");
        }
        return productRepository.findAllProducts();
    }

}

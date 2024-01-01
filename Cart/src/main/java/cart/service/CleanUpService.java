package cart.service;

import cart.model.Cart;
import cart.model.Product;
import cart.repository.CartRepository;
import cart.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CleanUpService {

    private static final Logger LOGGER = Logger.getLogger(CleanUpService.class.getName());

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartQueueService cartQueueService;

    public CleanUpService(CartRepository cartRepository, ProductRepository productRepository,
                          CartQueueService taskSchedulerService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartQueueService = taskSchedulerService;
    }

    public void deleteExpiredCartAndProductEntities(){
        List<Cart> expiredCartItems = cartRepository.findByExpirationTimeBefore(Instant.now());
        deleteCartsAndProducts(expiredCartItems);
        deleteOrphanProducts();
    }

    public void scheduleCleanUpTaskForExistingItems(){
        List<Long> userIds = cartRepository.findDistinctUserIds();
        for (Long userId : userIds){
            Instant scheduledTime = cartRepository.findTopByUserId(userId).getExpirationTime();
            cartQueueService.scheduleCartCleanup(userId, scheduledTime);
        }
    }

    public void deleteCartAndProductEntitiesByUser(Long userId){
        List<Cart> cartItems = cartRepository.findAllByUserId(userId);
        deleteCartsAndProducts(cartItems);
        deleteOrphanProducts();
    }


    private void deleteCartsAndProducts(List<Cart> cartItems){
        if (cartItems.isEmpty()) {
            LOGGER.info("No cart items found for deletion");
            return;
        }

        List<Product> products = cartItems.stream()
                .map(Cart::getProduct) // Get the product for each cart
                .toList();

        if (cartItems.size() != products.size()){
            LOGGER.info("Product item and cart items do not align");
            return;
        }

        cartRepository.deleteAll(cartItems);
        productRepository.deleteAll(products);
    }

    private void deleteOrphanProducts(){
        List<Product> orphanProducts = productRepository.findProductsNotInCart();
        if (orphanProducts.isEmpty()) {
            LOGGER.info("No orphan products found for deletion");
            return;
        }
        productRepository.deleteAll(orphanProducts);
    }

    @PostConstruct
    public void init() {
        deleteExpiredCartAndProductEntities();
        scheduleCleanUpTaskForExistingItems();
    }



}

package cart.resolver;

import cart.model.Cart;
import cart.model.Product;
import cart.model.TransactionInput;
import cart.queue.CartItemTask;
import cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.Optional;
import java.time.Duration;
import java.util.List;

@Controller
public class CartResolver {

    private final CartService cartService;

    @Autowired
    public CartResolver(CartService cartService) {
        this.cartService = cartService;
    }

    // ----------------- Queries -----------------
    @QueryMapping
    public Boolean findImageAvailability(@Argument String imageId) {
        return cartService.getTransactionImageAvailability(imageId);
    }

    @QueryMapping
    public Integer findImageByImageId(@Argument String imageId) {
        return cartService.getImageByImageId(imageId);
    }

    // @QueryMapping
    // public Integer findCartItemsByProductId(@Argument Long productId) {
    //     return cartService.getCartItemsByProductId(productId);
    // }

    @QueryMapping
    public List<Cart> findCartItemsByUserId(@Argument Long userId) {
        return cartService.getCartItemsByUserId(userId);
    }

    @QueryMapping
    public List<Product> checkCartProductsByUserId(@Argument Long userId) {
        return cartService.checkCartProductsByUserId(userId);
    }

    @QueryMapping
    public Optional<Product> findProductById(@Argument Long productId) {
        return cartService.getProductById(productId);
    }

    @QueryMapping
    public List<Product> findAllProducts() {
        return cartService.getAllProducts();
    }

    @QueryMapping
    public Long getRemainingCleanupTime() {
        Duration remainingTime = cartService.getRemainingCleanupTime();
        return remainingTime.getSeconds();
    }

    @QueryMapping
    public CartItemTask peekQueue() {
        return cartService.peekQueue();
    }

    @QueryMapping   
    public List<TransactionInput> findCartItemsByUserIdForPurchase(@Argument Long userId) {
        return cartService.getCartItemsByUserIdForPurchase(userId);
    }

    // ----------------- Mutations -----------------
    @MutationMapping
    public String deleteCartItemsByUserId(@Argument Long userId) {
        cartService.deleteCartItemsByUserId(userId);
        return "Cart items deleted successfully for user ID: " + userId;
    }

    @MutationMapping
    public String addItemtoCart(@Argument String imageId, @Argument Long stockId, @Argument Integer price, @Argument Long userId) {
        return cartService.addItemtoCart(imageId, stockId, price, userId);
    }

    @MutationMapping
    public Boolean completePurchase(@Argument Long userId) {
        return cartService.completePurchase(userId);
    }

}

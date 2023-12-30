package cart.resolver;

import cart.dto.CartResult;
import cart.dto.ProductResult;
import cart.model.Product;
import cart.queue.CartItemTask;
import cart.service.CartService;
import cart.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.util.List;

@Controller
public class CartResolver {

    private final CartService cartService;

    @Autowired
    public CartResolver(CartService cartService, ProductService productService) {
        this.cartService = cartService;
    }

    // ----------------- Queries -----------------
    @QueryMapping
    public Boolean findImageAvailability(@Argument String imageId) {
        return cartService.isImageAvailable(imageId);
    }

    @QueryMapping
    public Integer findImageByImageId(@Argument String imageId) {
        return cartService.getImageCountByImageId(imageId);
    }

    // @QueryMapping
    // public Integer findCartItemsByProductId(@Argument Long productId) {
    //     return cartService.getCartItemsByProductId(productId);
    // }

    @QueryMapping
    public List<CartResult> findCartItemsByUserId(@Argument Long userId) {
        return cartService.getCartItemsByUserId(userId);
    }

    @QueryMapping
    public List<ProductResult> checkCartProductsByUserId(@Argument Long userId) {
        return cartService.checkCartProductsByUserId(userId);
    }

    @QueryMapping
    public ProductResult findProductById(@Argument Long productId) {
        return cartService.getProductById(productId);

    }

    @QueryMapping
    public List<ProductResult> findAllProducts() {
        return cartService.getAllProducts();
    }

    @QueryMapping
    public Long getRemainingCleanupTime(Long userId) {
        Duration remainingTime = cartService.getRemainingCleanupTime(userId);
        return remainingTime.getSeconds();
    }

    @QueryMapping
    public CartItemTask peekQueue() {
        return cartService.peekQueue();
    }

    @QueryMapping   
    public List<CartResult> findCartItemsByUserIdForPurchase(@Argument Long userId) {
        return cartService.getCartItemsByUserId(userId);
    }

    // ----------------- Mutations -----------------
    @MutationMapping
    public String deleteCartItemsByUserId(@Argument Long userId) {
        cartService.deleteCartItemsByUserId(userId);
        return "Cart items deleted successfully for user ID: " + userId;
    }

    @MutationMapping
    public String addItemtoCart(@Argument String imageId, @Argument Long stockId, @Argument Integer price, @Argument Long userId) {
        return cartService.addItemToCart(imageId, stockId, price, userId);
    }

    @MutationMapping
    public Boolean completePurchase(@Argument Long userId) throws JsonProcessingException {
        return cartService.completePurchase(userId);
    }

}

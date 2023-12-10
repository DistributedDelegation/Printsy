package cart.resolver;

import cart.model.Cart;
import cart.model.Product;
import cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Optional;
import java.util.List;

@Controller
public class CartResolver {

    private final CartService cartService;
    private final WebClient client;

    @Autowired
    public CartResolver(CartService cartService, WebClient client) {
        this.cartService = cartService;
        this.client = client;
    }

    // ----------------- Queries -----------------
    @QueryMapping
    public Boolean checkAvailability(@Argument Long imageId) {
        // Fetch transaction data for the image from the TransactionGateway
        Integer transactionCount = client.post()
                .uri("/graphql")
                .bodyValue("{ \"query\": \"query GetTransactionsForImage($imageId: String!) { getTransactionsForImage(imageId: $imageId) { sender receiver imageId } }\", \"variables\": { \"imageId\": \"" + imageId + "\" } }")
                .retrieve()
                .bodyToMono(Integer.class)
                .block();

        return cartService.isImageAvailable(imageId, transactionCount);
    }

    @QueryMapping
    public Integer findCartItemsByProductId(@Argument Long productId) {
        return cartService.getCartItemsByProductId(productId);
    }

    @QueryMapping
    public List<Cart> findCartItemsByUserId(@Argument Long userId) {
        return cartService.getCartItemsByUserId(userId);
    }

    @QueryMapping
    public Optional<Product> findProductById(@Argument Long productId) {
        return cartService.getProductById(productId);
    }

    @QueryMapping
    public List<Product> findAllProducts() {
        return cartService.getAllProducts();
    }

    // ----------------- Mutations -----------------
    //@MutationMapping
    //public RequestStatus addItemToCart(@Argument Long userId, @Argument Long productId, @Argument String expirationTime) {
    //    return cartService.addItemToCart(userId, productId, expirationTime);
    //}

    //@MutationMapping
    //public String deleteCartItems(@Argument Long productId, @Argument Long userId) {
    //    return cartService.deleteCartItems(productId, userId);
    //}

}

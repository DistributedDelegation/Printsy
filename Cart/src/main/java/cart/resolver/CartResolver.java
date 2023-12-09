package cart.resolver;

import cart.model.Cart;
import cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CartResolver {

    private final CartService cartService;

    @Autowired
    public CartResolver(CartService cartService) {
        this.cartService = cartService;
    }

    // ----------------- Queries -----------------
    @QueryMapping
    public Boolean checkAvailability(@Argument Long imageId) {
        return cartService.isImageAvailable(imageId);
    }

    // ----------------- Mutations -----------------
    @MutationMapping
    public Cart addToCart(@Argument Long userId, @Argument Long imageId) {
        return cartService.addToCart(userId, imageId);
    }

}

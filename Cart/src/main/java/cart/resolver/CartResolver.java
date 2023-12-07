//import com.coxautodev.graphql.tools.GraphQLMutationResolver;
//import com.coxautodev.graphql.tools.GraphQLQueryResolver;
//
//import cart.service.cart.CartStorage;
//import servive.cart.cart.service.CartService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CartResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
//    private final cart.service.CartService cartService;
//
//    @Autowired
//    public CartResolver(cart.service.CartService cartService) {
//        this.cartService = cartService; // Dependency injection
//    }
//
//    // Mutation Resolvers
//    public CartStorage addToCart(String userId, String itemId, int quantity) {
//        return cartService.addToCart(userId, itemId, quantity);
//    }
//
//    public CartStorage removeFromCart(String userId, String itemId) {
//        return cartService.removeFromCart(userId, itemId);
//    }
//
//    public CartStorage resetCartTimer(String userId) {
//        return cartService.resetCartTimer(userId);
//    }
//
//    // Query Resolvers
//    public CartStorage cartTimer(String userId) {
//        return cartService.getCartTimer(userId);
//    }
//}

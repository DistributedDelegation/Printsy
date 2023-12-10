package cart.service;//package cart.service.cart;
//
import cart.messages.ActiveMQConfig;
import cart.messages.ExpirationInfo;
import cart.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
//
//// Organises item changes
@Service
public class CartService {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void addItemToCart(CartItem item) {
        // Add item to cart logic
        jmsTemplate.convertAndSend(ActiveMQConfig.CART_UPDATE_TOPIC, item);
    }

    public void removeItemFromCart(CartItem item) {
        // Remove item from cart logic
        jmsTemplate.convertAndSend(ActiveMQConfig.CART_UPDATE_TOPIC, item);
    }

    public void updateCartExpiration(ExpirationInfo expirationInfo) {
        // Update expiration logic
        jmsTemplate.convertAndSend(ActiveMQConfig.CART_EXPIRATION_TOPIC, expirationInfo);
    }
}

//    private Map<String, CartStorage> userCart;
//
//    public cart.service.CartService() {
//        this.userCart = new HashMap<>();
//    }
//
//    public CartStorage addToCart(String userId, String itemId, int quantity) {
//        CartStorage cart = userCart.computeIfAbsent(userId, k -> new CartStorage());
//        // Add the item to the cart
//        // ... implementation details
//        return cart;
//    }
//
//    public CartStorage removeFromCart(String userId, String itemId) {
//        CartStorage cart = userCart.get(userId);
//        // Remove the item from the cart
//        // ... implementation details
//        return cart;
//    }
//
//    public int getCartTimer(String userId) {
//        CartStorage cart = userCart.get(userId);
//        // Return the remaining time for the cart
//        // ... implementation details
//        return (int) cart.getTimer();
//    }
//
//    public CartStorage resetCartTimer(String userId) {
//        CartStorage cart = userCart.get(userId);
//        // Reset the timer for the cart
//        // ... implementation details
//        return cart;
//    }
//}
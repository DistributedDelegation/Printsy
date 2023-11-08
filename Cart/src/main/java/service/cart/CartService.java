package service.cart;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

// Organises item changes
@Service
public class CartService {
    private Map<String, CartStorage> userCart;

    public CartService() {
        this.userCart = new HashMap<>();
    }

    public CartStorage addToCart(String userId, String itemId, int quantity) {
        CartStorage cart = userCart.computeIfAbsent(userId, k -> new CartStorage());
        // Add the item to the cart
        // ... implementation details
        return cart;
    }

    public CartStorage removeFromCart(String userId, String itemId) {
        CartStorage cart = userCart.get(userId);
        // Remove the item from the cart
        // ... implementation details
        return cart;
    }

    public int getCartTimer(String userId) {
        CartStorage cart = userCart.get(userId);
        // Return the remaining time for the cart
        // ... implementation details
        return (int) cart.getTimer();
    }

    public CartStorage resetCartTimer(String userId) {
        CartStorage cart = userCart.get(userId);
        // Reset the timer for the cart
        // ... implementation details
        return cart;
    }
}
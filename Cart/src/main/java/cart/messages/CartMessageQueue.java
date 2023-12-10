package cart.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import cart.model.CartItem;

@Service
public class CartMessageQueue {

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

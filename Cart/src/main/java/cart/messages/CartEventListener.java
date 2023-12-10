package cart.messages;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import cart.model.CartItem;

@Component
public class CartEventListener {

    @JmsListener(destination = ActiveMQConfig.CART_UPDATE_TOPIC)
    public void onCartUpdate(CartItem item) {
        // Handle cart update, e.g., logging or other business logic
    }

    @JmsListener(destination = ActiveMQConfig.CART_EXPIRATION_TOPIC)
    public void onCartExpirationUpdate(ExpirationInfo info) {
        // Handle cart expiration update, e.g., removing expired items
    }
}

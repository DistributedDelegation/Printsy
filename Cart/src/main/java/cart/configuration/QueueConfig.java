package cart.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cart.queue.CartQueue;

@Configuration
public class QueueConfig {

    @Bean
    public CartQueue cartQueue() {
        return new CartQueue();
    }
}

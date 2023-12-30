package cart.service;

import cart.model.Cart;
import cart.queue.CartItemTask;
import cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Service
public class TaskSchedulerService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final TaskScheduler taskScheduler;
    private final CartRepository cartRepository;
    private final HashMap<Long, ScheduledFuture<?>> cleanupTasksByUser = new HashMap<>();

    @Autowired
    public TaskSchedulerService(TaskScheduler taskScheduler, CartRepository cartRepository) {
        this.taskScheduler = taskScheduler;
        this.cartRepository = cartRepository;
    }

    public void scheduleTask(Long userId, Runnable task, Instant scheduledTime) {
        LOGGER.info("Scheduling new task for userID: " + userId);
        cancelScheduledTask(userId);  // Cancel the existing task if any

        // Update expiration time in db
        List<Cart> cartItems = cartRepository.findAllByUserId(userId);
        cartItems.forEach(cart -> {
            cart.setExpirationTime(scheduledTime);
            cartRepository.save(cart);
            LOGGER.info("Updating expiration time for " + cart);
        });

        // Schedule task, add to hashmap
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(task, scheduledTime);
        cleanupTasksByUser.put(userId, scheduledFuture);  // Store the new task
        System.out.println("New delete task for user " + userId + " scheduled to run at " + scheduledTime);
    }

    public void cancelScheduledTask(Long userId) {
        ScheduledFuture<?> scheduledFuture = cleanupTasksByUser.get(userId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            cleanupTasksByUser.remove(userId);  // Remove the cancelled task
            LOGGER.info("Cancelling existing delete task for user " + userId + ".");
        }

        else {
            LOGGER.info("No existing task for user : " + userId);
        }
    }


    public Duration getRemainingTime(Long userId) {
        ScheduledFuture<?> scheduledFuture = cleanupTasksByUser.get(userId);
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            long delayMillis = scheduledFuture.getDelay(TimeUnit.MILLISECONDS);
            if (delayMillis < 0) {
                return Duration.ZERO;
            } else {
                return Duration.ofMillis(delayMillis);
            }
        }
        return Duration.ZERO;
    }

}
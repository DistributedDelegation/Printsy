package cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


@Service
public class TaskSchedulerService {

    private final TaskScheduler taskScheduler;
    private final HashMap<Long, ScheduledFuture<?>> cleanupTasksByUser = new HashMap<>();
    private final Duration delay = Duration.ofSeconds(120);  // Needs to be changed for final demo

    @Autowired
    public TaskSchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleTask(Long userId, Runnable task) {
        cancelScheduledTask(userId);  // Cancel the existing task if any
        Instant scheduledTime = Instant.now().plus(delay);
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(task, scheduledTime);
        cleanupTasksByUser.put(userId, scheduledFuture);  // Store the new task
        System.out.println("New delete task for user " + userId + " scheduled to run at " + scheduledTime);
    }

    public void cancelScheduledTask(Long userId) {
        ScheduledFuture<?> scheduledFuture = cleanupTasksByUser.get(userId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            cleanupTasksByUser.remove(userId);  // Remove the cancelled task
            System.out.println("Existing delete scheduled task for user " + userId + " cancelled.");
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
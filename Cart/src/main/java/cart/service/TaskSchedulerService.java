package cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;
import java.time.Duration;
import java.time.Instant;


@Service
public class TaskSchedulerService {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    // Needs to be changed for final demo
    private final Duration delay = Duration.ofSeconds(30);

    @Autowired
    public TaskSchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleTask(Runnable task) {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
            System.out.println("Existing scheduled delete task cancelled to reschedule...");
        }
        Instant scheduledTime = Instant.now().plus(delay);
        scheduledFuture = taskScheduler.schedule(task, scheduledTime);
        System.out.println("New delete task scheduled to run at " + scheduledTime);
    }

    public void cancelScheduledTask() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            System.out.println("Existing delete scheduled task cancelled without rescheduling.");
        }
    }
}
package Hard;

import java.util.concurrent.*;

/*
Asynchronous Rate Limiter
Problem Statement:
Design a rate limiter that allows up to N requests per time window (e.g., 1 second).
If more than N requests come in that time, the extra ones are delayed until they can be processed.
 */

public class AsyncRateLimiter {

    private final Semaphore semaphore;
    private final int maxTokens;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AsyncRateLimiter(int maxRequestsPerSecond) {
        this.maxTokens = maxRequestsPerSecond;
        this.semaphore = new Semaphore(maxRequestsPerSecond);

        // Schedule refill every 1 second
        scheduler.scheduleAtFixedRate(() -> {
            int permitsToRelease = maxTokens - semaphore.availablePermits();
            if (permitsToRelease > 0) {
                semaphore.release(permitsToRelease); // Refill tokens
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void acquire(Runnable task) {
        new Thread(() -> {
            try {
                semaphore.acquire(); // Wait until a token is available
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    // Main method to test
    public static void main(String[] args) {
        AsyncRateLimiter limiter = new AsyncRateLimiter(3); // 3 tasks/sec

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            limiter.acquire(() -> {
                System.out.println("Task " + taskId + " executed at " + System.currentTimeMillis());
            });

            try {
                Thread.sleep(100); // simulate frequent requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Allow time for tasks to finish
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        limiter.shutdown();
    }
}


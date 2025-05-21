package Hard;

import java.util.LinkedList;
import java.util.Queue;

/*
Custom Thread Pool Executor
Problem Statement:
Implement a custom thread pool with the ability to:

Accept tasks and run them concurrently using a fixed number of worker threads.

Maintain a queue of pending tasks.

Gracefully shut down the executor.
 */

public class CustomThreadPoolExecutor {

    private final int nThreads;
    private final PoolWorker[] workers;
    private final Queue<Runnable> taskQueue = new LinkedList<>();
    private volatile boolean isShutdown = false;

    public CustomThreadPoolExecutor(int nThreads) {
        this.nThreads = nThreads;
        this.workers = new PoolWorker[nThreads];

        for (int i = 0; i < nThreads; i++) {
            workers[i] = new PoolWorker();
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        synchronized (taskQueue) {
            if (!isShutdown) {
                taskQueue.add(task);
                taskQueue.notify(); // notify a worker thread
            } else {
                throw new IllegalStateException("ThreadPool is shutting down");
            }
        }
    }

    public void shutdown() {
        isShutdown = true;
        synchronized (taskQueue) {
            taskQueue.notifyAll(); // wake up all workers so they can exit
        }
    }

    private class PoolWorker extends Thread {
        public void run() {
            while (true) {
                Runnable task;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty()) {
                        if (isShutdown) return;
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    task = taskQueue.poll();
                }
                try {
                    task.run();
                } catch (RuntimeException e) {
                    System.err.println("Task failed: " + e.getMessage());
                }
            }
        }
    }

    // Main to test
    public static void main(String[] args) {
        CustomThreadPoolExecutor pool = new CustomThreadPoolExecutor(3);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            pool.submit(() -> {
                System.out.println("Running Task " + taskId + " by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        try {
            Thread.sleep(3000); // let tasks run for a while
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        pool.shutdown();
    }
}


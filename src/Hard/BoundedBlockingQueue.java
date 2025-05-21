package Hard;

import java.util.LinkedList;
import java.util.Queue;

/*
Bounded Blocking Queue
Problem Statement:

Design a thread-safe bounded blocking queue with the following methods:

enqueue(int item) — blocks if the queue is full.
dequeue() — blocks if the queue is empty.
size() — returns the current number of elements.
 */

public class BoundedBlockingQueue {

    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void enqueue(int item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait(); // wait if full
        }
        queue.offer(item);
        System.out.println("Enqueued: " + item);
        notifyAll(); // notify waiting dequeue threads
    }

    public synchronized int dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // wait if empty
        }
        int item = queue.poll();
        System.out.println("Dequeued: " + item);
        notifyAll(); // notify waiting enqueue threads
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }

    // Main to test
    public static void main(String[] args) {
        BoundedBlockingQueue queue = new BoundedBlockingQueue(3);

        // Producer thread
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    queue.enqueue(i);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    queue.dequeue();
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();
    }
}


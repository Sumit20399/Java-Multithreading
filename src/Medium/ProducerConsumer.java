package Medium;

import java.util.LinkedList;
import java.util.Queue;

/*
Producer-Consumer (Bounded Buffer)
Goal: One or more producers add items to a buffer. Consumers remove items. Buffer has size limit.
 */

public class ProducerConsumer {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;

    public void produce(int item) throws InterruptedException {
        synchronized (this) {
            while (buffer.size() == capacity) {
                wait();
            }
            buffer.add(item);
            System.out.println("Produced: " + item);
            notifyAll();
        }
    }

    public void consume() throws InterruptedException {
        synchronized (this) {
            while (buffer.isEmpty()) {
                wait();
            }
            int item = buffer.poll();
            System.out.println("Consumed: " + item);
            notifyAll();
        }
    }

    public static void main(String[] args) {
        ProducerConsumer pc = new ProducerConsumer();

        Thread producer = new Thread(() -> {
            int value = 1;
            while (true) {
                try {
                    pc.produce(value++);
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    pc.consume();
                    Thread.sleep(800);
                } catch (InterruptedException e) {}
            }
        });

        producer.start();
        consumer.start();
    }
}


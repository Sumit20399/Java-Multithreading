package Hard;

import java.util.concurrent.CountDownLatch;

/*
Call In Order
Problem Statement (Leetcode-style):
Implement a class with methods first(), second(), and third(). Ensure that:

first() runs before second()

second() runs before third()

Even if the methods are called from different threads and in random order, they must print "first", "second", "third" in sequence.
 */

public class CallInOrder {

    private final CountDownLatch firstDone = new CountDownLatch(1);
    private final CountDownLatch secondDone = new CountDownLatch(1);

    public void first(Runnable printFirst) {
        printFirst.run();
        firstDone.countDown(); // signal that first() is done
    }

    public void second(Runnable printSecond) {
        try {
            firstDone.await(); // wait for first()
            printSecond.run();
            secondDone.countDown(); // signal that second() is done
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void third(Runnable printThird) {
        try {
            secondDone.await(); // wait for second()
            printThird.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Main method to test
    public static void main(String[] args) {
        CallInOrder obj = new CallInOrder();

        Runnable printFirst = () -> System.out.println("first");
        Runnable printSecond = () -> System.out.println("second");
        Runnable printThird = () -> System.out.println("third");

        Thread t1 = new Thread(() -> obj.first(printFirst));
        Thread t2 = new Thread(() -> obj.second(printSecond));
        Thread t3 = new Thread(() -> obj.third(printThird));

        // Start in any order
        t3.start();
        t1.start();
        t2.start();
    }
}


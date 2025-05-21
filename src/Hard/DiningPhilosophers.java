package Hard;

import java.util.concurrent.Semaphore;

/*
 Dining Philosophers (Leetcode 1226 Style Problem)
Problem:

There are 5 philosophers sitting around a round table.

Each philosopher needs two forks to eat: the one on their left and the one on their right.

You must design a method wantToEat() that allows a philosopher to pick up forks, eat, and put down forks.

Ensure no deadlocks and starvation.
 */

public class DiningPhilosophers {

    private final Semaphore[] forks = new Semaphore[5];
    private final Semaphore maxDiners = new Semaphore(4); // prevent deadlock

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) {
            forks[i] = new Semaphore(1); // one permit per fork
        }
    }

    // Each action is a Runnable: pick, eat, put
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {

        int left = philosopher;
        int right = (philosopher + 1) % 5;

        maxDiners.acquire(); // limit total number of simultaneous eaters

        forks[left].acquire();
        forks[right].acquire();

        pickLeftFork.run();
        pickRightFork.run();

        eat.run();

        putLeftFork.run();
        forks[left].release();

        putRightFork.run();
        forks[right].release();

        maxDiners.release();
    }

    public static void main(String[] args) {
        DiningPhilosophers dp = new DiningPhilosophers();

        for (int i = 0; i < 5; i++) {
            final int philosopher = i;
            new Thread(() -> {
                try {
                    dp.wantsToEat(
                            philosopher,
                            () -> System.out.println("Philosopher " + philosopher + " picked up left fork."),
                            () -> System.out.println("Philosopher " + philosopher + " picked up right fork."),
                            () -> System.out.println("Philosopher " + philosopher + " is eating."),
                            () -> System.out.println("Philosopher " + philosopher + " put down left fork."),
                            () -> System.out.println("Philosopher " + philosopher + " put down right fork.")
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}


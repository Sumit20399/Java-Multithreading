package Hard;

import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;

/*
H₂O Generator (Leetcode 1117 Style Problem)
Problem:

You are given threads representing hydrogen (H) and oxygen (O).

You must allow exactly 2 hydrogen threads and 1 oxygen thread to bond and form water (H₂O).

Threads should block if they cannot participate in forming a molecule.
 */

public class H2O {

    private Semaphore hydrogen = new Semaphore(2);
    private Semaphore oxygen = new Semaphore(1);
    private CyclicBarrier barrier = new CyclicBarrier(3);

    public H2O() {}

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        hydrogen.acquire();
        try {
            barrier.await(); // wait for 2 H and 1 O
            releaseHydrogen.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hydrogen.release();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        oxygen.acquire();
        try {
            barrier.await(); // wait for 2 H and 1 O
            releaseOxygen.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            oxygen.release();
        }
    }

    public static void main(String[] args) {
        H2O h2o = new H2O();

        Runnable releaseHydrogen = () -> System.out.print("H");
        Runnable releaseOxygen = () -> System.out.print("O");

        // HHO HHO HHO...
        String input = "OOHHHHHH"; // should produce HHOHHO

        for (char c : input.toCharArray()) {
            if (c == 'H') {
                new Thread(() -> {
                    try {
                        h2o.hydrogen(releaseHydrogen);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                new Thread(() -> {
                    try {
                        h2o.oxygen(releaseOxygen);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}


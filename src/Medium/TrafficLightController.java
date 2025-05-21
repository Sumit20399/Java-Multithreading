package Medium;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
Traffic Light Controller (Java Solution)
Problem Statement:
You need to simulate a traffic light controller that lets cars from North-South and East-West directions pass alternately.

💡 Design
Two traffic lights: one for North-South (NS) and one for East-West (EW).
At any time, only one can be green.
Threads represent cars wanting to pass.
Use a shared lock or semaphore to alternate access.
 */
public class TrafficLightController {

    enum Direction { NORTH_SOUTH, EAST_WEST }

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition northSouthCondition = lock.newCondition();
    private final Condition eastWestCondition = lock.newCondition();

    private Direction currentGreen = Direction.NORTH_SOUTH;

    public void passNorthSouth(Runnable action) throws InterruptedException {
        lock.lock();
        try {
            while (currentGreen != Direction.NORTH_SOUTH) {
                northSouthCondition.await();
            }
            action.run();
            currentGreen = Direction.EAST_WEST;
            eastWestCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void passEastWest(Runnable action) throws InterruptedException {
        lock.lock();
        try {
            while (currentGreen != Direction.EAST_WEST) {
                eastWestCondition.await();
            }
            action.run();
            currentGreen = Direction.NORTH_SOUTH;
            northSouthCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Main method in same class
    public static void main(String[] args) {
        TrafficLightController controller = new TrafficLightController();

        Runnable carNS = () -> {
            try {
                controller.passNorthSouth(() -> {
                    System.out.println("Car passed NORTH-SOUTH at " + System.currentTimeMillis());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable carEW = () -> {
            try {
                controller.passEastWest(() -> {
                    System.out.println("Car passed EAST-WEST at " + System.currentTimeMillis());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Simulate 5 cars in each direction
        for (int i = 0; i < 5; i++) {
            new Thread(carNS).start();
            new Thread(carEW).start();
        }
    }
}


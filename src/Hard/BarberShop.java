package Hard;

import java.util.concurrent.Semaphore;

/*
The Barber Shop Problem (Sleeping Barber Problem)
Problem Statement:

One barber, multiple customers.
The barber sleeps if no customers are waiting.

When a customer arrives:

If the barber is asleep, wake him up.
If chairs are available, the customer waits.
If no chairs, the customer leaves.
 */

public class BarberShop {

    private final int totalChairs;
    private int waitingCustomers = 0;

    private final Semaphore customerSemaphore = new Semaphore(0);
    private final Semaphore barberSemaphore = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1); // for mutual exclusion

    public BarberShop(int chairs) {
        this.totalChairs = chairs;
    }

    // Customer thread
    public void customerArrives(int customerId) {
        try {
            mutex.acquire();
            if (waitingCustomers < totalChairs) {
                waitingCustomers++;
                System.out.println("Customer " + customerId + " is waiting. Waiting: " + waitingCustomers);
                mutex.release();

                customerSemaphore.release(); // notify barber
                barberSemaphore.acquire();   // wait for barber to be ready

                getHairCut(customerId);
            } else {
                System.out.println("Customer " + customerId + " left (no chair available).");
                mutex.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Barber thread
    public void barberWork() {
        while (true) {
            try {
                customerSemaphore.acquire(); // wait for customer

                mutex.acquire();
                waitingCustomers--;
                System.out.println("Barber is cutting hair. Waiting: " + waitingCustomers);
                mutex.release();

                barberSemaphore.release(); // barber ready
                cutHair();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void getHairCut(int customerId) {
        System.out.println("Customer " + customerId + " is getting a haircut.");
        try {
            Thread.sleep(500); // simulate haircut time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void cutHair() {
        System.out.println("Barber is giving a haircut...");
        try {
            Thread.sleep(500); // simulate haircut time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Main method
    public static void main(String[] args) {
        BarberShop shop = new BarberShop(3); // 3 waiting chairs

        // Start barber thread
        new Thread(shop::barberWork).start();

        // Simulate customers arriving every 200ms
        for (int i = 1; i <= 10; i++) {
            final int id = i;
            new Thread(() -> shop.customerArrives(id)).start();
            try {
                Thread.sleep(200); // interval between customers
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

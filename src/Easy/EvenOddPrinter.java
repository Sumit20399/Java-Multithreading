package Easy;

/*
Goal: Two threads print numbers from 1 to N,
one prints even numbers and the other prints odd numbers in order.
*/

public class EvenOddPrinter {
    private int n;
    private int count = 1;
    private final Object lock = new Object();

    public EvenOddPrinter(int n) {
        this.n = n;
    }

    public void printOdd() {
        while (count <= n) {
            synchronized (lock) {
                if (count % 2 == 1) {
                    System.out.println("Odd: " + count);
                    count++;
                    lock.notify();
                } else {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public void printEven() {
        while (count <= n) {
            synchronized (lock) {
                if (count % 2 == 0) {
                    System.out.println("Even: " + count);
                    count++;
                    lock.notify();
                } else {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        EvenOddPrinter printer = new EvenOddPrinter(10);
        Thread t1 = new Thread(printer::printOdd);
        Thread t2 = new Thread(printer::printEven);
        t1.start();
        t2.start();
    }
}


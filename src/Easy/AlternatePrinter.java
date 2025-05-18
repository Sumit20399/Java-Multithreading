package Easy;

/*
Goal: Alternating Threads with wait/notify.
 */

public class AlternatePrinter {
    private boolean aTurn = true;

    public void printA() {
        for (int i = 0; i < 5; i++) {
            synchronized (this) {
                while (!aTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("A");
                aTurn = false;
                notify();
            }
        }
    }

    public void printB() {
        for (int i = 0; i < 5; i++) {
            synchronized (this) {
                while (aTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("B");
                aTurn = true;
                notify();
            }
        }
    }

    public static void main(String[] args) {
        AlternatePrinter ap = new AlternatePrinter();
        new Thread(ap::printA).start();
        new Thread(ap::printB).start();
    }
}


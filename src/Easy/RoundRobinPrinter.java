package Easy;

/*
Goal: Print Numbers Using N Threads in Round-Robin Fashion.
*/

public class RoundRobinPrinter {
    private final int n;
    private final int max;
    private int current = 1;
    private int turn = 0;

    public RoundRobinPrinter(int n, int max) {
        this.n = n;
        this.max = max;
    }

    public void print(int threadId) {
        while (true) {
            synchronized (this) {
                while (threadId != turn && current <= max) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (current > max) {
                    notifyAll();
                    break;
                }

                System.out.println("Thread-" + threadId + ": " + current);
                current++;
                turn = (turn + 1) % n;
                notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        int nThreads = 3;
        int maxNum = 10;
        RoundRobinPrinter printer = new RoundRobinPrinter(nThreads, maxNum);

        for (int i = 0; i < nThreads; i++) {
            int threadId = i;
            new Thread(() -> printer.print(threadId)).start();
        }
    }
}


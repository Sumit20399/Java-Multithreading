package Easy;

/*
Goal: ABCABC Pattern Printing with 3 Threads.
*/

public class ABCPrinter {
    private int turn = 0; // 0 -> A, 1 -> B, 2 -> C
    private int n;

    public ABCPrinter(int n) {
        this.n = n;
    }

    public void printA() {
        printLetter('A', 0);
    }

    public void printB() {
        printLetter('B', 1);
    }

    public void printC() {
        printLetter('C', 2);
    }

    private void printLetter(char letter, int threadTurn) {
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                while (turn != threadTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.print(letter);
                turn = (turn + 1) % 3;
                notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        ABCPrinter printer = new ABCPrinter(5); // Will print ABC 5 times
        new Thread(printer::printA).start();
        new Thread(printer::printB).start();
        new Thread(printer::printC).start();
    }
}


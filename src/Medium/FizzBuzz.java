package Medium;

/*
FizzBuzz with 4 Threads
Threads:

Thread A prints "fizz" for multiples of 3.

Thread B prints "buzz" for multiples of 5.

Thread C prints "fizzbuzz" for multiples of 3 and 5.

Thread D prints the number if it's not divisible by 3 or 5.

 */
public class FizzBuzz {
    private int n;
    private int current = 1;

    public FizzBuzz(int n) {
        this.n = n;
    }

    public synchronized void fizz() throws InterruptedException {
        while (current <= n) {
            if (current % 3 == 0 && current % 5 != 0) {
                System.out.println("fizz");
                current++;
                notifyAll();
            } else {
                waitIfNeeded();
            }
        }
    }

    public synchronized void buzz() throws InterruptedException {
        while (current <= n) {
            if (current % 5 == 0 && current % 3 != 0) {
                System.out.println("buzz");
                current++;
                notifyAll();
            } else {
                waitIfNeeded();
            }
        }
    }

    public synchronized void fizzbuzz() throws InterruptedException {
        while (current <= n) {
            if (current % 3 == 0 && current % 5 == 0) {
                System.out.println("fizzbuzz");
                current++;
                notifyAll();
            } else {
                waitIfNeeded();
            }
        }
    }

    public synchronized void number() throws InterruptedException {
        while (current <= n) {
            if (current % 3 != 0 && current % 5 != 0) {
                System.out.println(current);
                current++;
                notifyAll();
            } else {
                waitIfNeeded();
            }
        }
    }

    private void waitIfNeeded() throws InterruptedException {
        notifyAll();
        wait();
    }

    public static void main(String[] args) {
        FizzBuzz fb = new FizzBuzz(20);

        new Thread(() -> {
            try {
                fb.fizz();
            } catch (InterruptedException e) {}
        }).start();

        new Thread(() -> {
            try {
                fb.buzz();
            } catch (InterruptedException e) {}
        }).start();

        new Thread(() -> {
            try {
                fb.fizzbuzz();
            } catch (InterruptedException e) {}
        }).start();

        new Thread(() -> {
            try {
                fb.number();
            } catch (InterruptedException e) {}
        }).start();
    }
}


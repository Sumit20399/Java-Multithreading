package Medium;

/*
Dining Philosophers
Goal: Prevent deadlock while allowing 5 philosophers to eat with 5 shared chopsticks.
*/

public class DiningPhilosophers {
    private final Object[] chopsticks = new Object[5];

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) {
            chopsticks[i] = new Object();
        }
    }

    public void wantsToEat(int id) throws InterruptedException {
        Object left = chopsticks[id];
        Object right = chopsticks[(id + 1) % 5];

        Object first = id % 2 == 0 ? left : right;
        Object second = id % 2 == 0 ? right : left;

        synchronized (first) {
            synchronized (second) {
                System.out.println("Philosopher " + id + " is eating.");
                Thread.sleep(500);
            }
        }
    }

    public static void main(String[] args) {
        DiningPhilosophers table = new DiningPhilosophers();

        for (int i = 0; i < 5; i++) {
            final int id = i;
            new Thread(() -> {
                while (true) {
                    try {
                        table.wantsToEat(id);
                    } catch (InterruptedException e) {}
                }
            }).start();
        }
    }
}


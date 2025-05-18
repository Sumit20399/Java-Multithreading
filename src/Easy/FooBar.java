package Easy;

/*
Goal: FooBar Alternation.
 */
public class FooBar {
    private int n;
    private boolean fooTurn = true;

    public FooBar(int n) {
        this.n = n;
    }

    public void foo() {
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                while (!fooTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.print("Foo");
                fooTurn = false;
                notifyAll();
            }
        }
    }

    public void bar() {
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                while (fooTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Bar");
                fooTurn = true;
                notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        FooBar fb = new FooBar(5);
        new Thread(fb::foo).start();
        new Thread(fb::bar).start();
    }
}


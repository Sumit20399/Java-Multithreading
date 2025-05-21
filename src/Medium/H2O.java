package Medium;

/*
H2O Problem (Water Formation)
Goal: Two hydrogen threads and one oxygen thread combine to form one water molecule (H₂O).
 */

public class H2O {
    private int hydrogenCount = 0;
    private int oxygenCount = 0;

    public synchronized void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        while (hydrogenCount == 2) {
            wait();
        }
        releaseHydrogen.run();
        hydrogenCount++;
        formWaterIfReady();
    }

    public synchronized void oxygen(Runnable releaseOxygen) throws InterruptedException {
        while (oxygenCount == 1) {
            wait();
        }
        releaseOxygen.run();
        oxygenCount++;
        formWaterIfReady();
    }

    private void formWaterIfReady() {
        if (hydrogenCount == 2 && oxygenCount == 1) {
            hydrogenCount = 0;
            oxygenCount = 0;
            notifyAll();
        }
    }

    public static void main(String[] args) {
        H2O h2o = new H2O();

        Runnable releaseHydrogen = () -> System.out.print("H");
        Runnable releaseOxygen = () -> System.out.print("O");

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                try {
                    h2o.hydrogen(releaseHydrogen);
                } catch (InterruptedException e) {}
            }).start();
        }

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    h2o.oxygen(releaseOxygen);
                } catch (InterruptedException e) {}
            }).start();
        }
    }
}


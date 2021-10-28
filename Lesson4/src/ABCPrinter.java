public class ABCPrinter {
    private final Object monitor = new Object();
    private final int REPEAT_COUNT = 5;
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {
        ABCPrinter w = new ABCPrinter();

        Thread threadA = new Thread(() -> w.printA());
        Thread threadB = new Thread(() -> w.printB());
        Thread threadC = new Thread(() -> w.printC());
        threadA.start();
        threadB.start();
        threadC.start();
    }

    public void printA() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < REPEAT_COUNT; i++) {
                    while (currentLetter != 'A') {
                        monitor.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void printB() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < REPEAT_COUNT; i++) {
                    while (currentLetter != 'B') {
                        monitor.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void printC() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < REPEAT_COUNT; i++) {
                    while (currentLetter != 'C') {
                        monitor.wait();
                    }
                    System.out.print("C");
                    currentLetter = 'A';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

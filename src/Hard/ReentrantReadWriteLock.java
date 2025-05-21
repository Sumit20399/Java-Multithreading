package Hard;

import java.util.HashMap;
import java.util.Map;

/*
Reentrant Read-Write Lock
Problem:

Multiple threads can acquire the read lock simultaneously if no writer is active.
Only one thread can acquire the write lock, and it must wait until all readers and writers have released.
Should support reentrancy: A thread holding the lock can reacquire it without deadlocking itself.
 */
public class ReentrantReadWriteLock {

    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;

    private final Map<Thread, Integer> readingThreads = new HashMap<>();
    private Thread writingThread = null;
    private int writeAccessCount = 0;

    public synchronized void lockRead() throws InterruptedException {
        Thread callingThread = Thread.currentThread();
        while (!canGrantReadAccess(callingThread)) {
            wait();
        }
        readingThreads.put(callingThread, getReadCount(callingThread) + 1);
        readers++;
    }

    public synchronized void unlockRead() {
        Thread callingThread = Thread.currentThread();
        int count = getReadCount(callingThread);
        if (count == 1) {
            readingThreads.remove(callingThread);
        } else {
            readingThreads.put(callingThread, count - 1);
        }
        readers--;
        notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {
        writeRequests++;
        Thread callingThread = Thread.currentThread();
        while (!canGrantWriteAccess(callingThread)) {
            wait();
        }
        writeRequests--;
        writers++;
        writingThread = callingThread;
        writeAccessCount++;
    }

    public synchronized void unlockWrite() {
        if (Thread.currentThread() != writingThread) {
            throw new IllegalMonitorStateException("Calling thread does not hold the write lock");
        }

        writeAccessCount--;
        if (writeAccessCount == 0) {
            writers--;
            writingThread = null;
        }
        notifyAll();
    }

    private boolean canGrantReadAccess(Thread thread) {
        if (isWriter(thread)) return true; // reentrant
        if (writers > 0) return false;
        if (writeRequests > 0) return false;
        return true;
    }

    private boolean canGrantWriteAccess(Thread thread) {
        if (isOnlyReader(thread)) return true; // upgrade from read
        if (writers == 0 && readers == 0) return true;
        if (isWriter(thread)) return true; // reentrant write
        return false;
    }

    private int getReadCount(Thread thread) {
        return readingThreads.getOrDefault(thread, 0);
    }

    private boolean isWriter(Thread thread) {
        return writingThread == thread;
    }

    private boolean isOnlyReader(Thread thread) {
        return readers == 1 && readingThreads.get(thread) != null;
    }

    // Main to test
    public static void main(String[] args) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        Runnable reader = () -> {
            try {
                lock.lockRead();
                System.out.println(Thread.currentThread().getName() + " reading...");
                Thread.sleep(300);
                lock.unlockRead();
                System.out.println(Thread.currentThread().getName() + " done reading.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable writer = () -> {
            try {
                lock.lockWrite();
                System.out.println(Thread.currentThread().getName() + " writing...");
                Thread.sleep(500);
                lock.unlockWrite();
                System.out.println(Thread.currentThread().getName() + " done writing.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        new Thread(reader, "Reader-1").start();
        new Thread(reader, "Reader-2").start();
        new Thread(writer, "Writer-1").start();
        new Thread(reader, "Reader-3").start();
    }
}


package com.paulok777.lab1;

import java.util.concurrent.LinkedBlockingQueue;

public class Task1 {

    private Mutex mutex;

    public Task1(Mutex mutex) {
        this.mutex = mutex;
    }

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<Runnable> listOfWaitingThreads = new LinkedBlockingQueue<>();
        Mutex mutex1 = new Mutex(listOfWaitingThreads);
        Mutex mutex2 = new Mutex(listOfWaitingThreads);

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                new Thread(new Test1(mutex1)).start();
            } else {
                new Thread(new Test1(mutex2)).start();
            }
        }

        // Test notify
//        for (int i = 0; i < 10; i++) {
//            Thread.sleep(500);
//            mutex1._notify();
//        }

        // Test notifyAll
        for (int i = 0; i < 5; i++) {
            Thread.sleep(500);
            mutex1._notifyAll();
        }
    }

    static class Test1 implements Runnable {

        private Mutex mutex;

        public Test1(Mutex mutex) {
            this.mutex = mutex;
        }

        @Override
        public void run() {
            try {
                mutex._lock();
                mutex._wait();
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            } finally {
                mutex._release();
            }
        }
    }
}

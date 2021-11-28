package com.paulok777.lab1;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Mutex {

    private static final long SLEEP_TIME = 100L;

    private AtomicReference<Runnable> currentThread = new AtomicReference<>();
    private LinkedBlockingQueue<Runnable> waitingThreads;

    public Mutex(LinkedBlockingQueue<Runnable> waitingThreads) {
        this.waitingThreads = waitingThreads;
    }

    public void _lock() throws InterruptedException {
        if (!Thread.currentThread().equals(currentThread.get())) {
            throw new RuntimeException("You can't lock mutex 2 or more times");
        }

        while (!currentThread.compareAndSet(null, Thread.currentThread())) {
            Thread.sleep(SLEEP_TIME);
        }
        System.out.println("Mutex took by: " + Thread.currentThread().getName());
    }

    public void _release() {
        if (!Thread.currentThread().equals(currentThread.get())) {
            throw new RuntimeException("You can't call release when you don't have lock");
        }

        currentThread.set(null);
        System.out.println("Mutex released by: " + Thread.currentThread().getName());
    }

    public void _wait() throws InterruptedException {
        Thread thread = Thread.currentThread();
        if (!thread.equals(currentThread.get())) {
            throw new RuntimeException("You should lock mutex before use of wait method");
        }

        waitingThreads.put(thread);
        System.out.println("Waiting: " + Thread.currentThread().getName());

        while (waitingThreads.contains(thread)) {
            Thread.sleep(SLEEP_TIME);
        }
        System.out.println("No waiting any more: " + Thread.currentThread().getName());
    }

    public void _notify() throws InterruptedException {
        waitingThreads.take();
        System.out.println("Notify: " + Thread.currentThread().getName());
    }

    public void _notifyAll() {
        waitingThreads.clear();
        System.out.println("Notify all: " + Thread.currentThread().getName());
    }
}

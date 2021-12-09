package com.paulok777.lab1.task1;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Mutex {

    private AtomicReference<Runnable> currentThread = new AtomicReference<>();
    private LinkedBlockingQueue<Runnable> waitingThreads;

    public Mutex(LinkedBlockingQueue<Runnable> waitingThreads) {
        this.waitingThreads = waitingThreads;
    }

    public void _lock() throws InterruptedException {
        if (Thread.currentThread().equals(currentThread.get())) {
            throw new RuntimeException("You can't lock mutex 2 or more times");
        }

        while (!currentThread.compareAndSet(null, Thread.currentThread())) {
            Thread.yield();
        }
        System.out.println("Mutex took by: " + Thread.currentThread().getName());
    }

    public void _unlock() {
        if (!Thread.currentThread().equals(currentThread.get())) {
            throw new RuntimeException("You can't call unlock when you don't have lock");
        }

        System.out.println("Mutex unlocked by: " + Thread.currentThread().getName());
        currentThread.set(null);
    }

    public void _wait() throws InterruptedException {
        Thread thread = Thread.currentThread();
        if (!thread.equals(currentThread.get())) {
            throw new RuntimeException("You should lock mutex before use of wait method");
        }

        waitingThreads.put(thread);
        System.out.println("Waiting: " + Thread.currentThread().getName());
        _unlock();

        while (waitingThreads.contains(thread)) {
            Thread.yield();
        }

        _lock();
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

package com.paulok777.lab1.task3;

public class Task3 {

    public static void main(String[] args) throws InterruptedException {
        MichaelAndScottQueue<String> michaelAndScottQueue = new MichaelAndScottQueue<>();
        Thread[] arr = new Thread[10];

        for (int i = 0; i < 10; i++) {
            arr[i] = new Thread(new Test3(michaelAndScottQueue));
            arr[i].start();
        }

        for (int i = 0; i < 10; i++) {
            arr[i].join();
        }

        michaelAndScottQueue.nonSafePrint();
    }

    static class Test3 implements Runnable {

        private MichaelAndScottQueue<String> michaelAndScottQueue;

        public Test3(MichaelAndScottQueue<String> michaelAndScottQueue) {
            this.michaelAndScottQueue = michaelAndScottQueue;
        }

        @Override
        public void run() {
            michaelAndScottQueue.add(Thread.currentThread().getName());
            if (Math.random() > 0.3) {
                michaelAndScottQueue.remove();
            }
        }
    }
}

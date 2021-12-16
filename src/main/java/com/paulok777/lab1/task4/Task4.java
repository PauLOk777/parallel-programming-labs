package com.paulok777.lab1.task4;

public class Task4 {

    public static void main(String[] args) throws InterruptedException {
        HarrisOrderedList<String> harrisOrderedList = new HarrisOrderedList<>();
        Thread[] arr = new Thread[10];

        for (int i = 0; i < 10; i++) {
            arr[i] = new Thread(new Test4(harrisOrderedList));
            arr[i].start();
        }

        for (int i = 0; i < 10; i++) {
            arr[i].join();
        }


        harrisOrderedList.nonSafePrint();
    }

    static class Test4 implements Runnable {

        HarrisOrderedList<String> harrisOrderedList;

        public Test4(HarrisOrderedList<String> harrisOrderedList) {
            this.harrisOrderedList = harrisOrderedList;
        }

        @Override
        public void run() {
            String currThreadName = Thread.currentThread().getName();
            harrisOrderedList.add(currThreadName);
            if (currThreadName.equals("Thread-6")
                    || currThreadName.equals("Thread-3")
                    || currThreadName.equals("Thread-0")
            ) {
                harrisOrderedList.remove(currThreadName);
            }
        }
    }
}

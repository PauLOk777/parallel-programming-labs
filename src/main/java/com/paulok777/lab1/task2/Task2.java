package com.paulok777.lab1.task2;

public class Task2 {

    public static void main(String[] args) throws InterruptedException {
        SkipList<String> skipList = new SkipList<>(16, 0.5);
        Thread[] arr = new Thread[10];

        for (int i = 0; i < 10; i++) {
            arr[i] = new Thread(new Test2(skipList));
            arr[i].start();
        }

        for (int i = 0; i < 10; i++) {
            arr[i].join();
        }

        System.out.println("Contains: " + skipList.contains("Thread-1"));
        skipList.nonSafePrint();
    }

    static class Test2 implements Runnable {

        private SkipList<String> skipList;

        public Test2(SkipList<String> skipList) {
            this.skipList = skipList;
        }

        @Override
        public void run() {
            String currThreadName = Thread.currentThread().getName();
            System.out.println("Add " + currThreadName + ": " + skipList.add(currThreadName));

            if (currThreadName.equals("Thread-4")) {
                System.out.println("Remove " + currThreadName + ": " + skipList.remove(currThreadName));
            }
        }
    }
}

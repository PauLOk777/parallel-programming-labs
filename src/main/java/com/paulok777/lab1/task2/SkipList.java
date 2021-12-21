package com.paulok777.lab1.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SkipList<T extends Comparable<? super T>> {

    private int height;
    private double p;
    private Node<T> head;

    static class Node<T> {

        public T data;
        public AtomicReference<Node<T>> right;
        public Node<T> down;

        public Node(T data, AtomicReference<Node<T>> right, Node<T> down) {
            this.data = data;
            this.right = right;
            this.down = down;
        }
    }

    public SkipList(int height, double p) {
        this.height = height;
        this.p = p;

        Node<T> element = new Node<>(null, new AtomicReference<>(null), null);
        head = element;

        for (int i = 0; i < height - 1; i++) {
            Node<T> newElementHead = new Node<>(null, new AtomicReference<>(null), null);
            element.down = newElementHead;
            element = newElementHead;
        }
    }

    public boolean remove(T data) {
        if (isNull(data)) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        Node<T> currEl = head;
        int currentLevel = height;
        boolean towerUnmarked = true;

        while (currentLevel > 0) {
            Node<T> rightEl = currEl.right.get();
            if (nonNull(rightEl) && rightEl.data.compareTo(data) == 0) {
                Node<T> afterRightEl = rightEl.right.get();
                if (towerUnmarked) {
                    Node<T> towerEl = rightEl;
                    while (nonNull(towerEl)) {
                        towerEl.right.compareAndSet(towerEl.right.get(), null);
                        towerEl = towerEl.down;
                    }
                    towerUnmarked = false;
                }

                currEl.right.compareAndSet(rightEl, afterRightEl);
            }

            if (nonNull(rightEl) && rightEl.data.compareTo(data) < 0) {
                currEl = rightEl;
            } else {
                currEl = currEl.down;
                currentLevel--;
            }
        }

        return !towerUnmarked;
    }

    public boolean add(T data) {
        if (isNull(data)) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        List<Node<T>> prev = new ArrayList<>();
        List<Node<T>> prevRight = new ArrayList<>();
        Node<T> currEl = head;
        int levelOfTower = generateHeight();
        int currentLevel = height;

        while (currentLevel > 0) {
            Node<T> rightEl = currEl.right.get();

            if (currentLevel <= levelOfTower) {
                if (isNull(rightEl) || rightEl.data.compareTo(data) >= 0) {
                    prev.add(currEl);
                    prevRight.add(rightEl);
                }
            }

            if (nonNull(rightEl) && rightEl.data.compareTo(data) < 0) {
                currEl = rightEl;
            } else {
                currEl = currEl.down;
                currentLevel--;
            }
        }

        Node<T> downEl = null;
        for (int i = prev.size() - 1; i >= 0; i--) {
            Node<T> newEl = new Node<>(data, new AtomicReference<>(prevRight.get(i)), null);

            if (nonNull(downEl)) {
                newEl.down = downEl;
            }

            if (!prev.get(i).right.compareAndSet(prevRight.get(i), newEl) && i == prev.size() - 1) {
                return false;
            }

            downEl = newEl;
        }

        return true;
    }

    public boolean contains(T data) {
        Node<T> currEl = head;

        while (nonNull(currEl)) {
            Node<T> rightEl = currEl.right.get();
            if (nonNull(currEl.data) && currEl.data.compareTo(data) == 0) {
                return true;
            }
            else if (nonNull(rightEl) && rightEl.data.compareTo(data) <= 0) {
                currEl = rightEl;
            } else {
                currEl = currEl.down;
            }
        }

        return false;
    }

    public void nonSafePrint() {
        Node<T> curr = head;

        while (nonNull(curr.down)) {
            curr = curr.down;
        }

        curr = curr.right.get();

        while (nonNull(curr)) {
            System.out.println(curr.data);
            curr = curr.right.get();
        }
    }

    private int generateHeight() {
        int lvl = 1;

        while (lvl < height && Math.random() < p) {
            lvl++;
        }

        return lvl;
    }
}

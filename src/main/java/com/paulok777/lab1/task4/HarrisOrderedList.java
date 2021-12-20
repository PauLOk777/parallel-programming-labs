package com.paulok777.lab1.task4;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;

public class HarrisOrderedList<T extends Comparable<? super T>> {

    private Node<T> head = new Node<>(null, new AtomicReference<>(null));

    static class Node<T> {

        public T data;
        public AtomicReference<Node<T>> next;

        public Node(T data, AtomicReference<Node<T>> next) {
            this.data = data;
            this.next = next;
        }
    }

    public boolean remove(T data) {
        if (isNull(data)) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        Node<T> prevEl = head;
        while (nonNull(prevEl.next.get())) {
            Node<T> currEl = prevEl.next.get();
            Node<T> nextEl = currEl.next.get();

            if (currEl.data.compareTo(data) == 0) {
                if (currEl.next.compareAndSet(nextEl, null) && prevEl.next.compareAndSet(currEl, nextEl)) {
                    return true;
                }
            } else {
                prevEl = currEl;
            }
        }

        return false;
    }

    public void add(T data) {
        if (isNull(data)) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        Node<T> newEl = new Node<>(data, new AtomicReference<>(null));
        Node<T> currentEl = head;

        while (true) {
            Node<T> nextEl = currentEl.next.get();

            if (nonNull(nextEl)) {
                if (nextEl.data.compareTo(data) >= 0) {
                    newEl.next = new AtomicReference<>(nextEl);
                    if (currentEl.next.compareAndSet(nextEl, newEl)) {
                        return;
                    }
                } else {
                    currentEl = nextEl;
                }
            } else if (currentEl.next.compareAndSet(null, newEl)) {
                return;
            }
        }
    }

    public boolean contains(T data) {
        Node<T> currentEl = head.next.get();

        while (nonNull(currentEl)) {
            if (currentEl.data.compareTo(data) == 0) {
                return true;
            }

            currentEl = currentEl.next.get();
        }

        return false;
    }

    public void nonSafePrint() {
        Node<T> current = head.next.get();
        while (nonNull(current)) {
            System.out.println(current.data);
            current = current.next.get();
        }
    }
}

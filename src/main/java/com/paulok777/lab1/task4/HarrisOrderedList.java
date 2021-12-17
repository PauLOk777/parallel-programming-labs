package com.paulok777.lab1.task4;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class HarrisOrderedList<T extends Comparable<? super T>> {

    private Node<T> dummy = new Node<>(null, new AtomicReference<>(null));
    private AtomicReference<Node<T>> head = new AtomicReference<>(dummy);

    static class Node<T> {

        public T key;
        public AtomicReference<Node<T>> next;

        public Node(T key, AtomicReference<Node<T>> next) {
            this.key= key;
            this.next = next;
        }
    }

    public boolean remove(T key) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        Node<T> prevEl = head.get();
        while (Objects.nonNull(prevEl.next.get())) {
            Node<T> currEl = prevEl.next.get();
            Node<T> nextEl = currEl.next.get();

            if (currEl.key.compareTo(key) == 0) {
                currEl.next.compareAndSet(nextEl, null);
                prevEl.next.compareAndSet(currEl, nextEl);
                return true;
            }

            prevEl = prevEl.next.get();
        }

        return false;
    }

    public void add(T key) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        Node<T> newEl = new Node<>(key, new AtomicReference<>(null));
        Node<T> currentEl = head.get();

        while (Objects.nonNull(currentEl.next.get())) {
            Node<T> nextEl = currentEl.next.get();
            if (nextEl.key.compareTo(key) >= 0) {
                newEl.next = new AtomicReference<>(nextEl);
                if (currentEl.next.compareAndSet(newEl.next.get(), newEl)) {
                    return;
                } else {
                    continue;
                }
            }

            currentEl = nextEl;
        }

        currentEl.next.compareAndSet(null, newEl);
    }

    public void nonSafePrint() {
        Node<T> current = head.get().next.get();
        while (Objects.nonNull(current)) {
            System.out.println(current.key);
            current = current.next.get();
        }
    }
}

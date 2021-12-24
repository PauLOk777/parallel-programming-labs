package com.paulok777.lab2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.paulok777.lab2.actors.Library;
import com.paulok777.lab2.actors.Visitor;
import com.paulok777.lab2.models.BookRequest;
import com.paulok777.lab2.models.LibraryBook;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.paulok777.lab2.commands.VisitorCommands.REQUEST_BOOK_FOR_LIBRARY;
import static com.paulok777.lab2.commands.VisitorCommands.RETURN_BOOK;

public class Main {

    private static final String BOOK_1 = "BOOK_1";
    private static final String BOOK_2 = "BOOK_2";
    private static final String BOOK_3 = "BOOK_3";

    public static void main(String[] args) throws InterruptedException {
        ActorSystem akkaSystem = ActorSystem.create("library-system");
        ActorRef library = akkaSystem.actorOf(Library.props(getBooks()), "library");
        Thread[] threads = new Thread[1];

        for (int i = 0; i < 1; i++) {
            threads[i] = new Thread(new TestVisitor(akkaSystem, library));
            threads[i].start();
        }

        for (int i = 0; i < 1; i++) {
            threads[i].join();
        }

        akkaSystem.terminate();
    }

    public static Map<String, LibraryBook> getBooks() {
        Map<String, LibraryBook> books = new ConcurrentHashMap<>();
        books.put(BOOK_1, new LibraryBook(BOOK_1, true, true, true));
        books.put(BOOK_2, new LibraryBook(BOOK_2, true, false, true));
        books.put(BOOK_3, new LibraryBook(BOOK_3, true, true, false));
        return books;
    }

    static class TestVisitor implements Runnable {

        private ActorSystem akkaSystem;
        private ActorRef library;

        public TestVisitor(ActorSystem akkaSystem, ActorRef library) {
            this.akkaSystem = akkaSystem;
            this.library = library;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            ActorRef visitor = akkaSystem.actorOf(Visitor.props(library), threadName);
            visitor.tell(new BookRequest(REQUEST_BOOK_FOR_LIBRARY, BOOK_1), ActorRef.noSender());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            visitor.tell(new BookRequest(RETURN_BOOK, BOOK_1), ActorRef.noSender());
        }
    }
}

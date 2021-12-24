package com.paulok777.lab2.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.paulok777.lab2.models.Book;
import com.paulok777.lab2.models.BookRequest;
import com.paulok777.lab2.models.BookResponse;

import java.util.HashMap;
import java.util.Map;

import static com.paulok777.lab2.commands.LibraryCommands.APPROVE_GIVING_BOOK_FOR_LIBRARY;
import static com.paulok777.lab2.commands.LibraryCommands.APPROVE_GIVING_BOOK_FOR_HOME;
import static com.paulok777.lab2.commands.LibraryCommands.REJECT_GIVING_BOOK;
import static com.paulok777.lab2.commands.VisitorCommands.RETURN_BOOK;

public class Visitor extends AbstractActor {

    private Map<String, Book> booksForLibrary = new HashMap();
    private Map<String, Book> booksForHome = new HashMap<>();
    private ActorRef library;

    public Visitor(ActorRef library) {
        this.library = library;
    }

    public static Props props(ActorRef library) {
        return Props.create(Visitor.class, library);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(BookRequest.class, this::handleBookRequest)
                .match(BookResponse.class, this::handleBookResponse)
                .build();
    }

    private void handleBookRequest(BookRequest request) {
        String command = request.getCommand();
        System.out.println("Visitor(" + self().path().name() + "): tell visitor to " + command);
        if (RETURN_BOOK.equals(command)) {
            returnBook(request.getBookName());
            return;
        }
        requestBook(command, request.getBookName());
    }

    private void handleBookResponse(BookResponse response) {
        switch (response.getCommand()) {
            case APPROVE_GIVING_BOOK_FOR_LIBRARY: {
                booksForLibrary.put(response.getBook().getName(), response.getBook());
                System.out.println("Visitor(" + self().path().name() + "): took book for library");
                break;
            }
            case APPROVE_GIVING_BOOK_FOR_HOME: {
                booksForHome.put(response.getBook().getName(), response.getBook());
                System.out.println("Visitor(" + self().path().name() + "): took book for home");
                break;
            }
            case REJECT_GIVING_BOOK: {
                System.out.println("Visitor(" + self().path().name() + "): unable to take a book");
                break;
            }
            default: {
                System.out.println("Visitor(" + self().path().name() + "): unknown command for visitor handler");
            }
        }
    }

    public void requestBook(String command, String bookName) {
        library.tell(new BookRequest(command, bookName), getSelf());
    }

    public void returnBook(String bookName) {
        if (booksForLibrary.containsKey(bookName)) {
            library.tell(new BookResponse(RETURN_BOOK, booksForLibrary.get(bookName)), ActorRef.noSender());
            booksForLibrary.remove(bookName);
            System.out.println("Visitor(" + self().path().name() + "): book returned from library");
        } else if (booksForHome.containsKey(bookName)) {
            library.tell(new BookResponse(RETURN_BOOK, booksForLibrary.get(bookName)), ActorRef.noSender());
            booksForHome.remove(bookName);
            System.out.println("Visitor(" + self().path().name() + "): book returned from home");
        } else {
            System.out.println("Visitor(" + self().path().name() + "): unable to return book that you don't have");
        }
    }
}

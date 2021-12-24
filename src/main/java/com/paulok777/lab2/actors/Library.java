package com.paulok777.lab2.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.paulok777.lab2.models.Book;
import com.paulok777.lab2.models.BookRequest;
import com.paulok777.lab2.models.BookResponse;
import com.paulok777.lab2.models.LibraryBook;

import java.util.Map;

import static com.paulok777.lab2.commands.LibraryCommands.APPROVE_GIVING_BOOK_FOR_LIBRARY;
import static com.paulok777.lab2.commands.LibraryCommands.APPROVE_GIVING_BOOK_FOR_HOME;
import static com.paulok777.lab2.commands.LibraryCommands.REJECT_GIVING_BOOK;
import static com.paulok777.lab2.commands.VisitorCommands.REQUEST_BOOK_FOR_HOME;
import static com.paulok777.lab2.commands.VisitorCommands.REQUEST_BOOK_FOR_LIBRARY;
import static com.paulok777.lab2.commands.VisitorCommands.RETURN_BOOK;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Library extends AbstractActor {

    private Map<String, LibraryBook> books;

    public Library(Map<String, LibraryBook> books) {
        this.books = books;
    }

    public static Props props(Map<String, LibraryBook> books) {
        return Props.create(Library.class, books);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(BookRequest.class, this::handleBookRequest)
                .match(BookResponse.class, this::handleBookResponse)
                .build();
    }

    private void handleBookRequest(BookRequest request) {
        ActorRef visitor = getSender();
        String bookName = request.getBookName();
        if (!books.containsKey(bookName) || !books.get(bookName).isAvailable()) {
            System.out.println("Library: rejected to give book (no book)");
            rejectGivingBook(visitor);
        }

        LibraryBook book = books.get(bookName);
        switch (request.getCommand()) {
            case REQUEST_BOOK_FOR_LIBRARY: {
                if (!book.isAvailableForLibrary()) {
                    System.out.println("Library: rejected to give book for library");
                    rejectGivingBook(visitor);
                } else {
                    System.out.println("Library: approved to give book for library");
                    book.setAvailable(FALSE);
                    approveGivingBook(visitor, APPROVE_GIVING_BOOK_FOR_LIBRARY, book);
                }
                break;
            }
            case REQUEST_BOOK_FOR_HOME: {
                if (!book.isAvailableForHome()) {
                    System.out.println("Library: rejected to give book for home");
                    rejectGivingBook(visitor);
                } else {
                    System.out.println("Library: approved to give book for home");
                    book.setAvailable(FALSE);
                    approveGivingBook(visitor, APPROVE_GIVING_BOOK_FOR_HOME, book);
                }
                break;
            }
            default: {
                System.out.println("Library: unknown command");
            }
        }
    }

    private void handleBookResponse(BookResponse response) {
        String command = response.getCommand();
        String bookName = response.getBook().getName();
        if (RETURN_BOOK.equals(command) && books.containsKey(bookName)) {
            LibraryBook book = books.get(bookName);
            book.setAvailable(TRUE);
            System.out.println("Library: book returned");
        } else {
            System.out.println("Library: unknown command for library handler");
        }
    }

    private void approveGivingBook(ActorRef visitor, String command, Book book) {
        visitor.tell(new BookResponse(command, book), ActorRef.noSender());
    }

    private void rejectGivingBook(ActorRef visitor) {
        visitor.tell(new BookResponse(REJECT_GIVING_BOOK, null), ActorRef.noSender());
    }
}

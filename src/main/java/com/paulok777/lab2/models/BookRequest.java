package com.paulok777.lab2.models;

public class BookRequest {

    private String command;
    private String bookName;

    public BookRequest(String command, String bookName) {
        this.command = command;
        this.bookName = bookName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}

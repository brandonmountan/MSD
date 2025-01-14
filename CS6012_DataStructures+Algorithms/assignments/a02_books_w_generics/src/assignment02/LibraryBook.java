package assignment02;

import java.util.GregorianCalendar;

public class LibraryBook<Type> extends Book {
    Type holder;
    GregorianCalendar dueDate;

    public LibraryBook(long isbn, String author, String title) {
        super(isbn, author, title);
    }

    public Type getHolder() {
        return holder;
    }

    public GregorianCalendar getDueDate() {
        return dueDate;
    }

    public void checkBookIn() {
        // if library book is checked in, holder and dueDate should be set to null
        this.holder = null;
        this.dueDate = null;
    }

    public boolean checkBookOut(Type holder, GregorianCalendar dueDate) {
        // first, ensure book isn't already checked out
        if (this.holder != null) {
            return false; // the book is already checked out
        }
        this.holder = holder;
        this.dueDate = dueDate;
        return true;
    }

}

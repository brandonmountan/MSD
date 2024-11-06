package assignment02;

import java.util.GregorianCalendar;

public class LibraryBook extends Book {
    String holder;
    GregorianCalendar dueDate;

    public LibraryBook(long isbn, String author, String title) {
        super(isbn, author, title);
        this.holder = null;
        this.dueDate = null;
    }

    public String getHolder() {
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

    public boolean checkBookOut(String holder, GregorianCalendar dueDate) {
        // first, ensure book isn't already checked out
        if (this.holder != null) {
            return false; // the book is already checked out
        }
        this.holder = holder;
        this.dueDate = dueDate;
        return true;
    }

    public void setDueDate(GregorianCalendar dueDate) {
        this.dueDate = dueDate;
    }
}

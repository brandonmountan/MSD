package assignment02;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    @Test
    public void testEmpty() {
        Library lib = new Library();
        assertNull(lib.lookup(978037429279L));

        ArrayList<LibraryBook> booksCheckedOut = lib.lookup("Jane Doe");
        assertEquals(booksCheckedOut.size(), 0);

        assertFalse(lib.checkout(978037429279L, "Jane Doe", 1, 1, 2008));
        assertFalse(lib.checkin(978037429279L));
        assertFalse(lib.checkin("Jane Doe"));
    }

    @Test
    public void testNonEmpty() {

        var lib = new Library<String>();
        // test a small library
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        assertNull(lib.lookup(9780330351690L)); //not checked out
        var res = lib.checkout(9780330351690L, "Jane Doe", 1, 1, 2008);
        assertTrue(res);
        var booksCheckedOut = lib.lookup("Jane Doe");
        assertEquals(booksCheckedOut.size(), 1);
        System.out.println(new Book(9780330351690L, "Jon Krakauer", "Into the Wild"));
        assertEquals(booksCheckedOut.get(0), new Book(9780330351690L, "Jon Krakauer", "Into the Wild"));
        assertEquals(booksCheckedOut.get(0).getHolder(), "Jane Doe");
        assertEquals(booksCheckedOut.get(0).getDueDate(), new GregorianCalendar(2008, 1, 1));
        res = lib.checkin(9780330351690L);
        assertTrue(res);
        res = lib.checkin("Jane Doe");
        assertFalse(res);
    }
    @Test
    public void testLargeLibrary(){
        // test a medium library
        var lib = new Library<String>();
        lib.addAll("Mushroom_Publishing.txt");

        // Initial check for a specific book that should not be checked out
        assertNull(lib.lookup(9781843190004L)); // Not checked out initially

        // Check out a book and validate holder and due date information
        var res = lib.checkout(9781843190004L, "Brandon Mountan", 2, 1, 2008);
        assertTrue(res);
        var booksCheckedOut = lib.lookup("Brandon Mountan");
        assertEquals(1, booksCheckedOut.size());
        assertEquals(booksCheckedOut.get(0), new Book(9781843190004L, "Moyra Caldecott", "Weapons of the Wolfhound"));
        assertEquals("Brandon Mountan", booksCheckedOut.get(0).getHolder());
        assertEquals(new GregorianCalendar(2008, 2, 1), booksCheckedOut.get(0).getDueDate());

        // Check out another book to a different person and confirm details
        res = lib.checkout(9781843190011L, "Emily Stone", 5, 15, 2023);
        assertTrue(res);
        booksCheckedOut = lib.lookup("Emily Stone");
        assertEquals(1, booksCheckedOut.size());
        assertEquals(booksCheckedOut.get(0), new Book(9781843190011L, "Moyra Caldecott", "The Eye of Callanish"));
        assertEquals("Emily Stone", booksCheckedOut.get(0).getHolder());
        assertEquals(new GregorianCalendar(2023, 5, 15), booksCheckedOut.get(0).getDueDate());

        // Attempt to check out a book already checked out
        res = lib.checkout(9781843190004L, "John Doe", 3, 1, 2023);
        assertFalse(res); // Should fail as it is already checked out by Brandon Mountan

        // Check in a book by ISBN and verify it’s no longer checked out
        boolean checkinResult = lib.checkin(9781843190004L);
        assertTrue(checkinResult);
        assertNull(lib.lookup(9781843190004L)); // Should return null after check-in

        // Check that "Brandon Mountan" has no books checked out after the check-in
        booksCheckedOut = lib.lookup("Brandon Mountan");
        assertEquals(0, booksCheckedOut.size());

        // Attempt to check in a non-checked-out book by ISBN (should return false)
        checkinResult = lib.checkin(9781843190349L);
        assertFalse(checkinResult, "Check-in should fail for a book not checked out.");

        // Attempt to check in by holder name, confirm it returns correct results
        checkinResult = lib.checkin("Emily Stone");
        assertTrue(checkinResult);
        booksCheckedOut = lib.lookup("Emily Stone");
        assertEquals(0, booksCheckedOut.size(), "Emily should have no checked out books after check-in.");
        assertNull(lib.lookup(9781843190011L)); // Confirm the book is now available

        // Verify a non-existent ISBN cannot be looked up or checked in
        assertNull(lib.lookup(9999999999999L), "Lookup should return null for non-existent ISBN.");
        assertFalse(lib.checkin(9999999999999L), "Check-in should return false for non-existent ISBN.");
    }

    @Test
    public void stringLibraryTest() {
        // test a library that uses names (String) to id patrons
        Library<String> lib = new Library<>();
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        String patron1 = "Jane Doe";

        assertTrue(lib.checkout(9780330351690L, patron1, 1, 1, 2008));
        assertTrue(lib.checkout(9780374292799L, patron1, 1, 1, 2008));

        var booksCheckedOut1 = lib.lookup(patron1);
        assertEquals(booksCheckedOut1.size(), 2);
        assertTrue(booksCheckedOut1.contains(new Book(9780330351690L, "Jon Krakauer", "Into the Wild")));
        assertTrue(booksCheckedOut1.contains(new Book(9780374292799L, "Thomas L. Friedman", "The World is Flat")));
        assertEquals(booksCheckedOut1.get(0).getHolder(), patron1);
        assertEquals(booksCheckedOut1.get(0).getDueDate(), new GregorianCalendar(2008, 1, 1));
        assertEquals(booksCheckedOut1.get(1).getHolder(),patron1);
        assertEquals(booksCheckedOut1.get(1).getDueDate(),new GregorianCalendar(2008, 1, 1));

        assertTrue(lib.checkin(patron1));

    }

    @Test
    public void phoneNumberTest(){
        // test a library that uses phone numbers (PhoneNumber) to id patrons
        var lib = new Library<PhoneNumber>();
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        PhoneNumber patron2 = new PhoneNumber("801.555.1234");

        assertTrue(lib.checkout(9780330351690L, patron2, 1, 1, 2008));
        assertTrue(lib.checkout(9780374292799L, patron2, 1, 1, 2008));

        ArrayList<LibraryBook<PhoneNumber>> booksCheckedOut2 = lib.lookup(patron2);

        assertEquals(booksCheckedOut2.size(), 2);
        assertTrue(booksCheckedOut2.contains(new Book(9780330351690L, "Jon Krakauer", "Into the Wild")));
        assertTrue(booksCheckedOut2.contains(new Book(9780374292799L, "Thomas L. Friedman", "The World is Flat")));
        assertEquals(booksCheckedOut2.get(0).getHolder(),patron2);
        assertEquals(booksCheckedOut2.get(0).getDueDate(),new GregorianCalendar(2008, 1, 1));
        assertEquals(booksCheckedOut2.get(1).getHolder(), patron2);
        assertEquals(booksCheckedOut2.get(1).getDueDate(), new GregorianCalendar(2008, 1, 1));

        assertTrue(lib.checkin(patron2));

    }

}
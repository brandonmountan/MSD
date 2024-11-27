package assignment05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

class WebBrowserTest {

    private WebBrowser browser;

    @BeforeEach
    void setUp() {
        browser = new WebBrowser();
    }

    @Test
    void testVisit() throws MalformedURLException {
        URL url = new URL("http://example.com");
        browser.visit(url);
        assertEquals(url, browser.getCurrentPage());
    }

    @Test
    void testBack() throws MalformedURLException {
        URL url1 = new URL("http://example1.com");
        URL url2 = new URL("http://example2.com");
        browser.visit(url1);
        browser.visit(url2);
        assertEquals(url1, browser.back());
    }

    @Test
    void testForward() throws MalformedURLException {
        URL url1 = new URL("http://example1.com");
        URL url2 = new URL("http://example2.com");
        browser.visit(url1);
        browser.visit(url2);
        browser.back(); // Move back to url1
        assertEquals(url2, browser.forward());
    }

    @Test
    void testBackNoHistory() {
        assertThrows(NoSuchElementException.class, () -> browser.back());
    }

    @Test
    void testForwardNoHistory() {
        assertThrows(NoSuchElementException.class, () -> browser.forward());
    }

    @Test
    void testGetCurrentPage() throws MalformedURLException {
        URL url = new URL("http://example.com");
        browser.visit(url);
        assertEquals(url, browser.getCurrentPage());
    }

    @Test
    void testHistory() throws MalformedURLException {
        URL url1 = new URL("http://example1.com");
        URL url2 = new URL("http://example2.com");
        browser.visit(url1);
        browser.visit(url2);

        SinglyLinkedList<URL> history = browser.history();
        assertEquals(2, history.size());
        assertEquals("http://example1.com", url1.toString());
        assertEquals("http://example2.com", url2.toString());
    }

    @Test
    void testVisitClearsForwardHistory() throws MalformedURLException {
        URL url1 = new URL("http://example1.com");
        URL url2 = new URL("http://example2.com");
        URL url3 = new URL("http://example3.com");
        browser.visit(url1);
        browser.visit(url2);
        browser.back();
        browser.visit(url3);

        assertTrue(browser.history().size() == 2); // Ensures forward history is cleared after visiting a new page
    }
}

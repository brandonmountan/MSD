package assignment05;

import java.net.URL;
import java.util.NoSuchElementException;

/**
 * A simple web browser implementation that supports back and forward navigation.
 *
 * This class uses two stacks, one for the back history and one for the forward history,
 * to simulate the navigation functionality in a web browser.
 */
public class WebBrowser {
    private LinkedListStack<URL> backStack;
    private LinkedListStack<URL> forwardStack;
    private URL currentPage;

    /**
     * Constructs an empty WebBrowser with no history and no current page.
     */
    public WebBrowser() {
        this.backStack = new LinkedListStack<>();
        this.forwardStack = new LinkedListStack<>();
        this.currentPage = null;
    }

    /**
     * Constructs a WebBrowser with an initial history.
     *
     * @param history a list of URLs representing the initial browser history
     */
    public WebBrowser(SinglyLinkedList<URL> history) {
        this.backStack = new LinkedListStack<>();
        this.forwardStack = new LinkedListStack<>();
        this.currentPage = history.getFirst();
        // populate back stack with all previous URLs in reverse order
        for (int i = history.size() - 1; i > 0; i--) {
            backStack.push(history.get(i));
        }
        // Set the last element as the current page
    }

    /**
     * Visits a new URL by adding the current page to the back stack and clearing the forward stack.
     *
     * @param webpage the URL to visit
     */
    public void visit(URL webpage) {
        if (currentPage != null) {
            backStack.push(currentPage);
        }
        currentPage = webpage;
        forwardStack.clear(); // Clear the forward stack when visiting a new page
    }

    /**
     * Goes back to the previous page in the history.
     *
     * @return the URL of the previous page
     * @throws NoSuchElementException if there is no back page available
     */
    public URL back() {
        if (backStack.isEmpty()) {
            throw new NoSuchElementException("No back page available.");
        }
        forwardStack.push(currentPage);
        currentPage = backStack.pop();
        return currentPage;
    }

    /**
     * Goes forward to the next page in the history.
     *
     * @return the URL of the next page
     * @throws NoSuchElementException if there is no forward page available
     */
    public URL forward() {
        if (forwardStack.isEmpty()) {
            throw new NoSuchElementException("No forward page available.");
        }
        backStack.push(currentPage);
        currentPage = forwardStack.pop();
        return currentPage;
    }

    /**
     * Gets the current page.
     *
     * @return the current URL of the browser, or null if no page is currently open
     */
    public URL getCurrentPage() {
        return currentPage;
    }

    /**
     * Returns the entire history of visited pages, including the back history, current page, and forward history.
     *
     * @return a SinglyLinkedList containing the full browsing history in chronological order
     */
    public SinglyLinkedList<URL> history() {
        SinglyLinkedList<URL> historyList = new SinglyLinkedList<>();

        // Add backStack elements in the correct order (back history is reversed)
        LinkedListStack<URL> tempBackStack = new LinkedListStack<>();
        while (!backStack.isEmpty()) {
            URL page = backStack.pop();
            tempBackStack.push(page);
        }
        while (!tempBackStack.isEmpty()) {
            URL page = tempBackStack.pop();
            historyList.insertFirst(page); // Insert back history in reverse order
            backStack.push(page); // Restore backStack
        }

        // Add the current page (if any)
        if (currentPage != null) {
            historyList.insertFirst(currentPage);
        }

        // Add forwardStack elements in order (forward history remains in the same order)
        LinkedListStack<URL> tempForwardStack = new LinkedListStack<>();
        while (!forwardStack.isEmpty()) {
            URL page = forwardStack.pop();
            tempForwardStack.push(page);
        }
        while (!tempForwardStack.isEmpty()) {
            URL page = tempForwardStack.pop();
            historyList.insertFirst(page); // Insert forward history in order (effectively appends)
            forwardStack.push(page); // Restore forwardStack
        }

        return historyList;
    }


}

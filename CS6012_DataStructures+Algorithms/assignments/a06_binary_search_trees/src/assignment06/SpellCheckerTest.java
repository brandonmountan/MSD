package assignment06;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpellCheckerTest {

    private SpellChecker spellChecker;

    @BeforeEach
    void setUp() {
        spellChecker = new SpellChecker();
    }

    @Test
    void testAddToDictionary() {
        spellChecker.addToDictionary("apple");
        spellChecker.addToDictionary("banana");

        List<String> words = spellChecker.spellCheck(createTemporaryFile("apple orange"));
        assertEquals(List.of("orange"), words, "Only 'orange' should be misspelled.");
    }

    @Test
    void testRemoveFromDictionary() {
        spellChecker.addToDictionary("apple");
        spellChecker.addToDictionary("orange");
        spellChecker.removeFromDictionary("orange");

        List<String> words = spellChecker.spellCheck(createTemporaryFile("apple orange grape"));
        assertEquals(List.of("orange", "grape"), words, "Both 'orange' and 'grape' should be misspelled.");
    }

    @Test
    void testSpellCheckWithEmptyDictionary() {
        List<String> words = spellChecker.spellCheck(createTemporaryFile("apple banana cherry"));
        assertEquals(List.of("apple", "banana", "cherry"), words, "All words should be misspelled.");
    }

    @Test
    void testBuildDictionaryFromList() {
        List<String> dictionaryWords = List.of("apple", "banana", "cherry");
        spellChecker = new SpellChecker(dictionaryWords);

        List<String> words = spellChecker.spellCheck(createTemporaryFile("apple banana grape"));
        assertEquals(List.of("grape"), words, "'grape' should be the only misspelled word.");
    }

    @Test
    void testBuildDictionaryFromFile() {
        File dictionaryFile = createTemporaryFile("apple\nbanana\ncherry");
        spellChecker = new SpellChecker(dictionaryFile);

        List<String> words = spellChecker.spellCheck(createTemporaryFile("apple banana grape"));
        assertEquals(List.of("grape"), words, "'grape' should be the only misspelled word.");
    }

    @Test
    void testSpellCheckCaseInsensitive() {
        spellChecker.addToDictionary("Apple");
        spellChecker.addToDictionary("BaNaNa");

        List<String> words = spellChecker.spellCheck(createTemporaryFile("apple banana APPLE BANANA grape"));
        assertEquals(List.of("grape"), words, "Only 'grape' should be misspelled regardless of case.");
    }

    @Test
    void testSpellCheckEmptyDocument() {
        spellChecker.addToDictionary("apple");
        List<String> words = spellChecker.spellCheck(createTemporaryFile(""));
        assertTrue(words.isEmpty(), "No words should be misspelled in an empty document.");
    }

    @Test
    void testSpellCheckSymbolsIgnored() {
        spellChecker.addToDictionary("hello");
        spellChecker.addToDictionary("world");

        List<String> words = spellChecker.spellCheck(createTemporaryFile("Hello, world! How's it going?"));
        assertEquals(List.of("how's", "it", "going"), words, "Words with symbols should be handled correctly.");
    }

    /**
     * Helper method to create a temporary file with given content.
     *
     * @param content the content to write to the temporary file
     * @return the created File
     */
    private File createTemporaryFile(String content) {
        try {
            File tempFile = File.createTempFile("test", ".txt");
            tempFile.deleteOnExit();
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(content);
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Error creating temporary file for testing.", e);
        }
    }
}

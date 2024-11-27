package assignment07;

/**
 * A mediocre hash functor with some improvement in distribution.
 */
public class MediocreHashFunctor implements HashFunctor {
    @Override
    public int hash(String item) {
        int hash = 0;
        for (char c : item.toCharArray()) {
            hash += c; // Sum of character codes.
        }
        return hash;
    }
}

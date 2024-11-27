package assignment07;

/**
 * A bad hash functor that results in many collisions.
 */
public class BadHashFunctor implements HashFunctor {
    @Override
    public int hash(String item) {
        return item.length(); // Poor distribution based on string length.
    }
}
